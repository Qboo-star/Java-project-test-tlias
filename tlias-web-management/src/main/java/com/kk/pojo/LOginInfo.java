package com.kk.pojo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@Data
@AllArgsConstructor
@NoArgsConstructor
/*封装登录结果*/
public class LOginInfo {
    private Integer id; // 用户ID
    private String username; // 用户名
    private String name; // 姓名
    private String token; // 登录令牌
}
