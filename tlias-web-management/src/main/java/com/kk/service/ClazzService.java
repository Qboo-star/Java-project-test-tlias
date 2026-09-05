package com.kk.service;

import com.kk.pojo.Clazz;
import com.kk.pojo.PageResult;

import java.time.LocalDate;
import java.util.List;

public interface ClazzService {
    /**
     * 条件分页查询
     */
    PageResult pageQuery(Integer page, Integer pageSize, String name, LocalDate begin, LocalDate end);

    /**
     * 根据ID查询
     */
    Clazz getById(Integer id);

    /**
     * 查询所有班级
     */
    List<Clazz> listAll();

    void delete(Integer id);

    void add(Clazz clazz);

    void update(Clazz clazz);
}
