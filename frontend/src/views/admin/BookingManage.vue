<template>
  <div>
    <el-card>
      <div slot="header">预约管理</div>
      <el-form :inline="true" :model="query" size="small" style="margin-bottom:10px">
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable>
            <el-option label="待审批" :value="0" />
            <el-option label="已通过" :value="1" />
            <el-option label="已拒绝" :value="2" />
            <el-option label="已取消" :value="3" />
            <el-option label="已过期" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">搜索</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="list" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="studentName" label="学生" />
        <el-table-column prop="teacherName" label="教师" />
        <el-table-column prop="courseName" label="课程" />
        <el-table-column prop="packageName" label="课时包" min-width="120" />
        <el-table-column prop="hoursCost" label="扣课时" width="80" />
        <el-table-column prop="classTime" label="上课时间" width="170" />
        <el-table-column prop="status" label="状态" width="90">
          <template slot-scope="scope">
            <el-tag :type="statusTag(scope.row.status)" size="small">
              {{ statusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
        <el-table-column label="操作" width="260" align="center">
          <template slot-scope="scope">
            <div class="table-action-btns">
              <el-button v-if="scope.row.status===0" size="mini" type="success" @click="approve(scope.row, 1)">通过</el-button>
              <el-button v-if="scope.row.status===0" size="mini" type="danger" @click="approve(scope.row, 2)">拒绝</el-button>
              <el-button size="mini" type="danger" plain @click="remove(scope.row)">删除</el-button>
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
  data() {
    return {
      list: [],
      total: 0,
      loading: false,
      query: { pageNum: 1, pageSize: 10, status: '' }
    }
  },
  created() {
    this.loadData()
  },
  methods: {
    loadData() {
      this.loading = true
      request.get('/api/booking', { params: this.query }).then(res => {
        this.list = res.data.records || []
        this.total = res.data.total || 0
      }).finally(() => { this.loading = false })
    },
    approve(row, status) {
      request.put('/api/booking', { ...row, status }).then(() => {
        this.$message.success('操作成功')
        this.loadData()
      })
    },
    statusText(status) {
      if (status === 1) return '已通过'
      if (status === 2) return '已拒绝'
      if (status === 3) return '已取消'
      if (status === 4) return '已过期'
      return '待审批'
    },
    statusTag(status) {
      if (status === 1) return 'success'
      if (status === 2) return 'danger'
      if (status === 3) return 'info'
      if (status === 4) return 'info'
      return 'warning'
    },
    remove(row) {
      this.$confirm('确定删除该预约?', '提示', { type: 'warning' }).then(() => {
        request.delete('/api/booking/' + row.id).then(() => {
          this.$message.success('删除成功')
          this.loadData()
        })
      }).catch(() => {})
    }
  }
}
</script>
