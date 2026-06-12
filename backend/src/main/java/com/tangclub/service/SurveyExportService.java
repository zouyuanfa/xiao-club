package com.tangclub.service;

import com.tangclub.entity.Survey;
import com.tangclub.repository.SurveyRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.OutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class SurveyExportService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final List<Column> COLUMNS = List.of(
            new Column("ID", survey -> survey.getId()),
            new Column("会员编号", Survey::getMemberNumber),
            new Column("姓名", Survey::getName),
            new Column("性别", Survey::getGender),
            new Column("年龄", Survey::getAge),
            new Column("手机号", Survey::getPhone),
            new Column("置业顾问", Survey::getAdvisor),
            new Column("居住区域", Survey::getLivingArea),
            new Column("工作区域", Survey::getWorkArea),
            new Column("所属行业", Survey::getIndustry),
            new Column("职业", Survey::getOccupation),
            new Column("当前居住面积", Survey::getFloorArea),
            new Column("意向面积", Survey::getPreferredArea),
            new Column("意向楼层", Survey::getPreferredFloor),
            new Column("意向户型", Survey::getUnitLayoutPreference),
            new Column("购房资金", Survey::getPurchaseFunds),
            new Column("付款方式", Survey::getPatType),
            new Column("置业次数", Survey::getPropertyPurchaseCount),
            new Column("项目认可点", Survey::getAccreditationMetrics),
            new Column("了解棠CLUB", Survey::getClubhouseSystem),
            new Column("关注项目", Survey::getTrackedItems),
            new Column("了解城西CID规划", Survey::getMasterPlanReview),
            new Column("感兴趣的活动", Survey::getEventInterest),
            new Column("意见建议", Survey::getCustomerInterests),
            new Column(
                    "提交时间",
                    survey -> survey.getCreatedAt() == null
                            ? ""
                            : DATE_TIME_FORMATTER.format(survey.getCreatedAt())
            )
    );

    private final SurveyRepository surveyRepository;

    @Transactional(readOnly = true)
    public int export(OutputStream outputStream) throws IOException {
        List<Survey> surveys = surveyRepository.findAll(
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        try (SXSSFWorkbook workbook = new SXSSFWorkbook(100)) {
            workbook.setCompressTempFiles(true);
            Sheet sheet = workbook.createSheet("问卷数据");
            sheet.createFreezePane(0, 1);
            sheet.setAutoFilter(new CellRangeAddress(0, 0, 0, COLUMNS.size() - 1));

            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle bodyStyle = createBodyStyle(workbook, false);
            CellStyle alternateBodyStyle = createBodyStyle(workbook, true);

            Row headerRow = sheet.createRow(0);
            headerRow.setHeightInPoints(28);
            for (int columnIndex = 0; columnIndex < COLUMNS.size(); columnIndex++) {
                Cell cell = headerRow.createCell(columnIndex);
                cell.setCellValue(COLUMNS.get(columnIndex).title());
                cell.setCellStyle(headerStyle);
            }

            for (int rowIndex = 0; rowIndex < surveys.size(); rowIndex++) {
                Survey survey = surveys.get(rowIndex);
                Row row = sheet.createRow(rowIndex + 1);
                row.setHeightInPoints(24);
                CellStyle rowStyle = rowIndex % 2 == 0 ? bodyStyle : alternateBodyStyle;

                for (int columnIndex = 0; columnIndex < COLUMNS.size(); columnIndex++) {
                    Cell cell = row.createCell(columnIndex);
                    Object value = COLUMNS.get(columnIndex).valueExtractor().apply(survey);
                    writeValue(cell, value);
                    cell.setCellStyle(rowStyle);
                }
            }

            setColumnWidths(sheet);
            workbook.write(outputStream);
            outputStream.flush();
        }

        return surveys.size();
    }

    private CellStyle createHeaderStyle(SXSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.DARK_TEAL.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        return style;
    }

    private CellStyle createBodyStyle(SXSSFWorkbook workbook, boolean alternate) {
        CellStyle style = workbook.createCellStyle();
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setWrapText(true);
        style.setBorderBottom(BorderStyle.HAIR);
        style.setBorderTop(BorderStyle.HAIR);
        style.setBorderLeft(BorderStyle.HAIR);
        style.setBorderRight(BorderStyle.HAIR);

        if (alternate) {
            style.setFillForegroundColor(IndexedColors.LIGHT_TURQUOISE.getIndex());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }
        return style;
    }

    private void writeValue(Cell cell, Object value) {
        if (value == null) {
            cell.setBlank();
        } else if (value instanceof Number number) {
            cell.setCellValue(number.doubleValue());
        } else {
            cell.setCellValue(value.toString());
        }
    }

    private void setColumnWidths(Sheet sheet) {
        int[] widths = {
                10, 22, 14, 10, 14, 18, 16, 18, 18, 18,
                18, 18, 18, 24, 24, 18, 18, 16, 32, 18,
                36, 22, 36, 42, 22
        };

        for (int columnIndex = 0; columnIndex < widths.length; columnIndex++) {
            sheet.setColumnWidth(columnIndex, widths[columnIndex] * 256);
        }
    }

    private record Column(String title, Function<Survey, Object> valueExtractor) {
    }
}
