package mx.nmp.mipp.customer.job.engine.batch.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(AdminBatchController.class)
class AdminBatchControllerTest {

    private static final String JOB_NAME = "sampleJob";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AdminBatchService adminBatchService;

    @Test
    @DisplayName("launchJob() with missing 'name' parameter in the request should return HTTP status 400")
    void launchJob_withPayloadMissingJobName_shouldReturn400() throws Exception {

        JobLaunchRequest jobLaunchRequest = JobLaunchRequest.builder().build();
        String payload = objectMapper.writeValueAsString(jobLaunchRequest);

        mockMvc.perform(post("/admin/batch/job")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("INVALID_ARGUMENT"))
                .andExpect(jsonPath("$.error[0]").value("'name' is a required argument"));
    }

    @Test
    @DisplayName("launchJob() with no jobParams in the request should return HTTP status 201")
    void launchJob_withValidPayload_whichIncludesNoJobLaunchParams_shouldReturn201() throws Exception {

        JobLaunchRequest jobLaunchRequest = JobLaunchRequest.builder()
                .name(JOB_NAME)
                .build();

        String payload = objectMapper.writeValueAsString(jobLaunchRequest);

        when(adminBatchService.launchJob(eq(JOB_NAME), eq(null))).thenReturn(5L);

        mockMvc.perform(post("/admin/batch/job")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/admin/batch/job/execution/5")));

    }

    @Test
    @DisplayName("launchJob() with single jobParam 'name' field in the request populated, should return HTTP status " +
            "400")
    void launchJob_withPayload_whichIncludesJobLaunchParam_withOnlyNameProvided_shouldReturn400() throws Exception {

        JobLaunchRequest jobLaunchRequest = JobLaunchRequest.builder()
                .name(JOB_NAME)
                .parameters(Collections.singletonList(JobLaunchRequest.JobLaunchParam.builder()
                        .type(JobLaunchParamType.STRING)
                        .build()))
                .build();

        String payload = objectMapper.writeValueAsString(jobLaunchRequest);

        mockMvc.perform(post("/admin/batch/job")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("INVALID_ARGUMENT"));

    }

    @Test
    @DisplayName("launchJob() with single jobParam 'type' field in the request populated, should return HTTP status " +
            "400")
    void launchJob_withPayload_whichIncludesJobLaunchParam_withOnlyTypeProvided_shouldReturn400() throws Exception {

        JobLaunchRequest jobLaunchRequest = JobLaunchRequest.builder()
                .name(JOB_NAME)
                .parameters(Collections.singletonList(JobLaunchRequest.JobLaunchParam.builder()
                        .type(JobLaunchParamType.STRING)
                        .build()))
                .build();

        String payload = objectMapper.writeValueAsString(jobLaunchRequest);

        mockMvc.perform(post("/admin/batch/job")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("INVALID_ARGUMENT"));

    }

    @Test
    @DisplayName("launchJob() with single jobParam 'value' field in the request populated, should return HTTP status " +
            "400")
    void launchJob_withPayload_whichIncludesJobLaunchParam_withOnlyValueProvided_shouldReturn400() throws Exception {

        JobLaunchRequest jobLaunchRequest = JobLaunchRequest.builder()
                .name(JOB_NAME)
                .parameters(Collections.singletonList(JobLaunchRequest.JobLaunchParam.builder()
                        .type(JobLaunchParamType.STRING)
                        .build()))
                .build();

        String payload = objectMapper.writeValueAsString(jobLaunchRequest);

        mockMvc.perform(post("/admin/batch/job")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("INVALID_ARGUMENT"));

    }

    @Test
    @DisplayName("launchJob() with single jobLaunchParam 'type' field in the request populated, where the param is of" +
            " 'DATE' type and is missing the 'format' field definition, should return HTTP status 400")
    void launchJob_withPayload_whichIncludesJobLaunchParam_withParamOfTypeDate_withNoFormatDefined_shouldReturn400() throws Exception {

        JobLaunchRequest jobLaunchRequest = JobLaunchRequest.builder()
                .name(JOB_NAME)
                .parameters(Collections.singletonList(JobLaunchRequest.JobLaunchParam.builder()
                        .name("runDate")
                        .type(JobLaunchParamType.DATE)
                        .value("2012-10-01")
                        .build()))
                .build();

        String payload = objectMapper.writeValueAsString(jobLaunchRequest);

        mockMvc.perform(post("/admin/batch/job")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("INVALID_ARGUMENT"));

    }

    @Test
    @DisplayName("launchJob() with single jobLaunchParam of 'DATE' type with the 'name', 'type', 'value' and 'format'" +
            " fields populated should return HTTP status 201")
    void launchJob_withPayload_whichIncludesJobLaunchParam_withParamOfTypeDate_withFormatDefined_shouldReturn201() throws Exception {

        JobLaunchRequest jobLaunchRequest = JobLaunchRequest.builder()
                .name(JOB_NAME)
                .parameters(Collections.singletonList(JobLaunchRequest.JobLaunchParam.builder()
                        .name("runDate")
                        .type(JobLaunchParamType.DATE)
                        .value("2012-10-01")
                        .format("yyyy-MM-dd")
                        .build()))
                .build();

        String payload = objectMapper.writeValueAsString(jobLaunchRequest);

        when(adminBatchService.launchJob(eq(JOB_NAME), anyList())).thenReturn(5L);

        mockMvc.perform(post("/admin/batch/job")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/admin/batch/job/execution/5")));

    }

    @Test
    @DisplayName("launchJob() with single jobLaunchParam with all fields populated, should return HTTP status 201 for" +
            " field not of 'DATE' type")
    void launchJob_withPayload_whichIncludesJobLaunchParam_withAllRelevantFieldsPopulated_shouldReturn201() throws Exception {

        JobLaunchRequest jobLaunchRequest = JobLaunchRequest.builder()
                .name(JOB_NAME)
                .parameters(Collections.singletonList(JobLaunchRequest.JobLaunchParam.builder()
                        .name("accountType")
                        .type(JobLaunchParamType.STRING)
                        .value("Chequing")
                        .build()))
                .build();

        String payload = objectMapper.writeValueAsString(jobLaunchRequest);

        when(adminBatchService.launchJob(eq(JOB_NAME), anyList())).thenReturn(5L);

        mockMvc.perform(post("/admin/batch/job")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/admin/batch/job/execution/5")));

    }

}