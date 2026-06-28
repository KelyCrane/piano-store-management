package com.piano.management.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.piano.management.common.Result;
import com.piano.management.entity.ConsultMessage;
import com.piano.management.entity.SysUser;
import com.piano.management.mapper.ConsultMessageMapper;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/consult")
public class ConsultController {

    @Resource
    private ConsultMessageMapper consultMessageMapper;

    @GetMapping("/messages")
    public Result<?> listMessages(HttpServletRequest request,
                                  @RequestParam Long courseId,
                                  @RequestParam Long teacherId,
                                  @RequestParam Long studentId) {
        SysUser login = (SysUser) request.getSession().getAttribute("loginUser");
        if (!canAccessRoom(login, courseId, teacherId, studentId)) {
            return Result.error(403, "无权查看该会话");
        }
        LambdaQueryWrapper<ConsultMessage> w = new LambdaQueryWrapper<>();
        w.eq(ConsultMessage::getCourseId, courseId)
                .eq(ConsultMessage::getTeacherId, teacherId)
                .eq(ConsultMessage::getStudentId, studentId)
                .orderByAsc(ConsultMessage::getCreateTime)
                .last("LIMIT 200");
        return Result.success(consultMessageMapper.selectList(w));
    }

    @PostMapping("/message")
    public Result<?> saveMessage(HttpServletRequest request, @RequestBody ConsultMessage body) {
        SysUser login = (SysUser) request.getSession().getAttribute("loginUser");
        if (login == null) {
            return Result.error(401, "未登录");
        }
        if (body.getCourseId() == null || body.getTeacherId() == null || body.getStudentId() == null) {
            return Result.error(400, "参数不完整");
        }
        if (!StringUtils.hasText(body.getContent()) || body.getContent().length() > 500) {
            return Result.error(400, "留言内容长度应在 1～500 字");
        }
        if (!canAccessRoom(login, body.getCourseId(), body.getTeacherId(), body.getStudentId())) {
            return Result.error(403, "无权发送消息到该会话");
        }
        ConsultMessage row = new ConsultMessage();
        row.setCourseId(body.getCourseId());
        row.setTeacherId(body.getTeacherId());
        row.setStudentId(body.getStudentId());
        row.setSenderId(login.getId());
        row.setSenderRole("TEACHER".equals(login.getRole()) ? "TEACHER" : "STUDENT");
        row.setContent(body.getContent().trim());
        consultMessageMapper.insert(row);
        return Result.success(row);
    }

    private boolean canAccessRoom(SysUser login, Long courseId, Long teacherId, Long studentId) {
        if (login == null) {
            return false;
        }
        if ("ADMIN".equals(login.getRole())) {
            return true;
        }
        if ("STUDENT".equals(login.getRole())) {
            return login.getId().equals(studentId);
        }
        if ("TEACHER".equals(login.getRole())) {
            return login.getId().equals(teacherId);
        }
        return false;
    }
}
