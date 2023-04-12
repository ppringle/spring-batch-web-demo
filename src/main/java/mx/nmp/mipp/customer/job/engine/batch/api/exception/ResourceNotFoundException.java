package mx.nmp.mipp.customer.job.engine.batch.api.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message){
        super(message);
    }

}
