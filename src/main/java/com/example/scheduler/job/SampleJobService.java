package com.example.scheduler.job;

import org.quartz.JobDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Service
public class SampleJobService {
    public static final long EXECUTION_TIME = 5000L;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final AtomicInteger count = new AtomicInteger();

    public void executeSampleJob(JobDetail jobDetail) {
        logger.info("The {} has begun...", jobDetail.getKey().getName());
        try {
            logger.info("The Job Map {}", jobDetail.getJobDataMap());
            Thread.sleep(EXECUTION_TIME);
        } catch (InterruptedException e) {
            logger.error("Error while executing sample job", e);
        } finally {
            count.incrementAndGet();
            logger.info("Sample job has finished...");
        }
    }

    public int getNumberOfInvocations() {
        return count.get();
    }
}