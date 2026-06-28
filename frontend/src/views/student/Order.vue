<template>
  <div>
    <el-card shadow="never">
      <div slot="header">我的订单</div>
      <el-table :data="list" border stripe v-loading="loading">
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column prop="orderType" label="类型" width="100">
          <template slot-scope="scope">
            <el-tag :type="orderTypeTag(scope.row.orderType)" size="small">
              {{ orderTypeText(scope.row.orderType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="productName" label="订单内容" min-width="180" />
        <el-table-column label="数量/课时" width="100">
          <template slot-scope="scope">
            {{ quantityText(scope.row) }}
          </template>
        </el-table-column>
        <el-table-column label="实付金额" width="100">
          <template slot-scope="scope">¥{{ formatMoney(scope.row.actualAmount) }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90">
          <template slot-scope="scope">
            <el-tag :type="statusTag(scope.row.status)" size="small">{{ statusText(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="下单时间" width="160" />
        <el-table-column label="操作" width="210" align="center">
          <template slot-scope="scope">
            <div class="table-action-btns">
              <el-button v-if="scope.row.status === 0" size="mini" type="primary" @click="payOrder(scope.row)">去支付</el-button>
              <el-button v-if="scope.row.status === 0" size="mini" type="danger" @click="cancelOrder(scope.row)">取消订单</el-button>
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
  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  name: 'StudentOrder',
  data() {
    return {
      list: [],
      total: 0,
      loading: false,
      query: {
        pageNum: 1,
        pageSize: 10
      }
    }
  },
  created() {
    this.loadData()
  },
  methods: {
    loadData() {
      this.loading = true
      const user = JSON.parse(sessionStorage.getItem('loginUser') || '{}')
      request.get('/api/order', {
        params: {
          ...this.query,
          studentId: user.id
        }
      }).then(res => {
        this.list = res.data.records || []
        this.total = res.data.total || 0
      }).finally(() => {
        this.loading = false
      })
    },
    quantityText(row) {
      if (row.orderType === 'COURSE') {
        return `${row.quantity || 0} 课时`
      }
      if (row.orderType === 'ACTIVITY') {
        return `${row.quantity || 1} 人`
      }
      return row.quantity || 0
    },
    formatMoney(value) {
      return Number(value || 0).toFixed(2)
    },
    orderTypeText(type) {
      if (type === 'COURSE') return '课程'
      if (type === 'INSTRUMENT') return '乐器'
      if (type === 'TEXTBOOK') return '教材'
      if (type === 'PRODUCT') return '商品'
      if (type === 'ACTIVITY') return '活动'
      return type || '-'
    },
    orderTypeTag(type) {
      if (type === 'COURSE') return 'warning'
      if (type === 'INSTRUMENT') return 'success'
      if (type === 'TEXTBOOK') return ''
      if (type === 'PRODUCT') return 'info'
      if (type === 'ACTIVITY') return 'danger'
      return 'info'
    },
    statusText(status) {
      if (status === 1) return '已支付'
      if (status === 2) return '已取消'
      return '待支付'
    },
    statusTag(status) {
      if (status === 1) return 'success'
      if (status === 2) return 'info'
      return 'warning'
    },
    cancelOrder(row) {
      this.$confirm('确定取消该订单吗？', '提示', { type: 'warning' }).then(() => {
        request.put('/api/order', { ...row, status: 2 }).then(() => {
          this.$message.success('已取消')
          this.loadData()
        })
      }).catch(() => {})
    },
    payOrder(row) {
      this.$confirm('确认支付该订单吗？', '提示').then(() => {
        request.put(`/api/order/pay/${row.id}`).then(() => {
          this.$message.success('支付成功')
          this.loadData()
        })
      }).catch(() => {})
    }
  }
}
</script>
