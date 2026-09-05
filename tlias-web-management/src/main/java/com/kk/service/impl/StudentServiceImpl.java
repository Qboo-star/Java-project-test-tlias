package com.kk.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.kk.mapper.StudentMapper;
import com.kk.pojo.PageResult;
import com.kk.pojo.Student;
import com.kk.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentMapper studentMapper;

    @Transactional(rollbackFor = {Exception.class})
    @Override
    public void deleteBatch(List<Integer> ids) {
        studentMapper.deleteBatch(ids);
    }

    @Override
    public PageResult pageQuery(Integer page, Integer pageSize, String name, String no, Integer clazzId) {
        // 开启分页插件
        PageHelper.startPage(page, pageSize);

        // 执行条件查询（联查班级名称）
        List<Student> studentList = studentMapper.list(name, no, clazzId);

        // 封装分页结果
        Page<Student> p = (Page<Student>) studentList;
        return new PageResult(p.getTotal(), p.getResult());
    }

    @Override
    public Student getById(Integer id) {
        return studentMapper.getById(id);
    }

    @Override
    public void delete(Integer id) {
        studentMapper.delete(id);
    }

    @Override
    public void add(Student student) {
        studentMapper.insert(student);
    }

    @Override
    public void update(Student student) {
        studentMapper.update(student);
    }

    @Override
    public void violation(Integer id, Integer score) {
        studentMapper.violation(id, score);
    }
}
