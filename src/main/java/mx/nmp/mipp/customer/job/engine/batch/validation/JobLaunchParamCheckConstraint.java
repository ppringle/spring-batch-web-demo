package mx.nmp.mipp.customer.job.engine.batch.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Constraint(validatedBy = JobLaunchParamCheckConstraintValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface JobLaunchParamCheckConstraint {

    String message() default "If job params are provided, then at a minimum the 'name', 'type' and 'value' fields must be " +
            "populated for each jobParameter. If the 'type' is a 'DATE', then the 'format' parameter should be provided";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}