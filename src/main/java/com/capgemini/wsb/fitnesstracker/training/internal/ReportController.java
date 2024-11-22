package com.capgemini.wsb.fitnesstracker.training.internal;

import com.capgemini.wsb.fitnesstracker.mail.api.EmailService;
import com.capgemini.wsb.fitnesstracker.training.api.MonthlyTrainingReport;
import com.capgemini.wsb.fitnesstracker.training.api.TrainingReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/v1/reports")
public class ReportController {

    @Autowired
    private TrainingReportService trainingReportService;

    @Autowired
    private EmailService emailService;

    @PostMapping("/sendMonthlyReports")
    public ResponseEntity<String> sendMonthlyReports() {
        LocalDate lastMonth = LocalDate.now().minusMonths(1);
        List<MonthlyTrainingReport> reports = trainingReportService.generateMonthlyReport(YearMonth.of(lastMonth.getYear(), lastMonth.getMonth()));

        for (MonthlyTrainingReport report : reports) {
            emailService.sendMonthlyReport(report.getUser().getEmail(), report);
        }

        return ResponseEntity.ok("Monthly reports sent successfully.");
    }
}