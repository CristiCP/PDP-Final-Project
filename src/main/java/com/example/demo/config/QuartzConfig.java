package com.example.demo.config;

import com.example.demo.superbet.FetchMatchesJobsSuperbet;
import com.example.demo.superbet.ProcessMatchDetailsJobSuperbet;
import com.example.demo.unibet.FetchMatchesJobsUnibet;
import com.example.demo.unibet.ProcessMatchDetailsJobUnibet;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Date;

@Configuration
public class QuartzConfig {

    @Profile("superbet")
    @Bean
    public JobDetail fetchMatchesJobDetailSuperbet() {
        return JobBuilder.newJob(FetchMatchesJobsSuperbet.class)
                .withIdentity("fetchMatchesJobSuperbet")
                .storeDurably()
                .build();
    }

    @Profile("superbet")
    @Bean
    public Trigger fetchMatchesTriggerSuperbet(JobDetail fetchMatchesJobDetailSuperbet) {
        return TriggerBuilder.newTrigger()
                .forJob(fetchMatchesJobDetailSuperbet)
                .withIdentity("fetchMatchesTriggerSuperbet")
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(60)
                        .repeatForever())
                .build();
    }

    @Profile("superbet")
    @Bean
    public JobDetail processMatchDetailsJobDetailSuperbet() {
        return JobBuilder.newJob(ProcessMatchDetailsJobSuperbet.class)
                .withIdentity("processMatchDetailsJobSuperbet")
                .storeDurably()
                .build();
    }

    @Profile("superbet")
    @Bean
    public Trigger processMatchDetailsTriggerSuperbet(JobDetail processMatchDetailsJobDetailSuperbet) {
        return TriggerBuilder.newTrigger()
                .forJob(processMatchDetailsJobDetailSuperbet)
                .withIdentity("processMatchDetailsTriggerSuperbet")
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(30)
                        .repeatForever())
                .startAt(new Date(System.currentTimeMillis() + 3000))
                .build();
    }

    @Profile("unibet")
    @Bean
    public JobDetail fetchMatchesJobDetailUnibet() {
        return JobBuilder.newJob(FetchMatchesJobsUnibet.class)
                .withIdentity("fetchMatchesJobSuperbetUnibet")
                .storeDurably()
                .build();
    }

    @Profile("unibet")
    @Bean
    public Trigger fetchMatchesTriggerUnibet(JobDetail fetchMatchesJobDetailUnibet) {
        return TriggerBuilder.newTrigger()
                .forJob(fetchMatchesJobDetailUnibet)
                .withIdentity("fetchMatchesTriggerUnibet")
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(60)
                        .repeatForever())
                .build();
    }

    @Profile("unibet")
    @Bean
    public JobDetail processMatchDetailsJobDetailUnibet() {
        return JobBuilder.newJob(ProcessMatchDetailsJobUnibet.class)
                .withIdentity("processMatchDetailsJobUnibet")
                .storeDurably()
                .build();
    }

    @Profile("unibet")
    @Bean
    public Trigger processMatchDetailsTriggerUnibet(JobDetail processMatchDetailsJobDetailUnibet) {
        return TriggerBuilder.newTrigger()
                .forJob(processMatchDetailsJobDetailUnibet)
                .withIdentity("processMatchDetailsTriggerUnibet")
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(30)
                        .repeatForever())
                .startAt(new Date(System.currentTimeMillis() + 3000))
                .build();
    }
}
