package com.kk.mapper;


import com.kk.pojo.Clazz;
import com.kk.vo.ClazzVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface ClazzMapper {
    /**
     * 条件分页查询班级，关联班主任姓名
     */
    List<ClazzVO> pageQuery(@Param("name") String name,
                            @Param("begin") LocalDate begin,
                            @Param("end") LocalDate end);

    /**
     * 根据ID查询班级
     */
    Clazz getById(Integer id);

    /**
     * 查询所有班级
     */
    List<Clazz> listAll();

    /**
     * 删除班级
     */
    @Delete("delete from clazz where id=#{id}")
    void delete(Integer id);

    /**
     * 添加班级
     */

    void insert(Clazz clazz);

    void update(Clazz clazz);
}
