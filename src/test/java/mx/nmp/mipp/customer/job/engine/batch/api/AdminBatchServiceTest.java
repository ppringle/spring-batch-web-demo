package mx.nmp.mipp.customer.job.engine.batch.api;

import mx.nmp.mipp.customer.job.engine.batch.api.JobLaunchRequest.JobLaunchParam;
import org.javatuples.Pair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.batch.core.*;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {AdminBatchService.class, AdminBatchServiceTest.Config.class})
class AdminBatchServiceTest {

    private final String JOB_NAME = "sampleJob";

    @Autowired
    private AdminBatchService adminBatchService;

    @Autowired
    private Job sampleJob;

    @Autowired
    private JobLaunchParamValidator jobLaunchParamValidator;

    @Autowired
    private JobLauncher jobLauncher;

    @Configuration
    public static class Config {

        @Bean
        @Primary
        public Job sampleJob() {
            return Mockito.mock(Job.class);
        }

        @Bean
        @Primary
        public JobLaunchParamValidator testJobLaunchParamValidator() {
            return Mockito.mock(JobLaunchParamValidator.class);
        }

        @Bean
        @Primary
        public JobLauncher testJobLauncher() {
            return Mockito.mock(JobLauncher.class);
        }

        @Bean
        @Primary
        public JobExplorer testJobExplorer() {
            return Mockito.mock(JobExplorer.class);
        }

    }

    @Test
    void launchJob_shouldNotLaunchJob_ifJobParameterValidationFails() {

        doThrow(JobLaunchParamValidatorException.class).when(jobLaunchParamValidator)
                .validateJobParameter(any(JobLaunchParam.class));

        JobLaunchParam jobLaunchParam = JobLaunchParam.builder()
                .name("totalRepetitions")
                .type(JobLaunchParamType.NUMBER)
                .value("two")
                .build();

        assertThrows(JobLaunchParamValidatorException.class, () -> adminBatchService.launchJob(JOB_NAME,
                Collections.singletonList(jobLaunchParam)));

        verifyNoInteractions(jobLauncher);

        verify(jobLaunchParamValidator, times(1)).validateJobParameter(eq(jobLaunchParam));

    }

    @Test
    void launchJob_shouldNotLaunchJob_ifJobReferenceCannotBeFoundInContext() {

        JobLaunchParam jobLaunchParam = JobLaunchParam.builder()
                .name("totalRepetitions")
                .type(JobLaunchParamType.NUMBER)
                .value("2")
                .build();

        String invalidJobName = JOB_NAME + "-invalid";

        setExpectionsForJobLaunchParameterValidator();

        assertThrows(IllegalArgumentException.class, () -> adminBatchService.launchJob(invalidJobName,
                Collections.singletonList(jobLaunchParam)));

        verifyNoInteractions(jobLauncher);

        verify(jobLaunchParamValidator, times(1)).validateJobParameter(eq(jobLaunchParam));

    }

    @Test
    void launchJob_withValidJobNameAndParameters_shouldLaunchJob() throws JobInstanceAlreadyCompleteException,
            JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException, ParseException {

        String runDateStringParam = "2022-04-03";

        List<JobLaunchParam> jobLaunchParamList = List.of(
                JobLaunchParam.builder()
                        .name("totalRepetitions")
                        .type(JobLaunchParamType.NUMBER)
                        .value("2")
                        .build(),
                JobLaunchParam.builder()
                        .name("subject")
                        .type(JobLaunchParamType.STRING)
                        .value("astronomy")
                        .build(),
                JobLaunchParam.builder()
                        .name("runDate")
                        .type(JobLaunchParamType.DATE)
                        .value(runDateStringParam)
                        .build()
        );

        setExpectionsForJobLaunchParameterValidator();

        JobExecution jobExecution = mock(JobExecution.class);
        when(jobExecution.getId()).thenReturn(1L);

        when(jobLauncher.run(any(Job.class), any(JobParameters.class))).thenReturn(jobExecution);

        long jobExecutionId = adminBatchService.launchJob("sampleJob", jobLaunchParamList);
        assertThat(jobExecutionId).isEqualTo(1L);

        ArgumentCaptor<JobParameters> jobParametersCaptor = ArgumentCaptor.forClass(JobParameters.class);

        verify(jobLauncher, times(1)).run(eq(sampleJob), jobParametersCaptor.capture());

        JobParameters jobParams = jobParametersCaptor.getValue();
        assertThat(jobParams).isNotNull();

        Map<String, JobParameter<?>> jobParameterMap = jobParams.getParameters();
        assertThat(jobParameterMap).hasSize(3);
        assertThat(jobParameterMap.keySet()).containsExactly("totalRepetitions", "subject",
                "runDate");

        JobParameter<Long> totalRepetitionsJobParameter = (JobParameter<Long>) jobParameterMap.get("totalRepetitions");
        assertThat(totalRepetitionsJobParameter).isNotNull();
        assertThat(totalRepetitionsJobParameter.getType()).isEqualTo(Long.class);
        assertThat(totalRepetitionsJobParameter.getValue()).isEqualTo(2);

        JobParameter<String> subjectJobParameter = (JobParameter<String>) jobParameterMap.get("subject");
        assertThat(subjectJobParameter).isNotNull();
        assertThat(subjectJobParameter.getType()).isEqualTo(String.class);
        assertThat(subjectJobParameter.getValue()).isEqualTo("astronomy");

        JobParameter<Date> runDateJobParameter = (JobParameter<Date>) jobParameterMap.get("runDate");
        assertThat(runDateJobParameter).isNotNull();
        assertThat(runDateJobParameter.getType()).isEqualTo(Date.class);
        assertThat(runDateJobParameter.getValue()).isEqualTo(new SimpleDateFormat("yyyy-MM-dd").parse(runDateStringParam));

    }

    private void setExpectionsForJobLaunchParameterValidator() {

        when(jobLaunchParamValidator.validateJobParameter(any(JobLaunchParam.class))).thenAnswer((Answer) invocation -> {
            JobLaunchParam jobLaunchParam = invocation.getArgument(0, JobLaunchParam.class);

            Pair pair = null;

            if (jobLaunchParam.getType() == JobLaunchParamType.STRING) {
                pair = new Pair(jobLaunchParam.getName(), jobLaunchParam.getValue());
            }

            if (jobLaunchParam.getType() == JobLaunchParamType.NUMBER) {
                pair = new Pair(jobLaunchParam.getName(), Long.parseLong(jobLaunchParam.getValue()));
            }

            if (jobLaunchParam.getType() == JobLaunchParamType.DATE) {
                pair = new Pair(jobLaunchParam.getName(),
                        new SimpleDateFormat("yyyy-MM-dd").parse(jobLaunchParam.getValue()));
            }

            return pair;
        });

    }

}