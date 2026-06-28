<template>
  <div>
    <el-card shadow="never">
      <div slot="header" class="header-row">
        <span>出入库记录</span>
        <el-button type="primary" size="small" @click="handleAdd">新增记录</el-button>
      </div>

      <el-form :inline="true" :model="query" size="small" style="margin-bottom:10px">
        <el-form-item label="商品名称">
          <el-input v-model="query.productName" placeholder="搜索商品" clearable />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="query.categoryId" placeholder="全部" clearable filterable>
            <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="商品">
          <el-select v-model="query.productId" placeholder="全部" clearable filterable>
            <el-option v-for="p in products" :key="p.id" :label="p.name" :value="p.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="query.type" placeholder="全部" clearable>
            <el-option label="入库" value="IN" />
            <el-option label="出库" value="OUT" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">搜索</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="list" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="productName" label="商品" min-width="160" />
        <el-table-column prop="type" label="类型" width="80">
          <template slot-scope="scope">
            <el-tag :type="scope.row.type === 'IN' ? 'success' : 'danger'" size="small">
              {{ scope.row.type === 'IN' ? '入库' : '出库' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="quantity" label="数量" width="80" />
        <el-table-column prop="remark" label="原因" show-overflow-tooltip />
        <el-table-column prop="operatorName" label="操作人" width="110" />
        <el-table-column prop="createTime" label="时间" width="160" />
        <el-table-column label="操作" width="160" align="center">
          <template slot-scope="scope">
            <div class="table-action-btns">
              <el-button size="mini" @click="handleEdit(scope.row)">编辑</el-button>
              <el-button size="mini" type="danger" @click="handleDel(scope.row)">删除</el-button>
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

    <el-dialog :title="form.id ? '编辑库存记录' : '新增库存记录'" :visible.sync="dialogVisible" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="商品">
          <el-select v-model="form.productId" filterable placeholder="选择商品" style="width:100%">
            <el-option v-for="p in products" :key="p.id" :label="p.name" :value="p.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="类型">
          <el-radio-group v-model="form.type">
            <el-radio label="IN">入库</el-radio>
            <el-radio label="OUT">出库</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="数量">
          <el-input-number v-model="form.quantity" :min="1" />
        </el-form-item>
        <el-form-item label="原因">
          <el-input v-model="form.remark" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveForm">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  name: 'AdminStockManage',
  data() {
    return {
      list: [],
      total: 0,
      loading: false,
      dialogVisible: false,
      query: { pageNum: 1, pageSize: 10, productId: '', productName: '', categoryId: '', type: '' },
      form: {},
      products: [],
      categories: []
    }
  },
  created() {
    this.loadData()
    this.loadProducts()
    this.loadCategories()
  },
  methods: {
    loadData() {
      this.loading = true
      request.get('/api/stockRecord', { params: this.query }).then(res => {
        this.list = res.data.records || []
        this.total = res.data.total || 0
      }).finally(() => {
        this.loading = false
      })
    },
    loadProducts() {
      request.get('/api/product', { params: { pageNum: 1, pageSize: 999 } }).then(res => {
        this.products = res.data.records || []
      })
    },
    loadCategories() {
      request.get('/api/category', { params: { pageNum: 1, pageSize: 999 } }).then(res => {
        this.categories = res.data.records || []
      })
    },
    handleAdd() {
      this.form = { type: 'IN', quantity: 1, productId: '', remark: '' }
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.form = { ...row }
      this.dialogVisible = true
    },
    saveForm() {
      if (!this.form.productId || !this.form.type || !this.form.quantity) {
        this.$message.warning('请填写完整库存记录')
        return
      }
      const api = this.form.id
        ? request.put('/api/stockRecord', this.form)
        : request.post('/api/stockRecord', this.form)
      api.then(() => {
        this.$message.success('操作成功')
        this.dialogVisible = false
        this.loadData()
        this.loadProducts()
      })
    },
    handleDel(row) {
      this.$confirm('确定删除该库存记录吗？库存数量会同步回滚。', '提示', { type: 'warning' }).then(() => {
        request.delete(`/api/stockRecord/${row.id}`).then(() => {
          this.$message.success('删除成功')
          this.loadData()
          this.loadProducts()
        })
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
.header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
