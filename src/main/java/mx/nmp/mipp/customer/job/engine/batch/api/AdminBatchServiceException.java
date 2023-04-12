package mx.nmp.mipp.customer.job.engine.batch.api;

public class AdminBatchServiceException extends RuntimeException {

    public AdminBatchServiceException(String message) {
        super(message);
    }

    public AdminBatchServiceException(String message, Throwable t) {
        super(message, t);
    }

}
