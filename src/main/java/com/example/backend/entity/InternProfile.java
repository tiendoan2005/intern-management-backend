package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "intern_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InternProfile {
    @Id
    private Long userId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name="student_code", length=50)
    private String studentCode;

    @Column(length=150)
    private String university;

    @Column(length=150)
    private String major;
    private Double gpa;

    @Column(length=30)
    private String phone;
    private java.sql.Date dob;

    @Column(length=255)
    private String address;

    @Column(name="cv_url", length=255)
    private String cvUrl;
}
