package com.capgemini.wsb.fitnesstracker.training.api;


import java.time.YearMonth;
import java.util.List;

public interface TrainingReportService {
    List<MonthlyTrainingReport> generateMonthlyReport(YearMonth yearMonth);
}
