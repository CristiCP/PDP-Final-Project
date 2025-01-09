package com.example.demo.superbet;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("superbet")
public class FetchMatchesJobsSuperbet implements Job {
    @Autowired
    private SuperbetService superbetService;

    @Override
    public void execute(JobExecutionContext context) {
        try {
            superbetService.fetchAndSendLiveMatches();
            System.out.println("Fetching ids Superbet.");
        } catch (Exception e) {
            System.err.println("Error in FetchMatchesJob: " + e.getMessage());
        }
    }
}
