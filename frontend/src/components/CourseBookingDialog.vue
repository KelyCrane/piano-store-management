<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="申请排课"
    width="760px"
    append-to-body
    @opened="handleOpened"
  >
    <div v-loading="loading">
      <el-alert
        title="请先选择课时包，再从教师开放的排课时段中勾选一个或多个时间提交预约。"
        type="info"
        :closable="false"
        show-icon
        style="margin-bottom: 16px"
      />

      <el-form :model="form" label-width="90px">
        <el-form-item label="课程">
          <el-input :value="course.name" disabled />
        </el-form-item>
        <el-form-item label="授课教师">
          <el-input :value="course.teacherName || '待定'" disabled />
        </el-form-item>
        <el-form-item label="课时包">
          <el-select
            v-model="form.studentPackageId"
            placeholder="请选择课时包"
            style="width: 100%"
            @change="handlePackageChange"
          >
            <el-option
              v-for="pkg in packageOptions"
              :key="pkg.id"
              :label="packageLabel(pkg)"
              :value="pkg.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="批量备注">
          <el-input
            v-model="form.remark"
            type="textarea"
            :rows="2"
            placeholder="可填写补课、调课说明"
          />
        </el-form-item>
      </el-form>

      <div class="slot-toolbar">
        <div class="slot-summary">
          <span>教师可排课时间</span>
          <span v-if="currentPackage" class="summary-detail">
            当前课时包：剩余 {{ currentPackage.remainingHours || 0 }} 课时，可排
            {{ currentPackage.availableHours || 0 }} 课时，已预留 {{ currentPackage.reservedHours || 0 }} 课时
          </span>
        </div>
        <el-button size="mini" icon="el-icon-refresh" @click="loadSlots">刷新时段</el-button>
      </div>

      <el-empty
        v-if="!slotOptions.length"
        description="当前没有可预约时段，请联系教师先维护排课时间。"
      />

      <el-table
        v-else
        ref="slotTable"
        :data="slotOptions"
        border
        stripe
        max-height="320"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" :selectable="slotSelectable" />
        <el-table-column prop="slotTime" label="上课时间" min-width="170" />
        <el-table-column prop="duration" label="时长(分钟)" width="100" />
        <el-table-column prop="classroomName" label="教室" min-width="120" />
        <el-table-column prop="slotStateText" label="状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="slotTagType(scope.row)" size="small">{{ scope.row.slotStateText }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
      </el-table>

      <div class="footer-summary">
        <span>已选择 {{ selectedRows.length }} 个时段</span>
        <span v-if="currentPackage">当前最多可预约 {{ currentPackage.availableHours || 0 }} 节</span>
      </div>
    </div>

    <span slot="footer">
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" :loading="submitting" :disabled="loading" @click="submitBooking">确认排课</el-button>
    </span>
  </el-dialog>
</template>

<script>
import request from '@/utils/request'

export default {
  name: 'CourseBookingDialog',
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
      loading: false,
      submitting: false,
      form: {
        studentPackageId: '',
        remark: ''
      },
      packageOptions: [],
      slotOptions: [],
      selectedRows: []
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
    currentUser() {
      return JSON.parse(sessionStorage.getItem('loginUser') || '{}')
    },
    currentPackage() {
      return this.packageOptions.find(item => item.id === this.form.studentPackageId) || null
    }
  },
  watch: {
    dialogVisible(value) {
      if (!value) {
        this.resetState()
      }
    }
  },
  methods: {
    handleOpened() {
      this.initialize()
    },
    initialize() {
      if (!this.course.id) {
        return
      }
      this.loading = true
      this.selectedRows = []
      Promise.all([
        this.loadPackageOptions(),
        this.loadSlots()
      ]).finally(() => {
        this.loading = false
      })
    },
    resetState() {
      this.form = {
        studentPackageId: '',
        remark: ''
      }
      this.packageOptions = []
      this.slotOptions = []
      this.selectedRows = []
      this.submitting = false
      if (this.$refs.slotTable) {
        this.$refs.slotTable.clearSelection()
      }
    },
    loadPackageOptions() {
      return request.get('/api/studentPackage', {
        params: {
          pageNum: 1,
          pageSize: 999,
          studentId: this.currentUser.id,
          courseId: this.course.id,
          status: 1
        }
      }).then(res => {
        const records = res.data.records || []
        this.packageOptions = records.filter(item => Number(item.availableHours || 0) > 0)
        if (!this.packageOptions.length) {
          this.form.studentPackageId = ''
          return
        }
        const exists = this.packageOptions.some(item => item.id === this.form.studentPackageId)
        this.form.studentPackageId = exists ? this.form.studentPackageId : this.packageOptions[0].id
      })
    },
    loadSlots() {
      if (!this.course.teacherId) {
        this.slotOptions = []
        return Promise.resolve()
      }
      return request.get('/api/teacherSlot', {
        params: {
          pageNum: 1,
          pageSize: 500,
          teacherId: this.course.teacherId,
          status: 1,
          studentId: this.currentUser.id,
          dateFrom: this.today()
        }
      }).then(res => {
        const records = res.data.records || []
        this.slotOptions = records.filter(item => item.slotState !== 2)
      })
    },
    today() {
      const date = new Date()
      const year = date.getFullYear()
      const month = `${date.getMonth() + 1}`.padStart(2, '0')
      const day = `${date.getDate()}`.padStart(2, '0')
      return `${year}-${month}-${day}`
    },
    packageLabel(pkg) {
      return `${pkg.name}（剩余 ${pkg.remainingHours || 0} 课时，可排 ${pkg.availableHours || 0} 课时，已预留 ${pkg.reservedHours || 0} 课时）`
    },
    handlePackageChange() {
      const limit = this.currentPackage ? Number(this.currentPackage.availableHours || 0) : 0
      if (!limit && this.selectedRows.length) {
        this.selectedRows = []
        if (this.$refs.slotTable) {
          this.$refs.slotTable.clearSelection()
        }
        return
      }
      if (limit > 0 && this.selectedRows.length > limit) {
        this.$message.warning(`当前课时包最多还能预约 ${limit} 节`)
        const keptRows = this.selectedRows.slice(0, limit)
        this.selectedRows = keptRows
        this.$nextTick(() => {
          if (!this.$refs.slotTable) {
            return
          }
          this.$refs.slotTable.clearSelection()
          keptRows.forEach(row => this.$refs.slotTable.toggleRowSelection(row, true))
        })
      }
    },
    handleSelectionChange(rows) {
      const limit = this.currentPackage ? Number(this.currentPackage.availableHours || 0) : 0
      if (limit > 0 && rows.length > limit) {
        this.$message.warning(`当前课时包最多还能预约 ${limit} 节`)
        const keptRows = rows.slice(0, limit)
        this.selectedRows = keptRows
        this.$nextTick(() => {
          if (!this.$refs.slotTable) {
            return
          }
          this.$refs.slotTable.clearSelection()
          keptRows.forEach(row => this.$refs.slotTable.toggleRowSelection(row, true))
        })
        return
      }
      this.selectedRows = rows
    },
    slotSelectable(row) {
      return !!row.bookable
    },
    slotTagType(row) {
      if (row.slotState === 0) return 'success'
      if (row.slotState === 1) return 'danger'
      if (row.slotState === 4) return 'warning'
      return 'info'
    },
    submitBooking() {
      if (!this.course.id || !this.course.teacherId) {
        this.$message.warning('课程或教师信息不完整')
        return
      }
      if (!this.form.studentPackageId) {
        this.$message.warning('当前没有可用课时包，请先购买')
        return
      }
      if (!this.selectedRows.length) {
        this.$message.warning('请至少选择一个上课时段')
        return
      }
      const availableHours = this.currentPackage ? Number(this.currentPackage.availableHours || 0) : 0
      if (this.selectedRows.length > availableHours) {
        this.$message.warning(`当前课时包最多还能预约 ${availableHours} 节`)
        return
      }
      if (this.submitting) {
        return
      }
      this.submitting = true
      request.post('/api/booking/batch', {
        studentId: this.currentUser.id,
        teacherId: this.course.teacherId,
        courseId: this.course.id,
        studentPackageId: this.form.studentPackageId,
        hoursCost: 1,
        remark: this.form.remark,
        classTimes: this.selectedRows.map(item => item.slotTime)
      }).then(() => {
        this.$message.success('排课申请已提交')
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
.slot-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  gap: 12px;
}

.slot-summary {
  display: flex;
  flex-direction: column;
  gap: 4px;
  font-weight: 600;
  color: #303133;
}

.summary-detail {
  font-size: 12px;
  color: #909399;
  font-weight: 400;
}

.footer-summary {
  display: flex;
  justify-content: space-between;
  margin-top: 12px;
  color: #606266;
  font-size: 13px;
}
</style>
