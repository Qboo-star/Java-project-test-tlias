package com.kk.mapper;

import com.kk.pojo.Emp;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface EmpMapper {
    // 查询员工总数量
    @Select("select count(*) from emp e left join dept d on e.dept_id=d.id")
    public Long count();

    //分页查询
    @Select("select e.*,d.name as deptName from emp e left join dept d on e.dept_id=d.id order by e.update_time desc limit #{start},#{pageSize}")
    public List<Emp> list(Integer start, Integer pageSize);

}
