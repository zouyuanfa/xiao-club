package com.tangclub.service;

import com.tangclub.entity.Survey;
import com.tangclub.repository.SurveyRepository;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SurveyExportServiceTest {

    @Test
    void exportsSurveyDataAsFormattedWorkbook() throws Exception {
        SurveyRepository repository = mock(SurveyRepository.class);
        Survey survey = createSurvey();
        when(repository.findAll(any(Sort.class))).thenReturn(List.of(survey));

        SurveyExportService service = new SurveyExportService(repository);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        int exportedCount = service.export(outputStream);

        assertThat(exportedCount).isEqualTo(1);
        assertThat(outputStream.size()).isGreaterThan(0);

        try (XSSFWorkbook workbook = new XSSFWorkbook(
                new ByteArrayInputStream(outputStream.toByteArray())
        )) {
            XSSFSheet sheet = workbook.getSheet("问卷数据");

            assertThat(sheet).isNotNull();
            assertThat(sheet.getRow(0).getLastCellNum()).isEqualTo((short) 24);
            assertThat(sheet.getRow(0).getCell(1).getStringCellValue()).isEqualTo("会员编号");
            assertThat(sheet.getRow(1).getCell(1).getStringCellValue())
                    .isEqualTo("TC20260612-0001");
            assertThat(sheet.getRow(1).getCell(2).getStringCellValue()).isEqualTo("测试用户");
            assertThat(sheet.getRow(1).getCell(23).getStringCellValue())
                    .isEqualTo("2026-06-12 14:00:00");
            assertThat(sheet.getPaneInformation().isFreezePane()).isTrue();
            assertThat(sheet.getCTWorksheet().isSetAutoFilter()).isTrue();
            assertThat(sheet.getCTWorksheet().getAutoFilter().getRef()).isEqualTo("A1:X1");
        }
    }

    private Survey createSurvey() {
        Survey survey = new Survey();
        survey.setId(1L);
        survey.setMemberNumber("TC20260612-0001");
        survey.setName("测试用户");
        survey.setGender("男");
        survey.setAge("18-25岁");
        survey.setPhone("123****3344");
        survey.setAdvisor("郭维");
        survey.setLivingArea("郫都区");
        survey.setWorkArea("郫都区");
        survey.setIndustry("自由职业");
        survey.setOccupation("企业中高管");
        survey.setFloorArea("70-90㎡");
        survey.setPreferredArea("100-120㎡");
        survey.setUnitLayoutPreference("三房双卫");
        survey.setPurchaseFunds("理财");
        survey.setPropertyPurchaseCount("二次");
        survey.setAccreditationMetrics("交通配套");
        survey.setTrackedItems("新房-其他,锦江区区二手房");
        survey.setMasterPlanReview("听说过");
        survey.setXichuanCampusLayout("非常了解");
        survey.setXichuanFaculty("听说过");
        survey.setEventInterest("室内多元化运动");
        survey.setCustomerInterests("测试建议");
        survey.setCreatedAt(LocalDateTime.of(2026, 6, 12, 14, 0));
        return survey;
    }
}
