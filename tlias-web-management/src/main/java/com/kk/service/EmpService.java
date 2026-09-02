package com.kk.service;

import com.kk.pojo.Emp;
import com.kk.pojo.PageResult;

public interface EmpService {
    PageResult<Emp> page(Integer page, Integer pageSize);
}
