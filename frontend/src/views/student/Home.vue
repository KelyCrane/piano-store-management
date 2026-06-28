<template>
  <div>
    <!-- 轮播图 -->
    <el-carousel height="300px" v-if="banners.length">
      <el-carousel-item v-for="b in banners" :key="b.id">
        <img :src="b.imageUrl" style="width:100%;height:300px;object-fit:cover" />
      </el-carousel-item>
    </el-carousel>
    <!-- 热门课程 -->
    <h3 style="margin:20px 0 10px">热门课程</h3>
    <el-row :gutter="16">
      <el-col :span="6" v-for="c in courses" :key="c.id">
        <el-card shadow="hover" style="margin-bottom:16px;cursor:pointer" @click.native="$router.push('/student/course/' + c.id)">
          <img v-if="c.coverImage" :src="c.coverImage" style="width:100%;height:120px;object-fit:cover">
          <div style="padding:10px 0">
            <div style="font-weight:bold">{{ c.name }}</div>
            <div style="color:#999;font-size:13px;margin-top:4px">教师：{{ c.teacherName }}</div>
            <div style="color:#E6A23C;margin-top:4px">¥{{ c.price }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <!-- 最新活动 -->
    <h3 style="margin:20px 0 10px">最新活动</h3>
    <el-row :gutter="16">
      <el-col :span="8" v-for="a in activities" :key="a.id">
        <el-card shadow="hover" style="margin-bottom:16px;cursor:pointer" @click.native="$router.push('/student/activity')">
          <img v-if="a.coverImage" :src="a.coverImage" style="width:100%;height:120px;object-fit:cover">
          <div style="padding:10px 0">
            <div style="font-weight:bold">{{ a.name }}</div>
            <div style="color:#999;font-size:13px;margin-top:4px">{{ a.startTime }} ~ {{ a.endTime }}</div>
            <el-tag :type="a.status===1?'success':a.status===2?'info':'warning'" size="small" style="margin-top:4px">
              {{ a.status===0?'未开始':a.status===1?'进行中':'已结束' }}
            </el-tag>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>
<script>
import request from '@/utils/request'
export default {
  data() { return { banners: [], courses: [], activities: [] } },
  created() { this.loadData() },
  methods: {
    loadData() {
      request.get('/api/banner', { params: { pageNum: 1, pageSize: 10, status: 1 } }).then(res => { this.banners = res.data.records || [] }).catch(() => {})
      request.get('/api/course', { params: { pageNum: 1, pageSize: 8, status: 1 } }).then(res => { this.courses = res.data.records || [] }).catch(() => {})
      request.get('/api/activity', { params: { pageNum: 1, pageSize: 6 } }).then(res => { this.activities = res.data.records || [] }).catch(() => {})
    }
  }
}
</script>
