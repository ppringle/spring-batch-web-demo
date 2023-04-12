package mx.nmp.mipp.customer.job.engine.batch.validation;

import io.micrometer.common.util.StringUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import mx.nmp.mipp.customer.job.engine.batch.api.JobLaunchParamType;
import mx.nmp.mipp.customer.job.engine.batch.api.JobLaunchRequest;
import org.springframework.util.CollectionUtils;

import java.util.List;

public class JobLaunchParamCheckConstraintValidator implements ConstraintValidator<JobLaunchParamCheckConstraint,
        List<JobLaunchRequest.JobLaunchParam>> {

    @Override
    public boolean isValid(List<JobLaunchRequest.JobLaunchParam> jobLaunchParams,
                           ConstraintValidatorContext constraintValidatorContext) {

        boolean isValid = true;

        if (!CollectionUtils.isEmpty(jobLaunchParams)) {
            boolean invalidJobParamExists = jobLaunchParams.stream().anyMatch(c ->
                    (StringUtils.isBlank(c.getName()) || StringUtils.isBlank(c.getValue()) || c.getType() == null)
                            || (c.getType() == JobLaunchParamType.DATE && StringUtils.isBlank(c.getFormat())));

            if(invalidJobParamExists) {
                isValid = false;
            }

        }

        return isValid;
    }

}
