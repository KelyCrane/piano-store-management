<template>
  <div class="teacher-list-page">
    <el-card shadow="never" class="teacher-card">
      <div slot="header" class="card-head">
        <span>教师信息</span>
        <el-input
          v-model="keyword"
          clearable
          size="small"
          prefix-icon="el-icon-search"
          placeholder="搜索教师或教授乐器"
          class="search-input"
        />
      </div>

      <el-row :gutter="18" v-loading="loading">
        <el-col v-for="teacher in filteredTeachers" :key="teacher.id" :xs="24" :sm="12" :lg="8">
          <el-card shadow="hover" class="teacher-profile">
            <div class="teacher-header">
              <img v-if="teacher.avatar" :src="teacher.avatar" class="teacher-avatar">
              <el-avatar v-else :size="72" class="teacher-avatar-fallback">{{ firstChar(teacher.name) }}</el-avatar>
              <div class="teacher-basic">
                <h3>{{ teacher.name || '-' }}</h3>
                <p>{{ teacher.phone || '暂无联系方式' }}</p>
                <p>{{ teacher.gender || '-' }}<span v-if="teacher.age"> / {{ teacher.age }}岁</span></p>
              </div>
            </div>

            <div class="info-block">
              <div class="block-title">教授乐器</div>
              <div v-if="teacher.instrumentTags.length" class="tag-list">
                <el-tag v-for="item in teacher.instrumentTags" :key="item" size="small" effect="plain">{{ item }}</el-tag>
              </div>
              <span v-else class="empty-text">暂无课程</span>
            </div>

            <div class="info-block">
              <div class="block-title">教师资质</div>
              <div v-if="teacher.qualifications.length" class="qualification-list">
                <div v-for="q in teacher.qualifications" :key="q.id" class="qualification-item">
                  <el-tag size="mini" :type="qualificationTag(q.type)">{{ qualificationTypeText(q.type) }}</el-tag>
                  <span>{{ q.name }}</span>
                </div>
              </div>
              <span v-else class="empty-text">暂无资质信息</span>
            </div>

            <div class="info-block">
              <div class="block-title">学生评价</div>
              <div v-if="teacher.evaluations.length" class="evaluation-list">
                <div v-for="e in teacher.evaluations" :key="e.id" class="evaluation-item">
                  <div class="evaluation-top">
                    <span>{{ e.studentName || '学生' }} / {{ e.courseName || '-' }}</span>
                    <el-rate :value="e.teacherScore || 0" disabled size="mini" />
                  </div>
                  <p>{{ e.evaluation }}</p>
                </div>
                <el-button
                  v-if="teacher.allEvaluations.length > teacher.evaluations.length"
                  type="text"
                  size="small"
                  @click="showAllEvaluations(teacher)"
                >
                  查看全部评论
                </el-button>
              </div>
              <span v-else class="empty-text">暂无评价</span>
            </div>

            <div class="info-block">
              <div class="block-title">我要评论</div>
              <div v-if="teacher.myRecords.length" class="comment-form">
                <el-select
                  v-model="evaluationForms[teacher.id].recordId"
                  size="small"
                  placeholder="选择上课记录"
                  class="comment-course"
                  @change="onEvaluationRecordChange(teacher)"
                >
                  <el-option
                    v-for="record in teacher.myRecords"
                    :key="record.id"
                    :label="recordLabel(record)"
                    :value="record.id"
                  />
                </el-select>
                <el-rate v-model="evaluationForms[teacher.id].teacherScore" class="comment-rate" />
                <el-input
                  v-model="evaluationForms[teacher.id].evaluation"
                  type="textarea"
                  :rows="2"
                  maxlength="500"
                  show-word-limit
                  placeholder="写下对老师的评价"
                />
                <el-button
                  type="primary"
                  size="small"
                  :loading="submittingEvaluationIds.includes(teacher.id)"
                  @click="submitEvaluation(teacher)"
                >
                  提交评论
                </el-button>
              </div>
              <span v-else class="empty-text">完成上课记录后可评论</span>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-empty v-if="!loading && !filteredTeachers.length" description="暂无教师信息" />
    </el-card>

    <el-dialog
      :title="`${evaluationDialogTeacher.name || '教师'}的全部评论`"
      :visible.sync="evaluationDialogVisible"
      width="620px"
    >
      <div class="evaluation-list all-evaluation-list">
        <div v-for="e in evaluationDialogTeacher.allEvaluations" :key="e.id" class="evaluation-item">
          <div class="evaluation-top">
            <span>{{ e.studentName || '学生' }} / {{ e.courseName || '-' }}</span>
            <el-rate :value="e.teacherScore || 0" disabled size="mini" />
          </div>
          <p>{{ e.evaluation }}</p>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  name: 'TeacherList',
  data() {
    return {
      teachers: [],
      keyword: '',
      loading: false,
      evaluationForms: {},
      submittingEvaluationIds: [],
      evaluationDialogVisible: false,
      evaluationDialogTeacher: {}
    }
  },
  computed: {
    filteredTeachers() {
      const key = (this.keyword || '').trim().toLowerCase()
      if (!key) {
        return this.teachers
      }
      return this.teachers.filter(item => {
        const text = [
          item.name,
          item.phone,
          item.instrumentTags.join(','),
          item.qualifications.map(q => q.name).join(',')
        ].join(' ').toLowerCase()
        return text.includes(key)
      })
    }
  },
  created() {
    this.loadData()
  },
  methods: {
    loadData() {
      this.loading = true
      request.get('/api/user', { params: { pageNum: 1, pageSize: 999, role: 'TEACHER' } }).then(res => {
        const teachers = res.data.records || []
        return Promise.all(teachers.map(this.loadTeacherDetail)).then(detailList => {
          this.teachers = detailList
          detailList.forEach(this.initEvaluationForm)
        })
      }).finally(() => {
        this.loading = false
      })
    },
    loadTeacherDetail(teacher) {
      return Promise.all([
        request.get('/api/course', { params: { pageNum: 1, pageSize: 999, teacherId: teacher.id, status: 1 } }).catch(() => ({ data: { records: [] } })),
        request.get('/api/qualification', { params: { pageNum: 1, pageSize: 999, teacherId: teacher.id } }).catch(() => ({ data: { records: [] } })),
        request.get('/api/classRecord', { params: { pageNum: 1, pageSize: 999, teacherId: teacher.id } }).catch(() => ({ data: { records: [] } }))
      ]).then(([courseRes, qualificationRes, recordRes]) => {
        const courses = courseRes.data.records || []
        const qualifications = qualificationRes.data.records || []
        const loginUser = JSON.parse(sessionStorage.getItem('loginUser') || '{}')
        const records = recordRes.data.records || []
        const evaluations = records.filter(item => item.evaluation)
        const myRecords = records.filter(item => Number(item.studentId) === Number(loginUser.id))
        const instrumentTags = Array.from(new Set(courses.map(item => item.categoryName || item.name).filter(Boolean)))
        return {
          ...teacher,
          courses,
          qualifications,
          evaluations: evaluations.slice(0, 3),
          allEvaluations: evaluations,
          myRecords,
          instrumentTags
        }
      })
    },
    initEvaluationForm(teacher) {
      const first = teacher.myRecords[0] || {}
      this.$set(this.evaluationForms, teacher.id, {
        recordId: first.id || '',
        teacherScore: first.teacherScore || 5,
        evaluation: first.evaluation || ''
      })
    },
    recordLabel(record) {
      const time = record.classTime ? ` / ${record.classTime}` : ''
      return `${record.courseName || '课程'}${time}`
    },
    onEvaluationRecordChange(teacher) {
      const form = this.evaluationForms[teacher.id]
      const record = teacher.myRecords.find(item => item.id === form.recordId) || {}
      form.teacherScore = record.teacherScore || 5
      form.evaluation = record.evaluation || ''
    },
    submitEvaluation(teacher) {
      const form = this.evaluationForms[teacher.id]
      if (!form || !form.recordId) {
        this.$message.warning('请选择上课记录')
        return
      }
      if (!form.evaluation || !form.evaluation.trim()) {
        this.$message.warning('请填写评价内容')
        return
      }
      if (!this.submittingEvaluationIds.includes(teacher.id)) {
        this.submittingEvaluationIds.push(teacher.id)
      }
      request.put('/api/classRecord/evaluate', {
        id: form.recordId,
        teacherScore: form.teacherScore || 5,
        evaluation: form.evaluation.trim()
      }).then(() => {
        this.$message.success('评论已提交')
        this.loadData()
      }).finally(() => {
        this.submittingEvaluationIds = this.submittingEvaluationIds.filter(id => id !== teacher.id)
      })
    },
    showAllEvaluations(teacher) {
      this.evaluationDialogTeacher = teacher
      this.evaluationDialogVisible = true
    },
    firstChar(name) {
      return (name || '师').charAt(0)
    },
    qualificationTypeText(type) {
      const map = { LEVEL: '等级', SCHOOL: '学校', HONOR: '荣誉' }
      return map[type] || type || '资质'
    },
    qualificationTag(type) {
      const map = { LEVEL: 'success', SCHOOL: '', HONOR: 'warning' }
      return map[type] || 'info'
    }
  }
}
</script>

<style scoped>
.teacher-list-page {
  max-width: 1200px;
  margin: 0 auto;
}

.teacher-card {
  border-radius: 10px;
}

.card-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  font-weight: 600;
}

.search-input {
  width: 260px;
}

.teacher-profile {
  margin-bottom: 18px;
  border-radius: 10px;
}

.teacher-header {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 16px;
}

.teacher-avatar,
.teacher-avatar-fallback {
  width: 72px;
  height: 72px;
  border-radius: 50%;
  object-fit: cover;
  flex-shrink: 0;
}

.teacher-avatar-fallback {
  background: #6c5ce7;
  color: #fff;
  font-size: 22px;
}

.teacher-basic h3 {
  margin: 0 0 6px;
  font-size: 18px;
  color: #303133;
}

.teacher-basic p {
  margin: 3px 0;
  color: #909399;
  font-size: 13px;
}

.info-block {
  padding-top: 12px;
  border-top: 1px solid #ebeef5;
  margin-top: 12px;
}

.block-title {
  font-size: 13px;
  font-weight: 600;
  color: #606266;
  margin-bottom: 8px;
}

.tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.qualification-list,
.evaluation-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.all-evaluation-list {
  max-height: 460px;
  overflow-y: auto;
}

.qualification-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #606266;
}

.evaluation-item {
  padding: 8px 10px;
  background: #f7f8fb;
  border-radius: 8px;
}

.evaluation-top {
  display: flex;
  justify-content: space-between;
  gap: 8px;
  font-size: 12px;
  color: #909399;
}

.evaluation-item p {
  margin: 6px 0 0;
  color: #606266;
  font-size: 13px;
  line-height: 1.5;
}

.comment-form {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.comment-course {
  width: 100%;
}

.comment-rate {
  line-height: 20px;
}

.empty-text {
  color: #c0c4cc;
  font-size: 13px;
}

@media (max-width: 768px) {
  .card-head {
    align-items: stretch;
    flex-direction: column;
  }

  .search-input {
    width: 100%;
  }
}
</style>
