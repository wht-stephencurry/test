package com.elder.preconsult.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.elder.preconsult.entity.Scale;
import org.apache.ibatis.annotations.Mapper;

/**
 * 量表持久层。只做数据访问，禁止在此写业务判断。
 */
@Mapper
public interface ScaleMapper extends BaseMapper<Scale> {
}
