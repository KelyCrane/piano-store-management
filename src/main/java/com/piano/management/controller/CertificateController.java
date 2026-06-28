package com.piano.management.controller;

import com.piano.management.common.Result;
import com.piano.management.entity.Certificate;
import com.piano.management.mapper.CertificateMapper;
import com.piano.management.service.CertificateService;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/certificate")
public class CertificateController {
    @Resource
    private CertificateService certificateService;
    @Resource
    private CertificateMapper certificateMapper;

    @GetMapping
    public Result<?> list(@RequestParam(defaultValue = "1") Integer pageNum,
                          @RequestParam(defaultValue = "10") Integer pageSize,
                          @RequestParam(required = false) Long studentId,
                          @RequestParam(required = false) String type) {
        Certificate param = new Certificate();
        param.setStudentId(studentId);
        param.setType(type);
        List<Certificate> list = certificateMapper.selectPageWithNames(param);
        int total = list.size();
        int from = (pageNum - 1) * pageSize;
        int to = Math.min(from + pageSize, total);
        java.util.Map<String, Object> page = new java.util.HashMap<>();
        page.put("records", from < total ? list.subList(from, to) : list.subList(0, 0));
        page.put("total", total);
        return Result.success(page);
    }

    @PostMapping
    public Result<?> save(@RequestBody Certificate cert) { certificateService.save(cert); return Result.success(); }

    @PutMapping
    public Result<?> update(@RequestBody Certificate cert) { certificateService.updateById(cert); return Result.success(); }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) { certificateService.removeById(id); return Result.success(); }
}