package mx.nmp.mipp.customer.job.engine.batch.api;

import lombok.*;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobParameter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Generated
public class JobExecutionDetail {

    private long executionId;

    private String jobName;

    private long jobInstanceId;

    private Map<String, JobParameter<?>> jobParameters;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private StatusDetail status;

    private List<StepExecutionDetail> steps;

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    @Builder
    @Generated
    public static class StepExecutionDetail {

        private String stepName;

        private StatusDetail status;

        private long readCount;

        private long writeCount;

        private long commitCount;

        private long rollbackCount;

        private long readSkipCount;

        private long processSkipCount;

        private long writeSkipCount;

        private long filterCount;

        private LocalDateTime startTime;

        private LocalDateTime endTime;

    }


}
