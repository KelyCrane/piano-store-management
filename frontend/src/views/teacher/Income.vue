<template>
  <div>
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card shadow="hover"><div class="stat-card"><div class="stat-num">{{ totalClasses }}</div><div class="stat-label">总授课次数</div></div></el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover"><div class="stat-card"><div class="stat-num">{{ totalHours }}</div><div class="stat-label">总授课时长(小时)</div></div></el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover"><div class="stat-card"><div class="stat-num">{{ totalStudents }}</div><div class="stat-label">学生数量</div></div></el-card>
      </el-col>
    </el-row>
    <el-card style="margin-top:20px">
      <div slot="header">授课记录明细</div>
      <el-table :data="records" border stripe>
        <el-table-column prop="studentName" label="学生" />
        <el-table-column prop="courseName" label="课程" />
        <el-table-column prop="classTime" label="上课时间" width="160" />
        <el-table-column prop="duration" label="时长(分钟)" width="100" />
      </el-table>
    </el-card>
  </div>
</template>
<script>
import request from '@/utils/request'
export default {
  data() { return { records: [], totalClasses: 0, totalHours: 0, totalStudents: 0 } },
  created() { this.loadData() },
  methods: {
    loadData() {
      const user = JSON.parse(sessionStorage.getItem('loginUser') || '{}')
      request.get('/api/classRecord', { params: { pageNum: 1, pageSize: 999, teacherId: user.id } }).then(res => {
        this.records = res.data.records || []
        this.totalClasses = this.records.length
        this.totalHours = (this.records.reduce((s, r) => s + (r.duration || 0), 0) / 60).toFixed(1)
        const students = new Set(this.records.map(r => r.studentId))
        this.totalStudents = students.size
      })
    }
  }
}
</script>
<style scoped>
.stat-card { text-align: center; padding: 20px 0 }
.stat-num { font-size: 36px; font-weight: bold; color: #409EFF }
.stat-label { font-size: 14px; color: #999; margin-top: 8px }
</style>
