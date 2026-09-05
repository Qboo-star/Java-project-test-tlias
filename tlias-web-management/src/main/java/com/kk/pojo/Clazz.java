package com.kk.pojo;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class Clazz {
    private Integer id;
    private String name;
    private String room;
    private LocalDate beginDate;   // 开班日期
    private LocalDate endDate;     // 结课日期
    private Integer masterId;      // 班主任ID
    private Integer subject;       // 学科
    private LocalDateTime createTime; // 创建时间
    private LocalDateTime updateTime; // 更新时间
}
