package com.piano.management.interceptor;

import com.alibaba.fastjson.JSON;
import com.piano.management.entity.OperationLog;
import com.piano.management.entity.SysUser;
import com.piano.management.service.OperationLogService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class OperationLogInterceptor implements HandlerInterceptor {
    private static final String LOG_USER_ATTR = "operationLogUser";

    @Resource
    private OperationLogService operationLogService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        SysUser user = currentUser(request);
        if (user != null) {
            request.setAttribute(LOG_USER_ATTR, user);
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        if (!shouldLog(request, response, ex)) {
            return;
        }
        SysUser user = request.getAttribute(LOG_USER_ATTR) instanceof SysUser
                ? (SysUser) request.getAttribute(LOG_USER_ATTR)
                : currentUser(request);
        if (user == null) {
            return;
        }

        OperationLog log = new OperationLog();
        log.setUserId(user.getId());
        log.setUsername(user.getUsername());
        log.setModule(resolveModule(request.getRequestURI()));
        log.setOperation(resolveOperation(request));
        log.setMethod(request.getMethod() + " " + request.getRequestURI());
        log.setParams(resolveParams(request));
        log.setIp(resolveClientIp(request));
        operationLogService.save(log);
    }

    private boolean shouldLog(HttpServletRequest request, HttpServletResponse response, Exception ex) {
        if (ex != null || response.getStatus() >= 400) {
            return false;
        }
        String method = request.getMethod();
        if ("GET".equalsIgnoreCase(method) || "OPTIONS".equalsIgnoreCase(method)) {
            return false;
        }
        String uri = request.getRequestURI();
        return !uri.startsWith("/api/log") && !uri.startsWith("/api/upload");
    }

    private SysUser currentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        Object value = session.getAttribute("loginUser");
        return value instanceof SysUser ? (SysUser) value : null;
    }

    private String resolveModule(String uri) {
        if (uri.startsWith("/api/user")) return "用户管理";
        if (uri.startsWith("/api/course")) return "课程管理";
        if (uri.startsWith("/api/category")) return "分类管理";
        if (uri.startsWith("/api/classroom")) return "教室管理";
        if (uri.startsWith("/api/product")) return "商品管理";
        if (uri.startsWith("/api/stockRecord")) return "库存管理";
        if (uri.startsWith("/api/order")) return "订单管理";
        if (uri.startsWith("/api/booking")) return "预约管理";
        if (uri.startsWith("/api/studentPackage")) return "课时包管理";
        if (uri.startsWith("/api/activity")) return "活动管理";
        if (uri.startsWith("/api/registration")) return "报名管理";
        if (uri.startsWith("/api/certificate")) return "证书管理";
        if (uri.startsWith("/api/banner")) return "轮播图管理";
        if (uri.startsWith("/api/teacherSlot")) return "排课管理";
        if (uri.startsWith("/api/qualification")) return "教师资质";
        if (uri.startsWith("/api/classRecord")) return "上课记录";
        if (uri.startsWith("/api/auth/password")) return "账号安全";
        if (uri.startsWith("/api/auth/profile")) return "个人中心";
        if (uri.startsWith("/api/auth/logout")) return "登录退出";
        return "系统操作";
    }

    private String resolveOperation(HttpServletRequest request) {
        String method = request.getMethod();
        String uri = request.getRequestURI();
        if ("POST".equalsIgnoreCase(method)) {
            return "新增" + resolveModule(uri);
        }
        if ("PUT".equalsIgnoreCase(method)) {
            if (uri.contains("/pay/")) return "支付订单";
            if (uri.contains("/refund/")) return "取消订单";
            if (uri.contains("/approve/")) return "审批报名";
            if (uri.contains("/result/")) return "录入成绩";
            if (uri.contains("/resetPwd/")) return "重置密码";
            if (uri.contains("/status/")) return "修改状态";
            return "修改" + resolveModule(uri);
        }
        if ("DELETE".equalsIgnoreCase(method)) {
            return "删除" + resolveModule(uri);
        }
        return method + " " + resolveModule(uri);
    }

    private String resolveParams(HttpServletRequest request) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("query", request.getQueryString());
        data.put("params", request.getParameterMap());
        String text = JSON.toJSONString(data);
        return text.length() > 2000 ? text.substring(0, 2000) : text;
    }

    private String resolveClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.trim().isEmpty()) {
            return forwarded.split(",")[0].trim();
        }
        String realIp = request.getHeader("X-Real-IP");
        if (realIp != null && !realIp.trim().isEmpty()) {
            return realIp.trim();
        }
        return request.getRemoteAddr();
    }
}
