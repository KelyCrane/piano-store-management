package com.piano.management.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.piano.management.common.Result;
import com.piano.management.entity.Product;
import com.piano.management.entity.StockRecord;
import com.piano.management.entity.SysUser;
import com.piano.management.exception.BusinessException;
import com.piano.management.service.ProductService;
import com.piano.management.service.StockRecordService;
import com.piano.management.service.SysUserService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/stockRecord")
public class StockRecordController {
    @Resource
    private StockRecordService stockRecordService;
    @Resource
    private ProductService productService;
    @Resource
    private SysUserService sysUserService;

    @GetMapping
    public Result<?> list(@RequestParam(defaultValue = "1") Integer pageNum,
                          @RequestParam(defaultValue = "10") Integer pageSize,
                          @RequestParam(required = false) Long productId,
                          @RequestParam(required = false) String productName,
                          @RequestParam(required = false) Long categoryId,
                          @RequestParam(required = false) String type) {
        LambdaQueryWrapper<StockRecord> wrapper = new LambdaQueryWrapper<>();
        if (productId != null) {
            wrapper.eq(StockRecord::getProductId, productId);
        }
        Set<Long> filteredProductIds = findProductIds(productName, categoryId);
        if (filteredProductIds != null) {
            if (filteredProductIds.isEmpty()) {
                return Result.success(new Page<StockRecord>(pageNum, pageSize));
            }
            wrapper.in(StockRecord::getProductId, filteredProductIds);
        }
        if (type != null && !type.isEmpty()) {
            wrapper.eq(StockRecord::getType, type);
        }
        wrapper.orderByDesc(StockRecord::getCreateTime);
        Page<StockRecord> page = stockRecordService.page(new Page<>(pageNum, pageSize), wrapper);
        fillNames(page.getRecords());
        return Result.success(page);
    }

    @PostMapping
    @Transactional(rollbackFor = Exception.class)
    public Result<?> save(@RequestBody StockRecord record, HttpServletRequest request) {
        validateRecord(record);
        fillOperator(record, request);
        applyStockEffect(record.getProductId(), record.getType(), record.getQuantity(), false);
        stockRecordService.save(record);
        return Result.success();
    }

    @PutMapping
    @Transactional(rollbackFor = Exception.class)
    public Result<?> update(@RequestBody StockRecord record, HttpServletRequest request) {
        if (record.getId() == null) {
            throw new BusinessException("库存记录ID不能为空");
        }
        validateRecord(record);
        StockRecord db = stockRecordService.getById(record.getId());
        if (db == null) {
            throw new BusinessException("库存记录不存在");
        }
        applyStockEffect(db.getProductId(), db.getType(), db.getQuantity(), true);
        applyStockEffect(record.getProductId(), record.getType(), record.getQuantity(), false);
        fillOperator(record, request);
        stockRecordService.updateById(record);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Transactional(rollbackFor = Exception.class)
    public Result<?> delete(@PathVariable Long id) {
        StockRecord db = stockRecordService.getById(id);
        if (db == null) {
            return Result.success();
        }
        applyStockEffect(db.getProductId(), db.getType(), db.getQuantity(), true);
        stockRecordService.removeById(id);
        return Result.success();
    }

    private Set<Long> findProductIds(String productName, Long categoryId) {
        boolean hasName = productName != null && !productName.trim().isEmpty();
        if (!hasName && categoryId == null) {
            return null;
        }
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        if (hasName) {
            wrapper.like(Product::getName, productName.trim());
        }
        if (categoryId != null) {
            wrapper.eq(Product::getCategoryId, categoryId);
        }
        return productService.list(wrapper).stream()
                .map(Product::getId)
                .collect(Collectors.toSet());
    }

    private void fillNames(List<StockRecord> records) {
        if (records == null || records.isEmpty()) {
            return;
        }
        Set<Long> productIds = records.stream()
                .map(StockRecord::getProductId)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, Product> productMap = productIds.isEmpty() ? Collections.emptyMap()
                : productService.listByIds(productIds).stream()
                .collect(Collectors.toMap(Product::getId, p -> p, (a, b) -> a));

        Set<Long> operatorIds = records.stream()
                .map(StockRecord::getOperatorId)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, SysUser> userMap = operatorIds.isEmpty() ? Collections.emptyMap()
                : sysUserService.listByIds(operatorIds).stream()
                .collect(Collectors.toMap(SysUser::getId, u -> u, (a, b) -> a));

        for (StockRecord record : records) {
            Product product = productMap.get(record.getProductId());
            if (product != null) {
                record.setProductName(product.getName());
            }
            SysUser user = userMap.get(record.getOperatorId());
            if (user != null) {
                record.setOperatorName(user.getName() != null ? user.getName() : user.getUsername());
            }
        }
    }

    private void validateRecord(StockRecord record) {
        if (record == null || record.getProductId() == null) {
            throw new BusinessException("商品不能为空");
        }
        if (!"IN".equals(record.getType()) && !"OUT".equals(record.getType())) {
            throw new BusinessException("库存类型不正确");
        }
        if (record.getQuantity() == null || record.getQuantity() <= 0) {
            throw new BusinessException("数量必须大于0");
        }
    }

    private void fillOperator(StockRecord record, HttpServletRequest request) {
        if (record.getOperatorId() != null) {
            return;
        }
        Object value = request.getSession().getAttribute("loginUser");
        if (value instanceof SysUser) {
            record.setOperatorId(((SysUser) value).getId());
        }
    }

    private void applyStockEffect(Long productId, String type, Integer quantity, boolean reverse) {
        Product product = productService.getById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        int stock = product.getStock() == null ? 0 : product.getStock();
        int qty = quantity == null ? 0 : quantity;
        int delta = "IN".equals(type) ? qty : -qty;
        if (reverse) {
            delta = -delta;
        }
        int nextStock = stock + delta;
        if (nextStock < 0) {
            throw new BusinessException("库存不足");
        }
        product.setStock(nextStock);
        productService.updateById(product);
    }
}
