<template>
  <div>
    <el-card>
      <div slot="header" style="display:flex;justify-content:space-between;align-items:center">
        <span>上课记录</span>
        <el-button type="primary" size="small" @click="handleAdd">新增记录</el-button>
      </div>
      <el-table :data="list" border stripe v-loading="loading">
        <el-table-column prop="studentName" label="学生" />
        <el-table-column prop="courseName" label="课程" />
        <el-table-column prop="classroomName" label="教室" />
        <el-table-column prop="classTime" label="上课时间" width="160" />
        <el-table-column prop="duration" label="时长(分钟)" width="90" />
        <el-table-column prop="learningStatus" label="学习情况" show-overflow-tooltip />
        <el-table-column prop="courseScore" label="评分" width="60" />
        <el-table-column label="操作" width="110" align="center">
          <template slot-scope="scope">
            <div class="table-action-btns">
              <el-button size="mini" @click="handleEdit(scope.row)">评价</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination style="margin-top:15px;text-align:right" background layout="total, prev, pager, next"
        :total="total" :page-size="query.pageSize" :current-page.sync="query.pageNum" @current-change="loadData" />
    </el-card>
    <el-dialog :title="form.id?'编辑记录':'新增记录'" :visible.sync="dialogVisible" width="600px">
      <el-form :model="form" label-width="100px">
        <el-form-item v-if="!form.id" label="关联预约">
          <el-select v-model="form.bookingId" filterable placeholder="可选：从已通过预约中选择" style="width:100%" @change="onBookingChange">
            <el-option
              v-for="b in bookingOptions"
              :key="b.id"
              :label="`${b.studentName || '-'} / ${b.courseName || '-'} / ${b.classTime || ''}`"
              :value="b.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item v-if="!form.id" label="学生">
          <el-select v-model="form.studentId" filterable placeholder="选择学生">
            <el-option v-for="s in students" :key="s.id" :label="s.name" :value="s.id" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="!form.id" label="课程">
          <el-select v-model="form.courseId" placeholder="选择课程">
            <el-option v-for="c in courses" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="!form.id" label="教室">
          <el-select v-model="form.classroomId" placeholder="选择教室">
            <el-option v-for="r in classrooms" :key="r.id" :label="r.name" :value="r.id" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="!form.id" label="上课时间">
          <el-date-picker v-model="form.classTime" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" />
        </el-form-item>
        <el-form-item v-if="!form.id" label="时长(分钟)"><el-input-number v-model="form.duration" :min="15" :step="15" /></el-form-item>
        <el-form-item label="学习情况"><el-input v-model="form.learningStatus" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="课程评分"><el-rate v-model="form.courseScore" :max="5" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="form.remark" type="textarea" :rows="2" /></el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="dialogVisible=false">取消</el-button>
        <el-button type="primary" @click="saveForm">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>
<script>
import request from '@/utils/request'
export default {
  data() {
    return {
      list: [], total: 0, loading: false, dialogVisible: false,
      query: { pageNum: 1, pageSize: 10 },
      form: {}, students: [], courses: [], classrooms: [], bookingOptions: []
    }
  },
  created() { this.loadData(); this.loadOptions() },
  methods: {
    loadData() {
      this.loading = true
      const user = JSON.parse(sessionStorage.getItem('loginUser') || '{}')
      request.get('/api/classRecord', { params: { ...this.query, teacherId: user.id } }).then(res => {
        this.list = res.data.records; this.total = res.data.total
      }).finally(() => { this.loading = false })
    },
    loadOptions() {
      request.get('/api/user', { params: { pageNum: 1, pageSize: 999, role: 'STUDENT' } }).then(res => { this.students = res.data.records || [] })
      const user = JSON.parse(sessionStorage.getItem('loginUser') || '{}')
      request.get('/api/course', { params: { pageNum: 1, pageSize: 999, teacherId: user.id } }).then(res => { this.courses = res.data.records || [] })
      request.get('/api/classroom', { params: { pageNum: 1, pageSize: 999 } }).then(res => { this.classrooms = res.data.records || [] })
      request.get('/api/booking', { params: { pageNum: 1, pageSize: 999, teacherId: user.id, status: 1 } }).then(res => {
        this.bookingOptions = res.data.records || []
      })
    },
    handleAdd() {
      const user = JSON.parse(sessionStorage.getItem('loginUser') || '{}')
      this.form = { teacherId: user.id, duration: 60, courseScore: 0, bookingId: '' }; this.dialogVisible = true
    },
    handleEdit(row) { this.form = { ...row }; this.dialogVisible = true },
    onBookingChange(bookingId) {
      const b = this.bookingOptions.find(v => v.id === bookingId)
      if (!b) return
      this.form.studentId = b.studentId
      this.form.teacherId = b.teacherId
      this.form.courseId = b.courseId
      this.form.classroomId = b.classroomId
      this.form.classTime = b.classTime
      this.form.duration = b.duration || this.form.duration
    },
    saveForm() {
      const api = this.form.id ? request.put('/api/classRecord', this.form) : request.post('/api/classRecord', this.form)
      api.then(() => { this.$message.success('操作成功'); this.dialogVisible = false; this.loadData() })
    }
  }
}
</script>
