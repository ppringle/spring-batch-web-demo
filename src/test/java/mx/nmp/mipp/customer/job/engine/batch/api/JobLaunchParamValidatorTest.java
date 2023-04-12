package mx.nmp.mipp.customer.job.engine.batch.api;

import mx.nmp.mipp.customer.job.engine.batch.api.JobLaunchRequest.JobLaunchParam;
import org.javatuples.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class JobLaunchParamValidatorTest {

    private JobLaunchParamValidator jobLaunchParamValidator;

    @BeforeEach
    void init() {
        jobLaunchParamValidator = new JobLaunchParamValidator();
    }


    @Test
    @DisplayName("validateJobParameter() with string parameter should be valid")
    void validateJobParameter_withStringParameter_shouldThrowException() {

        JobLaunchParam jobLaunchParam = JobLaunchParam.builder()
                .name("accountNumber")
                .type(JobLaunchParamType.STRING)
                .value("12345564645")
                .build();

        Pair pair = jobLaunchParamValidator.validateJobParameter(jobLaunchParam);
        assertThat(pair).isNotNull();
        assertThat(pair.getValue0()).isEqualTo("accountNumber");
        assertThat(pair.getValue1()).isEqualTo("12345564645");
    }

    @Test
    @DisplayName("validateJobParameter() with invalid number parameter should throw exception")
    void validateJobParameter_withInvalidLongParameter_shouldThrowException() {

        JobLaunchParam jobLaunchParam = JobLaunchParam.builder()
                .name("accountNumber")
                .type(JobLaunchParamType.NUMBER)
                .value("apple")
                .build();

        JobLaunchParamValidatorException jobLaunchParamValidatorException =
                assertThrows(JobLaunchParamValidatorException.class,
                () -> jobLaunchParamValidator.validateJobParameter(jobLaunchParam));

        assertThat(jobLaunchParamValidatorException.getCause()).isInstanceOf(NumberFormatException.class);

    }

    @Test
    @DisplayName("validateJobParameter() with valid number parameter should be valid")
    void validateJobParameter_withValidLongParameter_shouldValidate() {

        JobLaunchParam jobLaunchParam = JobLaunchParam.builder()
                .name("accountNumber")
                .type(JobLaunchParamType.NUMBER)
                .value("150273923929")
                .build();

        Pair pair = jobLaunchParamValidator.validateJobParameter(jobLaunchParam);
        assertThat(pair).isNotNull();
        assertThat(pair.getValue0()).isEqualTo("accountNumber");
        assertThat(pair.getValue1()).isEqualTo(150273923929L);

    }

    @Test
    @DisplayName("validateJobParameter() with invalid date parameter should throw exception")
    void validateJobParameter_withInvalidJobParameterDateValue_shouldThrowException() {

        JobLaunchParam jobLaunchParam = JobLaunchParam.builder()
                .name("runDate")
                .type(JobLaunchParamType.DATE)
                .value("apple")
                .build();

        JobLaunchParamValidatorException jobLaunchParamValidatorException =
                assertThrows(JobLaunchParamValidatorException.class,
                () -> jobLaunchParamValidator.validateJobParameter(jobLaunchParam));

        assertThat(jobLaunchParamValidatorException.getCause()).isInstanceOf(ParseException.class);

    }

    @Test
    @DisplayName("validateJobParameter() with valid date parameter should be valid")
    void validateJobParameter_withValidJobParameterDateValue_shouldValidate() {

        JobLaunchParam jobLaunchParam = JobLaunchParam.builder()
                .name("runDate")
                .type(JobLaunchParamType.DATE)
                .format("yyyy-MM-dd")
                .value("2021-05-24")
                .build();

        Pair pair = jobLaunchParamValidator.validateJobParameter(jobLaunchParam);
        assertThat(pair).isNotNull();
        assertThat(pair.getValue0()).isEqualTo("runDate");
        assertThat(pair.getValue1()).isInstanceOf(Date.class);

        Calendar cal = Calendar.getInstance();
        cal.setTime((Date) pair.getValue1());

        assertThat(cal.get(Calendar.MONTH)).isEqualTo(4);
        assertThat(cal.get(Calendar.DAY_OF_MONTH)).isEqualTo(24);
        assertThat(cal.get(Calendar.YEAR)).isEqualTo(2021);

    }

}