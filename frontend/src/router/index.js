import Vue from 'vue'
import VueRouter from 'vue-router'

Vue.use(VueRouter)

const routes = [
  { path: '/', redirect: '/login' },
  { path: '/login', component: () => import('../views/login/Login.vue') },

  {
    path: '/admin',
    component: () => import('../views/admin/Layout.vue'),
    children: [
      { path: '', redirect: 'dashboard' },
      { path: 'dashboard', component: () => import('../views/admin/Dashboard.vue') },
      { path: 'user', component: () => import('../views/admin/UserManage.vue') },
      { path: 'category', component: () => import('../views/admin/CategoryManage.vue') },
      { path: 'course', component: () => import('../views/admin/CourseManage.vue') },
      { path: 'classroom', component: () => import('../views/admin/ClassroomManage.vue') },
      { path: 'product', component: () => import('../views/admin/ProductManage.vue') },
      { path: 'stock', component: () => import('../views/admin/StockManage.vue') },
      { path: 'order', component: () => import('../views/admin/OrderManage.vue') },
      { path: 'booking', component: () => import('../views/admin/BookingManage.vue') },
      { path: 'activity', component: () => import('../views/admin/ActivityManage.vue') },
      { path: 'registration', component: () => import('../views/admin/RegistrationManage.vue') },
      { path: 'certificate', component: () => import('../views/admin/CertificateManage.vue') },
      { path: 'statistics', component: () => import('../views/admin/Statistics.vue') },
      { path: 'banner', component: () => import('../views/admin/BannerManage.vue') },
      { path: 'log', component: () => import('../views/admin/LogManage.vue') },
      { path: 'profile', component: () => import('../views/common/Profile.vue') }
    ]
  },

  {
    path: '/teacher',
    component: () => import('../views/teacher/Layout.vue'),
    children: [
      { path: '', redirect: 'booking' },
      { path: 'profile', component: () => import('../views/common/Profile.vue') },
      { path: 'timetable', component: () => import('../views/teacher/Timetable.vue') },
      { path: 'schedule', component: () => import('../views/teacher/ScheduleManage.vue') },
      { path: 'qualification', component: () => import('../views/teacher/Qualification.vue') },
      { path: 'evaluation', component: () => import('../views/teacher/Evaluation.vue') },
      { path: 'booking', component: () => import('../views/teacher/BookingApproval.vue') },
      { path: 'classRecord', component: () => import('../views/teacher/ClassRecord.vue') },
      { path: 'income', component: () => import('../views/teacher/Income.vue') },
      { path: 'notification', component: () => import('../views/teacher/Notification.vue') }
    ]
  },

  {
    path: '/student',
    component: () => import('../views/student/Layout.vue'),
    children: [
      { path: '', redirect: 'home' },
      { path: 'home', component: () => import('../views/student/Home.vue') },
      { path: 'profile', component: () => import('../views/common/Profile.vue') },
      { path: 'booking', component: () => import('../views/student/Booking.vue') },
      { path: 'course/:id', component: () => import('../views/student/CourseDetail.vue') },
      { path: 'schedule', component: () => import('../views/student/Schedule.vue') },
      { path: 'package', component: () => import('../views/student/Package.vue') },
      { path: 'classRecord', component: () => import('../views/student/ClassRecord.vue') },
      { path: 'teacher', component: () => import('../views/student/TeacherList.vue') },
      { path: 'shop', component: () => import('../views/student/Shop.vue') },
      { path: 'product/:id', component: () => import('../views/student/ProductDetail.vue') },
      { path: 'order', component: () => import('../views/student/Order.vue') },
      { path: 'activity', component: () => import('../views/student/Activity.vue') },
      { path: 'registration', component: () => import('../views/student/Registration.vue') },
      { path: 'certificate', component: () => import('../views/student/Certificate.vue') }
    ]
  }
]

const originalPush = VueRouter.prototype.push
VueRouter.prototype.push = function push(location) {
  return originalPush.call(this, location).catch(err => {
    if (err.name !== 'NavigationDuplicated') throw err
  })
}

const router = new VueRouter({
  routes
})

const whiteList = ['/login']
router.beforeEach((to, from, next) => {
  if (whiteList.includes(to.path)) {
    next()
    return
  }

  const user = sessionStorage.getItem('loginUser')
  if (!user) {
    next('/login')
    return
  }

  let userObj = {}
  try {
    userObj = JSON.parse(user)
  } catch (e) {
    sessionStorage.removeItem('loginUser')
    next('/login')
    return
  }
  const role = userObj.role
  if (to.path.startsWith('/admin') && role !== 'ADMIN') {
    next(from.path || '/')
  } else if (to.path.startsWith('/teacher') && role !== 'TEACHER') {
    next(from.path || '/')
  } else if (to.path.startsWith('/student') && role !== 'STUDENT') {
    next(from.path || '/')
  } else {
    next()
  }
})

export default router
