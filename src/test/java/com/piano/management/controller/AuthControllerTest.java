package com.piano.management.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.piano.management.common.Result;
import com.piano.management.entity.SysUser;
import com.piano.management.exception.BusinessException;
import com.piano.management.service.SysUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    private AuthController controller;
    private SysUserService sysUserService;

    @BeforeEach
    void setUp() {
        controller = new AuthController();
        sysUserService = mock(SysUserService.class);
        ReflectionTestUtils.setField(controller, "sysUserService", sysUserService);
    }

    @Test
    void infoShouldThrowBusinessExceptionWhenUserNoLongerExists() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        SysUser sessionUser = new SysUser();
        sessionUser.setId(1L);
        request.getSession().setAttribute("loginUser", sessionUser);
        when(sysUserService.getById(1L)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class, () -> controller.info(request));
        assertEquals(401, ex.getCode());
    }

    @Test
    void changePasswordShouldThrowBusinessExceptionWhenUserNoLongerExists() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        SysUser sessionUser = new SysUser();
        sessionUser.setId(1L);
        request.getSession().setAttribute("loginUser", sessionUser);
        when(sysUserService.getById(1L)).thenReturn(null);

        Map<String, String> params = new HashMap<>();
        params.put("oldPassword", "123456");
        params.put("newPassword", "654321");

        BusinessException ex = assertThrows(BusinessException.class, () -> controller.changePassword(params, request));
        assertEquals(401, ex.getCode());
    }

    @Test
    void loginShouldSetSessionAndHidePassword() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        Map<String, String> params = new HashMap<>();
        params.put("username", "alice");
        params.put("password", "123456");

        SysUser dbUser = new SysUser();
        dbUser.setId(10L);
        dbUser.setUsername("alice");
        dbUser.setStatus(1);
        dbUser.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        when(sysUserService.getOne(any(LambdaQueryWrapper.class))).thenReturn(dbUser);

        Result<Map<String, Object>> result = controller.login(params, request);

        assertEquals(200, result.getCode());
        assertNotNull(request.getSession(false));
        Object loginUser = request.getSession(false).getAttribute("loginUser");
        assertTrue(loginUser instanceof SysUser);
        assertNull(((SysUser) loginUser).getPassword());
        verify(sysUserService, times(1)).getOne(any(LambdaQueryWrapper.class));
    }
}
