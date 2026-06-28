<template>
  <div>
    <el-card shadow="never">
      <div slot="header" class="header-row">
        <span>课时包管理</span>
        <el-button type="primary" size="small" @click="handleAdd">新增课时包</el-button>
      </div>

      <el-form :inline="true" :model="query" size="small" style="margin-bottom:10px">
        <el-form-item label="学生">
          <el-select v-model="query.studentId" clearable filterable placeholder="全部">
            <el-option v-for="student in students" :key="student.id" :label="student.name" :value="student.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="课程">
          <el-select v-model="query.courseId" clearable filterable placeholder="全部">
            <el-option v-for="course in courses" :key="course.id" :label="course.name" :value="course.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" clearable placeholder="全部">
            <el-option label="有效" :value="1" />
            <el-option label="停用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">搜索</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="list" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="name" label="课时包" min-width="150" />
        <el-table-column prop="studentName" label="学生" width="110" />
        <el-table-column prop="courseName" label="课程" min-width="120" />
        <el-table-column prop="teacherName" label="教师" width="110" />
        <el-table-column prop="totalHours" label="总课时" width="90" />
        <el-table-column prop="remainingHours" label="剩余课时" width="100" />
        <el-table-column prop="availableHours" label="可排课时" width="100">
          <template slot-scope="scope">
            <el-tag :type="scope.row.availableHours > 0 ? 'warning' : 'info'" size="small">
              {{ scope.row.availableHours || 0 }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="reservedHours" label="已预留" width="100">
          <template slot-scope="scope">
            <el-tag :type="scope.row.reservedHours > 0 ? 'danger' : 'info'" size="small">
              {{ scope.row.reservedHours || 0 }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="总价(元)" width="100">
          <template slot-scope="scope">¥{{ formatMoney(scope.row.price) }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'info'" size="small">
              {{ scope.row.status === 1 ? '有效' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
        <el-table-column label="操作" width="170" align="center">
          <template slot-scope="scope">
            <div class="table-action-btns">
              <el-button size="mini" @click="handleEdit(scope.row)">编辑</el-button>
              <el-button size="mini" type="danger" @click="handleDel(scope.row)">删除</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        style="margin-top:15px;text-align:right"
        background
        layout="total, prev, pager, next"
        :total="total"
        :page-size="query.pageSize"
        :current-page.sync="query.pageNum"
        @current-change="loadData"
      />
    </el-card>

    <el-dialog :title="form.id ? '编辑课时包' : '新增课时包'" :visible.sync="dialogVisible" width="640px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="课时包名称">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="学生">
          <el-select v-model="form.studentId" filterable placeholder="选择学生" style="width:100%">
            <el-option v-for="student in students" :key="student.id" :label="student.name" :value="student.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="课程">
          <el-select v-model="form.courseId" filterable placeholder="选择课程" style="width:100%" @change="onCourseChange">
            <el-option v-for="course in courses" :key="course.id" :label="course.name" :value="course.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="教师">
          <el-select v-model="form.teacherId" filterable clearable placeholder="选择教师" style="width:100%">
            <el-option v-for="teacher in teachers" :key="teacher.id" :label="teacher.name" :value="teacher.id" />
          </el-select>
        </el-form-item>
        <el-row :gutter="12">
          <el-col :span="8">
            <el-form-item label="总课时">
              <el-input-number v-model="form.totalHours" :min="1" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="剩余课时">
              <el-input-number v-model="form.remainingHours" :min="0" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="总价">
              <el-input-number v-model="form.price" :min="0" :precision="2" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item v-if="form.id" label="当前占用">
          <div class="usage-text">
            可排 {{ form.availableHours || 0 }} 课时，已预留 {{ form.reservedHours || 0 }} 课时
          </div>
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveForm">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  name: 'AdminStudentPackageManage',
  data() {
    return {
      loading: false,
      dialogVisible: false,
      list: [],
      total: 0,
      students: [],
      courses: [],
      teachers: [],
      query: {
        pageNum: 1,
        pageSize: 10,
        studentId: '',
        courseId: '',
        status: ''
      },
      form: {}
    }
  },
  created() {
    this.loadOptions()
    this.loadData()
  },
  methods: {
    loadOptions() {
      request.get('/api/user', { params: { pageNum: 1, pageSize: 999, role: 'STUDENT' } }).then(res => {
        this.students = res.data.records || []
      })
      request.get('/api/user', { params: { pageNum: 1, pageSize: 999, role: 'TEACHER' } }).then(res => {
        this.teachers = res.data.records || []
      })
      request.get('/api/course', { params: { pageNum: 1, pageSize: 999 } }).then(res => {
        this.courses = res.data.records || []
      })
    },
    loadData() {
      this.loading = true
      request.get('/api/studentPackage', { params: this.query }).then(res => {
        this.list = res.data.records || []
        this.total = res.data.total || 0
      }).finally(() => {
        this.loading = false
      })
    },
    formatMoney(value) {
      return Number(value || 0).toFixed(2)
    },
    buildDefaultForm() {
      return {
        name: '',
        studentId: '',
        courseId: '',
        teacherId: '',
        totalHours: 10,
        remainingHours: 10,
        price: 0,
        status: 1,
        remark: ''
      }
    },
    handleAdd() {
      this.form = this.buildDefaultForm()
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.form = { ...row }
      this.dialogVisible = true
    },
    onCourseChange(courseId) {
      const course = this.courses.find(item => item.id === courseId)
      if (!course) {
        return
      }
      if (!this.form.teacherId) {
        this.form.teacherId = course.teacherId || ''
      }
      if (!this.form.name) {
        this.form.name = `${course.name} 课时包`
      }
      if ((!this.form.price || Number(this.form.price) === 0) && course.price) {
        this.form.price = Number(course.price) * Number(this.form.totalHours || 0)
      }
    },
    saveForm() {
      if (!this.form.studentId || !this.form.courseId || !this.form.name) {
        this.$message.warning('请先填写完整信息')
        return
      }
      if (Number(this.form.remainingHours || 0) > Number(this.form.totalHours || 0)) {
        this.$message.warning('剩余课时不能大于总课时')
        return
      }
      const payload = {
        ...this.form,
        teacherId: this.form.teacherId || null
      }
      const api = payload.id
        ? request.put('/api/studentPackage', payload)
        : request.post('/api/studentPackage', payload)
      api.then(() => {
        this.$message.success('操作成功')
        this.dialogVisible = false
        this.loadData()
      })
    },
    handleDel(row) {
      this.$confirm('确定删除该课时包吗？', '提示', { type: 'warning' }).then(() => {
        request.delete(`/api/studentPackage/${row.id}`).then(() => {
          this.$message.success('删除成功')
          this.loadData()
        })
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
.header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.usage-text {
  color: #606266;
}
</style>
