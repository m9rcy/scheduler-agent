package com.example.scheduler.job;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.stream.IntStream;

@Slf4j
public class SampleJob extends QuartzJobBean {
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        log.info("SampleJob Start................");

        // Add logic here on what to execute... call api... move files
        IntStream.range(0, 2).forEach(i -> {
            log.info("Countdown - {}", i);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            }
        });
        log.info("SampleJob End................");
    }
}