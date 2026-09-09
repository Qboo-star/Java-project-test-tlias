package com.kk.controller;


import com.kk.pojo.Emp;
import com.kk.pojo.LOginInfo;
import com.kk.pojo.Result;
import com.kk.service.EmpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
//@RequestMapping("/login")
public class LoginController {
    @Autowired
    private EmpService empService;

    @PostMapping("/login")
    public Result login(@RequestBody Emp emp) {

        log.info("login {}", emp);
        LOginInfo info = empService.login(emp);
        if(info != null)
            return Result.success(info);
        return Result.error("用户名或密码错误");
    }

}
