<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="购买课时包"
    width="460px"
    append-to-body
  >
    <el-form :model="form" label-width="90px">
      <el-form-item label="课程">
        <el-input :value="course.name" disabled />
      </el-form-item>
      <el-form-item label="授课教师">
        <el-input :value="course.teacherName || '待定'" disabled />
      </el-form-item>
      <el-form-item label="课时单价">
        <div class="price-text">¥{{ formatMoney(course.price) }}/课时</div>
      </el-form-item>
      <el-form-item label="快捷选择">
        <div class="quick-actions">
          <el-button size="mini" @click="setQuantity(10)">10 课时</el-button>
          <el-button size="mini" @click="setQuantity(20)">20 课时</el-button>
          <el-button size="mini" @click="setQuantity(30)">30 课时</el-button>
        </div>
      </el-form-item>
      <el-form-item label="课时数量">
        <el-input-number v-model="form.quantity" :min="1" :max="200" style="width: 180px" />
      </el-form-item>
      <el-form-item label="课包名称">
        <el-input v-model="form.remark" placeholder="例如：钢琴基础 10 课时包" />
      </el-form-item>
      <el-form-item label="订单金额">
        <div class="price-text total-price">¥{{ totalPrice }}</div>
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="submitOrder">确认下单</el-button>
    </span>
  </el-dialog>
</template>

<script>
import request from '@/utils/request'

export default {
  name: 'CoursePackageOrderDialog',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    course: {
      type: Object,
      default: () => ({})
    }
  },
  data() {
    return {
      submitting: false,
      form: {
        quantity: 10,
        remark: ''
      }
    }
  },
  computed: {
    dialogVisible: {
      get() {
        return this.visible
      },
      set(value) {
        this.$emit('update:visible', value)
      }
    },
    totalPrice() {
      const price = Number(this.course.price || 0)
      return (price * Number(this.form.quantity || 0)).toFixed(2)
    }
  },
  watch: {
    dialogVisible(value) {
      if (value) {
        this.resetForm()
      }
    }
  },
  methods: {
    resetForm() {
      const courseName = this.course.name || '课程'
      this.form = {
        quantity: 10,
        remark: `${courseName} 10 课时包`
      }
    },
    setQuantity(quantity) {
      this.form.quantity = quantity
      const courseName = this.course.name || '课程'
      this.form.remark = `${courseName} ${quantity} 课时包`
    },
    formatMoney(value) {
      return Number(value || 0).toFixed(2)
    },
    submitOrder() {
      if (!this.course.id) {
        this.$message.warning('课程信息不存在')
        return
      }
      if (!this.form.quantity || this.form.quantity <= 0) {
        this.$message.warning('课时数量必须大于 0')
        return
      }
      if (this.submitting) {
        return
      }
      this.submitting = true
      const user = JSON.parse(sessionStorage.getItem('loginUser') || '{}')
      request.post('/api/order', {
        studentId: user.id,
        productId: this.course.id,
        orderType: 'COURSE',
        quantity: this.form.quantity,
        originalAmount: Number(this.totalPrice),
        actualAmount: Number(this.totalPrice),
        status: 0,
        remark: this.form.remark || `${this.course.name} ${this.form.quantity} 课时包`
      }).then(() => {
        this.$message.success('课时包订单已创建，请前往订单页支付')
        this.dialogVisible = false
        this.$emit('success')
      }).finally(() => {
        this.submitting = false
      })
    }
  }
}
</script>

<style scoped>
.price-text {
  font-size: 18px;
  font-weight: 600;
  color: #e6a23c;
}

.total-price {
  font-size: 22px;
}

.quick-actions {
  display: flex;
  gap: 8px;
}
</style>
