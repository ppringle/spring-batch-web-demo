package mx.nmp.mipp.customer.job.engine.batch.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.nmp.mipp.customer.job.engine.batch.api.JobExecutionDetail.StepExecutionDetail;
import mx.nmp.mipp.customer.job.engine.batch.api.JobLaunchRequest.JobLaunchParam;
import mx.nmp.mipp.customer.job.engine.batch.api.exception.ResourceNotFoundException;
import org.javatuples.Pair;
import org.springframework.batch.core.*;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminBatchService implements ApplicationContextAware {

    private final JobLauncher jobLauncher;

    private final JobExplorer jobExplorer;

    private final JobLaunchParamValidator jobLaunchParamValidator;

    private ApplicationContext applicationContext;

    public long launchJob(String jobName, List<JobLaunchParam> jobLaunchParamList) {

        JobParameters jobParameters = buildJobParameters(jobLaunchParamList);

        Long jobExecutionId = null;

        Job jobReferenceBean;

        try {
            jobReferenceBean = (Job) this.applicationContext.getBean(jobName);
        } catch (BeansException be) {
            String errorMessage = String.format("Job: <%s> does not exists !", jobName);
            throw new IllegalArgumentException(errorMessage, be);
        }

        try {
            JobExecution jobExecution = jobLauncher.run(jobReferenceBean, jobParameters);
            jobExecutionId = jobExecution.getId();
        } catch (JobExecutionAlreadyRunningException | JobRestartException
                 | JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
            String errorMessage = String.format("The attempt to launch job: <%s> failed !");
            log.error(errorMessage, e);
        }

        return jobExecutionId;
    }

    public JobExecutionDetail getJobExecutionDetail(long executionId) {

        JobExecution jobExecution = jobExplorer.getJobExecution(executionId);

        if (jobExecution == null) {
            String errorMessage = String.format("No job execution reference found with id: <%s>", executionId);
            throw new ResourceNotFoundException(errorMessage);
        }

        List<FailureDetail> failureDetailList = null;

        if (!CollectionUtils.isEmpty(jobExecution.getFailureExceptions())) {
            failureDetailList =
                    jobExecution.getFailureExceptions().stream().map(f ->
                                    FailureDetail.builder()
                                            .exceptionType(f.getClass())
                                            .message(f.getMessage())
                                            .build())
                            .collect(Collectors.toList());
        }

        return JobExecutionDetail.builder()
                .executionId(jobExecution.getId())
                .jobName(jobExecution.getJobInstance().getJobName())
                .jobInstanceId(jobExecution.getJobInstance().getInstanceId())
                .jobParameters(jobExecution.getJobParameters().getParameters())
                .status(StatusDetail.builder()
                        .code(jobExecution.getStatus())
                        .failures(failureDetailList)
                        .build())
                .steps(populateStepDetails(jobExecution.getStepExecutions()))
                .startTime(jobExecution.getStartTime())
                .endTime(jobExecution.getEndTime())
                .build();

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private List<StepExecutionDetail> populateStepDetails(Collection<StepExecution> stepExecutions) {

        List<StepExecutionDetail> stepExecutionDetails = null;

        if (!CollectionUtils.isEmpty(stepExecutions)) {
            stepExecutionDetails = stepExecutions.stream().map(s -> StepExecutionDetail.builder()
                    .stepName(s.getStepName())
                    .status(StatusDetail.builder()
                            .code(s.getStatus())
                            .failures(populateStepFailureDetail(s.getFailureExceptions()))
                            .build())
                    .readCount(s.getReadCount())
                    .writeCount(s.getWriteCount())
                    .commitCount(s.getCommitCount())
                    .rollbackCount(s.getRollbackCount())
                    .readSkipCount(s.getReadSkipCount())
                    .processSkipCount(s.getProcessSkipCount())
                    .writeSkipCount(s.getWriteSkipCount())
                    .filterCount(s.getFilterCount())
                    .startTime(s.getStartTime())
                    .endTime(s.getEndTime())
                    .build())
                    .collect(Collectors.toList());
        }

        return stepExecutionDetails;

    }

    private List<FailureDetail> populateStepFailureDetail(List<Throwable> stepFailures) {

        List<FailureDetail> failureDetailList = null;

        if (!CollectionUtils.isEmpty(stepFailures)) {
            failureDetailList =
                    stepFailures.stream().map(f ->
                                    FailureDetail.builder()
                                            .exceptionType(f.getClass())
                                            .message(f.getMessage())
                                            .build())
                            .collect(Collectors.toList());
        }

        return failureDetailList;
    }

    private JobParameters buildJobParameters(List<JobLaunchParam> jobLaunchParamList) {

        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();

        if (!CollectionUtils.isEmpty(jobLaunchParamList)) {
            jobLaunchParamList.stream().forEach(jlp -> {

                Pair pair = jobLaunchParamValidator.validateJobParameter(jlp);
                String jobParameterName = (String) pair.getValue0();
                Object jobParameterValue = pair.getValue1();

                if (jobParameterValue instanceof String) {
                    jobParametersBuilder.addString(jobParameterName, jobParameterValue != null ?
                            (String) jobParameterValue : null);
                } else if (jobParameterValue instanceof Long) {
                    jobParametersBuilder.addLong(jobParameterName, (Long) jobParameterValue);
                } else if (jobParameterValue instanceof Date) {
                    jobParametersBuilder.addDate(jobParameterName, (Date) jobParameterValue);
                }
            });
        }

        return jobParametersBuilder.toJobParameters();
    }

}
