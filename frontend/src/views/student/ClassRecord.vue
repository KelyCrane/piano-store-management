<template>
  <div>
    <el-card>
      <div slot="header">我的上课记录</div>
      <el-table :data="list" border stripe v-loading="loading">
        <el-table-column prop="courseName" label="课程" />
        <el-table-column prop="teacherName" label="教师" />
        <el-table-column prop="classroomName" label="教室" />
        <el-table-column prop="classTime" label="上课时间" width="160" />
        <el-table-column prop="duration" label="时长(分钟)" width="90" />
        <el-table-column prop="evaluation" label="教师评价" show-overflow-tooltip />
        <el-table-column prop="score" label="评分" width="60" />
        <el-table-column prop="homework" label="作业" show-overflow-tooltip />
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
      request.get('/api/classRecord', { params: { ...this.query, studentId: user.id } }).then(res => {
        this.list = res.data.records; this.total = res.data.total
      }).finally(() => { this.loading = false })
    }
  }
}
</script>
