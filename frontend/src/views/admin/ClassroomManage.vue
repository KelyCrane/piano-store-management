<template>
  <div>
    <el-card>
      <div slot="header" style="display:flex;justify-content:space-between;align-items:center">
        <span>教室管理</span>
        <el-button type="primary" size="small" @click="handleAdd">新增教室</el-button>
      </div>
      <el-table :data="list" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="教室名称" />
        <el-table-column prop="location" label="位置" />
        <el-table-column prop="capacity" label="容量" width="80" />
        <el-table-column prop="equipment" label="设备" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="80">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status===1?'success':'info'" size="small">{{ scope.row.status===1?'可用':'不可用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="168" align="center">
          <template slot-scope="scope">
            <div class="table-action-btns">
              <el-button size="mini" @click="handleEdit(scope.row)">编辑</el-button>
              <el-button size="mini" type="danger" @click="handleDel(scope.row)">删除</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination style="margin-top:15px;text-align:right" background layout="total, prev, pager, next"
        :total="total" :page-size="query.pageSize" :current-page.sync="query.pageNum" @current-change="loadData" />
    </el-card>
    <el-dialog :title="form.id?'编辑教室':'新增教室'" :visible.sync="dialogVisible" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="位置"><el-input v-model="form.location" /></el-form-item>
        <el-form-item label="容量"><el-input-number v-model="form.capacity" :min="1" /></el-form-item>
        <el-form-item label="设备"><el-input v-model="form.equipment" type="textarea" /></el-form-item>
        <el-form-item label="状态"><el-switch v-model="form.status" :active-value="1" :inactive-value="0" /></el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="dialogVisible=false">取消</el-button>
        <el-button type="primary" @click="saveForm">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>
<script>
import request from '@/utils/request'
export default {
  data() {
    return {
      list: [], total: 0, loading: false, dialogVisible: false,
      query: { pageNum: 1, pageSize: 10 },
      form: {}
    }
  },
  created() { this.loadData() },
  methods: {
    loadData() {
      this.loading = true
      request.get('/api/classroom', { params: this.query }).then(res => {
        this.list = res.data.records; this.total = res.data.total
      }).finally(() => { this.loading = false })
    },
    handleAdd() { this.form = { status: 1, capacity: 10 }; this.dialogVisible = true },
    handleEdit(row) { this.form = { ...row }; this.dialogVisible = true },
    saveForm() {
      const api = this.form.id ? request.put('/api/classroom', this.form) : request.post('/api/classroom', this.form)
      api.then(() => { this.$message.success('操作成功'); this.dialogVisible = false; this.loadData() })
    },
    handleDel(row) {
      this.$confirm('确定删除?', '提示', { type: 'warning' }).then(() => {
        request.delete('/api/classroom/' + row.id).then(() => { this.$message.success('删除成功'); this.loadData() })
      }).catch(() => {})
    }
  }
}
</script>
