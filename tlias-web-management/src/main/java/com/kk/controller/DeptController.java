package com.kk.controller;


import com.kk.pojo.Dept;
import com.kk.pojo.Result;
import com.kk.service.DeptService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RequestMapping("/depts")
@RestController
public class DeptController {
    @Autowired
    private DeptService deptService;

    @GetMapping
    public Result list(){
        System.out.println("查询全部的部门");
        List<Dept> deptList=deptService.findAll();
        return Result.success(deptList);
    }

    //删除部门
    @DeleteMapping
    public Result delete(Integer id){
        System.out.println("删除部门");
        deptService.deleteById(id);
        return Result.success();
    }

    //新增部门
    @PostMapping
    public Result add(@RequestBody Dept dept){
        System.out.println("新增部门");
        deptService.add(dept);
        return Result.success();
    }

    //根据id查询部门
    @GetMapping("{id}")
    public Result getInfo(@PathVariable Integer id){
        System.out.println("根据id查询部门");
        Dept dept=deptService.getById(id);
        return Result.success(dept);
    }


    //修改数据
    @PutMapping
    public Result update(@RequestBody Dept dept){
        System.out.println("修改数据");
        deptService.update(dept);
        return Result.success();
    }
}
