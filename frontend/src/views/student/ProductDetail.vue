<template>
  <div class="product-detail-page" v-loading="loading">
    <el-button type="text" icon="el-icon-arrow-left" class="back-link" @click="$router.push('/student/shop')">
      返回商城
    </el-button>

    <el-card v-if="product.id">
      <el-row :gutter="20">
        <el-col :xs="24" :md="10">
          <img v-if="mainImage || product.imageUrl" :src="mainImage || product.imageUrl" class="main-image">
          <div v-else class="main-image empty">暂无图片</div>
          <div class="thumb-list" v-if="detailImages.length">
            <img v-for="(img,idx) in detailImages" :key="idx" :src="img" class="thumb-item" @click="mainImage = img">
          </div>
        </el-col>
        <el-col :xs="24" :md="14">
          <h2>{{ product.name }}</h2>
          <p style="color:#909399">品牌：{{ product.brand || '暂无' }} ｜ 库存：{{ product.stock }}</p>
          <div style="margin:12px 0;font-size:28px;color:#E6A23C;font-weight:700">¥{{ product.price }}</div>
          <el-alert
            v-if="product.remark"
            :title="'备注：' + product.remark"
            type="info"
            :closable="false"
            show-icon
            style="margin:12px 0"
          />
          <div class="desc-box">
            <h4>商品描述</h4>
            <p>{{ product.description || product.remark || '暂无描述' }}</p>
          </div>
          <el-button type="primary" :disabled="product.stock <= 0" @click="openBuy">立即购买</el-button>
        </el-col>
      </el-row>
    </el-card>

    <el-dialog title="购买商品" :visible.sync="dialogVisible" width="400px">
      <el-form :model="orderForm" label-width="80px">
        <el-form-item label="商品"><el-input :value="product.name" disabled /></el-form-item>
        <el-form-item label="单价"><el-input :value="'¥'+product.price" disabled /></el-form-item>
        <el-form-item label="数量"><el-input-number v-model="orderForm.quantity" :min="1" :max="product.stock" /></el-form-item>
        <el-form-item label="总价"><span style="color:#E6A23C;font-size:18px">¥{{ totalPrice }}</span></el-form-item>
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
      loading: false,
      product: {},
      mainImage: '',
      detailImages: [],
      dialogVisible: false,
      orderForm: { quantity: 1 }
    }
  },
  computed: {
    totalPrice() {
      const price = Number(this.product.price || 0)
      return (price * this.orderForm.quantity).toFixed(2)
    }
  },
  watch: {
    '$route.params.id': {
      immediate: true,
      handler() {
        this.loadDetail()
      }
    }
  },
  methods: {
    loadDetail() {
      const id = this.$route.params.id
      if (!id) return
      this.loading = true
      request.get('/api/product/' + id).then(res => {
        this.product = res.data || {}
        this.mainImage = this.product.imageUrl || ''
        this.detailImages = (this.product.detailImages || '')
          .split(',')
          .map(v => v.trim())
          .filter(Boolean)
        if (!this.mainImage && this.detailImages.length) {
          this.mainImage = this.detailImages[0]
        }
      }).finally(() => { this.loading = false })
    },
    openBuy() {
      this.orderForm = { quantity: 1 }
      this.dialogVisible = true
    },
    submitOrder() {
      const user = JSON.parse(sessionStorage.getItem('loginUser') || '{}')
      const totalPrice = this.totalPrice
      const data = {
        studentId: user.id,
        productId: this.product.id,
        orderType: this.product.type || 'PRODUCT',
        quantity: this.orderForm.quantity,
        originalAmount: totalPrice,
        actualAmount: totalPrice,
        status: 0
      }
      request.post('/api/order', data).then(() => {
        this.$message.success('下单成功')
        this.dialogVisible = false
        this.$router.push('/student/order')
      })
    }
  }
}
</script>

<style scoped>
.product-detail-page {
  max-width: 1100px;
  margin: 0 auto;
}
.back-link {
  margin-bottom: 10px;
}
.main-image {
  width: 100%;
  height: 320px;
  object-fit: cover;
  border-radius: 8px;
  border: 1px solid #ebeef5;
}
.main-image.empty {
  display: flex;
  align-items: center;
  justify-content: center;
  color: #c0c4cc;
}
.thumb-list {
  margin-top: 10px;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}
.thumb-item {
  width: 72px;
  height: 72px;
  object-fit: cover;
  border-radius: 6px;
  border: 1px solid #e4e7ed;
  cursor: pointer;
}
.desc-box {
  margin: 14px 0;
  line-height: 1.8;
  color: #606266;
}
.desc-box h4 {
  margin: 0 0 6px;
  color: #303133;
}
</style>
