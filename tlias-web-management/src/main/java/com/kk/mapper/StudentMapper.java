package com.kk.mapper;

import com.kk.pojo.Student;
import org.apache.ibatis.annotations.Delete;

import java.util.List;

public interface StudentMapper {
    /**
     * 条件查询学员列表（联查班级表）
     */
    List<Student> list(String name, String no, Integer clazzId);

    /**
     * 根据ID查询
     */
    Student getById(Integer id);


    @Delete("delete from student where id = #{id}")
    void delete(Integer id);

    void insert(Student student);

    void update(Student student);

    void violation(Integer id, Integer score);

    /**
     * 批量删除学员
     * @param ids 学员ID集合
     */
    void deleteBatch(List<Integer> ids);

}

