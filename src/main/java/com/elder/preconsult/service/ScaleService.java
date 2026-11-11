package com.elder.preconsult.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.elder.preconsult.common.BusinessException;
import com.elder.preconsult.common.ErrorCode;
import com.elder.preconsult.entity.Scale;
import com.elder.preconsult.mapper.ScaleMapper;
import com.elder.preconsult.vo.ScaleVO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 量表业务层。只返回 VO，不向调用方泄漏 entity。
 */
@Service
public class ScaleService {

    private final ScaleMapper scaleMapper;

    public ScaleService(ScaleMapper scaleMapper) {
        this.scaleMapper = scaleMapper;
    }

    /**
     * 查询启用状态的量表列表，按 id 升序。空列表属于正常业务结果，不抛异常。
     *
     * @param category 可选分类；为 null 或空白时不加筛选条件，行为与原来一致
     */
    public List<ScaleVO> listEnabled(String category) {
        LambdaQueryWrapper<Scale> wrapper = new LambdaQueryWrapper<Scale>()
                .eq(Scale::getStatus, 1);
        if (category != null && !category.isBlank()) {
            wrapper.eq(Scale::getCategory, category.trim());
        }
        wrapper.orderByAsc(Scale::getId);
        List<Scale> scales = scaleMapper.selectList(wrapper);
        return scales.stream().map(this::toVO).toList();
    }

    /**
     * 按编码查询量表，不存在时抛业务异常（错误码 40001）。
     */
    public ScaleVO getByCode(String code) {
        if (code == null || code.isBlank()) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "量表编码不能为空");
        }
        Scale scale = scaleMapper.selectOne(
                new LambdaQueryWrapper<Scale>()
                        .eq(Scale::getCode, code.trim().toUpperCase()));
        if (scale == null) {
            throw new BusinessException(ErrorCode.SCALE_NOT_FOUND, "量表不存在：" + code);
        }
        return toVO(scale);
    }

    private ScaleVO toVO(Scale scale) {
        return new ScaleVO(
                scale.getCode(),
                scale.getName(),
                scale.getCategory(),
                scale.getScoringType(),
                scale.getItemCount());
    }
}
