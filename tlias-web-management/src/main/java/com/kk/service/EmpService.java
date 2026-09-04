package com.kk.service;

import com.kk.pojo.Emp;
import com.kk.pojo.EmpQueryParam;
import com.kk.pojo.PageResult;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

public interface EmpService {
    /*PageResult<Emp> page(Integer page, Integer pageSize, String name, Integer gender,
                         LocalDate begin, LocalDate end);*/

    PageResult<Emp> page(EmpQueryParam empQueryParam);

    void save(Emp emp);

    void delete(List<Integer> ids);

    Emp getInfo(Integer id);

    void update(Emp emp);
}
