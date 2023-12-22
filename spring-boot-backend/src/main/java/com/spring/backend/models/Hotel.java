package com.spring.backend.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "Hotels")
public class Hotel {
    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name="name")
    private String name;

    @Column(name="country")
    private String country;
    @Column(name="region")
    private String region;

    @Column(name="city")
    private String city;
    @Column(name="street")
    private String street;
    @Column(name="house")
    private String house;
    @Column(name="description")
    private String description;
    @Column(name="phone_num")
    private String phone_num;
    @Column(name="email")
    private String email;
    @Column(name="web_site")
    private String web_site;
    @Column(name = "short_description")
    private String short_description;

    @Column (name = "main_feature")
    private String main_feature;

    @JsonIgnore
    @JsonManagedReference
    @OneToMany(mappedBy = "hotel_pic")
    private List<Photo> photoList;

    @JsonIgnore
    @JsonManagedReference
    @OneToMany(mappedBy = "hotel_rev")
    private List<ResidenceHistory> residenceHistoryList;

    public List<ResidenceHistory> getResidenceHistoryList() {
        return residenceHistoryList;
    }

    public void setResidenceHistoryList(List<ResidenceHistory> residenceHistoryList) {
        this.residenceHistoryList = residenceHistoryList;
    }

    public Hotel(int id, String name, String country, String region, String city, String street, String house, String description, String phone_num, String email, String web_site, String short_description, String main_feature, List<Photo> photoList, List<ResidenceHistory> residenceHistoryList, int family, int children, int theYouth, int oldFriends, int comfort, int distance, int price, int activity, int safety, int activeRecreationOnTheWater, int fishing, int football, int volleyball, int tableTennis, int tennis, int cycling) {
        this.id = id;
        this.name = name;
        this.country = country;
        this.region = region;
        this.city = city;
        this.street = street;
        this.house = house;
        this.description = description;
        this.phone_num = phone_num;
        this.email = email;
        this.web_site = web_site;
        this.short_description = short_description;
        this.main_feature = main_feature;
        this.photoList = photoList;
        this.residenceHistoryList = residenceHistoryList;
        this.family = family;
        this.children = children;
        this.theYouth = theYouth;
        this.oldFriends = oldFriends;
        this.comfort = comfort;
        this.distance = distance;
        this.price = price;
        this.activity = activity;
        this.safety = safety;
        this.activeRecreationOnTheWater = activeRecreationOnTheWater;
        this.fishing = fishing;
        this.football = football;
        this.volleyball = volleyball;
        this.tableTennis = tableTennis;
        this.tennis = tennis;
        this.cycling = cycling;
    }

    public String getShort_description() {
        return short_description;
    }

    public void setShort_description(String short_description) {
        this.short_description = short_description;
    }

    public String getMain_feature() {
        return main_feature;
    }

    public void setMain_feature(String main_feature) {
        this.main_feature = main_feature;
    }

    @Column(name = "family")
    private int family;

    @Column(name = "children")
    private int children;

    @Column(name = "the_youth")
    private int theYouth;

    @Column(name = "old_friends")
    private int oldFriends;

    @Column(name = "comfort")
    private int comfort;

    @Column(name = "distance")
    private int distance;

    @Column(name = "price")
    private int price;

    @Column(name = "activity")
    private int activity;

    @Column(name = "safety")
    private int safety;

    @Column(name = "active_recreation_on_the_water")
    private int activeRecreationOnTheWater;

    @Column(name = "fishing")
    private int fishing;

    @Column(name = "football")
    private int football;

    @Column(name = "volleyball")
    private int volleyball;

    @Column(name = "table_tennis")
    private int tableTennis;

    @Column(name = "tennis")
    private int tennis;

    @Column(name = "cycling")
    private int cycling;
/*
    @Column(name = "total")
    private double total;

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }*/

    public int getFamily() {
        return family;
    }

    public void setFamily(int family) {
        this.family = family;
    }

    public int getChildren() {
        return children;
    }

    public void setChildren(int children) {
        this.children = children;
    }

    public int getTheYouth() {
        return theYouth;
    }

    public void setTheYouth(int theYouth) {
        this.theYouth = theYouth;
    }

    public int getOldFriends() {
        return oldFriends;
    }

    public void setOldFriends(int oldFriends) {
        this.oldFriends = oldFriends;
    }

    public int getComfort() {
        return comfort;
    }

    public void setComfort(int comfort) {
        this.comfort = comfort;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getActivity() {
        return activity;
    }

    public void setActivity(int activity) {
        this.activity = activity;
    }

    public int getSafety() {
        return safety;
    }

    public void setSafety(int safety) {
        this.safety = safety;
    }

    public int getActiveRecreationOnTheWater() {
        return activeRecreationOnTheWater;
    }

    public void setActiveRecreationOnTheWater(int activeRecreationOnTheWater) {
        this.activeRecreationOnTheWater = activeRecreationOnTheWater;
    }

    public int getFishing() {
        return fishing;
    }

    public void setFishing(int fishing) {
        this.fishing = fishing;
    }

    public int getFootball() {
        return football;
    }

    public void setFootball(int football) {
        this.football = football;
    }

    public int getVolleyball() {
        return volleyball;
    }

    public void setVolleyball(int volleyball) {
        this.volleyball = volleyball;
    }

    public int getTableTennis() {
        return tableTennis;
    }

    public void setTableTennis(int tableTennis) {
        this.tableTennis = tableTennis;
    }

    public int getTennis() {
        return tennis;
    }

    public void setTennis(int tennis) {
        this.tennis = tennis;
    }

    public int getCycling() {
        return cycling;
    }

    public void setCycling(int cycling) {
        this.cycling = cycling;
    }



    public Hotel(){

    }

/*    public Hotel(int id, String name, String country, String region, String city, String street, String house, String description, String phone_num, String email, String web_site, List<Photo> photoList) {
        this.id = id;
        this.name = name;
        this.country = country;
        this.region = region;
        this.city = city;
        this.street = street;
        this.house = house;
        this.description = description;
        this.phone_num = phone_num;
        this.email = email;
        this.web_site = web_site;
        this.photoList = photoList;
    }*/

    @Override
    public String toString() {
        return "Hotel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", country='" + country + '\'' +
                ", region='" + region + '\'' +
                ", city='" + city + '\'' +
                ", street='" + street + '\'' +
                ", house='" + house + '\'' +
                ", description='" + description + '\'' +
                ", phone_num='" + phone_num + '\'' +
                ", email='" + email + '\'' +
                ", web_site='" + web_site + '\'' +
                ", photoList=" + photoList +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getHouse() {
        return house;
    }

    public void setHouse(String house) {
        this.house = house;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhone_num() {
        return phone_num;
    }

    public void setPhone_num(String phone_num) {
        this.phone_num = phone_num;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWeb_site() {
        return web_site;
    }

    public void setWeb_site(String web_site) {
        this.web_site = web_site;
    }

    public List<Photo> getPhotoList() {
        return photoList;
    }

    public void setPhotoList(List<Photo> photoList) {
        this.photoList = photoList;
    }

    public List<Photo> getPhotofromSecond(){return photoList.subList(1,photoList.size());}


}
