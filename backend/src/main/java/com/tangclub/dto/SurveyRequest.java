package com.tangclub.dto;

import lombok.Data;

import java.util.List;

/**
 * 前端提交的调查问卷请求体
 * 多选字段前端以数组形式提交
 */
@Data
public class SurveyRequest {

    private String name;
    private String gender;
    private String age;
    private String phone;
    private String advisor;
    private String livingArea;
    private String workArea;
    private String industry;
    private String occupation;
    private String floorArea;
    private String preferredArea;
    private List<String> unitLayoutPreference;
    private String purchaseFunds;
    private String propertyPurchaseCount;
    private List<String> accreditationMetrics;
    private List<String> trackedItems;
    private String masterPlanReview;
    private String xichuanCampusLayout;
    private String xichuanFaculty;
    private List<String> eventInterest;
    private String customerInterests;
}
