package mx.nmp.mipp.customer.job.engine.batch.api.exception;

import lombok.*;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Generated
public class ErrorPayload {

    private String codigo;

    private String detalle;

    private List<String> error;

}
