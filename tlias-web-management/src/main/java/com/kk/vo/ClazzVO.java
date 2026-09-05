package com.kk.vo;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ClazzVO {
    private Integer id;
    private String name;
    private String room;
    private LocalDate beginDate;
    private LocalDate endDate;
    private Integer masterId;
    private String masterName; // 班主任姓名，关联查询出来
    private Integer subject;
    private String status;     // 班级状态：已开班/未开班
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
