<template>
  <div class="student-wrapper">
    <header class="top-navbar">
      <div class="navbar-brand">
        <i class="el-icon-headset brand-icon"></i>
        <span>Melody <b>Student</b></span>
      </div>
      <nav class="nav-links">
        <router-link to="/student/home" class="nav-item"><i class="el-icon-s-home"></i> 首页</router-link>
        <router-link to="/student/booking" class="nav-item"><i class="el-icon-date"></i> 课程预约</router-link>
        <router-link to="/student/schedule" class="nav-item"><i class="el-icon-notebook-1"></i> 课程表</router-link>
        <router-link to="/student/package" class="nav-item"><i class="el-icon-s-operation"></i> 我的课时</router-link>
        <router-link to="/student/shop" class="nav-item"><i class="el-icon-shopping-cart-2"></i> 商城</router-link>
        <router-link to="/student/order" class="nav-item"><i class="el-icon-s-order"></i> 订单</router-link>
        <router-link to="/student/activity" class="nav-item"><i class="el-icon-trophy"></i> 活动</router-link>
        <router-link to="/student/registration" class="nav-item"><i class="el-icon-tickets"></i> 报名记录</router-link>
        <router-link to="/student/certificate" class="nav-item"><i class="el-icon-medal"></i> 证书</router-link>
        <router-link to="/student/teacher" class="nav-item"><i class="el-icon-user-solid"></i> 教师信息</router-link>
      </nav>
      <div class="navbar-user">
        <el-dropdown @command="handleCommand" trigger="click">
          <div class="user-btn">
            <el-avatar icon="el-icon-user-solid" size="small" style="background-color:#6C5CE7"></el-avatar>
            <span>{{ userName }}</span>
            <i class="el-icon-caret-bottom"></i>
          </div>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item command="profile" icon="el-icon-user">个人中心</el-dropdown-item>
            <el-dropdown-item command="logout" divided icon="el-icon-switch-button">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>
    </header>
    <main class="page-main">
      <transition name="fade-transform" mode="out-in">
        <router-view />
      </transition>
    </main>
  </div>
</template>

<script>
import request from '@/utils/request'
export default {
  name: 'StudentLayout',
  computed: {
    userName() {
      const u = JSON.parse(sessionStorage.getItem('loginUser') || '{}')
      return u.name || '同学'
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
        this.$router.push('/student/profile')
      }
    }
  }
}
</script>

<style scoped>
.student-wrapper {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f8f9fa;
}

.top-navbar {
  height: 64px;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  display: flex;
  align-items: center;
  padding: 0 30px;
  position: sticky;
  top: 0;
  z-index: 100;
}

.navbar-brand {
  display: flex;
  align-items: center;
  font-size: 20px;
  font-weight: 600;
  color: #2d3436;
  margin-right: 40px;
  white-space: nowrap;
}
.brand-icon {
  font-size: 24px;
  color: #6C5CE7;
  margin-right: 8px;
}

.nav-links {
  display: flex;
  align-items: center;
  flex: 1;
}
.nav-item {
  text-decoration: none;
  color: #606266;
  padding: 6px 14px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s;
  margin-right: 4px;
  display: flex;
  align-items: center;
  gap: 4px;
}
.nav-item:hover {
  background: #f0effe;
  color: #6C5CE7;
}
.nav-item.router-link-active {
  background: #6C5CE7;
  color: #fff;
}

.navbar-user {
  margin-left: auto;
}
.user-btn {
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  color: #5a5e66;
}

.page-main {
  flex: 1;
  padding: 24px 30px;
}

/* Transitions */
.fade-transform-leave-active,
.fade-transform-enter-active {
  transition: all .35s;
}
.fade-transform-enter {
  opacity: 0;
  transform: translateY(-10px);
}
.fade-transform-leave-to {
  opacity: 0;
  transform: translateY(10px);
}
</style>
