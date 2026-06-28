<template>
  <div>
    <el-card>
      <div slot="header" style="display:flex;justify-content:space-between;align-items:center">
        <span>用户管理</span>
        <el-button type="primary" size="small" @click="handleAdd">新增用户</el-button>
      </div>
      <el-form :inline="true" :model="query" size="small" style="margin-bottom:10px">
        <el-form-item label="姓名"><el-input v-model="query.name" placeholder="搜索姓名" clearable /></el-form-item>
        <el-form-item label="角色">
          <el-select v-model="query.role" placeholder="全部" clearable>
            <el-option label="管理员" value="ADMIN" /><el-option label="教师" value="TEACHER" /><el-option label="学生" value="STUDENT" />
          </el-select>
        </el-form-item>
        <el-form-item><el-button type="primary" @click="loadData">搜索</el-button></el-form-item>
      </el-form>
      <el-table :data="list" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="name" label="姓名" />
        <el-table-column prop="role" label="角色">
          <template slot-scope="scope">
            <el-tag :type="scope.row.role==='ADMIN'?'danger':scope.row.role==='TEACHER'?'warning':''" size="small">
              {{ scope.row.role==='ADMIN'?'管理员':scope.row.role==='TEACHER'?'教师':'学生' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="teacherStudentLimit" label="学生上限" width="90">
          <template slot-scope="scope">
            <span v-if="scope.row.role === 'TEACHER'">{{ scope.row.teacherStudentLimit > 0 ? scope.row.teacherStudentLimit : '不限' }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="age" label="年龄" width="70" />
        <el-table-column prop="phone" label="联系方式" />
        <el-table-column prop="gender" label="性别" width="60" />
        <el-table-column prop="status" label="状态" width="80">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status===1?'success':'info'" size="small">{{ scope.row.status===1?'启用':'禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="300" align="center">
          <template slot-scope="scope">
            <div class="table-action-btns">
              <el-button size="mini" @click="handleEdit(scope.row)">编辑</el-button>
              <el-button size="mini" type="warning" @click="resetPwd(scope.row)">重置密码</el-button>
              <el-button size="mini" type="danger" @click="handleDel(scope.row)">删除</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination style="margin-top:15px;text-align:right" background layout="total, prev, pager, next"
        :total="total" :page-size="query.pageSize" :current-page.sync="query.pageNum" @current-change="loadData" />
    </el-card>

    <el-dialog :title="form.id?'编辑用户':'新增用户'" :visible.sync="dialogVisible" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="用户名"><el-input v-model="form.username" :disabled="!!form.id" /></el-form-item>
        <el-form-item label="姓名"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="角色">
          <el-select v-model="form.role">
            <el-option label="管理员" value="ADMIN" /><el-option label="教师" value="TEACHER" /><el-option label="学生" value="STUDENT" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="form.role === 'TEACHER'" label="学生上限">
          <el-input-number v-model="form.teacherStudentLimit" :min="0" :max="999" />
          <span style="margin-left:8px;color:#909399;font-size:12px">0 表示不限</span>
        </el-form-item>
        <el-form-item label="联系方式"><el-input v-model="form.phone" /></el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="form.gender"><el-radio label="男">男</el-radio><el-radio label="女">女</el-radio></el-radio-group>
        </el-form-item>
        <el-form-item label="年龄"><el-input-number v-model="form.age" :min="1" :max="120" /></el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
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
      query: { pageNum: 1, pageSize: 10, name: '', role: '' },
      form: {}
    }
  },
  created() { this.loadData() },
  methods: {
    loadData() {
      this.loading = true
      request.get('/api/user', { params: this.query }).then(res => {
        this.list = res.data.records; this.total = res.data.total
      }).finally(() => { this.loading = false })
    },
    handleAdd() { this.form = { status: 1, teacherStudentLimit: 0 }; this.dialogVisible = true },
    handleEdit(row) { this.form = { ...row }; this.dialogVisible = true },
    saveForm() {
      if (this.form.role !== 'TEACHER') {
        this.form.teacherStudentLimit = 0
      }
      const api = this.form.id ? request.put('/api/user', this.form) : request.post('/api/user', this.form)
      api.then(() => { this.$message.success('操作成功'); this.dialogVisible = false; this.loadData() })
    },
    handleDel(row) {
      this.$confirm('确定删除该用户?', '提示', { type: 'warning' }).then(() => {
        request.delete('/api/user/' + row.id).then(() => { this.$message.success('删除成功'); this.loadData() })
      }).catch(() => {})
    },
    resetPwd(row) {
      this.$confirm('确定重置该用户密码为123456?', '提示', { type: 'warning' }).then(() => {
        request.put('/api/user/resetPwd/' + row.id).then(() => { this.$message.success('密码已重置为123456') })
      }).catch(() => {})
    }
  }
}
</script>
