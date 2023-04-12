package mx.nmp.mipp.customer.job.engine.batch.api;

import mx.nmp.mipp.customer.job.engine.batch.api.JobLaunchRequest.JobLaunchParam;
import org.apache.commons.lang3.StringUtils;
import org.javatuples.Pair;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class JobLaunchParamValidator {

    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";

    public Pair validateJobParameter(JobLaunchParam jobLaunchParam) {

        String parameterName = jobLaunchParam.getName();
        String parameterValue = jobLaunchParam.getValue();
        JobLaunchParamType parameterType = jobLaunchParam.getType();

        Pair jobParameterTuple = null;

        if (parameterType == JobLaunchParamType.STRING) {
            jobParameterTuple = new Pair(parameterName, parameterValue);
        }

        if (parameterType == JobLaunchParamType.NUMBER) {
            try {
                long parameterValueAsLong = Long.parseLong(parameterValue);
                jobParameterTuple = new Pair(parameterName, parameterValueAsLong);
            } catch (NumberFormatException nfe) {
                String errorMessage = String.format("Job parameter: <%s> is not a valid number !",
                        parameterName);
                throw new JobLaunchParamValidatorException(errorMessage, nfe);
            }
        }

        if (parameterType == JobLaunchParamType.DATE) {

            String format = StringUtils.isEmpty(jobLaunchParam.getFormat())
                    ? DEFAULT_DATE_FORMAT : jobLaunchParam.getFormat();

            jobLaunchParam.setFormat(format);

            try {
                if (parameterValue != null) {
                    Date parameterValueAsDate = new SimpleDateFormat(format).parse(parameterValue);
                    jobParameterTuple = new Pair(parameterName, parameterValueAsDate);
                }
            } catch (ParseException pe) {
                String errorMessage = String.format("Job parameter: <%s> is not a valid date based on format: <%s> !",
                        parameterName, format);
                throw new JobLaunchParamValidatorException(errorMessage, pe);
            }
        }

        return jobParameterTuple;
    }

}
