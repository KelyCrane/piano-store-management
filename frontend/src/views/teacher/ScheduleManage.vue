<template>
  <div class="schedule-page">
    <el-row :gutter="20">
      <el-col :lg="16" :md="24">
        <el-card class="main-card" shadow="never">
          <div slot="header" class="card-header">
            <div>
              <div class="card-title">排课管理</div>
              <div class="card-desc">维护可预约时段，学生只能从这些时间里发起排课申请。</div>
            </div>
            <div class="header-actions">
              <el-button icon="el-icon-refresh" @click="reloadAll">刷新</el-button>
              <el-button icon="el-icon-delete" @click="clearExpiredSlots">清理过期</el-button>
              <el-button icon="el-icon-plus" @click="openBatchDialog">批量生成</el-button>
              <el-button type="primary" icon="el-icon-date" @click="openSlotDialog()">新增时段</el-button>
            </div>
          </div>

          <el-form :inline="true" :model="query" size="small" class="filter-form">
            <el-form-item label="日期范围">
              <el-date-picker
                v-model="query.dateRange"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                value-format="yyyy-MM-dd"
                unlink-panels
              />
            </el-form-item>
            <el-form-item label="启用状态">
              <el-select v-model="query.status" clearable placeholder="全部">
                <el-option label="启用" :value="1" />
                <el-option label="停用" :value="0" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" icon="el-icon-search" @click="loadSlots">查询</el-button>
            </el-form-item>
          </el-form>

          <el-table :data="slotList" border stripe v-loading="slotLoading">
            <el-table-column prop="slotTime" label="上课时间" min-width="170" />
            <el-table-column prop="duration" label="时长(分钟)" width="110" />
            <el-table-column prop="classroomName" label="教室" min-width="120" />
            <el-table-column prop="bookingCount" label="已占用" width="90" />
            <el-table-column label="状态" width="110">
              <template slot-scope="scope">
                <el-tag :type="slotStateTag(scope.row)" size="small">
                  {{ scope.row.slotStateText || statusLabel(scope.row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="启用" width="90">
              <template slot-scope="scope">
                <el-tag :type="scope.row.status === 1 ? 'success' : 'info'" size="small">
                  {{ scope.row.status === 1 ? '启用' : '停用' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="remark" label="备注" show-overflow-tooltip />
            <el-table-column label="操作" width="180" align="center">
              <template slot-scope="scope">
                <div class="table-action-btns">
                  <el-button size="mini" @click="openSlotDialog(scope.row)">编辑</el-button>
                  <el-button size="mini" type="danger" @click="removeSlot(scope.row)">删除</el-button>
                </div>
              </template>
            </el-table-column>
          </el-table>

          <el-pagination
            class="pager"
            background
            layout="total, prev, pager, next"
            :total="slotTotal"
            :page-size="query.pageSize"
            :current-page.sync="query.pageNum"
            @current-change="loadSlots"
          />
        </el-card>
      </el-col>

      <el-col :lg="8" :md="24">
        <el-card class="side-card" shadow="never">
          <div slot="header" class="side-head">
            <span>已通过预约</span>
            <el-button type="text" @click="loadApprovedBookings">刷新</el-button>
          </div>

          <el-table :data="approvedBookings" border stripe size="small" v-loading="bookingLoading">
            <el-table-column prop="classTime" label="上课时间" min-width="150" />
            <el-table-column prop="studentName" label="学生" width="100" />
            <el-table-column prop="courseName" label="课程" min-width="120" />
            <el-table-column prop="packageName" label="课时包" min-width="120" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <el-dialog :title="slotForm.id ? '编辑排课时段' : '新增排课时段'" :visible.sync="slotDialogVisible" width="520px">
      <el-form :model="slotForm" label-width="90px">
        <el-form-item label="上课时间">
          <el-date-picker
            v-model="slotForm.slotTime"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            placeholder="选择日期时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="教室">
          <el-select v-model="slotForm.classroomId" clearable placeholder="请选择教室" style="width: 100%">
            <el-option
              v-for="classroom in classrooms"
              :key="classroom.id"
              :label="classroom.name"
              :value="classroom.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="时长">
          <el-input-number v-model="slotForm.duration" :min="30" :step="15" style="width: 180px" />
        </el-form-item>
        <el-form-item label="启用状态">
          <el-switch v-model="slotForm.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="slotForm.remark" type="textarea" :rows="3" placeholder="例如：可补课、仅限钢琴课" />
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="slotDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveSlot">保存</el-button>
      </span>
    </el-dialog>

    <el-dialog title="批量生成排课时段" :visible.sync="batchDialogVisible" width="600px">
      <el-form :model="batchForm" label-width="100px">
        <el-form-item label="开始日期">
          <el-date-picker v-model="batchForm.startDate" type="date" value-format="yyyy-MM-dd" placeholder="选择开始日期" style="width: 100%" />
        </el-form-item>
        <el-form-item label="结束日期">
          <el-date-picker v-model="batchForm.endDate" type="date" value-format="yyyy-MM-dd" placeholder="选择结束日期" style="width: 100%" />
        </el-form-item>
        <el-form-item label="重复星期">
          <el-checkbox-group v-model="batchForm.weekdays">
            <el-checkbox v-for="item in weekdayOptions" :key="item.value" :label="item.value">{{ item.label }}</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item label="时间点">
          <el-input
            v-model="batchForm.timePointsText"
            type="textarea"
            :rows="4"
            placeholder="每行一个时间点，例如：&#10;09:00&#10;10:30&#10;14:00"
          />
        </el-form-item>
        <el-form-item label="教室">
          <el-select v-model="batchForm.classroomId" clearable placeholder="请选择教室" style="width: 100%">
            <el-option
              v-for="classroom in classrooms"
              :key="classroom.id"
              :label="classroom.name"
              :value="classroom.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="时长">
          <el-input-number v-model="batchForm.duration" :min="30" :step="15" style="width: 180px" />
        </el-form-item>
        <el-form-item label="启用状态">
          <el-switch v-model="batchForm.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="批量备注">
          <el-input v-model="batchForm.remark" type="textarea" :rows="3" placeholder="例如：工作日晚间常规时段" />
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="batchDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitBatch">生成时段</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import request from '@/utils/request'

const weekdayOptions = [
  { label: '周一', value: 1 },
  { label: '周二', value: 2 },
  { label: '周三', value: 3 },
  { label: '周四', value: 4 },
  { label: '周五', value: 5 },
  { label: '周六', value: 6 },
  { label: '周日', value: 7 }
]

export default {
  name: 'TeacherScheduleManage',
  data() {
    return {
      weekdayOptions,
      classrooms: [],
      slotList: [],
      slotTotal: 0,
      approvedBookings: [],
      slotLoading: false,
      bookingLoading: false,
      slotDialogVisible: false,
      batchDialogVisible: false,
      query: {
        pageNum: 1,
        pageSize: 10,
        status: '',
        dateRange: []
      },
      slotForm: {},
      batchForm: {}
    }
  },
  computed: {
    currentUser() {
      return JSON.parse(sessionStorage.getItem('loginUser') || '{}')
    }
  },
  created() {
    this.resetSlotForm()
    this.resetBatchForm()
    this.loadClassrooms()
    this.reloadAll()
  },
  methods: {
    reloadAll() {
      this.loadSlots()
      this.loadApprovedBookings()
    },
    loadClassrooms() {
      request.get('/api/classroom', {
        params: {
          pageNum: 1,
          pageSize: 999,
          status: 1
        }
      }).then(res => {
        this.classrooms = res.data.records || []
      })
    },
    loadSlots() {
      this.slotLoading = true
      const params = {
        pageNum: this.query.pageNum,
        pageSize: this.query.pageSize,
        teacherId: this.currentUser.id
      }
      if (this.query.status !== '' && this.query.status !== null && this.query.status !== undefined) {
        params.status = this.query.status
      }
      if (this.query.dateRange && this.query.dateRange.length === 2) {
        params.dateFrom = this.query.dateRange[0]
        params.dateTo = this.query.dateRange[1]
      }
      request.get('/api/teacherSlot', { params }).then(res => {
        this.slotList = res.data.records || []
        this.slotTotal = res.data.total || 0
      }).finally(() => {
        this.slotLoading = false
      })
    },
    loadApprovedBookings() {
      this.bookingLoading = true
      request.get('/api/booking', {
        params: {
          pageNum: 1,
          pageSize: 20,
          teacherId: this.currentUser.id,
          status: 1,
          sortMode: 'schedule'
        }
      }).then(res => {
        this.approvedBookings = res.data.records || []
      }).finally(() => {
        this.bookingLoading = false
      })
    },
    resetSlotForm() {
      this.slotForm = {
        teacherId: this.currentUser.id,
        classroomId: '',
        slotTime: '',
        duration: 60,
        status: 1,
        remark: ''
      }
    },
    resetBatchForm() {
      this.batchForm = {
        startDate: '',
        endDate: '',
        weekdays: [1, 2, 3, 4, 5],
        timePointsText: '09:00\n10:30\n14:00',
        classroomId: '',
        duration: 60,
        status: 1,
        remark: ''
      }
    },
    openSlotDialog(row) {
      if (row) {
        this.slotForm = {
          id: row.id,
          teacherId: row.teacherId,
          classroomId: row.classroomId || '',
          slotTime: row.slotTime,
          duration: row.duration || 60,
          status: row.status,
          remark: row.remark || ''
        }
      } else {
        this.resetSlotForm()
      }
      this.slotDialogVisible = true
    },
    saveSlot() {
      if (!this.slotForm.slotTime) {
        this.$message.warning('请选择上课时间')
        return
      }
      const payload = {
        ...this.slotForm,
        teacherId: this.currentUser.id
      }
      const api = payload.id
        ? request.put('/api/teacherSlot', payload)
        : request.post('/api/teacherSlot', payload)
      api.then(() => {
        this.$message.success('时段保存成功')
        this.slotDialogVisible = false
        this.loadSlots()
      })
    },
    removeSlot(row) {
      this.$confirm('确定删除该排课时段吗？', '提示', { type: 'warning' }).then(() => {
        request.delete(`/api/teacherSlot/${row.id}`).then(() => {
          this.$message.success('删除成功')
          this.loadSlots()
        })
      }).catch(() => {})
    },
    openBatchDialog() {
      this.resetBatchForm()
      this.batchDialogVisible = true
    },
    parseTimePoints() {
      return (this.batchForm.timePointsText || '')
        .split(/[\n,，\s]+/)
        .map(item => item.trim())
        .filter(Boolean)
        .map(item => {
          const parts = item.split(':')
          if (parts.length !== 2) {
            return item
          }
          const hour = `${Number(parts[0])}`.padStart(2, '0')
          const minute = `${Number(parts[1])}`.padStart(2, '0')
          return `${hour}:${minute}`
        })
    },
    submitBatch() {
      const timePoints = this.parseTimePoints()
      if (!this.batchForm.startDate || !this.batchForm.endDate) {
        this.$message.warning('请选择开始和结束日期')
        return
      }
      if (!this.batchForm.weekdays.length) {
        this.$message.warning('请至少选择一个重复星期')
        return
      }
      if (!timePoints.length) {
        this.$message.warning('请至少填写一个时间点')
        return
      }
      request.post('/api/teacherSlot/batch', {
        teacherId: this.currentUser.id,
        classroomId: this.batchForm.classroomId || null,
        startDate: this.batchForm.startDate,
        endDate: this.batchForm.endDate,
        weekdays: this.batchForm.weekdays,
        timePoints,
        duration: this.batchForm.duration,
        status: this.batchForm.status,
        remark: this.batchForm.remark
      }).then(res => {
        const data = res.data || {}
        this.$message.success(`批量生成完成，新增 ${data.created || 0} 个，跳过 ${data.skipped || 0} 个`)
        this.batchDialogVisible = false
        this.loadSlots()
      })
    },
    clearExpiredSlots() {
      this.$confirm('确定清理没有预约占用的过期排课时段吗？', '提示', { type: 'warning' }).then(() => {
        request.delete('/api/teacherSlot/expired', {
          params: { teacherId: this.currentUser.id }
        }).then(res => {
          const data = res.data || {}
          this.$message.success(`清理完成，删除 ${data.deleted || 0} 个，保留 ${data.skipped || 0} 个已有预约时段`)
          this.loadSlots()
        })
      }).catch(() => {})
    },
    statusLabel(status) {
      return status === 1 ? '启用' : '停用'
    },
    slotStateTag(row) {
      if (row.slotState === 0) return 'success'
      if (row.slotState === 1) return 'danger'
      if (row.slotState === 4) return 'warning'
      return row.status === 1 ? 'success' : 'info'
    }
  }
}
</script>

<style scoped>
.schedule-page {
  max-width: 1320px;
  margin: 0 auto;
}

.main-card,
.side-card {
  border-radius: 16px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
}

.card-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.card-desc {
  margin-top: 4px;
  font-size: 13px;
  color: #909399;
}

.header-actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.filter-form {
  margin-bottom: 12px;
}

.side-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-weight: 600;
}

.pager {
  margin-top: 16px;
  text-align: right;
}
</style>
