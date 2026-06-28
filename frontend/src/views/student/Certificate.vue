<template>
  <div>
    <el-card>
      <div slot="header">我的证书</div>
      <el-row :gutter="16">
        <el-col :span="8" v-for="c in list" :key="c.id">
          <el-card shadow="hover" style="margin-bottom:16px;text-align:center">
            <img v-if="c.imageUrl" :src="c.imageUrl" style="width:100%;height:200px;object-fit:contain;background:#f5f5f5">
            <div style="padding:10px 0">
              <h4 style="margin:0">{{ c.name }}</h4>
              <div style="color:#999;font-size:13px;margin-top:4px">类型：{{ c.type }}</div>
              <div style="color:#999;font-size:13px">获得日期：{{ c.obtainTime }}</div>
              <div v-if="c.remark" style="color:#999;font-size:13px">备注：{{ c.remark }}</div>
            </div>
          </el-card>
        </el-col>
      </el-row>
      <el-empty v-if="!list.length" description="暂无证书" />
    </el-card>
  </div>
</template>
<script>
import request from '@/utils/request'
export default {
  data() { return { list: [] } },
  created() { this.loadData() },
  methods: {
    loadData() {
      const user = JSON.parse(sessionStorage.getItem('loginUser') || '{}')
      request.get('/api/certificate', { params: { pageNum: 1, pageSize: 999, studentId: user.id } }).then(res => {
        this.list = res.data.records || []
      })
    }
  }
}
</script>
