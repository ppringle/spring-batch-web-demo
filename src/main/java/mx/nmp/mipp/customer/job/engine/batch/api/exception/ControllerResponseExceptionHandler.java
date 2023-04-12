package mx.nmp.mipp.customer.job.engine.batch.api.exception;

import mx.nmp.mipp.customer.job.engine.batch.api.AdminBatchServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestControllerAdvice
public class ControllerResponseExceptionHandler extends RuntimeException {

    public static final String ERROR_CODE_BATCH_SERVICE_FAILURE = "BATCH_SERVICE_FAILURE";
    public static final String ERROR_CODE_RESOURCE_NOT_FOUND = "RECURSO_NO_ENCONTRADO";
    public static final String ERROR_CODE_INVALID_ARGUMENT = "INVALID_ARGUMENT";

    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<ErrorPayload> handleResourceNotFoundException(IllegalArgumentException iae) {

        return new ResponseEntity<>(getErrorPayload(ERROR_CODE_INVALID_ARGUMENT,
                Collections.emptyList(), iae.getMessage()), HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler({BindException.class, MethodArgumentNotValidException.class})
    public ResponseEntity<ErrorPayload> handleException(MethodArgumentNotValidException e) {

        List<String> errors = new ArrayList<>();
        e.getFieldErrors()
                .forEach(err -> errors.add(err.getDefaultMessage()));
        e.getGlobalErrors()
                .forEach(err -> errors.add(err.getDefaultMessage()));

        return new ResponseEntity<>(getErrorPayload(ERROR_CODE_INVALID_ARGUMENT,
                errors, "One or more invalid arguments were detected !"), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ResourceNotFoundException.class})
    public ResponseEntity<ErrorPayload> handleResourceNotFoundException(ResourceNotFoundException rnfe) {

        return new ResponseEntity<>(getErrorPayload(ERROR_CODE_RESOURCE_NOT_FOUND,
                Collections.emptyList(), rnfe.getMessage()), HttpStatus.NOT_FOUND);

    }

    @ExceptionHandler({AdminBatchServiceException.class})
    public ResponseEntity<ErrorPayload> handleResourceNotFoundException(AdminBatchServiceException abse) {

        return new ResponseEntity<>(getErrorPayload(ERROR_CODE_BATCH_SERVICE_FAILURE,
                Collections.emptyList(), abse.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);

    }

    private ErrorPayload getErrorPayload(String codigo, List<String> error, String detalle) {
        return ErrorPayload.builder()
                .codigo(codigo)
                .error(error)
                .detalle(detalle)
                .build();
    }

}