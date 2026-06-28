<template>
  <div class="login-wrapper">
    <div class="circles">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
    </div>
    <div class="login-card">
      <div class="left-panel">
        <div class="brand">
          <i class="el-icon-headset logo-icon"></i>
          <h1>Melody Master</h1>
          <p>专业的琴行管理系统</p>
        </div>
        <div class="illustration">
          <!-- CSS Illustration or just text -->
          <div class="piano-keys">
            <div class="key white"></div>
            <div class="key black"></div>
            <div class="key white"></div>
            <div class="key black"></div>
            <div class="key white"></div>
            <div class="key black"></div>
            <div class="key white"></div>
          </div>
        </div>
      </div>
      <div class="right-panel">
        <div class="form-header">
          <h2>欢迎回来</h2>
          <span class="subtitle">请登录您的账户</span>
        </div>
        
        <el-form :model="form" :rules="rules" ref="loginForm" class="login-form">
          <el-form-item prop="username">
            <el-input v-model="form.username" prefix-icon="el-icon-user" placeholder="用户名/手机号">
            </el-input>
          </el-form-item>
          <el-form-item prop="password">
            <el-input v-model="form.password" prefix-icon="el-icon-lock" placeholder="密码" type="password" show-password @keyup.enter.native="handleLogin">
            </el-input>
          </el-form-item>
          
          <div class="form-footer">
            <el-checkbox v-model="rememberMe">记住我</el-checkbox>
            <a href="#" class="forgot-pwd">忘记密码?</a>
          </div>

          <el-button type="primary" :loading="loading" class="login-btn" @click="handleLogin">
            立即登录 <i class="el-icon-right"></i>
          </el-button>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script>
import request from '@/utils/request'
export default {
  name: 'Login',
  data() {
    return {
      form: { username: '', password: '' },
      rememberMe: false,
      rules: {
        username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
      },
      loading: false
    }
  },
  methods: {
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (!valid) return
        this.loading = true
        request.post('/api/auth/login', this.form).then(res => {
          const user = res.data.user
          sessionStorage.setItem('loginUser', JSON.stringify(user))
          const roleMap = { ADMIN: '/admin', TEACHER: '/teacher', STUDENT: '/student' }
          this.$router.push(roleMap[user.role] || '/login')
          this.$message.success('登录成功')
        }).catch(() => {}).finally(() => { this.loading = false })
      })
    }
  }
}
</script>

<style scoped>
.login-wrapper {
  height: 100vh;
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #1e1e2f 0%, #2d2d44 100%);
  position: relative;
  overflow: hidden;
}

/* Background Circles Animation */
.circles {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  overflow: hidden;
  z-index: 0;
}
.circle {
  position: absolute;
  border-radius: 50%;
  opacity: 0.1;
  filter: blur(60px);
}
.circle-1 {
  width: 400px;
  height: 400px;
  background: #6C5CE7;
  top: -100px;
  left: -100px;
  animation: float 8s infinite ease-in-out;
}
.circle-2 {
  width: 500px;
  height: 500px;
  background: #00cec9;
  bottom: -150px;
  right: -100px;
  animation: float 10s infinite ease-in-out reverse;
}
.circle-3 {
  width: 300px;
  height: 300px;
  background: #fdcb6e;
  top: 40%;
  left: 60%;
  animation: float 12s infinite ease-in-out 2s;
}

@keyframes float {
  0%, 100% { transform: translate(0, 0); }
  50% { transform: translate(30px, 50px); }
}

.login-card {
  width: 900px;
  height: 550px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 24px;
  box-shadow: 0 20px 80px rgba(0, 0, 0, 0.4);
  display: flex;
  overflow: hidden;
  z-index: 10;
  backdrop-filter: blur(20px);
}

.left-panel {
  flex: 1;
  background: linear-gradient(135deg, #6C5CE7 0%, #a29bfe 100%);
  padding: 60px 40px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  color: white;
  position: relative;
  overflow: hidden;
}

.logo-icon {
  font-size: 48px;
  margin-bottom: 20px;
  background: rgba(255,255,255,0.2);
  padding: 15px;
  border-radius: 16px;
}

.brand h1 {
  font-size: 36px;
  font-weight: 700;
  margin-bottom: 10px;
  letter-spacing: 1px;
}
.brand p {
  font-size: 16px;
  opacity: 0.9;
  font-weight: 300;
}

.piano-keys {
  display: flex;
  justify-content: center;
  position: relative;
  height: 120px;
  margin-top: 40px;
  perspective: 500px;
  transform: rotateX(10deg);
}
.key {
  width: 30px;
  height: 100%;
  background: white;
  border-radius: 0 0 5px 5px;
  margin: 0 2px;
  box-shadow: 0 5px 10px rgba(0,0,0,0.2);
}
.key.black {
  background: #2d3436;
  height: 70%;
  width: 20px;
  margin-left: -10px;
  margin-right: -10px;
  z-index: 2;
  position: relative;
  box-shadow: 2px 5px 5px rgba(0,0,0,0.3);
}

.right-panel {
  flex: 1.2;
  padding: 60px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.form-header h2 {
  font-size: 28px;
  color: #2D3436;
  margin-bottom: 10px;
}
.subtitle {
  color: #b2bec3;
  font-size: 14px;
}

.login-form {
  margin-top: 40px;
}

/* Custom Input Styles handled by global.css, adding specific tweaks */
.el-input__inner {
  height: 50px;
  background: #f5f6fa;
  border: none;
  padding-left: 45px;
}
.el-input__prefix {
  left: 10px;
  top: 2px;
}
.el-input__icon {
  font-size: 18px;
  color: #a29bfe;
}

.form-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  margin-top: -10px;
}
.forgot-pwd {
  color: #6C5CE7;
  font-size: 14px;
  text-decoration: none;
}

.login-btn {
  width: 100%;
  height: 50px;
  font-size: 16px;
  letter-spacing: 2px;
  border-radius: 12px;
  background: linear-gradient(to right, #6C5CE7, #81ecec);
  border: none;
  transition: transform 0.3s, box-shadow 0.3s;
}
.login-btn:hover {
  transform: translateY(-3px);
  box-shadow: 0 10px 20px rgba(108, 92, 231, 0.3);
}

@media (max-width: 900px) {
  .login-card {
    width: 90%;
    flex-direction: column;
    height: auto;
  }
  .left-panel {
    padding: 30px;
    text-align: center;
  }
  .piano-keys { display: none; }
  .right-panel {
    padding: 40px;
  }
}
</style>
