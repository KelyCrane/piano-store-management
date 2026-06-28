<template>
  <el-dialog
    :title="dialogTitle"
    :visible.sync="innerVisible"
    width="520px"
    custom-class="consult-dialog"
    append-to-body
    @closed="onDialogClosed"
  >
    <div class="consult-status">
      <el-tag :type="wsStatusTag" size="small">{{ wsStatusText }}</el-tag>
      <span class="consult-hint">对方不在线时，消息会以留言保存，教师可在「预约审批」中查看记录。</span>
    </div>
    <div ref="scrollBox" class="consult-messages">
      <div
        v-for="(m, idx) in messages"
        :key="idx"
        :class="['consult-bubble', 'consult-bubble--' + m.side]"
      >
        <span class="consult-meta">{{ m.label }}</span>
        <div class="consult-text">{{ m.text }}</div>
      </div>
    </div>
    <div class="consult-input-row">
      <el-input
        v-model="draft"
        type="textarea"
        :rows="3"
        maxlength="500"
        show-word-limit
        :placeholder="inputPlaceholder"
        @keydown.native.enter.exact.prevent="send"
      />
      <el-button type="primary" :loading="sending" :disabled="!canSend" @click="send">发送</el-button>
    </div>
  </el-dialog>
</template>

<script>
import request from '@/utils/request'
import { buildConsultRoom, getConsultWsUrl } from '@/utils/consultWs'

export default {
  name: 'ConsultTeacherDialog',
  props: {
    visible: { type: Boolean, default: false },
    course: {
      type: Object,
      default: () => ({})
    },
    userRole: {
      type: String,
      default: 'student'
    }
  },
  data() {
    return {
      innerVisible: false,
      ws: null,
      draft: '',
      messages: [],
      wsConnected: false,
      sending: false,
      roomReady: false,
      wsEverOpened: false
    }
  },
  computed: {
    isTeacher() {
      return this.userRole === 'teacher'
    },
    dialogTitle() {
      if (this.isTeacher) {
        const n = this.course.studentName || this.course.name || '学生'
        return `咨询会话 · ${n}`
      }
      const t = this.course.teacherName || '任课教师'
      return `咨询 · ${t}`
    },
    inputPlaceholder() {
      return this.isTeacher ? '输入回复内容，Enter 发送…' : '输入想咨询老师的内容，Enter 发送…'
    },
    wsStatusTag() {
      if (this.wsConnected) return 'success'
      if (this.roomReady) return 'info'
      return 'info'
    },
    wsStatusText() {
      if (this.wsConnected) return '实时通道已连接'
      if (this.roomReady) return '留言模式（可随时发送）'
      return '准备中…'
    },
    canSend() {
      return this.roomReady && this.draft.trim().length > 0 && !this.sending
    }
  },
  watch: {
    visible: {
      immediate: true,
      handler(v) {
        this.innerVisible = v
        if (v) this.$nextTick(() => this.openSession())
      }
    },
    innerVisible(v) {
      this.$emit('update:visible', v)
    }
  },
  methods: {
    sessionIds() {
      const user = JSON.parse(sessionStorage.getItem('loginUser') || '{}')
      const cid = this.course.id || this.course.courseId
      const tid = this.course.teacherId
      const sid = this.isTeacher ? this.course.studentId : user.id
      return { cid, tid, sid, user }
    },
    async openSession() {
      if (this.ws) {
        try {
          this.ws.close()
        } catch (e) {}
        this.ws = null
      }
      this.wsConnected = false
      this.wsEverOpened = false
      this.roomReady = false
      this.messages = []
      this.draft = ''
      const { cid, tid, sid, user } = this.sessionIds()
      if (!cid || !tid || !sid) {
        this.$message.warning(this.isTeacher ? '无法打开：缺少课程或学生信息' : '无法打开：缺少课程或登录信息')
        return
      }
      this.roomReady = true
      await this.loadHistory(user)
      this.tryWebSocket(cid, tid, sid)
    },
    async loadHistory(loginUser) {
      const { cid, tid, sid } = this.sessionIds()
      try {
        const res = await request.get('/api/consult/messages', {
          params: { courseId: cid, teacherId: tid, studentId: sid }
        })
        const list = res.data || []
        list.forEach(m => {
          const mine = m.senderId === loginUser.id
          const label = mine ? '我' : (m.senderRole === 'TEACHER' ? '教师' : '学生')
          this.messages.push({ side: mine ? 'mine' : 'other', label, text: m.content })
        })
        if (list.length) {
          this.pushLine('system', '系统', '以上为历史留言记录。')
        }
      } catch (e) {
        /* 已由拦截器提示 */
      }
      this.$nextTick(this.scrollBottom)
    },
    tryWebSocket(cid, tid, sid) {
      const room = buildConsultRoom(cid, tid, sid)
      const url = getConsultWsUrl(room)
      try {
        this.ws = new WebSocket(url)
      } catch (e) {
        this.pushLine('system', '系统', '实时通道不可用，您仍可通过留言发送消息。')
        return
      }
      this.ws.onopen = () => {
        this.wsEverOpened = true
        this.wsConnected = true
        this.pushLine('system', '系统', '实时通道已建立，双方在线时可即时看到消息。')
      }
      this.ws.onmessage = ev => {
        let side = 'other'
        let label = '对方'
        let text = ev.data
        try {
          const o = JSON.parse(ev.data)
          if (o.role === 'system') {
            side = 'system'
            label = '提示'
          } else if (o.role === 'student') {
            label = o.name || '学生'
          } else if (o.role === 'teacher') {
            label = o.name || '教师'
          }
          if (o.text) text = o.text
        } catch (e) {
          label = '对方'
        }
        this.messages.push({ side, label, text })
        this.$nextTick(this.scrollBottom)
      }
      this.ws.onerror = () => {
        this.wsConnected = false
        if (!this.wsEverOpened) {
          this.pushLine('system', '系统', '实时连接失败，已切换为仅留言模式（不影响发送）。')
        }
      }
      this.ws.onclose = () => {
        this.wsConnected = false
      }
    },
    pushLine(side, label, text) {
      this.messages.push({ side, label, text })
      this.$nextTick(this.scrollBottom)
    },
    send() {
      const text = this.draft.trim()
      if (!text || this.sending || !this.roomReady) return
      const { cid, tid, sid } = this.sessionIds()
      const user = JSON.parse(sessionStorage.getItem('loginUser') || '{}')
      this.sending = true
      request.post('/api/consult/message', {
        courseId: cid,
        teacherId: tid,
        studentId: sid,
        content: text
      }).then(() => {
        if (this.ws && this.ws.readyState === WebSocket.OPEN) {
          const role = this.isTeacher ? 'teacher' : 'student'
          const payload = JSON.stringify({
            role,
            name: user.name || (this.isTeacher ? '教师' : '学生'),
            text
          })
          this.ws.send(payload)
        }
        this.pushLine('mine', '我', text)
        this.draft = ''
        if (!this.wsConnected) {
          this.$message.success('留言已保存')
        }
      }).catch(() => {}).finally(() => {
        this.sending = false
      })
    },
    scrollBottom() {
      const el = this.$refs.scrollBox
      if (el) el.scrollTop = el.scrollHeight
    },
    onDialogClosed() {
      if (this.ws) {
        try {
          this.ws.close()
        } catch (e) {}
        this.ws = null
      }
      this.wsConnected = false
      this.roomReady = false
      this.$emit('update:visible', false)
    }
  }
}
</script>

<style scoped>
.consult-status {
  margin-bottom: 12px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.consult-hint {
  font-size: 12px;
  color: #909399;
  line-height: 1.5;
}
.consult-messages {
  max-height: 300px;
  overflow-y: auto;
  padding: 8px 4px;
  background: #f6f7fb;
  border-radius: 12px;
  margin-bottom: 12px;
}
.consult-bubble {
  margin-bottom: 12px;
}
.consult-meta {
  display: block;
  font-size: 11px;
  color: #909399;
  margin-bottom: 4px;
}
.consult-text {
  display: inline-block;
  max-width: 100%;
  padding: 10px 12px;
  border-radius: 12px;
  font-size: 14px;
  line-height: 1.45;
  word-break: break-word;
}
.consult-bubble--mine {
  text-align: right;
}
.consult-bubble--mine .consult-meta {
  text-align: right;
}
.consult-bubble--mine .consult-text {
  background: linear-gradient(135deg, #7c6cf0, #6c5ce7);
  color: #fff;
  border-bottom-right-radius: 4px;
}
.consult-bubble--other .consult-text {
  background: #fff;
  color: #303133;
  border: 1px solid #ebeef5;
  border-bottom-left-radius: 4px;
}
.consult-bubble--system .consult-text {
  background: #fdf6ec;
  color: #b88230;
  border: 1px solid #faecd8;
  font-size: 13px;
}
.consult-input-row {
  display: flex;
  gap: 10px;
  align-items: flex-end;
}
.consult-input-row >>> .el-textarea {
  flex: 1;
}
</style>

<style>
.consult-dialog .el-dialog__body {
  padding-top: 8px;
}
</style>
