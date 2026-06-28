<template>
  <div>
    <el-card>
      <div slot="header" style="display:flex;justify-content:space-between;align-items:center">
        <span>证书管理</span>
        <el-button type="primary" size="small" @click="handleAdd">颁发证书</el-button>
      </div>
      <el-table :data="list" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="studentName" label="学生" />
        <el-table-column prop="name" label="证书名称" />
        <el-table-column prop="type" label="类型" width="80" />
        <el-table-column prop="obtainTime" label="获得日期" width="120" />
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
        <el-table-column label="操作" width="168" align="center">
          <template slot-scope="scope">
            <div class="table-action-btns">
              <el-button size="mini" @click="handleEdit(scope.row)">编辑</el-button>
              <el-button size="mini" type="danger" @click="handleDel(scope.row)">删除</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination style="margin-top:15px;text-align:right" background layout="total, prev, pager, next"
        :total="total" :page-size="query.pageSize" :current-page.sync="query.pageNum" @current-change="loadData" />
    </el-card>
    <el-dialog :title="form.id?'编辑证书':'颁发证书'" :visible.sync="dialogVisible" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="学生">
          <el-select v-model="form.studentId" filterable placeholder="选择学生">
            <el-option v-for="s in students" :key="s.id" :label="s.name" :value="s.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="证书名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="类型">
          <el-select v-model="form.type">
            <el-option label="考级" value="考级" /><el-option label="演出" value="演出" /><el-option label="比赛" value="比赛" />
          </el-select>
        </el-form-item>
        <el-form-item label="获得日期"><el-date-picker v-model="form.obtainTime" type="date" value-format="yyyy-MM-dd" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="form.remark" type="textarea" :rows="2" /></el-form-item>
        <el-form-item label="证书图片">
          <el-upload action="/api/upload" :show-file-list="false" :on-success="(r)=>{form.imageUrl=r.data}">
            <img v-if="form.imageUrl" :src="form.imageUrl" style="width:120px;height:80px;object-fit:cover">
            <el-button v-else size="small">上传图片</el-button>
          </el-upload>
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
      query: { pageNum: 1, pageSize: 10 },
      form: {}, students: []
    }
  },
  created() { this.loadData(); this.loadStudents() },
  methods: {
    loadData() {
      this.loading = true
      request.get('/api/certificate', { params: this.query }).then(res => {
        this.list = res.data.records; this.total = res.data.total
      }).finally(() => { this.loading = false })
    },
    loadStudents() {
      request.get('/api/user', { params: { pageNum: 1, pageSize: 999, role: 'STUDENT' } }).then(res => { this.students = res.data.records || [] })
    },
    handleAdd() { this.form = {}; this.dialogVisible = true },
    handleEdit(row) { this.form = { ...row }; this.dialogVisible = true },
    saveForm() {
      const api = this.form.id ? request.put('/api/certificate', this.form) : request.post('/api/certificate', this.form)
      api.then(() => { this.$message.success('操作成功'); this.dialogVisible = false; this.loadData() })
    },
    handleDel(row) {
      this.$confirm('确定删除?', '提示', { type: 'warning' }).then(() => {
        request.delete('/api/certificate/' + row.id).then(() => { this.$message.success('删除成功'); this.loadData() })
      }).catch(() => {})
    }
  }
}
</script>
