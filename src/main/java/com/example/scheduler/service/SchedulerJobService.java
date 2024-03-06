package com.example.scheduler.service;

import com.example.scheduler.model.SchedulerJobDto;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class SchedulerJobService {

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private JobScheduleBuilder jobScheduleBuilder;

    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    @Autowired
    private ApplicationContext context;

    public List<JobDetail> findAllJobs() throws SchedulerException {
        List<JobDetail> jobDetails = new ArrayList<>();
        for (String groupName : scheduler.getJobGroupNames()) {
            for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
                JobDetail jobDetail = scheduler.getJobDetail(jobKey);
                if (jobDetail != null) jobDetails.add(jobDetail);
            }
        }
        return jobDetails;
    }

    public JobDetail findJobKey(String jobName) throws SchedulerException {
        // Check running jobs first
        for (JobExecutionContext runningJob : scheduler.getCurrentlyExecutingJobs()) {
            if (Objects.equals(jobName, runningJob.getJobDetail().getKey().getName())) {
                return runningJob.getJobDetail();
            }
        }
        return findAllJobs()
                .stream()
                .filter(j -> j.getKey().getName().equals(jobName))
                .findFirst()
                .orElse(null);
    }

    public SchedulerMetaData getMetaData() throws SchedulerException {
        return scheduler.getMetaData();
    }


    public void scheduleNewJob(SchedulerJobDto jobDto) throws SchedulerException {
        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();

            JobDetail jobDetail = JobBuilder
                    .newJob((Class<? extends QuartzJobBean>) Class.forName(jobDto.getJobClass()))
                    .withIdentity(jobDto.getJobName(), jobDto.getJobGroup()).build();
            if (!scheduler.checkExists(jobDetail.getKey())) {
                jobDetail = jobScheduleBuilder.createJob(
                        jobDto.getJobName(), jobDto.getJobGroup(), (Class<? extends QuartzJobBean>) Class.forName(jobDto.getJobClass()),
                        false, context, jobDto.getParameters());

                Trigger trigger;
                if (jobDto.getCronJob()) {
                    trigger = jobScheduleBuilder.createCronTrigger(jobDto.getJobName(), new Date(),
                            jobDto.getCronExpression(), SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
                } else {
                    trigger = jobScheduleBuilder.createSimpleTrigger(jobDto.getJobName(), new Date(),
                            jobDto.getRepeatTime(), SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
                }
                scheduler.scheduleJob(jobDetail, trigger);
                jobDto.setJobStatus("Scheduled");
                log.info("Job %s is scheduled".formatted(jobDto.getJobName()));
            } else {
                log.error("SchedulerJob.jobAlreadyExist");
            }
        } catch (ClassNotFoundException e) {
            log.error("Class Not Found - {}", jobDto.getJobClass(), e);
            throw new SchedulerException("Class Not Found - %s".formatted(jobDto.getJobClass()));
        }
    }

    public boolean deleteJob(String jobName) {
        try {
            log.info("Job %s is deleted.".formatted(jobName));
            return schedulerFactoryBean.getScheduler().deleteJob(new JobKey(jobName));
        } catch (SchedulerException e) {
            log.error("Failed to delete job - {}", jobName, e);
            return false;
        }
    }

    public boolean pauseJob(String jobName) {
        try {
            schedulerFactoryBean.getScheduler().pauseJob(new JobKey(jobName));
            log.info("Job %s is paused.".formatted(jobName));
            return true;
        } catch (SchedulerException e) {
            log.error("Failed to pause job - {}", jobName, e);
            return false;
        }
    }

    public boolean resumeJob(String jobName) {
        try {
            schedulerFactoryBean.getScheduler().resumeJob(new JobKey(jobName));
            log.info("Job %s has resumed.".formatted(jobName));
            return true;
        } catch (SchedulerException e) {
            log.error("Failed to resume job - {}", jobName, e);
            return false;
        }
    }

}
