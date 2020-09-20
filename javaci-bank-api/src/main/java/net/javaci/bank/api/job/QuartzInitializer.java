package net.javaci.bank.api.job;

import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
public class QuartzInitializer {

    @Autowired
    private Scheduler scheduler;

    @PostConstruct
    private void init() {
        log.info("Quartz initializer started ...");
        scheduleExchangeRateJob();
    }

    private void scheduleExchangeRateJob() {
        JobDetail jobDetail = JobBuilder.newJob(ExchangeRateJob.class)
                .withIdentity(UUID.randomUUID().toString(), "javacibank-jobs")
                .withDescription("Exchange rate job")
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("trigger3", "group1")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 7 ? * MON-FRI"))
                //.startNow()
                .build();

        try {
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            log.error("Exception occured while Exchange rate job is initializind");
        }
    }

}
