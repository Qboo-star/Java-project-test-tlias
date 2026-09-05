package com.kk.controller;

import com.kk.pojo.Clazz;
import com.kk.pojo.PageResult;
import com.kk.pojo.Result;
import com.kk.service.ClazzService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/clazzs")
public class ClazzController {

    @Autowired
    private ClazzService clazzService;

    /**
     * 1. 条件分页查询班级
     * GET /clazzs?name=xxx&begin=xxx&end=xxx&page=1&pageSize=10
     */
    @GetMapping
    public Result pageQuery(@RequestParam(required = false) String name,
                            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end,
                            @RequestParam(defaultValue = "1") Integer page,
                            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult pageResult = clazzService.pageQuery(page, pageSize, name, begin, end);
        return Result.success(pageResult);
    }

    /**
     * 2. 根据ID查询班级
     * GET /clazzs/{id}
     */
    @GetMapping("/{id}")
    public Result getById(@PathVariable Integer id) {
        Clazz clazz = clazzService.getById(id);
        return Result.success(clazz);
    }

    /**
     * 3. 查询所有班级
     * GET /clazzs/list
     */
    @GetMapping("/list")
    public Result listAll() {
        List<Clazz> list = clazzService.listAll();
        return Result.success(list);
    }

    //删除班级
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        clazzService.delete(id);
        return Result.success();
    }

    //添加班级
    @PostMapping
    public Result add(@RequestBody Clazz clazz) {
        clazzService.add(clazz);
        return Result.success();
    }

    //修改班级
    @PutMapping
    public Result update(@RequestBody Clazz clazz) {
        clazzService.update(clazz);
        return Result.success();
    }
}
