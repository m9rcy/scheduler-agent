package com.example.scheduler.controller;

import com.example.scheduler.Exception.NotFoundException;
import com.example.scheduler.model.SchedulerJobDto;
import com.example.scheduler.service.SchedulerJobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class SchedulerJobController {

    private final SchedulerJobService scheduleJobService;
    @RequestMapping("/metaData")
    @ResponseStatus(code = HttpStatus.OK, reason = "OK")
    public Object metaData() throws SchedulerException {
        return scheduleJobService.getMetaData();
    }

    @GetMapping("/jobs")
    @ResponseStatus(code = HttpStatus.OK, reason = "OK")
    public ResponseEntity<List<JobDetail>> getAllJobs() throws SchedulerException {

        // TODO make this optional
        List<JobDetail> jobDetails = scheduleJobService.findAllJobs();
        if (jobDetails != null) { return ResponseEntity.ok(jobDetails); }
        else return ResponseEntity.notFound().build();
    }

    @GetMapping("/jobs/{jobName}")
    @ResponseStatus(code = HttpStatus.OK, reason = "CREATED")
    public ResponseEntity<JobDetail> getJobByName(@PathVariable String jobName) throws SchedulerException, NotFoundException {
        return Optional.ofNullable(scheduleJobService.findJobKey(jobName)).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/jobs")
    @ResponseStatus(code = HttpStatus.CREATED, reason = "CREATED")
    public ResponseEntity<?> scheduleNewJob(SchedulerJobDto jobDto) throws SchedulerException {
        try {
            scheduleJobService.scheduleNewJob(jobDto);
        } catch (Exception e) {
            return ResponseEntity.unprocessableEntity().body(e.getMessage());
        }

        return Optional.ofNullable(scheduleJobService.findJobKey(jobDto.getJobName()))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.unprocessableEntity().build());
    }

    @DeleteMapping("/jobs/{jobName}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT, reason = "DELETED")
    public ResponseEntity<Void> deleteJob(@PathVariable String jobName) throws SchedulerException {
        if (scheduleJobService.deleteJob(jobName)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.unprocessableEntity().build();
    }

    @GetMapping("/jobs/{jobName}/pause")
    @ResponseStatus(code = HttpStatus.OK, reason = "Ok")
    public ResponseEntity<Void> pauseJob(@PathVariable String jobName) throws SchedulerException {
        if (scheduleJobService.pauseJob(jobName)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.unprocessableEntity().build();
    }

    @GetMapping("/jobs/{jobName}/resume")
    @ResponseStatus(code = HttpStatus.OK, reason = "Ok")
    public ResponseEntity<Void> resumeJob(@PathVariable String jobName) throws SchedulerException {
        if (scheduleJobService.resumeJob(jobName)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.unprocessableEntity().build();
    }

}
