package com.spring.backend.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

import java.sql.Date;

@Entity
@Table(name = "residence_history", schema = "public", catalog = "natural_navigator_rest")
public class ResidenceHistory {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Basic
    @Column(name = "check_in_date")
    private Date checkInDate;
    @Basic
    @Column(name = "check_out_date")
    private Date checkOutDate;
    @Basic
    @Column(name = "total_cost")
    private double totalCost;

    @Column(name = "review")
    private String review;
    @Basic
    @Column(name = "grade")
    private Integer grade;

    @JsonIgnore
    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "id_user")
    private User user_rev;



    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "id_hotel")
    private Hotel hotel_rev;





    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(Date checkInDate) {
        this.checkInDate = checkInDate;
    }

    public Date getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(Date checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public User getUsers_rev() {
        return user_rev;
    }

    public void setUsers_rev(User user_rev) {
        this.user_rev = user_rev;
    }

    public Hotel getHotel_rev() {
        return hotel_rev;
    }

    public void setHotel_rev(Hotel hotel_rev) {
        this.hotel_rev = hotel_rev;
    }
    public ResidenceHistory(){

    }
    public ResidenceHistory(int id, Date checkInDate, Date checkOutDate, double totalCost, String review, Integer grade, User user_rev, Hotel hotel_rev) {
        this.id = id;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.totalCost = totalCost;
        this.review = review;
        this.grade = grade;
        this.user_rev = user_rev;
        this.hotel_rev = hotel_rev;
    }
}
