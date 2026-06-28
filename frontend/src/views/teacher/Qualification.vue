<template>
  <div>
    <el-card>
      <div slot="header" style="display:flex;justify-content:space-between;align-items:center">
        <span>我的资质</span>
        <el-button type="primary" size="small" @click="handleAdd">新增资质</el-button>
      </div>
      <el-table :data="list" border stripe v-loading="loading">
        <el-table-column prop="name" label="资质名称" />
        <el-table-column prop="type" label="类型" width="100" />
        <el-table-column prop="description" label="说明" show-overflow-tooltip />
        <el-table-column label="证书图片" width="100">
          <template slot-scope="scope">
            <img v-if="scope.row.imageUrl" :src="scope.row.imageUrl" style="width:60px;height:40px;object-fit:cover">
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="110" align="center">
          <template slot-scope="scope">
            <div class="table-action-btns">
              <el-button size="mini" @click="handleEdit(scope.row)">编辑</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <el-dialog :title="form.id?'编辑资质':'新增资质'" :visible.sync="dialogVisible" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="资质名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="类型">
          <el-select v-model="form.type" placeholder="选择类型">
            <el-option label="学历" value="SCHOOL" /><el-option label="等级" value="LEVEL" />
            <el-option label="荣誉" value="HONOR" /><el-option label="竞赛" value="COMPETITION" />
          </el-select>
        </el-form-item>
        <el-form-item label="说明"><el-input v-model="form.description" type="textarea" :rows="2" /></el-form-item>
        <el-form-item label="证书图片">
          <el-upload action="/api/upload" :show-file-list="false" :on-success="(r)=>{form.imageUrl=r.data}">
            <img v-if="form.imageUrl" :src="form.imageUrl" style="width:120px;height:80px;object-fit:cover">
            <el-button v-else size="small">上传图片</el-button>
          </el-upload>
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
    return { list: [], loading: false, dialogVisible: false, form: {} }
  },
  created() { this.loadData() },
  methods: {
    loadData() {
      this.loading = true
      const user = JSON.parse(sessionStorage.getItem('loginUser') || '{}')
      request.get('/api/qualification', { params: { pageNum: 1, pageSize: 999, teacherId: user.id } }).then(res => {
        this.list = res.data.records || res.data || []
      }).finally(() => { this.loading = false })
    },
    handleAdd() {
      const user = JSON.parse(sessionStorage.getItem('loginUser') || '{}')
      this.form = { teacherId: user.id }; this.dialogVisible = true
    },
    handleEdit(row) { this.form = { ...row }; this.dialogVisible = true },
    saveForm() {
      const api = this.form.id ? request.put('/api/qualification', this.form) : request.post('/api/qualification', this.form)
      api.then(() => { this.$message.success('操作成功'); this.dialogVisible = false; this.loadData() })
    }
  }
}
</script>
