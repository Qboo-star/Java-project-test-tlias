package com.kk.service;

import com.kk.pojo.PageResult;
import com.kk.pojo.Student;

import java.util.List;

public interface StudentService {
    /**
     * 条件分页查询学员
     */
    PageResult pageQuery(Integer page, Integer pageSize, String name, String no, Integer clazzId);

    /**
     * 根据ID查询学员详情
     */
    Student getById(Integer id);

    void delete(Integer id);

    void add(Student student);

    void update(Student student);

    void violation(Integer id, Integer score);

    void deleteBatch(List<Integer> ids);
}
