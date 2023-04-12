package mx.nmp.mipp.customer.job.engine.batch.job.samplejob;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JobCompletionNotificationListener implements JobExecutionListener {

    @Override
    public void afterJob(JobExecution jobExecution) {

        log.info("Job: <{}> with instanceId: <{}> completed with status: <{}>",
                jobExecution.getJobInstance().getJobName(),
                jobExecution.getJobInstance().getInstanceId(),
                jobExecution.getStatus());

    }

}
