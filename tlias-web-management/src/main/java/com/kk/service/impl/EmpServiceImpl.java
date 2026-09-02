package com.kk.service.impl;


import com.kk.mapper.EmpMapper;
import com.kk.pojo.Emp;
import com.kk.pojo.PageResult;
import com.kk.service.EmpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmpServiceImpl implements EmpService {
    @Autowired
    private EmpMapper empMapper;

    @Override
    public PageResult<Emp> page(Integer page, Integer pageSize) {
        //1.调用Mapper接口，查询总记录
        Long total = empMapper.count();
        //2.调用Mapper接口，查询当前页数据
        List<Emp> rows = empMapper.list((page - 1) * pageSize, pageSize);
        //3.封装结果PageResult
        return new PageResult<Emp>(total, rows);
    }
}
