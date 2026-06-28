<template>
  <div class="profile-page">
    <el-row :gutter="24" class="profile-row">
      <el-col :span="24">
        <el-card class="profile-card profile-card--hero" shadow="never">
          <div slot="header" class="card-header">
            <span class="card-header-icon"><i class="el-icon-user" /></span>
            <div class="card-header-text">
              <span class="card-header-title">个人信息</span>
              <span class="card-header-sub">完善资料，便于课程与订单服务</span>
            </div>
          </div>
          <div class="profile-hero">
            <div class="avatar-panel">
              <div class="avatar-glow" aria-hidden="true" />
              <el-upload
                action="/api/upload"
                :show-file-list="false"
                :on-success="onAvatarSuccess"
                class="avatar-uploader"
              >
                <div class="avatar-wrapper">
                  <img v-if="form.avatar" :src="form.avatar" class="avatar-img">
                  <div v-else class="avatar-placeholder">
                    <i class="el-icon-camera" />
                  </div>
                  <div class="avatar-overlay">
                    <i class="el-icon-camera" />
                    <span>更换头像</span>
                  </div>
                </div>
              </el-upload>
              <div class="avatar-info">
                <h3 class="user-name">{{ form.name || '未设置姓名' }}</h3>
                <el-tag size="small" :type="roleTagType" effect="plain" class="role-tag">{{ roleLabel }}</el-tag>
              </div>
            </div>
            <div class="profile-form-shell">
              <el-form :model="form" label-width="88px" class="profile-form">
                <el-row :gutter="20">
                  <el-col :xs="24" :sm="12">
                    <el-form-item label="用户名">
                      <el-input v-model="form.username" disabled prefix-icon="el-icon-user" />
                    </el-form-item>
                  </el-col>
                  <el-col :xs="24" :sm="12">
                    <el-form-item label="姓名">
                      <el-input v-model="form.name" prefix-icon="el-icon-s-custom" placeholder="请输入姓名" />
                    </el-form-item>
                  </el-col>
                </el-row>
                <el-row :gutter="20">
                  <el-col :xs="24" :sm="12">
                    <el-form-item label="联系方式">
                      <el-input v-model="form.phone" prefix-icon="el-icon-phone" placeholder="请输入手机号" />
                    </el-form-item>
                  </el-col>
                  <el-col :xs="24" :sm="12">
                    <el-form-item label="年龄">
                      <el-input-number v-model="form.age" :min="1" :max="120" controls-position="right" class="age-input" />
                    </el-form-item>
                  </el-col>
                </el-row>
                <el-form-item label="性别">
                  <el-radio-group v-model="form.gender" class="gender-group">
                    <el-radio-button label="男"><i class="el-icon-male" /> 男</el-radio-button>
                    <el-radio-button label="女"><i class="el-icon-female" /> 女</el-radio-button>
                  </el-radio-group>
                </el-form-item>
                <el-form-item class="form-actions">
                  <el-button type="primary" icon="el-icon-check" @click="saveProfile">保存修改</el-button>
                  <el-button class="btn-ghost" @click="loadUser"><i class="el-icon-refresh" /> 重置</el-button>
                </el-form-item>
              </el-form>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="24" class="profile-row">
      <el-col :span="24">
        <el-card class="profile-card profile-card--tabs" shadow="never">
          <div slot="header" class="card-header">
            <span class="card-header-icon"><i class="el-icon-s-tools" /></span>
            <div class="card-header-text">
              <span class="card-header-title">账户与安全</span>
              <span class="card-header-sub">管理密码与查看账户详情</span>
            </div>
          </div>
          <el-tabs v-model="accountTab" class="profile-tabs">
            <el-tab-pane name="security">
              <span slot="label" class="tab-label"><i class="el-icon-lock" /> 安全设置</span>
              <div class="tab-pane-inner">
                <div class="security-hint">
                  <span class="security-hint-icon"><i class="el-icon-unlock" /></span>
                  <p>建议定期更换密码，勿与其他网站使用相同密码。</p>
                </div>
                <el-form :model="pwdForm" label-width="88px" class="profile-form profile-form--compact">
                  <el-form-item label="原密码">
                    <el-input v-model="pwdForm.oldPassword" type="password" show-password prefix-icon="el-icon-lock" placeholder="请输入原密码" />
                  </el-form-item>
                  <el-form-item label="新密码">
                    <el-input v-model="pwdForm.newPassword" type="password" show-password prefix-icon="el-icon-key" placeholder="至少 6 位字符" />
                  </el-form-item>
                  <el-form-item label="确认密码">
                    <el-input v-model="pwdForm.confirmPassword" type="password" show-password prefix-icon="el-icon-key" placeholder="请再次输入新密码" />
                  </el-form-item>
                  <el-form-item>
                    <el-button type="primary" icon="el-icon-refresh-right" class="btn-block" @click="changePwd">修改密码</el-button>
                  </el-form-item>
                </el-form>
              </div>
            </el-tab-pane>
            <el-tab-pane name="account">
              <span slot="label" class="tab-label"><i class="el-icon-document" /> 账户信息</span>
              <div class="tab-pane-inner tab-pane-inner--account">
                <div class="info-tiles">
                  <div class="info-tile">
                    <div class="info-tile-icon info-tile-icon--ok">
                      <i class="el-icon-success" />
                    </div>
                    <div class="info-tile-body">
                      <span class="info-tile-label">账户状态</span>
                      <el-tag size="small" :type="form.status === 1 ? 'success' : 'danger'" effect="dark" class="info-tile-tag">{{ form.status === 1 ? '正常' : '已禁用' }}</el-tag>
                    </div>
                  </div>
                  <div class="info-tile">
                    <div class="info-tile-icon">
                      <i class="el-icon-time" />
                    </div>
                    <div class="info-tile-body">
                      <span class="info-tile-label">注册时间</span>
                      <span class="info-tile-value">{{ form.createTime || '—' }}</span>
                    </div>
                  </div>
                  <div class="info-tile">
                    <div class="info-tile-icon">
                      <i class="el-icon-refresh" />
                    </div>
                    <div class="info-tile-body">
                      <span class="info-tile-label">最近更新</span>
                      <span class="info-tile-value">{{ form.updateTime || '—' }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import request from '@/utils/request'
export default {
  data() {
    return {
      form: {},
      pwdForm: { oldPassword: '', newPassword: '', confirmPassword: '' },
      accountTab: 'security'
    }
  },
  computed: {
    roleLabel() {
      const map = { ADMIN: '管理员', TEACHER: '教师', STUDENT: '学生' }
      return map[this.form.role] || '未知'
    },
    roleTagType() {
      const map = { ADMIN: 'danger', TEACHER: 'warning', STUDENT: '' }
      return map[this.form.role] || 'info'
    }
  },
  created() { this.loadUser() },
  methods: {
    loadUser() {
      request.get('/api/auth/info').then(res => { this.form = res.data })
    },
    onAvatarSuccess(res) {
      if (res.code === 200) {
        this.form.avatar = res.data
        this.$message.success('头像上传成功')
      }
    },
    saveProfile() {
      request.put('/api/auth/profile', this.form).then(() => {
        this.$message.success('保存成功')
        sessionStorage.setItem('loginUser', JSON.stringify(this.form))
      })
    },
    changePwd() {
      if (!this.pwdForm.oldPassword || !this.pwdForm.newPassword) {
        return this.$message.warning('请填写完整密码信息')
      }
      if (this.pwdForm.newPassword !== this.pwdForm.confirmPassword) {
        return this.$message.warning('两次输入的新密码不一致')
      }
      if (this.pwdForm.newPassword.length < 6) {
        return this.$message.warning('新密码长度不能少于6位')
      }
      request.put('/api/auth/password', this.pwdForm).then(() => {
        this.$message.success('密码修改成功，请重新登录')
        this.pwdForm = { oldPassword: '', newPassword: '', confirmPassword: '' }
      })
    }
  }
}
</script>

<style scoped>
.profile-page {
  --profile-accent: #6c5ce7;
  --profile-accent-hover: #5b4cdb;
  --profile-accent-soft: rgba(108, 92, 231, 0.08);
  --profile-surface: #f3f4f9;
  --profile-card-bg: #fff;
  --profile-border: #e4e7ed;
  --profile-muted: #909399;
  --profile-text: #303133;

  min-height: calc(100vh - 120px);
  max-width: 920px;
  margin: 0 auto;
  padding: 16px 12px 40px;
  box-sizing: border-box;
  background:
    radial-gradient(ellipse 80% 50% at 50% -20%, rgba(108, 92, 231, 0.12), transparent),
    linear-gradient(180deg, #eef0f8 0%, #f6f7fb 45%, #f0f2f8 100%);
}

.profile-row + .profile-row {
  margin-top: 20px;
}

.profile-card {
  border-radius: 16px;
  overflow: hidden;
  border: 1px solid rgba(228, 231, 237, 0.9);
  background: var(--profile-card-bg);
  box-shadow: 0 4px 24px rgba(15, 23, 42, 0.06);
}

.profile-card--hero >>> .el-card__body {
  padding: 8px 20px 24px;
}

.profile-card >>> .el-card__header {
  background: linear-gradient(180deg, #fcfcff 0%, #f8f9fd 100%);
  border-bottom: 1px solid var(--profile-border);
  padding: 18px 22px;
}

.card-header {
  display: flex;
  align-items: flex-start;
  gap: 14px;
}

.card-header-text {
  display: flex;
  flex-direction: column;
  gap: 4px;
  min-width: 0;
}

.card-header-icon {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  background: linear-gradient(145deg, rgba(108, 92, 231, 0.18), rgba(108, 92, 231, 0.08));
  color: var(--profile-accent);
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  flex-shrink: 0;
}

.card-header-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--profile-text);
  letter-spacing: 0.02em;
}

.card-header-sub {
  font-size: 12px;
  color: var(--profile-muted);
  line-height: 1.4;
}

/* 头像区 */
.profile-hero {
  display: flex;
  flex-wrap: wrap;
  align-items: flex-start;
  gap: 28px 40px;
  padding: 4px 0 4px;
}

.avatar-panel {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  flex-shrink: 0;
  min-width: 132px;
  padding: 12px 0 0;
}

.avatar-glow {
  position: absolute;
  width: 140px;
  height: 140px;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -52%);
  background: radial-gradient(circle, rgba(108, 92, 231, 0.25) 0%, transparent 70%);
  pointer-events: none;
  z-index: 0;
}

.avatar-uploader,
.avatar-wrapper {
  position: relative;
  z-index: 1;
}

.avatar-wrapper {
  width: 104px;
  height: 104px;
  border-radius: 50%;
  overflow: hidden;
  cursor: pointer;
  box-shadow:
    0 0 0 4px #fff,
    0 12px 32px rgba(108, 92, 231, 0.28);
  flex-shrink: 0;
}

.avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-placeholder {
  width: 100%;
  height: 100%;
  background: linear-gradient(150deg, #8b7ff0 0%, #6c5ce7 40%, #4c3db8 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 36px;
}

.avatar-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.52);
  color: #fff;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 4px;
  font-size: 11px;
  opacity: 0;
  transition: opacity 0.25s ease;
}

.avatar-wrapper:hover .avatar-overlay {
  opacity: 1;
}

.avatar-info {
  margin-top: 16px;
}

.avatar-info .user-name {
  margin: 0 0 10px 0;
  font-size: 19px;
  font-weight: 600;
  color: var(--profile-text);
  letter-spacing: 0.02em;
}

.role-tag {
  border-radius: 20px !important;
}

.profile-form-shell {
  flex: 1;
  min-width: 260px;
  max-width: 100%;
}

@media (min-width: 900px) {
  .profile-form-shell {
    max-width: 520px;
  }
}

.profile-form {
  padding-top: 6px;
}

.profile-form--compact {
  padding-top: 0;
  max-width: 440px;
}

.profile-form >>> .el-form-item {
  margin-bottom: 18px;
}

.profile-form >>> .el-form-item:last-child {
  margin-bottom: 0;
}

.profile-form >>> .el-input__inner {
  border-radius: 10px;
}

.age-input {
  width: 100%;
}

.form-actions {
  margin-top: 8px;
  margin-bottom: 0 !important;
  padding-top: 4px;
}

.form-actions >>> .el-button--primary {
  background: linear-gradient(135deg, #7c6cf0, #6c5ce7);
  border: none;
  border-radius: 10px;
  padding-left: 22px;
  padding-right: 22px;
  box-shadow: 0 4px 14px rgba(108, 92, 231, 0.35);
}

.form-actions >>> .el-button--primary:hover {
  background: linear-gradient(135deg, #6c5ce7, #5b4cdb);
}

.btn-ghost {
  border-radius: 10px;
  border-color: var(--profile-border);
  color: #606266;
}

.btn-ghost:hover {
  border-color: var(--profile-accent);
  color: var(--profile-accent);
  background: var(--profile-accent-soft);
}

.gender-group >>> .el-radio-button__inner {
  border-radius: 8px !important;
  padding-left: 18px;
  padding-right: 18px;
}

/* Tabs 卡片 */
.profile-card--tabs >>> .el-card__body {
  padding: 0;
}

.profile-tabs >>> .el-tabs__header {
  margin: 0;
  padding: 0 20px;
  background: #fafbfd;
  border-bottom: 1px solid var(--profile-border);
}

.profile-tabs >>> .el-tabs__nav-wrap::after {
  display: none;
}

.profile-tabs >>> .el-tabs__item {
  height: 52px;
  line-height: 52px;
  font-size: 14px;
  color: #606266;
  padding: 0 20px;
}

.profile-tabs >>> .el-tabs__item.is-active {
  color: var(--profile-accent);
  font-weight: 600;
}

.profile-tabs >>> .el-tabs__active-bar {
  height: 3px;
  border-radius: 3px 3px 0 0;
  background: linear-gradient(90deg, #6c5ce7, #9b8cf7);
}

.profile-tabs >>> .el-tabs__content {
  padding: 0;
}

.tab-label i {
  margin-right: 6px;
  font-size: 14px;
}

.tab-pane-inner {
  padding: 22px 22px 26px;
}

.tab-pane-inner--account {
  padding-bottom: 28px;
}

.security-hint {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 14px 16px;
  margin-bottom: 18px;
  background: linear-gradient(135deg, var(--profile-accent-soft), rgba(108, 92, 231, 0.04));
  border-radius: 12px;
  border: 1px solid rgba(108, 92, 231, 0.12);
}

.security-hint-icon {
  flex-shrink: 0;
  width: 38px;
  height: 38px;
  border-radius: 10px;
  background: #fff;
  color: var(--profile-accent);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  box-shadow: 0 2px 8px rgba(108, 92, 231, 0.12);
}

.security-hint p {
  margin: 4px 0 0;
  font-size: 13px;
  line-height: 1.6;
  color: #606266;
}

.btn-block {
  width: 100%;
  max-width: 440px;
  border-radius: 10px;
  border: none;
  background: linear-gradient(135deg, #7c6cf0, #6c5ce7);
  box-shadow: 0 4px 14px rgba(108, 92, 231, 0.35);
}

.btn-block:hover {
  background: linear-gradient(135deg, #6c5ce7, #5b4cdb);
}

/* 账户信息 tiles */
.info-tiles {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.info-tile {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px 18px;
  background: var(--profile-surface);
  border-radius: 12px;
  border: 1px solid #e8eaf2;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.info-tile:hover {
  border-color: rgba(108, 92, 231, 0.25);
  box-shadow: 0 4px 16px rgba(15, 23, 42, 0.05);
}

.info-tile-icon {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  background: #fff;
  color: var(--profile-accent);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  flex-shrink: 0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.info-tile-icon--ok {
  color: #67c23a;
}

.info-tile-body {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 8px;
}

@media (min-width: 480px) {
  .info-tile-body {
    flex-direction: row;
    align-items: center;
    justify-content: space-between;
  }
}

.info-tile-label {
  font-size: 13px;
  color: var(--profile-muted);
}

.info-tile-value {
  font-size: 14px;
  font-weight: 500;
  color: var(--profile-text);
  text-align: left;
  word-break: break-all;
}

@media (min-width: 480px) {
  .info-tile-value {
    text-align: right;
  }
}

.info-tile-tag {
  border-radius: 6px !important;
}

@media (min-width: 768px) {
  .profile-hero {
    flex-wrap: nowrap;
    align-items: center;
    padding: 8px 4px 12px;
  }

  .avatar-panel {
    flex-direction: row;
    text-align: left;
    align-items: center;
    padding: 8px 8px 8px 4px;
    min-width: auto;
    gap: 22px;
  }

  .avatar-info {
    margin-top: 0;
    align-items: flex-start;
  }

  .avatar-panel .avatar-info {
    display: flex;
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
