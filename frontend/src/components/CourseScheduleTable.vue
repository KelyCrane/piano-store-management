<template>
  <el-card class="schedule-card" shadow="never">
    <div slot="header" class="schedule-head">
      <div>
        <div class="schedule-title">{{ title }}</div>
        <div class="schedule-range">{{ weekRangeText }}</div>
      </div>
      <div class="schedule-actions">
        <el-button size="small" icon="el-icon-arrow-left" @click="$emit('prev-week')">上一周</el-button>
        <el-button size="small" icon="el-icon-date" @click="$emit('today')">本周</el-button>
        <el-button size="small" @click="$emit('next-week')">下一周<i class="el-icon-arrow-right el-icon--right" /></el-button>
        <el-button size="small" type="primary" icon="el-icon-refresh" @click="$emit('refresh')">刷新</el-button>
      </div>
    </div>

    <el-table
      v-if="timeRows.length"
      :data="timeRows"
      border
      class="week-table"
      v-loading="loading"
    >
      <el-table-column prop="label" label="节次" width="118" fixed />
      <el-table-column
        v-for="day in weekDays"
        :key="day.key"
        min-width="170"
      >
        <template slot="header">
          <div class="day-head">
            <span>{{ day.monthDay }}</span>
            <small>{{ day.weekday }}</small>
          </div>
        </template>
        <template slot-scope="scope">
          <div class="cell-lessons">
            <div
              v-for="lesson in lessonsFor(day.key, scope.row.key)"
              :key="lesson.id"
              class="lesson-block"
            >
              <div class="lesson-course">{{ lesson.courseName || '课程' }}</div>
              <div class="lesson-meta">
                <i class="el-icon-user" />
                <span v-if="role === 'teacher'">{{ lesson.studentName || '学生' }}</span>
                <span v-else>{{ lesson.teacherName || '教师' }}</span>
              </div>
              <div class="lesson-meta">
                <i class="el-icon-location-outline" />
                <span>{{ lesson.classroomName || '未安排教室' }}</span>
              </div>
              <div class="lesson-meta" v-if="lesson.packageName">
                <i class="el-icon-collection" />
                <span>{{ lesson.packageName }}</span>
              </div>
            </div>
          </div>
        </template>
      </el-table-column>
    </el-table>

    <el-empty v-else :description="emptyText" v-loading="loading" />
  </el-card>
</template>

<script>
const weekdays = ['星期一', '星期二', '星期三', '星期四', '星期五', '星期六', '星期日']

export default {
  name: 'CourseScheduleTable',
  props: {
    title: {
      type: String,
      default: '我的课程表'
    },
    bookings: {
      type: Array,
      default: () => []
    },
    weekStart: {
      type: Date,
      required: true
    },
    role: {
      type: String,
      default: 'student'
    },
    loading: {
      type: Boolean,
      default: false
    }
  },
  computed: {
    weekDays() {
      return Array.from({ length: 7 }).map((_, index) => {
        const date = this.addDays(this.weekStart, index)
        return {
          key: this.dateKey(date),
          monthDay: `${date.getMonth() + 1}月${date.getDate()}日`,
          weekday: weekdays[index]
        }
      })
    },
    weekRangeText() {
      const end = this.addDays(this.weekStart, 6)
      return `${this.formatDate(this.weekStart)} 至 ${this.formatDate(end)}`
    },
    timeRows() {
      const rowMap = new Map()
      this.bookings.forEach(booking => {
        const start = this.parseDateTime(booking.classTime)
        if (!start || !this.inCurrentWeek(start)) return
        const duration = Number(booking.duration || 60)
        const end = new Date(start.getTime() + duration * 60000)
        const key = `${this.timeKey(start)}-${duration}`
        rowMap.set(key, {
          key,
          minutes: start.getHours() * 60 + start.getMinutes(),
          label: `${this.timeKey(start)}-${this.timeKey(end)}`
        })
      })
      return Array.from(rowMap.values()).sort((a, b) => a.minutes - b.minutes)
    },
    emptyText() {
      return this.loading ? '正在加载课程表' : '本周暂无已确认课程'
    }
  },
  methods: {
    lessonsFor(dayKey, rowKey) {
      return this.bookings.filter(booking => {
        const start = this.parseDateTime(booking.classTime)
        if (!start) return false
        const duration = Number(booking.duration || 60)
        return this.dateKey(start) === dayKey && `${this.timeKey(start)}-${duration}` === rowKey
      })
    },
    inCurrentWeek(date) {
      const start = new Date(this.weekStart)
      start.setHours(0, 0, 0, 0)
      const end = this.addDays(start, 7)
      return date >= start && date < end
    },
    parseDateTime(value) {
      if (!value) return null
      if (value instanceof Date) return value
      const parts = String(value).split(' ')
      const dateParts = (parts[0] || '').split('-').map(Number)
      const timeParts = (parts[1] || '00:00:00').split(':').map(Number)
      if (dateParts.length < 3 || dateParts.some(item => Number.isNaN(item))) return null
      return new Date(
        dateParts[0],
        dateParts[1] - 1,
        dateParts[2],
        timeParts[0] || 0,
        timeParts[1] || 0,
        timeParts[2] || 0
      )
    },
    addDays(date, days) {
      const next = new Date(date)
      next.setDate(next.getDate() + days)
      return next
    },
    dateKey(date) {
      return this.formatDate(date)
    },
    formatDate(date) {
      const year = date.getFullYear()
      const month = `${date.getMonth() + 1}`.padStart(2, '0')
      const day = `${date.getDate()}`.padStart(2, '0')
      return `${year}-${month}-${day}`
    },
    timeKey(date) {
      return `${date.getHours()}`.padStart(2, '0') + ':' + `${date.getMinutes()}`.padStart(2, '0')
    }
  }
}
</script>

<style scoped>
.schedule-card {
  border-radius: 12px;
}

.schedule-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  flex-wrap: wrap;
}

.schedule-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.schedule-range {
  margin-top: 4px;
  color: #909399;
  font-size: 13px;
}

.schedule-actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.week-table >>> th {
  background: #f5f7fa;
}

.day-head {
  display: flex;
  flex-direction: column;
  gap: 2px;
  line-height: 1.2;
}

.day-head span {
  font-weight: 600;
  color: #303133;
}

.day-head small {
  color: #909399;
}

.cell-lessons {
  min-height: 86px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.lesson-block {
  padding: 8px;
  border: 1px solid #d9ecff;
  border-left: 4px solid #409eff;
  border-radius: 8px;
  background: #f4f9ff;
}

.lesson-course {
  font-weight: 600;
  color: #1f2d3d;
  margin-bottom: 6px;
  line-height: 1.35;
}

.lesson-meta {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #606266;
  font-size: 12px;
  line-height: 1.5;
}

.lesson-meta span {
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
