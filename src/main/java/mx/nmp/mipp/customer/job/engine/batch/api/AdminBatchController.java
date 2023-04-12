package mx.nmp.mipp.customer.job.engine.batch.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "/admin/batch")
@RequiredArgsConstructor
public class AdminBatchController {

    private final AdminBatchService adminBatchService;

    @PostMapping("/job")
    public ResponseEntity<Object> launchJob(@RequestBody @Valid JobLaunchRequest jobLaunchRequest) {

        long jobExecutionId = adminBatchService.launchJob(jobLaunchRequest.getName(), jobLaunchRequest.getParameters());

        URI location =
                ServletUriComponentsBuilder.fromCurrentRequest().path("/execution/{id}").buildAndExpand(jobExecutionId).toUri();

        return ResponseEntity.created(location).build();
    }

    @GetMapping("/job/execution/{id}")
    public JobExecutionDetail getJobDetailById(@PathVariable long id) {

        return adminBatchService.getJobExecutionDetail(id);

    }

}