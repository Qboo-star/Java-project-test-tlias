package com.kk.controller;


import com.kk.pojo.Emp;
import com.kk.pojo.PageResult;
import com.kk.pojo.Result;
import com.kk.service.EmpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping(("/emps"))
@RestController
public class EmpController {
    @Autowired
    private EmpService empService;
    @GetMapping
    public Result page(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer pageSize){
        log.info("分页查询：{},{}",page,pageSize);
        PageResult<Emp> pageResultesult = empService.page(page, pageSize);
        return Result.success(pageResultesult);
    }
}
