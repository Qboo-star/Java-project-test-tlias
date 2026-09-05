package com.kk.controller;

import com.kk.pojo.PageResult;
import com.kk.pojo.Result;
import com.kk.pojo.Student;
import com.kk.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    /**
     * 1. 学员条件分页查询
     * GET /students?name=xxx&no=xxx&clazzId=1&page=1&pageSize=10
     */
    @GetMapping
    public Result pageQuery(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String no,
            @RequestParam(required = false) Integer clazzId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {

        PageResult pageResult = studentService.pageQuery(page, pageSize, name, no, clazzId);
        return Result.success(pageResult);
    }

    /**
     * 2. 根据ID查询学员详情（编辑弹窗回显用）
     * GET /students/{id}
     */
    @GetMapping("/{id}")
    public Result getById(@PathVariable Integer id) {
        Student student = studentService.getById(id);
        return Result.success(student);
    }

    //删除学员
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        studentService.delete(id);
        return Result.success();
    }

    //添加学员
    @PostMapping
    public Result add(@RequestBody Student student) {
        studentService.add(student);
        return Result.success();
    }

    //修改学员
    @PutMapping
    public Result update(@RequestBody Student student) {
        studentService.update(student);
        return Result.success();
    }

    //违纪处理
    @PutMapping("/violation/{id}/{score}")
    public Result violation(@PathVariable Integer id, @PathVariable Integer score) {
        studentService.violation(id, score);
        return Result.success();
    }

    /**
     * 4. 批量删除学员
     * DELETE /students
     * 请求体：[1, 2, 3]
     */
    @DeleteMapping
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        studentService.deleteBatch(ids);
        return Result.success();
    }

}
