import axios from 'axios'
import { Message } from 'element-ui'
import router from '../router'

const request = axios.create({
  baseURL: '',
  timeout: 15000,
  withCredentials: true
})

/** 由拦截器处理过的失败，用于避免开发环境「未捕获异常」全屏遮罩 */
export function createRequestError(message, extra = {}) {
  const error = new Error(message || '请求失败')
  error.__requestError = true
  Object.assign(error, extra)
  return error
}

function parseResponseBodyMessage(data) {
  if (data == null) return ''
  if (typeof data === 'string') return data
  if (typeof data === 'object') {
    return data.message || data.msg || data.error || ''
  }
  return ''
}

function httpErrorUserMessage(status, rawMessage) {
  if (status >= 500) {
    return '服务暂时不可用，请稍后再试'
  }
  if (status === 401) {
    return '未登录或登录已过期，请重新登录'
  }
  if (status === 403) {
    return '没有权限执行该操作'
  }
  if (status === 404) {
    return '请求的资源不存在'
  }
  if (rawMessage) return rawMessage
  return '网络请求失败'
}

// 响应拦截器 - 统一异常处理（勿 reject(Error)，以免未 catch 时触发 dev overlay）
request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200) {
      const msg = res.message || res.msg || '操作失败'
      Message.error(msg)
      return Promise.reject(createRequestError(msg, { type: 'business', code: res.code }))
    }
    return res
  },
  error => {
    if (error.response) {
      const status = error.response.status
      const raw = parseResponseBodyMessage(error.response.data)
      if (status === 401) {
        Message.error('未登录或登录已过期，请重新登录')
        router.push('/login')
        return Promise.reject(createRequestError(raw || '未授权', { type: 'http', status }))
      }
      const msg = httpErrorUserMessage(status, raw)
      Message.error(msg)
      if (process.env.NODE_ENV === 'development' && status >= 500 && raw) {
        console.warn('[API]', status, raw)
      }
      return Promise.reject(createRequestError(msg, { type: 'http', status, rawMessage: raw }))
    }
    Message.error('网络连接异常，请稍后重试')
    return Promise.reject(createRequestError('网络连接异常', { type: 'network' }))
  }
)

export default request
