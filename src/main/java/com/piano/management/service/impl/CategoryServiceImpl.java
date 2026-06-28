package com.piano.management.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.piano.management.entity.Category;
import com.piano.management.mapper.CategoryMapper;
import com.piano.management.service.CategoryService;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
}