package com.tangclub.controller;

import com.tangclub.dto.ApiResponse;
import com.tangclub.dto.SurveyRequest;
import com.tangclub.service.SurveyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/survey")
@CrossOrigin(origins = "*")
public class SurveyController {

    private final SurveyService surveyService;

    public SurveyController(SurveyService surveyService) {
        this.surveyService = surveyService;
    }

    /**
     * 提交调查问卷
     * 前端调用: POST /api/survey/add
     * 请求体: JSON
     * 成功返回: { success: true, data: "会员编号" }
     */
    @PostMapping("/add")
    public ApiResponse<String> addSurvey(@RequestBody SurveyRequest request) {
        log.info("收到问卷提交: name={}, phone={}", request.getName(), request.getPhone());

        try {
            String memberNumber = surveyService.submitSurvey(request);
            log.info("问卷提交成功, 会员编号: {}", memberNumber);
            return ApiResponse.ok(memberNumber);
        } catch (Exception e) {
            log.error("问卷提交失败", e);
            return ApiResponse.fail("提交失败: " + e.getMessage());
        }
    }
}
