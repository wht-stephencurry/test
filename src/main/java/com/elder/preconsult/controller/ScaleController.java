package com.elder.preconsult.controller;

import com.elder.preconsult.common.ApiResponse;
import com.elder.preconsult.service.ScaleService;
import com.elder.preconsult.vo.ScaleVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 量表接口。Controller 只做参数接收与响应包装，不写业务逻辑、不注入 Mapper。
 */
@RestController
@RequestMapping("/api/scales")
public class ScaleController {

    private final ScaleService scaleService;

    public ScaleController(ScaleService scaleService) {
        this.scaleService = scaleService;
    }

    /**
     * 查询启用状态的量表列表。
     *
     * @param category 可选分类；不传则返回全部，传空串等同不传
     */
    @GetMapping
    public ApiResponse<List<ScaleVO>> list(
            @RequestParam(value = "category", required = false) String category) {
        return ApiResponse.ok(scaleService.listEnabled(category));
    }

    /**
     * 按编码查询单个量表，code 不区分大小写。
     */
    @GetMapping("/{code}")
    public ApiResponse<ScaleVO> detail(@PathVariable("code") String code) {
        return ApiResponse.ok(scaleService.getByCode(code));
    }
}
