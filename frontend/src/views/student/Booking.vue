<template>
  <div class="booking-page">
    <section class="booking-toolbar">
      <div class="toolbar-title">
        <h2>课程预约</h2>
        <p>先购买课程课时包，再从老师开放的排课时段中预约上课。</p>
      </div>
      <el-form :inline="true" :model="query" size="small" class="toolbar-form">
        <el-form-item label="课程名称">
          <el-input
            v-model="query.name"
            placeholder="搜索课程"
            clearable
            prefix-icon="el-icon-search"
            @keyup.enter.native="loadCourses"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="loadCourses">搜索</el-button>
        </el-form-item>
      </el-form>
    </section>

    <el-row v-if="courses.length" :gutter="20" class="course-grid">
      <el-col v-for="course in courses" :key="course.id" :xs="24" :sm="12" :md="8" :lg="6">
        <article class="course-card" @click="goDetail(course)">
          <div class="course-card-cover">
            <img v-if="course.coverImage" :src="course.coverImage" alt="">
            <div v-else class="course-card-cover--empty"><i class="el-icon-headset" /></div>
            <span class="course-card-price">¥{{ formatMoney(course.price) }}/课时</span>
          </div>
          <div class="course-card-body">
            <h3 class="course-card-title">{{ course.name }}</h3>
            <div class="course-card-teacher">
              <i class="el-icon-user-solid" />
              <span class="course-card-teacher-name">{{ course.teacherName || '待定' }}</span>
            </div>
            <div class="course-card-meta">
              <span><i class="el-icon-time" /> {{ course.duration || 60 }} 分钟/节</span>
            </div>
            <div class="course-card-actions" @click.stop>
              <el-button size="small" class="btn-consult" icon="el-icon-chat-dot-round" @click="openConsult(course)">咨询</el-button>
              <el-button size="small" plain icon="el-icon-shopping-cart-2" @click="openOrder(course)">购课时包</el-button>
              <el-button type="primary" size="small" icon="el-icon-date" @click="openBooking(course)">预约</el-button>
            </div>
          </div>
        </article>
      </el-col>
    </el-row>

    <el-empty v-else description="暂无符合条件的课程" class="empty-block" />

    <el-card class="my-bookings-card" shadow="never">
      <div slot="header" class="my-bookings-header">我的预约</div>
      <el-table :data="myBookings" border stripe>
        <el-table-column prop="courseName" label="课程" min-width="140" />
        <el-table-column prop="teacherName" label="教师" width="110" />
        <el-table-column prop="packageName" label="课时包" min-width="140" />
        <el-table-column prop="hoursCost" label="扣课时" width="80" />
        <el-table-column prop="classTime" label="上课时间" width="170" />
        <el-table-column prop="status" label="状态" width="90">
          <template slot-scope="scope">
            <el-tag :type="statusTagType(scope.row.status)" size="small">{{ statusText(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
        <el-table-column label="操作" width="170" align="center">
          <template slot-scope="scope">
            <div class="table-action-btns">
              <el-button v-if="canChangeBooking(scope.row)" size="mini" @click="openReschedule(scope.row)">改约</el-button>
              <el-button v-if="canCancelBooking(scope.row)" size="mini" type="danger" plain @click="cancelBooking(scope.row)">取消</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <course-booking-dialog
      :visible.sync="bookingVisible"
      :course="selectedCourse"
      @success="loadMyBookings"
    />

    <course-package-order-dialog
      :visible.sync="orderVisible"
      :course="selectedCourse"
      @success="handleOrderSuccess"
    />

    <consult-teacher-dialog :visible.sync="consultVisible" :course="consultCourse" />

    <el-dialog title="改约课程" :visible.sync="rescheduleVisible" width="720px">
      <div v-loading="rescheduleLoading">
        <el-alert
          title="改约后需要教师重新审批，通过前不会进入课程表。"
          type="info"
          :closable="false"
          show-icon
          style="margin-bottom: 14px"
        />
        <el-table
          v-if="rescheduleSlots.length"
          :data="rescheduleSlots"
          border
          stripe
          max-height="320"
          @row-click="selectRescheduleSlot"
        >
          <el-table-column width="55" align="center">
            <template slot-scope="scope">
              <el-radio v-model="rescheduleForm.classTime" :label="scope.row.slotTime">&nbsp;</el-radio>
            </template>
          </el-table-column>
          <el-table-column prop="slotTime" label="可改约时间" min-width="170" />
          <el-table-column prop="duration" label="时长(分钟)" width="100" />
          <el-table-column prop="classroomName" label="教室" min-width="120" />
          <el-table-column prop="remark" label="备注" show-overflow-tooltip />
        </el-table>
        <el-empty v-else description="暂无可改约时段" />
        <el-input
          v-model="rescheduleForm.remark"
          type="textarea"
          :rows="2"
          placeholder="可补充改约原因"
          style="margin-top: 14px"
        />
      </div>
      <span slot="footer">
        <el-button @click="rescheduleVisible = false">取消</el-button>
        <el-button type="primary" :loading="rescheduleSubmitting" @click="submitReschedule">提交改约</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import request from '@/utils/request'
import ConsultTeacherDialog from '@/components/ConsultTeacherDialog.vue'
import CourseBookingDialog from '@/components/CourseBookingDialog.vue'
import CoursePackageOrderDialog from '@/components/CoursePackageOrderDialog.vue'

export default {
  name: 'StudentBooking',
  components: {
    ConsultTeacherDialog,
    CourseBookingDialog,
    CoursePackageOrderDialog
  },
  data() {
    return {
      query: {
        name: ''
      },
      courses: [],
      myBookings: [],
      consultVisible: false,
      bookingVisible: false,
      orderVisible: false,
      selectedCourse: {},
      consultCourse: {},
      rescheduleVisible: false,
      rescheduleLoading: false,
      rescheduleSubmitting: false,
      rescheduleRow: null,
      rescheduleSlots: [],
      rescheduleForm: {
        classTime: '',
        remark: ''
      }
    }
  },
  created() {
    this.loadCourses()
    this.loadMyBookings()
  },
  methods: {
    loadCourses() {
      request.get('/api/course', {
        params: {
          pageNum: 1,
          pageSize: 999,
          status: 1,
          ...this.query
        }
      }).then(res => {
        this.courses = res.data.records || []
      })
    },
    loadMyBookings() {
      const user = JSON.parse(sessionStorage.getItem('loginUser') || '{}')
      request.get('/api/booking', {
        params: {
          pageNum: 1,
          pageSize: 999,
          studentId: user.id
        }
      }).then(res => {
        this.myBookings = res.data.records || []
      })
    },
    formatMoney(value) {
      return Number(value || 0).toFixed(2)
    },
    goDetail(course) {
      this.$router.push(`/student/course/${course.id}`)
    },
    openConsult(course) {
      this.consultCourse = { ...course }
      this.consultVisible = true
    },
    openBooking(course) {
      this.selectedCourse = { ...course }
      this.bookingVisible = true
    },
    openOrder(course) {
      this.selectedCourse = { ...course }
      this.orderVisible = true
    },
    handleOrderSuccess() {
      this.$router.push('/student/order')
    },
    statusText(status) {
      if (status === 1) return '已通过'
      if (status === 2) return '已拒绝'
      if (status === 3) return '已取消'
      if (status === 4) return '已过期'
      return '待审核'
    },
    statusTagType(status) {
      if (status === 1) return 'success'
      if (status === 2) return 'danger'
      if (status === 3) return 'info'
      if (status === 4) return 'info'
      return 'warning'
    },
    canCancelBooking(row) {
      return (row.status === 0 || row.status === 1) && !this.isPast(row.classTime)
    },
    canChangeBooking(row) {
      return (row.status === 0 || row.status === 1) && !this.isPast(row.classTime)
    },
    isPast(value) {
      const date = this.parseDateTime(value)
      return !!date && date.getTime() < Date.now()
    },
    parseDateTime(value) {
      if (!value) return null
      const parts = String(value).split(' ')
      const dateParts = (parts[0] || '').split('-').map(Number)
      const timeParts = (parts[1] || '00:00:00').split(':').map(Number)
      if (dateParts.length < 3 || dateParts.some(item => Number.isNaN(item))) return null
      return new Date(dateParts[0], dateParts[1] - 1, dateParts[2], timeParts[0] || 0, timeParts[1] || 0, timeParts[2] || 0)
    },
    today() {
      const date = new Date()
      const year = date.getFullYear()
      const month = `${date.getMonth() + 1}`.padStart(2, '0')
      const day = `${date.getDate()}`.padStart(2, '0')
      return `${year}-${month}-${day}`
    },
    cancelBooking(row) {
      this.$confirm('确定取消该预约吗？', '提示', { type: 'warning' }).then(() => {
        request.put(`/api/booking/cancel/${row.id}`).then(() => {
          this.$message.success('预约已取消')
          this.loadMyBookings()
        })
      }).catch(() => {})
    },
    openReschedule(row) {
      this.rescheduleRow = row
      this.rescheduleForm = {
        classTime: '',
        remark: row.remark || ''
      }
      this.rescheduleSlots = []
      this.rescheduleVisible = true
      this.loadRescheduleSlots(row)
    },
    loadRescheduleSlots(row) {
      this.rescheduleLoading = true
      const user = JSON.parse(sessionStorage.getItem('loginUser') || '{}')
      request.get('/api/teacherSlot', {
        params: {
          pageNum: 1,
          pageSize: 500,
          teacherId: row.teacherId,
          status: 1,
          studentId: user.id,
          dateFrom: this.today()
        }
      }).then(res => {
        const currentTime = row.classTime
        this.rescheduleSlots = (res.data.records || []).filter(item => item.bookable && item.slotTime !== currentTime)
      }).finally(() => {
        this.rescheduleLoading = false
      })
    },
    selectRescheduleSlot(row) {
      this.rescheduleForm.classTime = row.slotTime
    },
    submitReschedule() {
      if (!this.rescheduleRow) return
      if (!this.rescheduleForm.classTime) {
        this.$message.warning('请选择新的上课时间')
        return
      }
      this.rescheduleSubmitting = true
      request.put(`/api/booking/reschedule/${this.rescheduleRow.id}`, {
        teacherId: this.rescheduleRow.teacherId,
        classTime: this.rescheduleForm.classTime,
        remark: this.rescheduleForm.remark
      }).then(() => {
        this.$message.success('改约申请已提交')
        this.rescheduleVisible = false
        this.loadMyBookings()
      }).finally(() => {
        this.rescheduleSubmitting = false
      })
    }
  }
}
</script>

<style scoped>
.booking-page {
  max-width: 1200px;
  margin: 0 auto;
}

.booking-toolbar {
  display: flex;
  flex-wrap: wrap;
  align-items: flex-end;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 24px;
  padding: 20px 22px;
  background: #fff;
  border-radius: 16px;
  border: 1px solid #eef0f4;
  box-shadow: 0 4px 20px rgba(15, 23, 42, 0.05);
}

.toolbar-title h2 {
  margin: 0 0 6px;
  font-size: 20px;
  font-weight: 700;
  color: #1a1d26;
}

.toolbar-title p {
  margin: 0;
  font-size: 13px;
  color: #909399;
}

.toolbar-form >>> .el-input__inner {
  border-radius: 10px;
}

.course-grid {
  margin-bottom: 8px;
}

.course-card {
  background: #fff;
  border-radius: 16px;
  overflow: hidden;
  border: 1px solid #eceef3;
  box-shadow: 0 4px 18px rgba(15, 23, 42, 0.06);
  margin-bottom: 20px;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.course-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 28px rgba(108, 92, 231, 0.15);
}

.course-card-cover {
  position: relative;
  height: 140px;
  background: linear-gradient(145deg, #eef0fb, #e4e7f5);
}

.course-card-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.course-card-cover--empty {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 48px;
  color: #c8cce0;
}

.course-card-price {
  position: absolute;
  right: 10px;
  bottom: 10px;
  padding: 4px 10px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 600;
  color: #fff;
  background: rgba(26, 29, 38, 0.72);
  backdrop-filter: blur(4px);
}

.course-card-body {
  padding: 14px 14px 16px;
}

.course-card-title {
  margin: 0 0 10px;
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  line-height: 1.35;
  min-height: 40px;
}

.course-card-teacher {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #6c5ce7;
  font-weight: 600;
  margin-bottom: 8px;
}

.course-card-teacher i {
  font-size: 15px;
}

.course-card-teacher-name {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.course-card-meta {
  font-size: 12px;
  color: #909399;
  margin-bottom: 12px;
}

.course-card-meta i {
  margin-right: 4px;
}

.course-card-actions {
  display: flex;
  gap: 8px;
}

.course-card-actions >>> .el-button {
  flex: 1;
  min-width: 0;
  border-radius: 10px;
  margin: 0;
}

.btn-consult {
  border-color: #dcdfe6;
  color: #606266;
}

.btn-consult:hover {
  border-color: #6c5ce7;
  color: #6c5ce7;
  background: rgba(108, 92, 231, 0.06);
}

.empty-block {
  margin-bottom: 20px;
  background: #fff;
  border-radius: 16px;
}

.my-bookings-card {
  margin-top: 8px;
  border-radius: 16px;
  border: 1px solid #eef0f4;
}

.my-bookings-header {
  font-weight: 600;
  font-size: 15px;
}
</style>
