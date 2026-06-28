<template>
  <div>
    <el-card>
      <div slot="header" style="display:flex;justify-content:space-between;align-items:center">
        <span>活动管理</span>
        <el-button type="primary" size="small" @click="handleAdd">新增活动</el-button>
      </div>
      <el-table :data="list" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="活动名称" />
        <el-table-column prop="type" label="类型" width="80" />
        <el-table-column prop="startTime" label="开始时间" width="160" />
        <el-table-column prop="endTime" label="结束时间" width="160" />
        <el-table-column prop="maxParticipants" label="人数上限" width="80" />
        <el-table-column prop="status" label="状态" width="80">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status===1?'success':scope.row.status===2?'info':'warning'" size="small">
              {{ scope.row.status===0?'未开始':scope.row.status===1?'进行中':'已结束' }}
            </el-tag>
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
    <el-dialog :title="form.id?'编辑活动':'新增活动'" :visible.sync="dialogVisible" width="600px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="活动名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="类型">
          <el-select v-model="form.type" placeholder="选择类型">
            <el-option label="考级" value="考级" /><el-option label="比赛" value="比赛" />
            <el-option label="演出" value="演出" /><el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="开始时间"><el-date-picker v-model="form.startTime" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" /></el-form-item>
        <el-form-item label="结束时间"><el-date-picker v-model="form.endTime" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" /></el-form-item>
        <el-form-item label="地点"><el-input v-model="form.location" /></el-form-item>
        <el-form-item label="人数上限"><el-input-number v-model="form.maxParticipants" :min="1" /></el-form-item>
        <el-form-item label="费用"><el-input-number v-model="form.fee" :min="0" :precision="2" /></el-form-item>
        <el-form-item label="封面">
          <el-upload action="/api/upload" :show-file-list="false" :on-success="(r)=>{form.coverImage=r.data}">
            <img v-if="form.coverImage" :src="form.coverImage" style="width:120px;height:80px;object-fit:cover">
            <el-button v-else size="small">上传图片</el-button>
          </el-upload>
        </el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status">
            <el-option label="未开始" :value="0" /><el-option label="进行中" :value="1" /><el-option label="已结束" :value="2" />
          </el-select>
        </el-form-item>
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
      query: { pageNum: 1, pageSize: 10 },
      form: {}
    }
  },
  created() { this.loadData() },
  methods: {
    loadData() {
      this.loading = true
      request.get('/api/activity', { params: this.query }).then(res => {
        this.list = res.data.records; this.total = res.data.total
      }).finally(() => { this.loading = false })
    },
    handleAdd() { this.form = { status: 0, maxParticipants: 50, fee: 0, title: '' }; this.dialogVisible = true },
    handleEdit(row) { this.form = { ...row }; this.dialogVisible = true },
    saveForm() {
      if (this.form.startTime && this.form.endTime && new Date(this.form.endTime).getTime() < new Date(this.form.startTime).getTime()) {
        this.$message.warning('结束时间不能早于开始时间')
        return
      }
      const payload = { ...this.form, title: this.form.title || this.form.name }
      const api = payload.id ? request.put('/api/activity', payload) : request.post('/api/activity', payload)
      api.then(() => { this.$message.success('操作成功'); this.dialogVisible = false; this.loadData() })
        .catch(() => {})
    },
    handleDel(row) {
      this.$confirm('确定删除?', '提示', { type: 'warning' }).then(() => {
        request.delete('/api/activity/' + row.id).then(() => { this.$message.success('删除成功'); this.loadData() })
      }).catch(() => {})
    }
  }
}
</script>
