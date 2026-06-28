<template>
  <div>
    <el-card>
      <div slot="header">乐器商城</div>
      <el-form :inline="true" size="small" style="margin-bottom:10px">
        <el-form-item label="名称"><el-input v-model="query.name" placeholder="搜索" clearable /></el-form-item>
        <el-form-item label="分类">
          <el-select v-model="query.categoryId" placeholder="全部" clearable>
            <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item><el-button type="primary" @click="loadProducts">搜索</el-button></el-form-item>
      </el-form>
      <el-row :gutter="16">
        <el-col :span="6" v-for="p in products" :key="p.id">
          <el-card shadow="hover" style="margin-bottom:16px">
            <img v-if="p.imageUrl" :src="p.imageUrl" style="width:100%;height:150px;object-fit:cover">
            <div style="padding:10px 0">
              <div style="font-weight:bold">{{ p.name }}</div>
              <div style="color:#999;font-size:13px;margin-top:4px">品牌：{{ p.brand }} | 库存：{{ p.stock }}</div>
              <div style="display:flex;justify-content:space-between;align-items:center;margin-top:8px">
                <span style="color:#E6A23C;font-size:18px;font-weight:bold">¥{{ p.price }}</span>
                <div style="display:flex;gap:8px">
                  <el-button size="mini" @click="goDetail(p)">详情</el-button>
                  <el-button type="primary" size="mini" @click="openBuy(p)" :disabled="p.stock<=0">购买</el-button>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
    <el-dialog title="购买商品" :visible.sync="dialogVisible" width="400px">
      <el-form :model="orderForm" label-width="80px">
        <el-form-item label="商品"><el-input :value="selectedProduct.name" disabled /></el-form-item>
        <el-form-item label="单价"><el-input :value="'¥'+selectedProduct.price" disabled /></el-form-item>
        <el-form-item label="数量"><el-input-number v-model="orderForm.quantity" :min="1" :max="selectedProduct.stock" /></el-form-item>
        <el-form-item label="总价"><span style="color:#E6A23C;font-size:18px">¥{{ (selectedProduct.price * orderForm.quantity).toFixed(2) }}</span></el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="dialogVisible=false">取消</el-button>
        <el-button type="primary" @click="submitOrder">确认下单</el-button>
      </span>
    </el-dialog>
  </div>
</template>
<script>
import request from '@/utils/request'
export default {
  data() {
    return {
      products: [], categories: [], dialogVisible: false,
      query: { name: '', categoryId: '' },
      selectedProduct: {}, orderForm: { quantity: 1 }
    }
  },
  created() { this.loadProducts(); this.loadCategories() },
  methods: {
    loadProducts() {
      request.get('/api/product', { params: { pageNum: 1, pageSize: 999, status: 1, ...this.query } }).then(res => {
        this.products = res.data.records || []
      })
    },
    loadCategories() {
      request.get('/api/category', { params: { pageNum: 1, pageSize: 999 } }).then(res => { this.categories = res.data.records || [] })
    },
    openBuy(product) {
      this.selectedProduct = product
      this.orderForm = { quantity: 1 }
      this.dialogVisible = true
    },
    goDetail(product) {
      this.$router.push('/student/product/' + product.id)
    },
    submitOrder() {
      const user = JSON.parse(sessionStorage.getItem('loginUser') || '{}')
      const totalPrice = (this.selectedProduct.price * this.orderForm.quantity).toFixed(2)
      const data = {
        studentId: user.id,
        productId: this.selectedProduct.id,
        orderType: this.selectedProduct.type || 'PRODUCT',
        quantity: this.orderForm.quantity,
        originalAmount: totalPrice,
        actualAmount: totalPrice,
        status: 0
      }
      request.post('/api/order', data).then(() => {
        this.$message.success('下单成功')
        this.dialogVisible = false
        this.loadProducts()
        this.$router.push('/student/order')
      })
    }
  }
}
</script>
