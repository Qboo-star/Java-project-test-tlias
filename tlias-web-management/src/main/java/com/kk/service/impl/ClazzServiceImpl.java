package com.kk.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.kk.mapper.ClazzMapper;
import com.kk.pojo.Clazz;
import com.kk.pojo.PageResult;
import com.kk.service.ClazzService;
import com.kk.vo.ClazzVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ClazzServiceImpl implements ClazzService {

    @Autowired
    private ClazzMapper clazzMapper;

    @Override
    public PageResult pageQuery(Integer page, Integer pageSize, String name, LocalDate begin, LocalDate end) {
        // 1. 开启分页
        PageHelper.startPage(page, pageSize);

        // 2. 执行条件查询
        List<ClazzVO> list = clazzMapper.pageQuery(name, begin, end);

        // 3. 遍历计算每条数据的班级状态
        LocalDate today = LocalDate.now();
        for (ClazzVO vo : list) {
            if (today.isBefore(vo.getBeginDate())) {
                vo.setStatus("未开班");
            } else {
                vo.setStatus("已开班");
            }
        }

        // 4. 封装分页结果
        Page<ClazzVO> p = (Page<ClazzVO>) list;
        return new PageResult(p.getTotal(), p.getResult());
    }

    @Override
    public Clazz getById(Integer id) {
        return clazzMapper.getById(id);
    }

    @Override
    public List<Clazz> listAll() {
        return clazzMapper.listAll();
    }

    @Override
    public void delete(Integer id) {
        clazzMapper.delete(id);
    }

    @Override
    public void add(Clazz clazz) {
        LocalDateTime now = LocalDateTime.now();
        clazz.setCreateTime(now);
        clazz.setUpdateTime(now);
        clazzMapper.insert(clazz);
    }

    @Override
    public void update(Clazz clazz) {
        clazzMapper.update(clazz);
    }
}
