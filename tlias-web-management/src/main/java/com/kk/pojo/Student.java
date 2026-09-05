package com.kk.pojo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Student {
    private Integer id;
    private String name;
    private String no; //学号
    private Integer gender; //1男 2女
    private String phone;
    private String idCard; //身份证
    private String address;
    private Integer degree; //学历
    private Integer clazzId; //所属班级id

    private Integer violationCount; //违纪次数
    private Integer violationScore; //违纪扣分

    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    //=======【扩展：用于页面回显，数据库没有此字段！！】=======
    //页面表格要展示班级名称，数据库student表只有clazz_id，没有班级名
    private String clazzName;
}
