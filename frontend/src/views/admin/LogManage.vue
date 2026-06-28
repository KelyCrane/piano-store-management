<template>
  <div>
    <el-card>
      <div slot="header">操作日志</div>
      <el-form :inline="true" :model="query" size="small" style="margin-bottom:10px">
        <el-form-item label="操作人"><el-input v-model="query.username" placeholder="搜索" clearable /></el-form-item>
        <el-form-item label="模块"><el-input v-model="query.module" placeholder="搜索" clearable /></el-form-item>
        <el-form-item><el-button type="primary" @click="loadData">搜索</el-button></el-form-item>
      </el-form>
      <el-table :data="list" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="username" label="操作人" width="100" />
        <el-table-column prop="module" label="模块" width="100" />
        <el-table-column prop="operation" label="操作" width="220" show-overflow-tooltip />
        <el-table-column label="内容" show-overflow-tooltip>
          <template slot-scope="scope">
            <span>{{ scope.row.method }}</span>
            <span v-if="scope.row.params"> {{ scope.row.params }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="ip" label="IP" width="130" />
        <el-table-column prop="createTime" label="时间" width="160" />
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
    return {
      list: [], total: 0, loading: false,
      query: { pageNum: 1, pageSize: 10, username: '', module: '' }
    }
  },
  created() { this.loadData() },
  methods: {
    loadData() {
      this.loading = true
      request.get('/api/log', { params: this.query }).then(res => {
        this.list = res.data.records; this.total = res.data.total
      }).finally(() => { this.loading = false })
    }
  }
}
</script>
