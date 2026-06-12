package com.tangclub.service;

import com.tangclub.dto.SurveyRequest;
import com.tangclub.entity.Survey;
import com.tangclub.repository.SurveyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SurveyService {

    private final SurveyRepository surveyRepository;

    /**
     * 提交问卷并生成会员编号
     */
    @Transactional
    public String submitSurvey(SurveyRequest request) {
        Survey survey = new Survey();

        // 基本信息
        survey.setName(request.getName());
        survey.setGender(request.getGender());
        survey.setAge(request.getAge());
        survey.setPhone(request.getPhone());
        survey.setAdvisor(request.getAdvisor());

        // 区域与职业
        survey.setLivingArea(request.getLivingArea());
        survey.setWorkArea(request.getWorkArea());
        survey.setIndustry(request.getIndustry());
        survey.setOccupation(request.getOccupation());

        // 户型相关
        survey.setFloorArea(request.getFloorArea());
        survey.setPreferredArea(request.getPreferredArea());
        survey.setUnitLayoutPreference(joinList(request.getUnitLayoutPreference()));

        // 购房信息
        survey.setPropertyPurchaseCount(request.getPropertyPurchaseCount());

        // 多选评价
        survey.setAccreditationMetrics(joinList(request.getAccreditationMetrics()));
        survey.setTrackedItems(joinList(request.getTrackedItems()));
        survey.setMasterPlanReview(request.getMasterPlanReview());
        survey.setXichuanCampusLayout(request.getXichuanCampusLayout());
        survey.setEventInterest(joinList(request.getEventInterest()));

        // 建议
        survey.setCustomerInterests(request.getCustomerInterests());

        // 生成会员编号: TC + 日期 + 4位序号
        String memberNumber = generateMemberNumber();
        survey.setMemberNumber(memberNumber);

        surveyRepository.save(survey);

        return memberNumber;
    }

    /**
     * 生成会员编号
     * 格式: TC20260611-0001
     */
    private String generateMemberNumber() {
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = surveyRepository.count() + 1;
        return String.format("TC%s-%04d", dateStr, count);
    }

    /**
     * 将 List<String> 转换为逗号分隔的字符串存储
     */
    private String joinList(List<String> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        return String.join(",", list);
    }
}
