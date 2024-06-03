package com.spring.backend.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.apache.spark.sql.catalyst.expressions.Like;

import javax.persistence.*;

import java.sql.Date;

@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Table(name = "likes", schema = "public", catalog = "natural_navigator_rest")
public class Likes {

    @Id
    @Column(name = "id")
    @GeneratedValue()
    private int id;


    @ManyToOne
    @JsonBackReference("user-back-ref-likes")
    @JoinColumn(name = "id_user", nullable = false, foreignKey = @ForeignKey(name = "likes_users_id_fk"))
    private User user;


    @ManyToOne
    @JsonBackReference("hotel-back-ref-likes")
    @JoinColumn(name = "id_hotel", nullable = false, foreignKey = @ForeignKey(name = "likes_hotels_id_fk"))
    private Hotel hotel;

    public Likes(){

    }

    public Likes(int id, User user, Hotel hotel) {
        this.id = id;
        this.user = user;
        this.hotel = hotel;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Hotel getHotel() {
        return hotel;
    }

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }

}
