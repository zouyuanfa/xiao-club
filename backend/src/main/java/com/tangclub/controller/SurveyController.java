package com.tangclub.controller;

import com.tangclub.dto.ApiResponse;
import com.tangclub.dto.SurveyRequest;
import com.tangclub.service.SurveyExportService;
import com.tangclub.service.SurveyService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Slf4j
@RestController
@RequestMapping("/api/survey")
public class SurveyController {

    private final SurveyService surveyService;
    private final SurveyExportService surveyExportService;
    private final String exportToken;

    public SurveyController(
            SurveyService surveyService,
            SurveyExportService surveyExportService,
            @Value("${app.export-token:}") String exportToken
    ) {
        this.surveyService = surveyService;
        this.surveyExportService = surveyExportService;
        this.exportToken = exportToken;
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

    @GetMapping("/export")
    public void exportSurveys(
            @RequestHeader(value = "X-Export-Token", required = false) String requestToken,
            HttpServletResponse response
    ) throws IOException {
        if (!isExportAuthorized(requestToken)) {
            log.warn("拒绝未授权的问卷导出请求");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
            return;
        }

        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        String filename = "xiao-club-surveys-" + timestamp + ".xlsx";

        response.setContentType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        );
        response.setHeader(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + filename + "\""
        );

        int exportedCount = surveyExportService.export(response.getOutputStream());
        log.info("问卷导出成功, 共 {} 条", exportedCount);
    }

    private boolean isExportAuthorized(String requestToken) {
        if (exportToken == null || exportToken.isBlank()
                || requestToken == null || requestToken.isBlank()) {
            return false;
        }

        return MessageDigest.isEqual(
                exportToken.getBytes(StandardCharsets.UTF_8),
                requestToken.getBytes(StandardCharsets.UTF_8)
        );
    }
}
