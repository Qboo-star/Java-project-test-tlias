package com.kk.controller;

import com.kk.mapper.ReportMapper;
import com.kk.pojo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据统计报表接口
 */
@RestController
@RequestMapping("/report")
public class ReportController {

    @Autowired
    private ReportMapper reportMapper;

    // 员工性别统计：[{name:"男性员工", value:5}, ...]
    @GetMapping("/empGenderData")
    public Result empGenderData() {
        List<Map<String, Object>> list = reportMapper.empGenderData();
        return Result.success(list);
    }

    // 员工职位人数统计：{jobList:[...], dataList:[...]}
    @GetMapping("/empJobData")
    public Result empJobData() {
        List<Map<String, Object>> rows = reportMapper.empJobData();
        // 职位映射：1班主任,2讲师,3学工主管,4教研主管,5咨询师,其他
        Map<Integer, String> jobMap = new HashMap<>();
        jobMap.put(1, "班主任");
        jobMap.put(2, "讲师");
        jobMap.put(3, "学工主管");
        jobMap.put(4, "教研主管");
        jobMap.put(5, "咨询师");
        jobMap.put(0, "其他");

        // 确保所有职位都出现在结果中（即便为 0）
        List<String> jobList = new ArrayList<>();
        List<Integer> dataList = new ArrayList<>();
        // 固定展示顺序
        int[] order = {1, 2, 3, 4, 5, 0};
        for (int job : order) {
            jobList.add(jobMap.get(job));
            int count = 0;
            for (Map<String, Object> row : rows) {
                Number jobNum = (Number) row.get("job");
                Number cnt = (Number) row.get("cnt");
                int j = jobNum == null ? 0 : jobNum.intValue();
                if (j == job) {
                    count = cnt == null ? 0 : cnt.intValue();
                    break;
                }
            }
            dataList.add(count);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("jobList", jobList);
        result.put("dataList", dataList);
        return Result.success(result);
    }

    // 学员学历统计：[{name:"初中", value:5}, ...]
    @GetMapping("/studentDegreeData")
    public Result studentDegreeData() {
        List<Map<String, Object>> rows = reportMapper.studentDegreeData();
        // 学历映射：1初中,2高中,3大专,4本科,5硕士,6博士
        Map<Integer, String> degreeMap = new HashMap<>();
        degreeMap.put(1, "初中");
        degreeMap.put(2, "高中");
        degreeMap.put(3, "大专");
        degreeMap.put(4, "本科");
        degreeMap.put(5, "硕士");
        degreeMap.put(6, "博士");

        List<Map<String, Object>> list = new ArrayList<>();
        int[] order = {1, 2, 3, 4, 5, 6};
        for (int degree : order) {
            int count = 0;
            for (Map<String, Object> row : rows) {
                Number d = (Number) row.get("degree");
                Number c = (Number) row.get("cnt");
                int dv = d == null ? 0 : d.intValue();
                if (dv == degree) {
                    count = c == null ? 0 : c.intValue();
                    break;
                }
            }
            if (count > 0) {
                Map<String, Object> item = new HashMap<>();
                item.put("name", degreeMap.get(degree));
                item.put("value", count);
                list.add(item);
            }
        }
        return Result.success(list);
    }

    // 班级人数统计：{clazzList:[...], dataList:[...]}
    @GetMapping("/studentCountData")
    public Result studentCountData() {
        List<Map<String, Object>> rows = reportMapper.studentCountData();
        List<String> clazzList = new ArrayList<>();
        List<Integer> dataList = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            clazzList.add((String) row.get("clazzName"));
            Number cnt = (Number) row.get("cnt");
            dataList.add(cnt == null ? 0 : cnt.intValue());
        }
        Map<String, Object> result = new HashMap<>();
        result.put("clazzList", clazzList);
        result.put("dataList", dataList);
        return Result.success(result);
    }
}
