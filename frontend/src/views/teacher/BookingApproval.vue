<template>
  <div class="booking-approval-page">
    <el-card shadow="never" class="main-card">
      <div slot="header" class="card-head">预约审批</div>
      <el-form :inline="true" :model="query" size="small" class="filter-form">
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable>
            <el-option label="待审批" :value="0" /><el-option label="已通过" :value="1" /><el-option label="已拒绝" :value="2" />
            <el-option label="已取消" :value="3" />
            <el-option label="已过期" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item><el-button type="primary" icon="el-icon-search" @click="loadData">搜索</el-button></el-form-item>
      </el-form>
      <el-table :data="list" border stripe v-loading="loading">
        <el-table-column prop="studentName" label="学生" min-width="100" />
        <el-table-column prop="courseName" label="课程" min-width="120" />
        <el-table-column prop="packageName" label="课时包" min-width="120" />
        <el-table-column prop="hoursCost" label="扣课时" width="80" />
        <el-table-column prop="classTime" label="上课时间" width="168" />
        <el-table-column prop="remark" label="备注" show-overflow-tooltip min-width="100" />
        <el-table-column prop="status" label="状态" width="88">
          <template slot-scope="scope">
            <el-tag :type="statusTag(scope.row.status)" size="small">
              {{ statusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="300" align="center">
          <template slot-scope="scope">
            <div class="table-action-btns">
              <el-button size="mini" type="primary" plain icon="el-icon-chat-dot-round" @click="openConsult(scope.row)">咨询</el-button>
              <template v-if="scope.row.status===0">
                <el-button size="mini" type="success" @click="approve(scope.row,1)">通过</el-button>
                <el-button size="mini" type="danger" @click="approve(scope.row,2)">拒绝</el-button>
              </template>
              <span v-else class="done-hint">已处理</span>
              <el-button size="mini" type="danger" plain @click="remove(scope.row)">删除</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        class="pager"
        background
        layout="total, prev, pager, next"
        :total="total"
        :page-size="query.pageSize"
        :current-page.sync="query.pageNum"
        @current-change="loadData"
      />
    </el-card>

    <consult-teacher-dialog :visible.sync="consultVisible" user-role="teacher" :course="consultCourse" />
  </div>
</template>

<script>
import request from '@/utils/request'
import ConsultTeacherDialog from '@/components/ConsultTeacherDialog.vue'

export default {
  components: { ConsultTeacherDialog },
  data() {
    return {
      list: [],
      total: 0,
      loading: false,
      query: { pageNum: 1, pageSize: 10, status: '' },
      consultVisible: false,
      consultCourse: {}
    }
  },
  created() {
    this.loadData()
  },
  methods: {
    loadData() {
      this.loading = true
      const user = JSON.parse(sessionStorage.getItem('loginUser') || '{}')
      request.get('/api/booking', { params: { ...this.query, teacherId: user.id } }).then(res => {
        this.list = res.data.records || []
        this.total = res.data.total || 0
      }).catch(() => {}).finally(() => { this.loading = false })
    },
    openConsult(row) {
      this.consultCourse = {
        courseId: row.courseId,
        id: row.courseId,
        teacherId: row.teacherId,
        studentId: row.studentId,
        studentName: row.studentName,
        name: row.courseName
      }
      this.consultVisible = true
    },
    approve(row, status) {
      const msg = status === 1 ? '确定通过该预约?' : '确定拒绝该预约?'
      this.$confirm(msg, '提示').then(() => {
        request.put('/api/booking', { ...row, status }).then(() => {
          this.$message.success('操作成功')
          this.loadData()
        }).catch(() => {})
      }).catch(() => {})
    },
    statusText(status) {
      if (status === 1) return '已通过'
      if (status === 2) return '已拒绝'
      if (status === 3) return '已取消'
      if (status === 4) return '已过期'
      return '待审批'
    },
    statusTag(status) {
      if (status === 1) return 'success'
      if (status === 2) return 'danger'
      if (status === 3) return 'info'
      if (status === 4) return 'info'
      return 'warning'
    },
    remove(row) {
      this.$confirm('确定删除该预约?', '提示', { type: 'warning' }).then(() => {
        request.delete('/api/booking/' + row.id).then(() => {
          this.$message.success('删除成功')
          this.loadData()
        }).catch(() => {})
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
.booking-approval-page {
  max-width: 1200px;
  margin: 0 auto;
}
.main-card {
  border-radius: 12px;
}
.card-head {
  font-weight: 600;
  font-size: 15px;
}
.filter-form {
  margin-bottom: 12px;
}
.pager {
  margin-top: 16px;
  text-align: right;
}
.done-hint {
  color: #c0c4cc;
  font-size: 12px;
  padding: 0 4px;
}
</style>
