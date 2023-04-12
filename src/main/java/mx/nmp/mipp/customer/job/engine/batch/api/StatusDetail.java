package mx.nmp.mipp.customer.job.engine.batch.api;

import lombok.*;
import org.springframework.batch.core.BatchStatus;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Generated
public class StatusDetail {

    private BatchStatus code;

    private List<FailureDetail> failures;

}