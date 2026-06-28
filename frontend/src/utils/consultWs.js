/**
 * 师生咨询 WebSocket（与后端 /ws/consult 对应）
 * room 建议：课程 + 教师 + 学生，避免会话串线
 */
export function buildConsultRoom(courseId, teacherId, studentId) {
  return `c${courseId}_t${teacherId || 0}_s${studentId || 0}`
}

export function getConsultWsUrl(roomId) {
  const proto = window.location.protocol === 'https:' ? 'wss:' : 'ws:'
  const host = window.location.host
  return `${proto}//${host}/ws/consult?room=${encodeURIComponent(roomId)}`
}
