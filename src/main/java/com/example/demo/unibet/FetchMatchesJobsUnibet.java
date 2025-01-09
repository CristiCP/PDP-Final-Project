package com.example.demo.unibet;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("unibet")
public class FetchMatchesJobsUnibet implements Job {
    @Autowired
    private UnibetService unibetService;

    @Override
    public void execute(JobExecutionContext context) {
        try {
            unibetService.fetchAndSendLiveMatches();
            System.out.println("Fetching ids Unibet.");
        } catch (Exception e) {
            System.err.println("Error in FetchMatchesJob: " + e.getMessage());
        }
    }
}