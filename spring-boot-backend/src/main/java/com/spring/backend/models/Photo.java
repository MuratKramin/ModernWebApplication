package com.spring.backend.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import javax.persistence.*;

@Entity
@Table(name = "photos")
public class Photo {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JsonBackReference("hotel-photo-ref")
    @JoinColumn(name = "id_hotel")
    private Hotel hotel_pic;
    @Column(name="link")
    private String link;

    public Photo() {

    }
    public Photo(int id, Hotel hotel_pic, String link) {
        this.id = id;
        this.hotel_pic = hotel_pic;
        this.link = link;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Hotel getHotel_pic() {
        return hotel_pic;
    }

    public void setHotel_pic(Hotel hotel_pic) {
        this.hotel_pic = hotel_pic;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
