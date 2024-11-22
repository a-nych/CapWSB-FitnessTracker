package com.capgemini.wsb.fitnesstracker.training.internal;

import com.capgemini.wsb.fitnesstracker.mail.api.EmailService;
import com.capgemini.wsb.fitnesstracker.training.api.MonthlyTrainingReport;
import com.capgemini.wsb.fitnesstracker.training.api.TrainingReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Component
public class MonthlyReportScheduler {

    @Autowired
    private TrainingReportService trainingReportService;

    @Autowired
    private EmailService emailService;

    @Scheduled(cron = "0 0 0 1 * ?")  // Run at midnight on the first day of each month
    public void sendMonthlyReports() {
        LocalDate lastMonth =  LocalDate.now().minusMonths(1);
        List<MonthlyTrainingReport> reports = trainingReportService.generateMonthlyReport(YearMonth.of(lastMonth.getYear(), lastMonth.getMonth()));

        for (MonthlyTrainingReport report : reports) {
            emailService.sendMonthlyReport(report.getUser().getEmail(), report);
        }
    }

}