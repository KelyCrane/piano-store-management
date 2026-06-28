<template>
  <div class="package-page">
    <el-row :gutter="16" class="summary-row">
      <el-col :md="6" :sm="12" :xs="24">
        <el-card shadow="never" class="summary-card">
          <div class="summary-value">{{ list.length }}</div>
          <div class="summary-label">课时包数量</div>
        </el-card>
      </el-col>
      <el-col :md="6" :sm="12" :xs="24">
        <el-card shadow="never" class="summary-card">
          <div class="summary-value">{{ totalHours }}</div>
          <div class="summary-label">总剩余课时</div>
        </el-card>
      </el-col>
      <el-col :md="6" :sm="12" :xs="24">
        <el-card shadow="never" class="summary-card">
          <div class="summary-value">{{ availableHours }}</div>
          <div class="summary-label">可排课时</div>
        </el-card>
      </el-col>
      <el-col :md="6" :sm="12" :xs="24">
        <el-card shadow="never" class="summary-card">
          <div class="summary-value">{{ reservedHours }}</div>
          <div class="summary-label">已预留课时</div>
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="never">
      <div slot="header">我的课时包</div>
      <el-table :data="list" border stripe v-loading="loading">
        <el-table-column prop="name" label="课时包" min-width="150" />
        <el-table-column prop="courseName" label="课程" min-width="120" />
        <el-table-column prop="teacherName" label="教师" width="110" />
        <el-table-column prop="totalHours" label="总课时" width="90" />
        <el-table-column prop="remainingHours" label="剩余课时" width="100">
          <template slot-scope="scope">
            <el-tag :type="scope.row.remainingHours > 0 ? 'success' : 'info'" size="small">
              {{ scope.row.remainingHours || 0 }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="availableHours" label="可排课时" width="100">
          <template slot-scope="scope">
            <el-tag :type="scope.row.availableHours > 0 ? 'warning' : 'info'" size="small">
              {{ scope.row.availableHours || 0 }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="reservedHours" label="已预留" width="100">
          <template slot-scope="scope">
            <el-tag :type="scope.row.reservedHours > 0 ? 'danger' : 'info'" size="small">
              {{ scope.row.reservedHours || 0 }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="price" label="总价(元)" width="110">
          <template slot-scope="scope">¥{{ formatMoney(scope.row.price) }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'info'" size="small">
              {{ scope.row.status === 1 ? '有效' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
      </el-table>
    </el-card>
  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  name: 'StudentPackage',
  data() {
    return {
      list: [],
      loading: false
    }
  },
  computed: {
    totalHours() {
      return this.list.reduce((sum, item) => sum + Number(item.remainingHours || 0), 0)
    },
    availableHours() {
      return this.list.reduce((sum, item) => sum + Number(item.availableHours || 0), 0)
    },
    reservedHours() {
      return this.list.reduce((sum, item) => sum + Number(item.reservedHours || 0), 0)
    }
  },
  created() {
    this.loadData()
  },
  methods: {
    loadData() {
      const user = JSON.parse(sessionStorage.getItem('loginUser') || '{}')
      this.loading = true
      request.get('/api/studentPackage', {
        params: {
          pageNum: 1,
          pageSize: 999,
          studentId: user.id
        }
      }).then(res => {
        this.list = res.data.records || []
      }).finally(() => {
        this.loading = false
      })
    },
    formatMoney(value) {
      return Number(value || 0).toFixed(2)
    }
  }
}
</script>

<style scoped>
.package-page {
  max-width: 1200px;
  margin: 0 auto;
}

.summary-row {
  margin-bottom: 16px;
}

.summary-card {
  border-radius: 14px;
}

.summary-value {
  font-size: 28px;
  font-weight: 700;
  color: #303133;
}

.summary-label {
  margin-top: 6px;
  font-size: 13px;
  color: #909399;
}
</style>
