package com.capgemini.wsb.fitnesstracker.mail.api;

import com.capgemini.wsb.fitnesstracker.training.api.MonthlyTrainingReport;

public interface EmailService {
    void sendMonthlyReport(String recipient, MonthlyTrainingReport report);
}