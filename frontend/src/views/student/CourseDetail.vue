<template>
  <div class="course-detail-page" v-loading="loading">
    <el-button type="text" icon="el-icon-arrow-left" class="back-link" @click="$router.push('/student/booking')">
      返回课程列表
    </el-button>

    <div v-if="course.id" class="detail-layout">
      <div class="detail-cover">
        <img v-if="course.coverImage" :src="course.coverImage" alt="" class="cover-img">
        <div v-else class="cover-placeholder">
          <i class="el-icon-headset" />
        </div>
      </div>

      <div class="detail-main">
        <p class="detail-category">{{ course.categoryName || '音乐课程' }}</p>
        <h1 class="detail-title">{{ course.name }}</h1>

        <div class="teacher-highlight">
          <el-avatar :size="48" icon="el-icon-user-solid" style="background:#6C5CE7" />
          <div class="teacher-meta">
            <span class="teacher-label">授课教师</span>
            <span class="teacher-name">{{ course.teacherName || '待定' }}</span>
          </div>
        </div>

        <ul class="detail-stats">
          <li><i class="el-icon-time" /> 单次时长 <b>{{ course.duration || 60 }}</b> 分钟</li>
          <li><i class="el-icon-coin" /> 课时单价 <b>¥{{ formatMoney(course.price) }}</b></li>
          <li v-if="course.level"><i class="el-icon-reading" /> 难度 {{ course.level }}</li>
          <li v-if="course.maxStudents"><i class="el-icon-user" /> 人数上限 {{ course.maxStudents }} 人</li>
        </ul>

        <div v-if="course.description" class="detail-desc">
          <h3>课程介绍</h3>
          <p>{{ course.description }}</p>
        </div>
        <div v-else class="detail-desc empty-desc">
          <p>暂无详细图文介绍，购课后老师会进一步与您沟通学习目标、教材和排课安排。</p>
        </div>

        <div class="detail-actions">
          <el-button type="primary" size="medium" icon="el-icon-chat-dot-round" @click="consultVisible = true">咨询教师</el-button>
          <el-button size="medium" plain icon="el-icon-shopping-cart-2" @click="orderVisible = true">购买课时包</el-button>
          <el-button type="primary" size="medium" plain icon="el-icon-date" @click="bookingVisible = true">立即预约</el-button>
        </div>
      </div>
    </div>

    <course-booking-dialog
      :visible.sync="bookingVisible"
      :course="course"
      @success="handleBookingSuccess"
    />

    <course-package-order-dialog
      :visible.sync="orderVisible"
      :course="course"
      @success="handleOrderSuccess"
    />

    <consult-teacher-dialog :visible.sync="consultVisible" :course="course" />
  </div>
</template>

<script>
import request from '@/utils/request'
import ConsultTeacherDialog from '@/components/ConsultTeacherDialog.vue'
import CourseBookingDialog from '@/components/CourseBookingDialog.vue'
import CoursePackageOrderDialog from '@/components/CoursePackageOrderDialog.vue'

export default {
  name: 'CourseDetail',
  components: {
    ConsultTeacherDialog,
    CourseBookingDialog,
    CoursePackageOrderDialog
  },
  data() {
    return {
      course: {},
      loading: false,
      consultVisible: false,
      bookingVisible: false,
      orderVisible: false
    }
  },
  watch: {
    '$route.params.id': {
      immediate: true,
      handler() {
        this.loadCourse()
      }
    }
  },
  methods: {
    loadCourse() {
      const id = this.$route.params.id
      if (!id) {
        return
      }
      this.loading = true
      request.get(`/api/course/${id}`).then(res => {
        this.course = res.data || {}
      }).finally(() => {
        this.loading = false
      })
    },
    formatMoney(value) {
      return Number(value || 0).toFixed(2)
    },
    handleBookingSuccess() {
      this.$router.push('/student/booking')
    },
    handleOrderSuccess() {
      this.$router.push('/student/order')
    }
  }
}
</script>

<style scoped>
.course-detail-page {
  max-width: 960px;
  margin: 0 auto;
}

.back-link {
  margin-bottom: 16px;
  color: #6c5ce7;
  font-size: 14px;
}

.detail-layout {
  display: grid;
  grid-template-columns: minmax(0, 1fr) minmax(0, 1.15fr);
  gap: 28px;
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 8px 32px rgba(15, 23, 42, 0.08);
  border: 1px solid #eef0f4;
}

@media (max-width: 768px) {
  .detail-layout {
    grid-template-columns: 1fr;
  }
}

.detail-cover {
  border-radius: 14px;
  overflow: hidden;
  min-height: 220px;
  background: #f0f2f8;
}

.cover-img {
  width: 100%;
  height: 100%;
  min-height: 240px;
  object-fit: cover;
  display: block;
}

.cover-placeholder {
  min-height: 240px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 64px;
  color: #c8cce0;
  background: linear-gradient(145deg, #eef0fb, #e4e7f5);
}

.detail-category {
  margin: 0 0 8px;
  font-size: 13px;
  color: #6c5ce7;
  font-weight: 600;
  letter-spacing: 0.04em;
}

.detail-title {
  margin: 0 0 20px;
  font-size: 26px;
  font-weight: 700;
  color: #1a1d26;
  line-height: 1.25;
}

.teacher-highlight {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 14px 16px;
  background: linear-gradient(135deg, rgba(108, 92, 231, 0.1), rgba(108, 92, 231, 0.04));
  border-radius: 14px;
  border: 1px solid rgba(108, 92, 231, 0.15);
  margin-bottom: 20px;
}

.teacher-label {
  display: block;
  font-size: 12px;
  color: #909399;
  margin-bottom: 2px;
}

.teacher-name {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.detail-stats {
  list-style: none;
  padding: 0;
  margin: 0 0 22px;
  display: flex;
  flex-wrap: wrap;
  gap: 12px 20px;
  color: #606266;
  font-size: 14px;
}

.detail-stats li {
  display: flex;
  align-items: center;
  gap: 6px;
}

.detail-stats i {
  color: #6c5ce7;
}

.detail-desc h3 {
  margin: 0 0 10px;
  font-size: 15px;
  color: #303133;
}

.detail-desc p {
  margin: 0;
  font-size: 14px;
  line-height: 1.7;
  color: #606266;
}

.empty-desc p {
  color: #909399;
}

.detail-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 28px;
}

.detail-actions >>> .el-button {
  border-radius: 10px;
  min-width: 120px;
}
</style>
