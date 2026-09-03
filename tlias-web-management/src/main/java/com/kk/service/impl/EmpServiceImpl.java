package com.kk.service.impl;


import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.kk.mapper.EmpExprMapper;
import com.kk.mapper.EmpMapper;
import com.kk.pojo.Emp;
import com.kk.pojo.EmpExpr;
import com.kk.pojo.EmpQueryParam;
import com.kk.pojo.PageResult;
import com.kk.service.EmpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmpServiceImpl implements EmpService {
    @Autowired
    private EmpMapper empMapper;

    @Autowired
    private EmpExprMapper empExprMapper;

    /*@Override
    public PageResult<Emp> page(Integer page, Integer pageSize, String name, Integer gender, LocalDate begin, LocalDate end) {
        //1.调用Mapper接口，查询总记录（也需要加搜索条件）
        PageHelper.startPage(page, pageSize);
        //2.调用Mapper接口，查询当前页数据
        List<Emp> empList = empMapper.list(name, gender, begin, end);
        //3.封装结果PageResult
        Page<Emp> p = (Page<Emp>) empList;
        return new PageResult<Emp>(p.getTotal(), p.getResult());
    }*/

    @Override
    public PageResult<Emp> page(EmpQueryParam empQueryParam) {
        //1.调用Mapper接口，查询总记录（也需要加搜索条件）
        PageHelper.startPage(empQueryParam.getPage(), empQueryParam.getPageSize());
        //2.调用Mapper接口，查询当前页数据
        List<Emp> empList = empMapper.list(empQueryParam);
        //3.封装结果PageResult
        Page<Emp> p = (Page<Emp>) empList;
        return new PageResult<Emp>(p.getTotal(), p.getResult());
    }

    @Transactional
    @Override
    public void save(Emp emp) {
        emp.setCreateTime(LocalDateTime.now());
        emp.setUpdateTime(LocalDateTime.now());
        empMapper.insert(emp);

        //保存工作经历
        List<EmpExpr> exprList = emp.getExprList();
        if(!CollectionUtils.isEmpty(exprList)){
            exprList.forEach(expr -> {
                expr.setEmpId(emp.getId());
            });
            empExprMapper.insertBatch(exprList);
        }

    }

    @Transactional(rollbackFor = {Exception.class})
    @Override
    public void delete(List<Integer> ids) {
        //批量删除员工基本信息
        empMapper.deleteByIds(ids);
        //批量删除员工经历
        empExprMapper.deleteByEmpIds(ids);
    }

    @Override
    public Emp getInfo(Integer id) {
        return empMapper.getById(id);
    }
}
