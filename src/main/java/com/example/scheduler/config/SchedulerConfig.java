package com.example.scheduler.config;

import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.quartz.QuartzProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.util.Properties;

//@Configuration
public class SchedulerConfig {

//	@Autowired
//	private DataSource dataSource;

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private QuartzProperties quartzProperties;

	@Bean
	@Primary
	public Scheduler scheduler() throws Exception {

		SchedulerJobFactory jobFactory = new SchedulerJobFactory();
		jobFactory.setApplicationContext(applicationContext);

		Properties properties = new Properties();
		properties.putAll(quartzProperties.getProperties());


		SchedulerFactoryBean factory = new SchedulerFactoryBean();
		factory.setOverwriteExistingJobs(true);
		//factory.setConfigLocation(new ClassPathResource("quartz.properties"));
		factory.setQuartzProperties(properties);
		factory.setJobFactory(jobFactory);

		factory.afterPropertiesSet();

		Scheduler scheduler = factory.getScheduler();
		scheduler.setJobFactory(jobFactory);
		scheduler.start();
		return scheduler;
	}


}
