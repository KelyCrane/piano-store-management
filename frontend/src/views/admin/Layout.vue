<template>
  <el-container class="app-wrapper">
    <el-aside width="240px" class="sidebar-container">
      <div class="logo-container">
        <i class="el-icon-headset logo-icon"></i>
        <span class="logo-text">Melody Admin</span>
      </div>
      <el-menu :default-active="$route.path" router background-color="#2C3E50" text-color="#bdc3c7" active-text-color="#fff" :unique-opened="false" class="el-menu-vertical">
        <el-menu-item index="/admin/dashboard">
          <i class="el-icon-data-board"></i>
          <span slot="title">工作台</span>
        </el-menu-item>
        
        <el-submenu index="user-mgmt">
          <template slot="title"><i class="el-icon-user"></i><span>用户管理</span></template>
          <el-menu-item index="/admin/user">用户列表</el-menu-item>
        </el-submenu>
        
        <el-submenu index="course-mgmt">
          <template slot="title"><i class="el-icon-notebook-2"></i><span>课程教学</span></template>
          <el-menu-item index="/admin/category">分类管理</el-menu-item>
          <el-menu-item index="/admin/course">课程列表</el-menu-item>
          <el-menu-item index="/admin/classroom">教室管理</el-menu-item>
        </el-submenu>
        
        <el-submenu index="inventory">
          <template slot="title"><i class="el-icon-box"></i><span>库存中心</span></template>
          <el-menu-item index="/admin/product">商品管理</el-menu-item>
          <el-menu-item index="/admin/stock">出入库记录</el-menu-item>
        </el-submenu>
        
        <el-menu-item index="/admin/order">
          <i class="el-icon-s-order"></i>
          <span slot="title">订单管理</span>
        </el-menu-item>

        <el-menu-item index="/admin/booking">
          <i class="el-icon-date"></i>
          <span slot="title">预约管理</span>
        </el-menu-item>

        <el-submenu index="activity-mgmt">
          <template slot="title"><i class="el-icon-trophy"></i><span>活动赛事</span></template>
          <el-menu-item index="/admin/activity">活动列表</el-menu-item>
          <el-menu-item index="/admin/registration">报名管理</el-menu-item>
          <el-menu-item index="/admin/certificate">证书管理</el-menu-item>
        </el-submenu>
        
        <el-menu-item index="/admin/statistics">
          <i class="el-icon-pie-chart"></i>
          <span slot="title">数据统计</span>
        </el-menu-item>
        
        <el-submenu index="system">
          <template slot="title"><i class="el-icon-setting"></i><span>系统设置</span></template>
          <el-menu-item index="/admin/banner">轮播图管理</el-menu-item>
          <el-menu-item index="/admin/log">操作日志</el-menu-item>
        </el-submenu>
      </el-menu>
    </el-aside>
    
    <el-container class="main-container">
      <el-header class="navbar">
        <div class="left-section">
          <i class="el-icon-s-fold toggle-icon"></i>
          <el-breadcrumb separator="/" class="breadcrumb">
            <el-breadcrumb-item :to="{ path: '/admin/dashboard' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ $route.meta.title || '当前页面' }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        
        <div class="right-section">
          <el-dropdown @command="handleCommand" trigger="click">
            <div class="avatar-wrapper">
              <el-avatar icon="el-icon-user-solid" size="small" style="background-color: #6C5CE7"></el-avatar>
              <span class="username">{{ userName }}</span>
              <i class="el-icon-caret-bottom"></i>
            </div>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="profile" icon="el-icon-user">个人中心</el-dropdown-item>
              <el-dropdown-item command="logout" divided icon="el-icon-switch-button">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </el-header>
      
      <el-main class="app-main">
        <transition name="fade-transform" mode="out-in">
          <router-view />
        </transition>
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
import request from '@/utils/request'
export default {
  name: 'AdminLayout',
  computed: {
    userName() {
      const u = JSON.parse(sessionStorage.getItem('loginUser') || '{}')
      return u.name || u.username || '管理员'
    }
  },
  methods: {
    handleCommand(cmd) {
      if (cmd === 'logout') {
        request.post('/api/auth/logout').then(() => {
          sessionStorage.removeItem('loginUser')
          this.$router.push('/login')
        })
      } else if (cmd === 'profile') {
        this.$router.push('/admin/profile')
      }
    }
  }
}
</script>

<style scoped>
.app-wrapper {
  height: 100vh;
  background-color: #f0f2f5;
}

.sidebar-container {
  background-color: #2C3E50;
  box-shadow: 2px 0 6px rgba(0,21,41,.35);
  z-index: 100;
  overflow-x: hidden;
  transition: width 0.3s;
}

.logo-container {
  height: 60px;
  line-height: 60px;
  background: #243342;
  text-align: center;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 20px;
  font-weight: 600;
}

.logo-icon {
  margin-right: 10px;
  color: #6C5CE7;
  font-size: 24px;
}

.el-menu-vertical {
  border: none;
}
/* Menu Item Hover/Active */
.el-menu-item:hover, .el-submenu__title:hover {
  background-color: #34495E !important;
}
.el-menu-item.is-active {
  background-color: #6C5CE7 !important;
  color: #fff !important;
}

.main-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  overflow: hidden;
}

.navbar {
  height: 60px;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0,21,41,.08);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  z-index: 9;
}

.left-section {
  display: flex;
  align-items: center;
}

.toggle-icon {
  font-size: 20px;
  cursor: pointer;
  margin-right: 20px;
  color: #5a5e66;
}

.breadcrumb {
  font-size: 14px;
  line-height: 60px;
  display: inline-block;
}

.right-section {
  height: 100%;
  display: flex;
  align-items: center;
}

.avatar-wrapper {
  cursor: pointer;
  display: flex;
  align-items: center;
  color: #5a5e66;
}
.username {
  margin: 0 8px;
  font-weight: 500;
}

.app-main {
  flex: 1;
  padding: 20px;
  background-color: #F8F9FA;
  overflow-y: auto;
}

/* Transitions */
.fade-transform-leave-active,
.fade-transform-enter-active {
  transition: all .4s;
}
.fade-transform-enter {
  opacity: 0;
  transform: translateX(-30px);
}
.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(30px);
}
</style>
