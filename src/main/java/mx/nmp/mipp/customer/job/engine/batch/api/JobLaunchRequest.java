package mx.nmp.mipp.customer.job.engine.batch.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import mx.nmp.mipp.customer.job.engine.batch.validation.JobLaunchParamCheckConstraint;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Generated
public class JobLaunchRequest {

    @NotBlank(message = "'name' is a required argument")
    private String name;

    @JobLaunchParamCheckConstraint
    private List<JobLaunchParam> parameters;

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    @Builder
    @Generated
    public static class JobLaunchParam {

        @NotBlank(message = "'name' is a required argument")
        private String name;

        @NotNull
        private JobLaunchParamType type;

        private String format;

        @NotBlank
        private String value;

    }

}
