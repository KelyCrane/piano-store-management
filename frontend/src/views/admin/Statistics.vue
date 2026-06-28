<template>
  <div class="statistics-page" v-loading="loading">
    <!-- 核心指标 -->
    <el-row :gutter="16" class="kpi-row">
      <el-col :xs="12" :sm="6" v-for="card in kpiCards" :key="card.key">
        <div class="kpi-card">
          <div class="kpi-icon" :style="{ background: card.tint }"><i :class="card.icon" /></div>
          <div class="kpi-body">
            <div class="kpi-label">{{ card.label }}</div>
            <div class="kpi-value">{{ card.display }}</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 月度收入 + 订单量 双轴 -->
    <el-card shadow="never" class="chart-card chart-card--hero">
      <div slot="header" class="card-title">
        <span class="title-main">月度营收与订单量</span>
        <span class="title-sub">近 12 个月已付款订单</span>
      </div>
      <div ref="comboChart" class="chart-box chart-box--lg" />
    </el-card>

    <el-row :gutter="16" class="chart-row">
      <el-col :xs="24" :lg="12">
        <el-card shadow="never" class="chart-card">
          <div slot="header" class="card-title">
            <span class="title-main">收入类型分布</span>
            <span class="title-sub">按订单类型汇总实收</span>
          </div>
          <div ref="typeChart" class="chart-box" />
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="12">
        <el-card shadow="never" class="chart-card">
          <div slot="header" class="card-title">
            <span class="title-main">课程预约审批</span>
            <span class="title-sub">各状态预约单量</span>
          </div>
          <div ref="bookingChart" class="chart-box" />
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="chart-row">
      <el-col :xs="24" :lg="14">
        <el-card shadow="never" class="chart-card">
          <div slot="header" class="card-title">
            <span class="title-main">活动报名热度</span>
            <span class="title-sub">按活动统计报名人数 TOP</span>
          </div>
          <div ref="activityChart" class="chart-box" />
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="10">
        <el-card shadow="never" class="chart-card">
          <div slot="header" class="card-title">
            <span class="title-main">畅销商品 TOP</span>
            <span class="title-sub">已付款订单实收汇总</span>
          </div>
          <div ref="topProductChart" class="chart-box" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import request from '@/utils/request'
import * as echarts from 'echarts'

const PALETTE = ['#5B7CF9', '#7C6CF0', '#00C9A7', '#FFB547', '#FF6B8A', '#4ECDC4', '#95E1D3', '#AA96DA']

const ORDER_TYPE_LABEL = {
  COURSE: '课程订单',
  PRODUCT: '商品订单',
  TEXTBOOK: '教材订单',
  INSTRUMENT: '乐器订单',
  course: '课程订单',
  product: '商品订单',
  textbook: '教材订单',
  instrument: '乐器订单'
}

const BOOKING_STATUS = {
  0: '待审批',
  1: '已通过',
  2: '已拒绝',
  3: '已取消',
  4: '已过期',
  '0': '待审批',
  '1': '已通过',
  '2': '已拒绝',
  '3': '已取消',
  '4': '已过期'
}

const CHART_KEYS = ['chartCombo', 'chartType', 'chartBooking', 'chartActivity', 'chartTopProduct']

function num(v) {
  if (v == null || v === '') return 0
  const n = Number(v)
  return Number.isFinite(n) ? n : 0
}

function pick(obj, keys) {
  if (!obj || typeof obj !== 'object') return undefined
  for (const k of keys) {
    if (obj[k] !== undefined && obj[k] !== null) return obj[k]
  }
  return undefined
}

function money(n) {
  const v = num(n)
  return v.toLocaleString('zh-CN', { minimumFractionDigits: 0, maximumFractionDigits: 2 })
}

export default {
  data() {
    return {
      loading: false,
      overview: {},
      monthlyData: [],
      typeData: [],
      bookingData: [],
      activityData: [],
      topProductData: [],
      chartCombo: null,
      chartType: null,
      chartBooking: null,
      chartActivity: null,
      chartTopProduct: null
    }
  },
  computed: {
    kpiCards() {
      const o = this.overview || {}
      const amt = pick(o, ['total_amount', 'totalAmount', 'TOTAL_AMOUNT'])
      const orders = pick(o, ['paid_order_count', 'paidOrderCount', 'PAID_ORDER_COUNT'])
      return [
        {
          key: 'amt',
          label: '累计实收（元）',
          display: money(amt),
          icon: 'el-icon-coin',
          tint: 'linear-gradient(135deg,#667eea,#764ba2)'
        },
        {
          key: 'ord',
          label: '已付款订单数',
          display: String(num(orders)),
          icon: 'el-icon-s-order',
          tint: 'linear-gradient(135deg,#5B7CF9,#4a9eff)'
        },
        {
          key: 'stu',
          label: '学员人数',
          display: String(num(pick(o, ['studentCount', 'student_count']))),
          icon: 'el-icon-user',
          tint: 'linear-gradient(135deg,#00c9a7,#00a896)'
        },
        {
          key: 'pend',
          label: '待审批预约',
          display: String(num(pick(o, ['pendingBookings', 'pending_bookings']))),
          icon: 'el-icon-time',
          tint: 'linear-gradient(135deg,#ffb547,#ff8c42)'
        }
      ]
    }
  },
  mounted() {
    this.loadData()
    window.addEventListener('resize', this.resizeCharts)
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.resizeCharts)
    this.disposeCharts()
  },
  methods: {
    disposeCharts() {
      CHART_KEYS.forEach(key => {
        if (this[key]) {
          this[key].dispose()
          this[key] = null
        }
      })
    },
    resizeCharts() {
      CHART_KEYS.forEach(key => this[key] && this[key].resize())
    },
    normalizeMonthly(list) {
      if (!Array.isArray(list)) return []
      return list.map(row => ({
        month: pick(row, ['month', 'MONTH']) || '',
        amount: num(pick(row, ['income', 'amount', 'value', 'INCOME', 'AMOUNT'])),
        orders: num(pick(row, ['order_count', 'orderCount', 'ORDER_COUNT']))
      }))
    },
    normalizeType(list) {
      if (!Array.isArray(list)) return []
      return list.map(row => {
        const raw = String(pick(row, ['order_type', 'orderType', 'type', 'ORDER_TYPE']) || '其他').toUpperCase()
        const label = ORDER_TYPE_LABEL[raw] || ORDER_TYPE_LABEL[raw.toLowerCase()] || raw
        return {
          name: label,
          value: num(pick(row, ['income', 'amount', 'value', 'INCOME', 'AMOUNT']))
        }
      })
    },
    normalizeBooking(list) {
      if (!Array.isArray(list)) return []
      return list.map(row => {
        const st = pick(row, ['status', 'STATUS'])
        const name = BOOKING_STATUS[st] != null ? BOOKING_STATUS[st] : `状态${st}`
        return { name, value: num(pick(row, ['cnt', 'count', 'CNT'])) }
      })
    },
    normalizeActivity(list) {
      if (!Array.isArray(list)) return []
      return list.map(row => ({
        name: pick(row, ['name', 'NAME', 'title']) || '未命名',
        count: num(pick(row, ['count', 'COUNT', 'cnt', 'value']))
      }))
    },
    normalizeTopProducts(list) {
      if (!Array.isArray(list)) return []
      return list.map(row => ({
        name: pick(row, ['name', 'NAME']) || '—',
        income: num(pick(row, ['income', 'INCOME', 'amount'])),
        orders: num(pick(row, ['order_count', 'orderCount', 'ORDER_COUNT']))
      }))
    },
    loadData() {
      this.loading = true
      Promise.all([
        request.get('/api/statistics/overview').then(res => { this.overview = res.data || {} }).catch(() => { this.overview = {} }),
        request.get('/api/statistics/monthlyIncome').then(res => { this.monthlyData = this.normalizeMonthly(res.data) }).catch(() => { this.monthlyData = [] }),
        request.get('/api/statistics/incomeByType').then(res => { this.typeData = this.normalizeType(res.data) }).catch(() => { this.typeData = [] }),
        request.get('/api/statistics/bookingByStatus').then(res => { this.bookingData = this.normalizeBooking(res.data) }).catch(() => { this.bookingData = [] }),
        request.get('/api/statistics/activityParticipation').then(res => { this.activityData = this.normalizeActivity(res.data) }).catch(() => { this.activityData = [] }),
        request.get('/api/statistics/topProducts').then(res => { this.topProductData = this.normalizeTopProducts(res.data) }).catch(() => { this.topProductData = [] })
      ]).finally(() => {
        this.loading = false
        this.$nextTick(() => {
          this.disposeCharts()
          this.renderCombo()
          this.renderType()
          this.renderBooking()
          this.renderActivity()
          this.renderTopProducts()
        })
      })
    },
    emptyGraphic(text) {
      return {
        type: 'text',
        left: 'center',
        top: 'middle',
        style: { text, fill: '#909399', fontSize: 14, fontWeight: 500 }
      }
    },
    renderCombo() {
      const el = this.$refs.comboChart
      if (!el) return
      this.chartCombo = echarts.init(el)
      const months = this.monthlyData.map(d => d.month)
      const amounts = this.monthlyData.map(d => d.amount)
      const orders = this.monthlyData.map(d => d.orders)
      const empty = !months.length || (amounts.every(v => v === 0) && orders.every(v => v === 0))
      if (empty) {
        this.chartCombo.setOption({
          backgroundColor: 'transparent',
          graphic: this.emptyGraphic('暂无月度订单数据'),
          xAxis: { show: false },
          yAxis: { show: false },
          series: []
        })
        return
      }
      this.chartCombo.setOption({
        backgroundColor: 'transparent',
        color: PALETTE,
        tooltip: {
          trigger: 'axis',
          backgroundColor: 'rgba(255,255,255,0.96)',
          borderColor: '#e4e7ed',
          borderWidth: 1,
          textStyle: { color: '#303133' },
          axisPointer: { type: 'cross', crossStyle: { color: '#999' } }
        },
        legend: { data: ['实收金额(元)', '订单笔数'], top: 8, textStyle: { color: '#606266' } },
        grid: { left: 56, right: 56, bottom: 40, top: 52 },
        xAxis: {
          type: 'category',
          data: months,
          axisLine: { lineStyle: { color: '#dcdfe6' } },
          axisLabel: { color: '#909399' }
        },
        yAxis: [
          {
            type: 'value',
            name: '金额(元)',
            nameTextStyle: { color: '#909399' },
            splitLine: { lineStyle: { type: 'dashed', color: '#eef0f4' } },
            axisLabel: { color: '#909399', formatter: v => (v >= 10000 ? `${v / 10000}万` : v) }
          },
          {
            type: 'value',
            name: '订单数',
            nameTextStyle: { color: '#909399' },
            splitLine: { show: false },
            axisLabel: { color: '#909399' }
          }
        ],
        series: [
          {
            name: '实收金额(元)',
            type: 'bar',
            barMaxWidth: 36,
            itemStyle: {
              borderRadius: [8, 8, 0, 0],
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: '#7C9BFF' },
                { offset: 1, color: '#5B7CF9' }
              ])
            },
            data: amounts
          },
          {
            name: '订单笔数',
            type: 'line',
            yAxisIndex: 1,
            smooth: true,
            symbol: 'circle',
            symbolSize: 8,
            lineStyle: { width: 3, color: '#00C9A7' },
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: 'rgba(0,201,167,0.35)' },
                { offset: 1, color: 'rgba(0,201,167,0.02)' }
              ])
            },
            data: orders
          }
        ],
        animationDuration: 800,
        animationEasing: 'cubicOut'
      })
    },
    renderType() {
      const el = this.$refs.typeChart
      if (!el) return
      this.chartType = echarts.init(el)
      const pieData = this.typeData.filter(i => i.value > 0).map((d, i) => ({
        ...d,
        itemStyle: { color: PALETTE[i % PALETTE.length] }
      }))
      if (!pieData.length) {
        this.chartType.setOption({ graphic: this.emptyGraphic('暂无收入类型数据'), series: [] })
        return
      }
      this.chartType.setOption({
        backgroundColor: 'transparent',
        tooltip: {
          trigger: 'item',
          formatter: '{b}<br/>¥{c} ({d}%)',
          backgroundColor: 'rgba(255,255,255,0.96)',
          borderColor: '#e4e7ed',
          borderWidth: 1
        },
        series: [{
          type: 'pie',
          radius: ['38%', '68%'],
          roseType: 'area',
          avoidLabelOverlap: true,
          itemStyle: { borderRadius: 8, borderColor: '#fff', borderWidth: 2 },
          label: {
            color: '#606266',
            formatter: '{b}\n{d}%'
          },
          labelLine: { length: 14, length2: 10, lineStyle: { color: '#c0c4cc' } },
          data: pieData
        }],
        animationDuration: 1000,
        animationEasing: 'elasticOut'
      })
    },
    renderBooking() {
      const el = this.$refs.bookingChart
      if (!el) return
      this.chartBooking = echarts.init(el)
      const data = this.bookingData.filter(i => i.value > 0)
      if (!data.length) {
        this.chartBooking.setOption({ graphic: this.emptyGraphic('暂无预约数据'), series: [] })
        return
      }
      this.chartBooking.setOption({
        backgroundColor: 'transparent',
        tooltip: { trigger: 'item', formatter: '{b}: {c} 单 ({d}%)' },
        series: [{
          type: 'pie',
          radius: ['48%', '72%'],
          padAngle: 4,
          itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 3 },
          color: ['#E6A23C', '#67C23A', '#F56C6C'],
          label: { formatter: '{b}\n{c} 单' },
          data
        }],
        animationDuration: 900
      })
    },
    renderActivity() {
      const el = this.$refs.activityChart
      if (!el) return
      this.chartActivity = echarts.init(el)
      const names = this.activityData.map(i => i.name)
      const counts = this.activityData.map(i => i.count)
      if (!names.length) {
        this.chartActivity.setOption({ graphic: this.emptyGraphic('暂无活动报名数据'), series: [] })
        return
      }
      this.chartActivity.setOption({
        backgroundColor: 'transparent',
        tooltip: {
          trigger: 'axis',
          axisPointer: { type: 'shadow' },
          formatter: p => `${p[0].name}<br/>报名：${p[0].value} 人`
        },
        grid: { left: 8, right: 24, top: 16, bottom: 8, containLabel: true },
        xAxis: { type: 'value', splitLine: { lineStyle: { type: 'dashed', color: '#eef0f4' } }, axisLabel: { color: '#909399' } },
        yAxis: {
          type: 'category',
          data: names,
          axisLine: { show: false },
          axisTick: { show: false },
          axisLabel: { color: '#606266', width: 100, overflow: 'truncate', ellipsis: '…' }
        },
        series: [{
          type: 'bar',
          data: counts.map((v, i) => ({
            value: v,
            itemStyle: {
              borderRadius: [0, 8, 8, 0],
              color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
                { offset: 0, color: PALETTE[i % PALETTE.length] },
                { offset: 1, color: '#c9d6ff' }
              ])
            }
          })),
          barMaxWidth: 22
        }],
        animationDuration: 800
      })
    },
    renderTopProducts() {
      const el = this.$refs.topProductChart
      if (!el) return
      this.chartTopProduct = echarts.init(el)
      const list = this.topProductData.filter(i => i.income > 0)
      if (!list.length) {
        this.chartTopProduct.setOption({ graphic: this.emptyGraphic('暂无商品销售数据'), series: [] })
        return
      }
      const names = list.map(i => i.name)
      const values = list.map(i => i.income)
      this.chartTopProduct.setOption({
        backgroundColor: 'transparent',
        tooltip: {
          trigger: 'axis',
          formatter: p => {
            const i = p[0].dataIndex
            const row = list[i]
            return `${row.name}<br/>实收：¥${money(row.income)}<br/>订单数：${row.orders}`
          }
        },
        grid: { left: 8, right: 28, top: 12, bottom: 8, containLabel: true },
        xAxis: { type: 'value', splitLine: { lineStyle: { type: 'dashed', color: '#eef0f4' } }, axisLabel: { color: '#909399', formatter: v => (v >= 1000 ? `${(v / 1000).toFixed(1)}k` : v) } },
        yAxis: {
          type: 'category',
          data: names,
          axisLine: { show: false },
          axisTick: { show: false },
          axisLabel: { color: '#606266', width: 88, overflow: 'truncate', ellipsis: '…' }
        },
        series: [{
          type: 'bar',
          data: values.map((v, i) => ({
            value: v,
            itemStyle: {
              borderRadius: [0, 10, 10, 0],
              color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
                { offset: 0, color: '#764ba2' },
                { offset: 1, color: '#667eea' }
              ])
            }
          })),
          barMaxWidth: 20
        }],
        animationDuration: 800
      })
    }
  }
}
</script>

<style scoped>
.statistics-page {
  max-width: 1400px;
  margin: 0 auto;
  padding-bottom: 24px;
}

.kpi-row {
  margin-bottom: 16px;
}

.kpi-card {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 18px 20px;
  background: #fff;
  border-radius: 14px;
  border: 1px solid #eef0f4;
  box-shadow: 0 4px 20px rgba(15, 23, 42, 0.06);
  margin-bottom: 16px;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.kpi-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 28px rgba(15, 23, 42, 0.08);
}

.kpi-icon {
  width: 48px;
  height: 48px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 22px;
  flex-shrink: 0;
}

.kpi-label {
  font-size: 13px;
  color: #909399;
  margin-bottom: 6px;
}

.kpi-value {
  font-size: 22px;
  font-weight: 700;
  color: #303133;
  letter-spacing: 0.02em;
}

.chart-card {
  border-radius: 14px;
  border: 1px solid #eef0f4;
  margin-bottom: 16px;
  overflow: hidden;
}

.chart-card--hero {
  margin-bottom: 16px;
}

.chart-card >>> .el-card__header {
  border-bottom: 1px solid #f0f2f6;
  padding: 14px 20px;
  background: linear-gradient(180deg, #fafbff, #fff);
}

.card-title {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.title-main {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.title-sub {
  font-size: 12px;
  color: #909399;
}

.chart-row {
  margin-bottom: 0;
}

.chart-box {
  height: 320px;
}

.chart-box--lg {
  height: 380px;
}
</style>
