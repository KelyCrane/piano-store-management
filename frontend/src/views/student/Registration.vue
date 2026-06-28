<template>
  <div>
    <el-card>
      <div slot="header">我的报名记录</div>
      <el-table :data="list" border stripe v-loading="loading">
        <el-table-column prop="activityTitle" label="活动" />
        <el-table-column prop="createTime" label="报名时间" width="160" />
        <el-table-column prop="feePaid" label="缴费" width="80">
          <template slot-scope="scope">
            <el-tag :type="scope.row.feePaid === 1 ? 'success' : 'warning'" size="small">
              {{ scope.row.feePaid === 1 ? '已缴费' : '待缴费' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template slot-scope="scope">
            <el-tag :type="statusTag(scope.row.status)" size="small">
              {{ statusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="passed" label="成绩结果" width="90">
          <template slot-scope="scope">
            <span v-if="scope.row.passed===1">通过</span>
            <span v-else-if="scope.row.passed===0">未通过</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="rank" label="名次" width="80" />
        <el-table-column prop="awardLevel" label="获奖等级" show-overflow-tooltip />
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
        <el-table-column label="操作" width="110" align="center">
          <template slot-scope="scope">
            <el-button v-if="canCancel(scope.row)" size="mini" type="danger" plain @click="cancelRegistration(scope.row)">取消报名</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>
<script>
import request from '@/utils/request'
export default {
  data() { return { list: [], loading: false } },
  created() { this.loadData() },
  methods: {
    loadData() {
      this.loading = true
      const user = JSON.parse(sessionStorage.getItem('loginUser') || '{}')
      request.get('/api/registration', { params: { pageNum: 1, pageSize: 999, studentId: user.id } }).then(res => {
        this.list = res.data.records || []
      }).finally(() => { this.loading = false })
    },
    statusText(status) {
      if (status === 1) return '已通过'
      if (status === 2) return '已拒绝'
      if (status === 3) return '已取消'
      return '待审核'
    },
    statusTag(status) {
      if (status === 1) return 'success'
      if (status === 2) return 'danger'
      if (status === 3) return 'info'
      return 'warning'
    },
    canCancel(row) {
      return (row.status === 0 || row.status === 1) && !this.hasResult(row)
    },
    hasResult(row) {
      return row && (row.passed !== null && row.passed !== undefined ||
        row.rank !== null && row.rank !== undefined ||
        !!row.awardLevel)
    },
    cancelRegistration(row) {
      this.$confirm('确定取消该活动报名吗？如已缴费将同步处理退款。', '提示', { type: 'warning' }).then(() => {
        request.put(`/api/registration/cancel/${row.id}`).then(() => {
          this.$message.success('报名已取消')
          this.loadData()
        })
      }).catch(() => {})
    }
  }
}
</script>
