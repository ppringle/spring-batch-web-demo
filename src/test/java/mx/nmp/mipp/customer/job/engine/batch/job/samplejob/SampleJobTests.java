package mx.nmp.mipp.customer.job.engine.batch.job.samplejob;

import mx.nmp.mipp.customer.job.engine.batch.job.samplejob.config.SampleJobConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.sql.DataSource;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBatchTest
@ContextConfiguration(classes = {SampleJobTests.Config.class, SampleJobConfig.class,
        JobCompletionNotificationListener.class})

@ActiveProfiles("localtest")
public class SampleJobTests {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private JobRepositoryTestUtils jobRepositoryTestUtils;

    @Autowired
    private Job sampleJob;

    @Configuration
    @EnableBatchProcessing
    static class Config {

        @Bean
        public DataSource dataSource() {
            return new EmbeddedDatabaseBuilder()
                    .setType(EmbeddedDatabaseType.H2)
                    .addScript("/org/springframework/batch/core/schema-h2.sql")
                    .build();
        }

        @Bean
        @Primary
        public DataSourceTransactionManager transactionManager(@Qualifier("dataSource") DataSource batchDatasource) {
            return new DataSourceTransactionManager(batchDatasource);
        }

    }

    @AfterEach
    public void cleanUp() {
        jobRepositoryTestUtils.removeJobExecutions();
    }

    @Test
    public void sampleJobRunsSuccessfully() throws Exception {

        jobLauncherTestUtils.setJob(sampleJob);

        JobExecution jobExecution = jobLauncherTestUtils.launchJob();
        assertThat(jobExecution).isNotNull();
        assertThat(jobExecution.getJobInstance().getJobName()).isEqualTo("sampleJob");
        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);

        ArrayList<StepExecution> stepExecutions = new ArrayList<>(jobExecution.getStepExecutions());
        assertThat(stepExecutions).hasSize(1);
        assertThat(stepExecutions.get(0).getStepName()).isEqualTo("exportCustomerRecordsToStdOut");
        assertThat(stepExecutions.get(0).getCommitCount()).isEqualTo(1);
        assertThat(stepExecutions.get(0).getReadCount()).isEqualTo(5);
        assertThat(stepExecutions.get(0).getProcessSkipCount()).isEqualTo(0);
        assertThat(stepExecutions.get(0).getWriteCount()).isEqualTo(5);
        assertThat(stepExecutions.get(0).getStatus()).isEqualTo(BatchStatus.COMPLETED);

    }

}
