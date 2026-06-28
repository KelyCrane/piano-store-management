<template>
  <div class="schedule-page">
    <course-schedule-table
      title="我的课表"
      role="teacher"
      :bookings="list"
      :week-start="weekStart"
      :loading="loading"
      @prev-week="moveWeek(-1)"
      @next-week="moveWeek(1)"
      @today="goCurrentWeek"
      @refresh="loadData"
    />
  </div>
</template>

<script>
import request from '@/utils/request'
import CourseScheduleTable from '@/components/CourseScheduleTable.vue'

export default {
  name: 'TeacherTimetable',
  components: { CourseScheduleTable },
  data() {
    return {
      list: [],
      loading: false,
      weekStart: null
    }
  },
  created() {
    this.weekStart = this.getWeekStart(new Date())
    this.loadData()
  },
  methods: {
    loadData() {
      this.loading = true
      const user = JSON.parse(sessionStorage.getItem('loginUser') || '{}')
      request.get('/api/booking', {
        params: {
          pageNum: 1,
          pageSize: 999,
          teacherId: user.id,
          status: 1,
          dateFrom: this.formatDate(this.weekStart),
          dateTo: this.formatDate(this.addDays(this.weekStart, 6)),
          sortMode: 'schedule'
        }
      }).then(res => {
        this.list = res.data.records || []
      }).finally(() => {
        this.loading = false
      })
    },
    moveWeek(offset) {
      this.weekStart = this.addDays(this.weekStart, offset * 7)
      this.loadData()
    },
    goCurrentWeek() {
      this.weekStart = this.getWeekStart(new Date())
      this.loadData()
    },
    getWeekStart(date) {
      const current = new Date(date)
      current.setHours(0, 0, 0, 0)
      const day = current.getDay() || 7
      current.setDate(current.getDate() - day + 1)
      return current
    },
    addDays(date, days) {
      const next = new Date(date)
      next.setDate(next.getDate() + days)
      return next
    },
    formatDate(date) {
      const year = date.getFullYear()
      const month = `${date.getMonth() + 1}`.padStart(2, '0')
      const day = `${date.getDate()}`.padStart(2, '0')
      return `${year}-${month}-${day}`
    }
  }
}
</script>

<style scoped>
.schedule-page {
  max-width: 1320px;
  margin: 0 auto;
}
</style>
