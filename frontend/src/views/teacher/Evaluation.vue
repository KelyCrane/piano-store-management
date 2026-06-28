<template>
  <div>
    <el-card>
      <div slot="header">学生对我的评价</div>
      <el-table :data="list" border stripe v-loading="loading">
        <el-table-column prop="studentName" label="学生" />
        <el-table-column prop="courseName" label="课程" />
        <el-table-column prop="classTime" label="上课时间" width="160" />
        <el-table-column prop="teacherScore" label="评分" width="80">
          <template slot-scope="scope">
            <el-rate :value="scope.row.teacherScore" disabled />
          </template>
        </el-table-column>
        <el-table-column prop="evaluation" label="评价内容" show-overflow-tooltip />
        <el-table-column label="操作" width="90" align="center">
          <template slot-scope="scope">
            <el-button size="mini" type="danger" plain @click="deleteEvaluation(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination style="margin-top:15px;text-align:right" background layout="total, prev, pager, next"
        :total="total" :page-size="query.pageSize" :current-page.sync="query.pageNum" @current-change="loadData" />
    </el-card>
  </div>
</template>
<script>
import request from '@/utils/request'
export default {
  data() {
    return { list: [], total: 0, loading: false, query: { pageNum: 1, pageSize: 10 } }
  },
  created() { this.loadData() },
  methods: {
    loadData() {
      this.loading = true
      const user = JSON.parse(sessionStorage.getItem('loginUser') || '{}')
      request.get('/api/classRecord', { params: { ...this.query, teacherId: user.id } }).then(res => {
        this.list = (res.data.records || []).filter(r => r.evaluation)
        this.total = this.list.length
      }).finally(() => { this.loading = false })
    },
    deleteEvaluation(row) {
      this.$confirm('确定删除该学生评价吗？', '提示', { type: 'warning' }).then(() => {
        request.delete(`/api/classRecord/evaluation/${row.id}`).then(() => {
          this.$message.success('删除成功')
          this.loadData()
        })
      }).catch(() => {})
    }
  }
}
</script>
