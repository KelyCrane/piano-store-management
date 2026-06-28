<template>
  <div>
    <el-card>
      <div slot="header">报名管理</div>
      <el-form :inline="true" :model="query" size="small" style="margin-bottom:10px">
        <el-form-item label="活动">
          <el-select v-model="query.activityId" placeholder="全部" clearable filterable>
            <el-option v-for="a in activities" :key="a.id" :label="a.name" :value="a.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="活动类型">
          <el-select v-model="query.activityType" placeholder="全部" clearable>
            <el-option label="考级" value="考级" />
            <el-option label="比赛" value="比赛" />
            <el-option label="演出" value="演出" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable>
            <el-option label="待审核" :value="0" /><el-option label="已通过" :value="1" /><el-option label="已拒绝" :value="2" />
            <el-option label="已取消" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item><el-button type="primary" @click="loadData">搜索</el-button></el-form-item>
      </el-form>
      <el-table :data="list" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="activityTitle" label="活动" />
        <el-table-column prop="activityType" label="类型" width="80" />
        <el-table-column prop="studentName" label="报名学生" />
        <el-table-column prop="createTime" label="报名时间" width="160" />
        <el-table-column prop="feePaid" label="缴费" width="80">
          <template slot-scope="scope">
            <el-tag :type="scope.row.feePaid === 1 ? 'success' : 'warning'" size="small">
              {{ scope.row.feePaid === 1 ? '已缴费' : '待缴费' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template slot-scope="scope">
            <el-tag :type="statusTag(scope.row.status)" size="small">
              {{ statusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="awardLevel" label="获奖等级" width="100" />
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
        <el-table-column label="操作" width="300" align="center">
          <template slot-scope="scope">
            <div class="table-action-btns">
              <el-button v-if="scope.row.status===0" size="mini" type="success" @click="approve(scope.row,1)">通过</el-button>
              <el-button v-if="scope.row.status===0" size="mini" type="danger" @click="approve(scope.row,2)">拒绝</el-button>
              <el-button v-if="canEditResult(scope.row)" size="mini" @click="editResult(scope.row)">录入成绩</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination style="margin-top:15px;text-align:right" background layout="total, prev, pager, next"
        :total="total" :page-size="query.pageSize" :current-page.sync="query.pageNum" @current-change="loadData" />
    </el-card>
    <el-dialog title="录入成绩" :visible.sync="resultDialog" width="500px">
      <el-form :model="resultForm" label-width="80px">
        <el-form-item label="是否通过">
          <el-radio-group v-model="resultForm.passed">
            <el-radio :label="1">通过</el-radio><el-radio :label="0">未通过</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="名次"><el-input-number v-model="resultForm.rank" :min="0" /></el-form-item>
        <el-form-item label="获奖等级"><el-input v-model="resultForm.awardLevel" placeholder="如：金奖、银奖、铜奖" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="resultForm.remark" type="textarea" :rows="2" /></el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="resultDialog=false">取消</el-button>
        <el-button type="primary" @click="saveResult">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>
<script>
import request from '@/utils/request'
export default {
  data() {
    return {
      list: [], total: 0, loading: false, resultDialog: false,
      query: { pageNum: 1, pageSize: 10, activityId: '', activityType: '', status: '' },
      resultForm: {}, activities: []
    }
  },
  created() { this.loadData(); this.loadActivities() },
  methods: {
    loadData() {
      this.loading = true
      request.get('/api/registration', { params: this.query }).then(res => {
        this.list = res.data.records; this.total = res.data.total
      }).finally(() => { this.loading = false })
    },
    loadActivities() {
      request.get('/api/activity', { params: { pageNum: 1, pageSize: 999 } }).then(res => { this.activities = res.data.records || [] })
    },
    approve(row, status) {
      request.put('/api/registration', { ...row, status }).then(() => { this.$message.success('操作成功'); this.loadData() })
    },
    statusText(status) {
      if (status === 1) return '已通过'
      if (status === 2) return '已拒绝'
      if (status === 3) return '已取消'
      return '待审核'
    },
    statusTag(status) {
      if (status === 1) return 'success'
      if (status === 2) return 'danger'
      if (status === 3) return 'info'
      return 'warning'
    },
    canEditResult(row) {
      return row.status === 1 && row.feePaid === 1
    },
    editResult(row) {
      if (row.status !== 1) {
        this.$message.warning('仅已通过报名可录入成绩')
        return
      }
      if (row.feePaid !== 1) {
        this.$message.warning('报名未缴费，不能录入成绩')
        return
      }
      this.resultForm = { ...row }
      this.resultDialog = true
    },
    saveResult() {
      request.put('/api/registration', this.resultForm).then(() => {
        this.$message.success('保存成功'); this.resultDialog = false; this.loadData()
      })
    }
  }
}
</script>
