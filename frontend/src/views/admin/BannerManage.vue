<template>
  <div>
    <el-card>
      <div slot="header" style="display:flex;justify-content:space-between;align-items:center">
        <span>轮播图管理</span>
        <el-button type="primary" size="small" @click="handleAdd">新增轮播图</el-button>
      </div>
      <el-table :data="list" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column label="图片" width="150">
          <template slot-scope="scope">
            <img v-if="scope.row.imageUrl" :src="scope.row.imageUrl" style="width:120px;height:60px;object-fit:cover">
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="linkUrl" label="链接" show-overflow-tooltip />
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column prop="status" label="状态" width="80">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status===1?'success':'info'" size="small">{{ scope.row.status===1?'启用':'禁用' }}</el-tag>
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
    <el-dialog :title="form.id?'编辑轮播图':'新增轮播图'" :visible.sync="dialogVisible" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="标题"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="图片">
          <el-upload action="/api/upload" :show-file-list="false" :on-success="(r)=>{form.imageUrl=r.data}">
            <img v-if="form.imageUrl" :src="form.imageUrl" style="width:200px;height:100px;object-fit:cover">
            <el-button v-else size="small">上传图片</el-button>
          </el-upload>
        </el-form-item>
        <el-form-item label="链接"><el-input v-model="form.linkUrl" placeholder="可选" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="form.sort" :min="0" /></el-form-item>
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
      request.get('/api/banner', { params: this.query }).then(res => {
        this.list = res.data.records; this.total = res.data.total
      }).finally(() => { this.loading = false })
    },
    handleAdd() { this.form = { status: 1, sort: 0 }; this.dialogVisible = true },
    handleEdit(row) { this.form = { ...row }; this.dialogVisible = true },
    saveForm() {
      const api = this.form.id ? request.put('/api/banner', this.form) : request.post('/api/banner', this.form)
      api.then(() => { this.$message.success('操作成功'); this.dialogVisible = false; this.loadData() })
    },
    handleDel(row) {
      this.$confirm('确定删除?', '提示', { type: 'warning' }).then(() => {
        request.delete('/api/banner/' + row.id).then(() => { this.$message.success('删除成功'); this.loadData() })
      }).catch(() => {})
    }
  }
}
</script>
