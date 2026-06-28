<template>
  <div>
    <el-card>
      <div slot="header">活动报名</div>
      <el-row :gutter="16">
        <el-col :span="8" v-for="a in activities" :key="a.id">
          <el-card shadow="hover" style="margin-bottom:16px">
            <img v-if="a.coverImage" :src="a.coverImage" style="width:100%;height:140px;object-fit:cover">
            <div style="padding:10px 0">
              <div style="font-weight:bold;font-size:16px">{{ a.name }}</div>
              <div style="color:#999;font-size:13px;margin-top:4px">类型：{{ a.type }}</div>
              <div style="color:#999;font-size:13px">时间：{{ a.startTime }}</div>
              <div style="color:#999;font-size:13px">地点：{{ a.location }}</div>
              <div style="color:#999;font-size:13px">费用：{{ a.fee > 0 ? '¥'+a.fee : '免费' }}</div>
              <el-tag :type="a.status===1?'success':a.status===2?'info':'warning'" size="small" style="margin-top:4px">
                {{ a.status===0?'未开始':a.status===1?'进行中':'已结束' }}
              </el-tag>
              <template v-if="a.status!==2">
                <el-button type="primary" size="small" style="margin-top:8px;width:100%"
                  @click="register(a)" :loading="registeringId === a.id" :disabled="isRegistered(a.id) || registeringId === a.id">
                  {{ isRegistered(a.id)?'已报名':'立即报名' }}
                </el-button>
                <el-button v-if="canCancelRegistration(findRegistration(a.id))" size="small" type="danger" plain style="margin-top:8px;width:100%;margin-left:0"
                  @click="cancelRegistration(findRegistration(a.id))" :loading="cancelingId === findRegistration(a.id).id">
                  取消报名
                </el-button>
              </template>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>
<script>
import request from '@/utils/request'
export default {
  data() { return { activities: [], myRegistrations: [], registeringId: null, cancelingId: null } },
  created() { this.loadData() },
  methods: {
    loadData() {
      request.get('/api/activity', { params: { pageNum: 1, pageSize: 999 } }).then(res => { this.activities = res.data.records || [] })
      const user = JSON.parse(sessionStorage.getItem('loginUser') || '{}')
      request.get('/api/registration', { params: { pageNum: 1, pageSize: 999, studentId: user.id } }).then(res => {
        this.myRegistrations = res.data.records || []
      })
    },
    isRegistered(activityId) {
      return !!this.findRegistration(activityId)
    },
    findRegistration(activityId) {
      return this.myRegistrations.find(r => Number(r.activityId) === Number(activityId) && Number(r.status) !== 2 && Number(r.status) !== 3)
    },
    register(activity) {
      if (this.registeringId) return
      const user = JSON.parse(sessionStorage.getItem('loginUser') || '{}')
      this.$confirm('确定报名该活动?', '提示').then(() => {
        this.registeringId = activity.id
        return request.post('/api/registration', { activityId: activity.id, studentId: user.id, status: 0 }).then(() => {
          const fee = Number(activity.fee || 0)
          this.$message.success(fee > 0 ? '报名成功，已生成缴费订单，请前往订单页支付' : '报名成功')
          this.loadData()
          if (fee > 0) {
            this.$router.push('/student/order')
          }
        }).finally(() => {
          this.registeringId = null
        })
      }).catch(() => {})
    },
    cancelRegistration(reg) {
      if (!reg || this.cancelingId) return
      this.$confirm('确定取消该活动报名吗？如已缴费将同步处理退款。', '提示', { type: 'warning' }).then(() => {
        this.cancelingId = reg.id
        return request.put(`/api/registration/cancel/${reg.id}`).then(() => {
          this.$message.success('报名已取消')
          this.loadData()
        }).finally(() => {
          this.cancelingId = null
        })
      }).catch(() => {})
    },
    canCancelRegistration(reg) {
      return !!reg && (Number(reg.status) === 0 || Number(reg.status) === 1) && !this.hasResult(reg)
    },
    hasResult(reg) {
      return reg && (reg.passed !== null && reg.passed !== undefined ||
        reg.rank !== null && reg.rank !== undefined ||
        !!reg.awardLevel)
    }
  }
}
</script>
