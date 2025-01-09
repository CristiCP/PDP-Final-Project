package com.example.demo.unibet;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("unibet")
public class ProcessMatchDetailsJobUnibet implements Job {

    @Autowired
    private UnibetService unibetService;

    @Override
    public void execute(JobExecutionContext context) {
        try {
            unibetService.fetchAndProcessMatchDetails();
            System.out.println("Processing match details executed Unibet.");
        } catch (Exception e) {
            System.err.println("Error in ProcessMatchDetailsJob: " + e.getMessage());
        }
    }
}