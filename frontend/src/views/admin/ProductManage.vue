<template>
  <div>
    <el-card>
      <div slot="header" style="display:flex;justify-content:space-between;align-items:center">
        <span>商品管理</span>
        <el-button type="primary" size="small" @click="handleAdd">新增商品</el-button>
      </div>
      <el-form :inline="true" :model="query" size="small" style="margin-bottom:10px">
        <el-form-item label="名称"><el-input v-model="query.name" placeholder="搜索" clearable /></el-form-item>
        <el-form-item label="分类">
          <el-select v-model="query.categoryId" placeholder="全部" clearable>
            <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item><el-button type="primary" @click="loadData">搜索</el-button></el-form-item>
      </el-form>
      <el-table :data="list" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column label="图片" width="80">
          <template slot-scope="scope">
            <img v-if="scope.row.imageUrl" :src="scope.row.imageUrl" style="width:50px;height:50px;object-fit:cover">
          </template>
        </el-table-column>
        <el-table-column prop="name" label="商品名称" />
        <el-table-column prop="categoryName" label="分类" />
        <el-table-column prop="price" label="价格(元)" width="90" />
        <el-table-column prop="stock" label="库存" width="70" />
        <el-table-column prop="brand" label="品牌" />
        <el-table-column prop="status" label="状态" width="80">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status===1?'success':'info'" size="small">{{ scope.row.status===1?'上架':'下架' }}</el-tag>
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
    <el-dialog :title="form.id?'编辑商品':'新增商品'" :visible.sync="dialogVisible" width="600px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="分类">
          <el-select v-model="form.categoryId" placeholder="选择分类">
            <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="form.type" placeholder="选择类型">
            <el-option label="乐器" value="INSTRUMENT" />
            <el-option label="教材" value="TEXTBOOK" />
          </el-select>
        </el-form-item>
        <el-form-item label="价格"><el-input-number v-model="form.price" :min="0" :precision="2" /></el-form-item>
        <el-form-item label="库存"><el-input-number v-model="form.stock" :min="0" /></el-form-item>
        <el-form-item label="品牌"><el-input v-model="form.brand" /></el-form-item>
        <el-form-item label="图片">
          <el-upload action="/api/upload" :show-file-list="false" :on-success="(r)=>{form.imageUrl=r.data}">
            <img v-if="form.imageUrl" :src="form.imageUrl" style="width:120px;height:80px;object-fit:cover">
            <el-button v-else size="small">上传图片</el-button>
          </el-upload>
        </el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="详情图">
          <el-upload
            action="/api/upload"
            list-type="picture-card"
            multiple
            :file-list="detailFileList"
            :on-success="handleDetailSuccess"
            :on-remove="handleDetailRemove"
          >
            <i class="el-icon-plus"></i>
          </el-upload>
        </el-form-item>
        <el-form-item label="备注"><el-input v-model="form.remark" type="textarea" :rows="2" /></el-form-item>
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
      query: { pageNum: 1, pageSize: 10, name: '', categoryId: '' },
      form: {}, categories: [], detailFileList: []
    }
  },
  created() { this.loadData(); this.loadCategories() },
  methods: {
    loadData() {
      this.loading = true
      request.get('/api/product', { params: this.query }).then(res => {
        this.list = res.data.records; this.total = res.data.total
      }).finally(() => { this.loading = false })
    },
    loadCategories() {
      request.get('/api/category', { params: { pageNum: 1, pageSize: 999 } }).then(res => { this.categories = res.data.records || [] })
    },
    handleAdd() {
      this.form = { status: 1, stock: 0, price: 0, type: 'TEXTBOOK', detailImages: '' }
      this.detailFileList = []
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.form = { ...row, type: row.type || 'TEXTBOOK' }
      this.detailFileList = (this.form.detailImages || '')
        .split(',')
        .map(item => item.trim())
        .filter(Boolean)
        .map((url, index) => ({ name: `详情图${index + 1}`, url }))
      this.dialogVisible = true
    },
    handleDetailSuccess(res, file, fileList) {
      file.url = res.data
      this.detailFileList = fileList
      this.syncDetailImages()
    },
    handleDetailRemove(file, fileList) {
      this.detailFileList = fileList
      this.syncDetailImages()
    },
    syncDetailImages() {
      this.form.detailImages = (this.detailFileList || [])
        .map(file => file.url || (file.response && file.response.data))
        .filter(Boolean)
        .join(',')
    },
    saveForm() {
      if (!this.form.name || !this.form.categoryId || !this.form.type) {
        this.$message.warning('请填写名称、分类和类型')
        return
      }
      this.syncDetailImages()
      const api = this.form.id ? request.put('/api/product', this.form) : request.post('/api/product', this.form)
      api.then(() => { this.$message.success('操作成功'); this.dialogVisible = false; this.loadData() })
    },
    handleDel(row) {
      this.$confirm('确定删除?', '提示', { type: 'warning' }).then(() => {
        request.delete('/api/product/' + row.id).then(() => { this.$message.success('删除成功'); this.loadData() })
      }).catch(() => {})
    }
  }
}
</script>
