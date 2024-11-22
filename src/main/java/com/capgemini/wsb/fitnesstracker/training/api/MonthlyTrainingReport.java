package com.capgemini.wsb.fitnesstracker.training.api;

import com.capgemini.wsb.fitnesstracker.user.api.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Entity
@Table(name = "MonthlyTrainingReports")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class MonthlyTrainingReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "userName", nullable = false)
    private String userName;

    @Column(name = "totalTrainings", nullable = false)
    private int totalTrainings;

    @ElementCollection
    @Column(name = "trainings", nullable = true)
    private List<Long> trainings;

    @Column(name = "monthYear", nullable = false)
    private String monthYear; // np., "November 2024"


}