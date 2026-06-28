<template>
  <div>
    <el-card>
      <div slot="header">报名通知</div>
      <el-table :data="list" border stripe v-loading="loading">
        <el-table-column prop="activityTitle" label="活动名称" />
        <el-table-column prop="studentName" label="报名学生" />
        <el-table-column prop="createTime" label="报名时间" width="160" />
        <el-table-column prop="status" label="状态" width="80">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status===1?'success':scope.row.status===2?'danger':'warning'" size="small">
              {{ scope.row.status===0?'待审核':scope.row.status===1?'已通过':'已拒绝' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="成绩" show-overflow-tooltip>
          <template slot-scope="scope">
            <span v-if="scope.row.passed===1">通过</span>
            <span v-else-if="scope.row.passed===0">未通过</span>
            <span v-else>-</span>
            <span v-if="scope.row.rank"> / 名次 {{ scope.row.rank }}</span>
            <span v-if="scope.row.awardLevel"> / {{ scope.row.awardLevel }}</span>
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
      request.get('/api/registration', { params: this.query }).then(res => {
        this.list = res.data.records; this.total = res.data.total
      }).finally(() => { this.loading = false })
    }
  }
}
</script>
