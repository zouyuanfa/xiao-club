avpackage com.tangclub.service;

import com.tangclub.dto.SurveyRequest;
import com.tangclub.entity.Survey;
import com.tangclub.repository.SurveyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
        // 检查手机号是否已注册
        if (request.getPhone() != null && !request.getPhone().isBlank()
                && surveyRepository.existsByPhone(request.getPhone())) {
            throw new IllegalArgumentException("该手机已注册，请勿重复提交");
        }

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

        // 生成会员编号: 登记日期到小时 + 手机号后四位
        String memberNumber = generateMemberNumber(request.getPhone());
        survey.setMemberNumber(memberNumber);

        surveyRepository.save(survey);

        return memberNumber;
    }

    /**
     * 生成会员编号
     * 格式: 20260618183344
     */
    private String generateMemberNumber(String phone) {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHH"));
        return dateStr + getLastFourDigits(phone);
    }

    private String getLastFourDigits(String phone) {
        if (phone == null) {
            return "0000";
        }

        String digits = phone.replaceAll("\\D", "");
        if (digits.length() >= 4) {
            return digits.substring(digits.length() - 4);
        }
        return String.format("%4s", digits).replace(' ', '0');
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
