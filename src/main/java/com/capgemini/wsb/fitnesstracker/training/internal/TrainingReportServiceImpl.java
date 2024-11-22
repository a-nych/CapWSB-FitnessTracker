package com.capgemini.wsb.fitnesstracker.training.internal;

import com.capgemini.wsb.fitnesstracker.training.api.MonthlyTrainingReport;
import com.capgemini.wsb.fitnesstracker.training.api.Training;
import com.capgemini.wsb.fitnesstracker.training.api.TrainingReportService;
import com.capgemini.wsb.fitnesstracker.training.api.TrainingService;
import com.capgemini.wsb.fitnesstracker.user.api.User;
import com.capgemini.wsb.fitnesstracker.user.api.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TrainingReportServiceImpl implements TrainingReportService {
    @Autowired
    private TrainingService trainingService;  // Assuming you have a service to interact with training data

    @Autowired
    private UserService userService;  // Service to retrieve user details

    @Transactional
    public List<MonthlyTrainingReport> generateMonthlyReport(YearMonth yearMonth) {
        List<User> users = userService.findAllUsers();  // Get all users
        List<MonthlyTrainingReport> reports = new ArrayList<>();
        LocalDate firstDayOfMonth = yearMonth.atDay(1);
        LocalDate lastDayOfMonth = yearMonth.atEndOfMonth();
        for (User user : users) {
            List<Training> userTrainings = trainingService.findTrainingsByUserId(user.getId());
            List<Training> userMonthlyTrainings = userTrainings.stream().filter(training -> {
                        LocalDateTime startTime = LocalDateTime.ofInstant(training.getStartTime().toInstant(), ZoneId.systemDefault());
                        LocalDateTime endTime = LocalDateTime.ofInstant(training.getEndTime().toInstant(), ZoneId.systemDefault());

                        // Check if the training was active during the current month
                        return !(startTime.isAfter(lastDayOfMonth.atStartOfDay()) || endTime.isBefore(firstDayOfMonth.atStartOfDay()));
                    }).toList();
            MonthlyTrainingReport report = getMonthlyTrainingReport(yearMonth, user, userMonthlyTrainings);
            reports.add(report);
        }
        return reports;
    }

    private static MonthlyTrainingReport getMonthlyTrainingReport(YearMonth yearMonth, User user, List<Training> userMonthlyTrainings) {
        int totalTrainings = userMonthlyTrainings.size();
        ArrayList<Long> trainings = new ArrayList<Long>();
        if(totalTrainings > 0) {
            for (Training training : userMonthlyTrainings) {
                trainings.add(training.getId());
            }
        }
        MonthlyTrainingReport report = new MonthlyTrainingReport();
        report.setUser(user);
        report.setUserName(user.getFirstName() + " " + user.getLastName());
        report.setTotalTrainings(totalTrainings);
        report.setMonthYear(yearMonth.toString());
        report.setTrainings(trainings);
        return report;
    }
}
