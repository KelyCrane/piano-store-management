package com.piano.management.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.piano.management.common.Result;
import com.piano.management.entity.Category;
import com.piano.management.entity.Product;
import com.piano.management.service.CategoryService;
import com.piano.management.service.ProductService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/product")
public class ProductController {
    @Resource
    private ProductService productService;
    @Resource
    private CategoryService categoryService;

    @GetMapping
    public Result<?> list(@RequestParam(defaultValue = "1") Integer pageNum,
                          @RequestParam(defaultValue = "10") Integer pageSize,
                          @RequestParam(required = false) String type,
                          @RequestParam(required = false) String name,
                          @RequestParam(required = false) Long categoryId,
                          @RequestParam(required = false) Integer status) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(type)) wrapper.eq(Product::getType, type);
        if (StringUtils.isNotBlank(name)) wrapper.like(Product::getName, name);
        if (categoryId != null) wrapper.eq(Product::getCategoryId, categoryId);
        if (status != null) wrapper.eq(Product::getStatus, status);
        wrapper.orderByDesc(Product::getCreateTime);
        Page<Product> p = productService.page(new Page<>(pageNum, pageSize), wrapper);
        Map<Long, String> catMap = categoryService.list().stream()
                .collect(Collectors.toMap(Category::getId, Category::getName, (a, b) -> a));
        p.getRecords().forEach(pr -> pr.setCategoryName(catMap.get(pr.getCategoryId())));
        return Result.success(p);
    }

    @GetMapping("/warning")
    public Result<?> warning() {
        return Result.success(productService.list(
                new LambdaQueryWrapper<Product>().apply("stock <= stock_warning")));
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) { return Result.success(productService.getById(id)); }

    @PostMapping
    public Result<?> save(@RequestBody Product product) {
        normalizeProduct(product);
        productService.save(product);
        return Result.success();
    }

    @PutMapping
    public Result<?> update(@RequestBody Product product) {
        normalizeProduct(product);
        productService.updateById(product);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) { productService.removeById(id); return Result.success(); }

    private void normalizeProduct(Product product) {
        if (StringUtils.isBlank(product.getType())) {
            product.setType("TEXTBOOK");
        }
        if (product.getStatus() == null) {
            product.setStatus(1);
        }
        if (product.getStock() == null) {
            product.setStock(0);
        }
    }
}
