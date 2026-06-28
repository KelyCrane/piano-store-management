package com.piano.management.interceptor;

import com.alibaba.fastjson.JSON;
import com.piano.management.common.Result;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // OPTIONS 预检请求直接放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("loginUser") != null) {
            return true;
        }

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(401);
        response.getWriter().write(JSON.toJSONString(Result.error(401, "未登录或登录已过期")));
        return false;
    }
}
