package com.tangclub.service;

import com.tangclub.dto.SurveyRequest;
import com.tangclub.entity.Survey;
import com.tangclub.repository.SurveyRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SurveyServiceTest {

    @Test
    void generatesMemberNumberFromRegistrationHourAndPhoneSuffix() {
        SurveyRepository repository = mock(SurveyRepository.class);
        when(repository.save(any(Survey.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SurveyService service = new SurveyService(repository);
        SurveyRequest request = new SurveyRequest();
        request.setPhone("123****3344");

        String beforeHour = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHH"));
        String memberNumber = service.submitSurvey(request);
        String afterHour = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHH"));

        assertThat(memberNumber)
                .endsWith("3344")
                .matches("\\d{14}");
        assertThat(memberNumber.substring(0, 10)).isIn(beforeHour, afterHour);

        ArgumentCaptor<Survey> surveyCaptor = ArgumentCaptor.forClass(Survey.class);
        verify(repository).save(surveyCaptor.capture());
        assertThat(surveyCaptor.getValue().getMemberNumber()).isEqualTo(memberNumber);
    }
}
