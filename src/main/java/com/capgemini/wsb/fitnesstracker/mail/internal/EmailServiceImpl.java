package com.capgemini.wsb.fitnesstracker.mail.internal;

import com.capgemini.wsb.fitnesstracker.mail.api.EmailService;
import com.capgemini.wsb.fitnesstracker.training.api.MonthlyTrainingReport;
import com.capgemini.wsb.fitnesstracker.training.api.Training;
import com.capgemini.wsb.fitnesstracker.training.api.TrainingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private TrainingService TrainingService;

    public void sendMonthlyReport(String recipientEmail, MonthlyTrainingReport report) {
        SimpleMailMessage message = new SimpleMailMessage();
        ArrayList<Optional<Training>> trainings = new ArrayList<>();
        for(Long trainingId : report.getTrainings()){
            trainings.add(TrainingService.getTraining(trainingId));
        }
        message.setTo(recipientEmail);
        message.setSubject("Monthly Training Report: " + report.getMonthYear());
        message.setText("Hello " + report.getUserName() + ",\n\n" +
                "You have registered " + report.getTotalTrainings() + " trainings in the month of " + report.getMonthYear() + ".\n\n" +
                "Below you can find details about your trainings:" + trainings + "\n\n" +
                "Best regards,\n" +
                "Your Fitness Tracker Team");

        mailSender.send(message);
    }
}
