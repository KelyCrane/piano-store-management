<template>
  <div class="teacher-wrapper">
    <header class="top-navbar">
      <div class="navbar-brand">
        <i class="el-icon-monitor brand-icon"></i>
        <span>Melody <b>Teacher</b></span>
      </div>

      <nav class="nav-links">
        <router-link to="/teacher/timetable" class="nav-item"><i class="el-icon-notebook-1"></i> 我的课表</router-link>
        <router-link to="/teacher/schedule" class="nav-item"><i class="el-icon-date"></i> 排课管理</router-link>
        <router-link to="/teacher/booking" class="nav-item"><i class="el-icon-s-check"></i> 预约审批</router-link>
        <router-link to="/teacher/classRecord" class="nav-item"><i class="el-icon-notebook-2"></i> 上课记录</router-link>
        <router-link to="/teacher/qualification" class="nav-item"><i class="el-icon-medal-1"></i> 我的资质</router-link>
        <router-link to="/teacher/evaluation" class="nav-item"><i class="el-icon-star-on"></i> 学生评价</router-link>
        <router-link to="/teacher/income" class="nav-item"><i class="el-icon-data-line"></i> 课时统计</router-link>
        <router-link to="/teacher/notification" class="nav-item"><i class="el-icon-bell"></i> 报名通知</router-link>
      </nav>

      <div class="navbar-user">
        <el-dropdown @command="handleCommand" trigger="click">
          <div class="user-btn">
            <el-avatar icon="el-icon-user-solid" size="small" style="background-color:#0984e3"></el-avatar>
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
  name: 'TeacherLayout',
  computed: {
    userName() {
      const user = JSON.parse(sessionStorage.getItem('loginUser') || '{}')
      return user.name || '教师'
    }
  },
  methods: {
    handleCommand(command) {
      if (command === 'logout') {
        request.post('/api/auth/logout').then(() => {
          sessionStorage.removeItem('loginUser')
          this.$router.push('/login')
        })
        return
      }
      if (command === 'profile') {
        this.$router.push('/teacher/profile')
      }
    }
  }
}
</script>

<style scoped>
.teacher-wrapper {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f8f9fa;
}

.top-navbar {
  min-height: 64px;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 12px 30px;
  position: sticky;
  top: 0;
  z-index: 100;
  flex-wrap: wrap;
}

.navbar-brand {
  display: flex;
  align-items: center;
  font-size: 20px;
  font-weight: 600;
  color: #2d3436;
  white-space: nowrap;
}

.brand-icon {
  font-size: 24px;
  color: #0984e3;
  margin-right: 8px;
}

.nav-links {
  display: flex;
  align-items: center;
  flex: 1;
  flex-wrap: wrap;
  gap: 6px;
}

.nav-item {
  text-decoration: none;
  color: #606266;
  padding: 6px 14px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  gap: 4px;
}

.nav-item:hover {
  background: #e1f0fa;
  color: #0984e3;
}

.nav-item.router-link-active {
  background: #0984e3;
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

.fade-transform-leave-active,
.fade-transform-enter-active {
  transition: all 0.35s;
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
