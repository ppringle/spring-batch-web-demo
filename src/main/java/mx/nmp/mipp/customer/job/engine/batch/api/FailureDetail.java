package mx.nmp.mipp.customer.job.engine.batch.api;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Generated
public class FailureDetail {

    private Class<?> exceptionType;

    private String message;

}