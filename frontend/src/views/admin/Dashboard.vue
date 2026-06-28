<template>
  <div class="dashboard-container">
    <el-row :gutter="24">
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover" class="stat-card blue-card">
          <div class="stat-content">
            <div class="stat-icon"><i class="el-icon-user"></i></div>
            <div class="stat-info">
              <div class="stat-num">{{ stats.userCount || 0 }}</div>
              <div class="stat-label">用户总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover" class="stat-card green-card">
          <div class="stat-content">
            <div class="stat-icon"><i class="el-icon-notebook-2"></i></div>
            <div class="stat-info">
              <div class="stat-num">{{ stats.courseCount || 0 }}</div>
              <div class="stat-label">课程总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover" class="stat-card orange-card">
          <div class="stat-content">
            <div class="stat-icon"><i class="el-icon-s-order"></i></div>
            <div class="stat-info">
              <div class="stat-num">{{ stats.orderCount || 0 }}</div>
              <div class="stat-label">订单总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover" class="stat-card purple-card">
          <div class="stat-content">
            <div class="stat-icon"><i class="el-icon-trophy"></i></div>
            <div class="stat-info">
              <div class="stat-num">{{ stats.activityCount || 0 }}</div>
              <div class="stat-label">活动总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="24" style="margin-top:24px">
      <el-col :span="12">
        <el-card shadow="never" class="list-card">
          <div slot="header" class="clearfix">
            <span><i class="el-icon-time"></i> 最近预约</span>
            <el-button style="float: right; padding: 3px 0" type="text" @click="$router.push('/admin/booking')">查看更多</el-button>
          </div>
          <el-table :data="recentBookings" size="small" :header-cell-style="{background:'#f5f7fa'}">
            <el-table-column prop="studentName" label="学生" width="120">
              <template slot-scope="scope">
                <div class="user-cell">
                  <el-avatar size="small" icon="el-icon-user-solid"></el-avatar>
                  <span style="margin-left:8px">{{ scope.row.studentName }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="courseName" label="课程" show-overflow-tooltip />
            <el-table-column prop="classTime" label="上课时间" width="160" />
            <el-table-column prop="status" label="状态" width="100" align="center">
              <template slot-scope="scope">
                <el-tag :type="bookingStatusTag(scope.row.status)" effect="dark" size="mini">
                  {{ bookingStatusText(scope.row.status) }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="never" class="list-card">
          <div slot="header" class="clearfix">
            <span><i class="el-icon-s-ticket"></i> 最近订单</span>
            <el-button style="float: right; padding: 3px 0" type="text" @click="$router.push('/admin/order')">查看更多</el-button>
          </div>
          <el-table :data="recentOrders" size="small" :header-cell-style="{background:'#f5f7fa'}">
            <el-table-column prop="orderNo" label="订单号" width="140" show-overflow-tooltip />
            <el-table-column prop="studentName" label="学生" width="100" />
            <el-table-column prop="actualAmount" label="金额" width="100">
              <template slot-scope="scope">
                <span style="color:#F56C6C;font-weight:bold">¥{{ scope.row.actualAmount }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" align="center">
              <template slot-scope="scope">
                <el-tag :type="scope.row.status===1?'success':scope.row.status===2?'info':'warning'" effect="plain" size="mini">
                  {{ scope.row.status===0?'待付款':scope.row.status===1?'已付款':'已取消' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import request from '@/utils/request'
export default {
  data() {
    return {
      stats: { userCount: 0, courseCount: 0, orderCount: 0, activityCount: 0 },
      recentBookings: [],
      recentOrders: []
    }
  },
  created() {
    this.loadData()
  },
  methods: {
    async loadData() {
      try {
        const [u, c, o, a, bookings, orders] = await Promise.all([
          request.get('/api/user', { params: { pageNum: 1, pageSize: 1 } }),
          request.get('/api/course', { params: { pageNum: 1, pageSize: 1 } }),
          request.get('/api/order', { params: { pageNum: 1, pageSize: 1 } }),
          request.get('/api/activity', { params: { pageNum: 1, pageSize: 1 } }),
          request.get('/api/booking', { params: { pageNum: 1, pageSize: 5 } }),
          request.get('/api/order', { params: { pageNum: 1, pageSize: 5 } })
        ])
        
        this.stats = {
          userCount: u.data.total || 0,
          courseCount: c.data.total || 0,
          orderCount: o.data.total || 0,
          activityCount: a.data.total || 0
        }
        this.recentBookings = bookings.data.records || []
        this.recentOrders = orders.data.records || []
      } catch (e) {
        console.error('Failed to load dashboard data', e)
      }
    },
    bookingStatusText(status) {
      if (status === 1) return '已通过'
      if (status === 2) return '已拒绝'
      if (status === 3) return '已取消'
      if (status === 4) return '已过期'
      return '待审批'
    },
    bookingStatusTag(status) {
      if (status === 1) return 'success'
      if (status === 2) return 'danger'
      if (status === 3 || status === 4) return 'info'
      return 'warning'
    }
  }
}
</script>

<style scoped>
.dashboard-container {
  padding: 10px;
}

.stat-card {
  border: none;
  border-radius: 12px;
  color: #fff;
  transition: transform 0.3s;
  cursor: pointer;
  overflow: hidden;
  position: relative;
}
.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0,0,0,0.15);
}

.blue-card { background: linear-gradient(135deg, #6C5CE7 0%, #a29bfe 100%); }
.green-card { background: linear-gradient(135deg, #00b894 0%, #55efc4 100%); }
.orange-card { background: linear-gradient(135deg, #e17055 0%, #fab1a0 100%); }
.purple-card { background: linear-gradient(135deg, #fd79a8 0%, #fab1a0 100%); } /* Actually pink/orange mix */

.stat-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
}

.stat-icon i {
  font-size: 48px;
  opacity: 0.8;
}

.stat-info {
  text-align: right;
}

.stat-num {
  font-size: 32px;
  font-weight: 700;
  line-height: 1.2;
}

.stat-label {
  font-size: 14px;
  opacity: 0.9;
  margin-top: 5px;
}

.list-card {
  border-radius: 12px;
  border: none;
}
.list-card .el-card__header {
  border-bottom: 1px solid #f0f2f5;
  padding: 15px 20px;
  font-weight: 600;
}

.user-cell {
  display: flex;
  align-items: center;
}
</style>
