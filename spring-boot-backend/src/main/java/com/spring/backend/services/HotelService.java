package com.spring.backend.services;

import com.spring.backend.models.Hotel;
import com.spring.backend.repository.HotelRepository;
import com.spring.backend.repository.PhotoRepository;
import com.spring.backend.repository.ResidenceHistoryRepository;
import com.spring.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class HotelService {
    @Autowired
    private HotelRepository hotelRepository;
    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private ResidenceHistoryRepository residenceHistoryRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Hotel> getBestHotels(int family,
                                     int children,
                                     int the_youth,
                                     int old_friends,
                                     int comfort,
                                     int distance,
                                     int price,
                                     int activity,
                                     int safety,
                                     int active_recreation_on_the_water,
                                     int fishing,
                                     int football,
                                     int volleyball,
                                     int table_tennis,
                                     int tennis,
                                     int cycling,
                                     int distance_from_Kazan,
                                     int budget,
                                     int d5_comfort,
                                     int d6_distance,
                                     int d7_price,
                                     int d8_activity,
                                     int d9_safety, int qty, int days){
        return hotelRepository.findBestHotels(
                family,
                children,
                the_youth,
                old_friends,
                comfort,
                distance,
                price,
                activity,
                safety,
                active_recreation_on_the_water,
                fishing,
                football,
                volleyball,
                table_tennis,
                tennis,
                cycling,
                distance_from_Kazan,
                budget,1,1,1,1,1,qty,days);

    }

    public double getTotal(int family,
                    int children,
                    int the_youth,
                    int old_friends,
                    int comfort,
                    int distance,
                    int price,
                    int activity,
                    int safety,
                    int active_recreation_on_the_water,
                    int fishing,
                    int football,
                    int volleyball,
                    int table_tennis,
                    int tennis,
                    int cycling,
                    int distance_from_Kazan,
                    int budget,
                    int d5_comfort,
                    int d6_distance,
                    int d7_price,
                    int d8_activity,
                    int d9_safety, int qty,int days,
                    int id
    ){
        return hotelRepository.getTotal(family,
                children,
                the_youth,
                old_friends,
                comfort,
                distance,
                price,
                activity,
                safety,
                active_recreation_on_the_water,
                fishing,
                football,
                volleyball,
                table_tennis,
                tennis,
                cycling,
                distance_from_Kazan,
                budget,1,1,1,1,1,qty,days,id);
    }
    public Map<Double,Hotel> getSortedBestHotels(int family,
                                              int children,
                                              int the_youth,
                                              int old_friends,
                                              int comfort,
                                              int distance,
                                              int price,
                                              int activity,
                                              int safety,
                                              int active_recreation_on_the_water,
                                              int fishing,
                                              int football,
                                              int volleyball,
                                              int table_tennis,
                                              int tennis,
                                              int cycling,
                                              int distance_from_Kazan,
                                              int cost_of_stay_per_day,
                                              int d5_comfort,
                                              int d6_distance,
                                              int d7_price,
                                              int d8_activity,
                                              int d9_safety, int qty,int days
                                              ){
        List<Hotel>BestHotels=getBestHotels(family,
                children,
                the_youth,
                old_friends,
                comfort,
                distance,
                price,
                activity,
                safety,
                active_recreation_on_the_water,
                fishing,
                football,
                volleyball,
                table_tennis,
                tennis,
                cycling,
                distance_from_Kazan,
                cost_of_stay_per_day,1,1,1,1,1,qty,days);

        HashMap<Double,Hotel> HotelTotal=new HashMap<>() ;
        for(Hotel hotel:BestHotels){
            HotelTotal.put(getTotal(family,
                    children,
                    the_youth,
                    old_friends,
                    comfort,
                    distance,
                    price,
                    activity,
                    safety,
                    active_recreation_on_the_water,
                    fishing,
                    football,
                    volleyball,
                    table_tennis,
                    tennis,
                    cycling,
                    distance_from_Kazan,
                    cost_of_stay_per_day,1,1,1,1,1,qty,days,hotel.getId()),hotel);
        }

        Map<Double, Hotel> SortedBestHotels = new TreeMap<>(HotelTotal).descendingMap();
        return SortedBestHotels;
    }

    public Map<Double,Hotel> Top3(int family,
                                  int children,
                                  int the_youth,
                                  int old_friends,
                                  int comfort,
                                  int distance,
                                  int price,
                                  int activity,
                                  int safety,
                                  int active_recreation_on_the_water,
                                  int fishing,
                                  int football,
                                  int volleyball,
                                  int table_tennis,
                                  int tennis,
                                  int cycling,
                                  int distance_from_Kazan,
                                  int cost_of_stay_per_day,
                                  int d5_comfort,
                                  int d6_distance,
                                  int d7_price,
                                  int d8_activity,
                                  int d9_safety, int qty,int days){
        Map<Double,Hotel> TopF3=new TreeMap<>();
        Iterator<Map.Entry<Double,Hotel>> iterator2=getSortedBestHotels(family,
                children,
                the_youth,
                old_friends,
                comfort,
                distance,
                price,
                activity,
                safety,
                active_recreation_on_the_water,
                fishing,
                football,
                volleyball,
                table_tennis,
                tennis,
                cycling,
                distance_from_Kazan,
                cost_of_stay_per_day,1,1,1,1,1,qty,days).entrySet().iterator();
        for(int i=0;i<3&& iterator2.hasNext();i++){
            Map.Entry<Double,Hotel> pair = iterator2.next();
            TopF3.put(pair.getKey(),pair.getValue());
        }
        Map<Double,Hotel> Top3=new TreeMap<>(TopF3).descendingMap();
        return Top3;
    }
    public Map<Double,Hotel> TopDown3(int family,
                                      int children,
                                      int the_youth,
                                      int old_friends,
                                      int comfort,
                                      int distance,
                                      int price,
                                      int activity,
                                      int safety,
                                      int active_recreation_on_the_water,
                                      int fishing,
                                      int football,
                                      int volleyball,
                                      int table_tennis,
                                      int tennis,
                                      int cycling,
                                      int distance_from_Kazan,
                                      int cost_of_stay_per_day,
                                      int d5_comfort,
                                      int d6_distance,
                                      int d7_price,
                                      int d8_activity,
                                      int d9_safety, int qty,int days){
        Map<Double,Hotel> TopDownF3=new TreeMap<>();
        Iterator<Map.Entry<Double,Hotel>> iterator=getSortedBestHotels(family,
                children,
                the_youth,
                old_friends,
                comfort,
                distance,
                price,
                activity,
                safety,
                active_recreation_on_the_water,
                fishing,
                football,
                volleyball,
                table_tennis,
                tennis,
                cycling,
                distance_from_Kazan,
                cost_of_stay_per_day,1,1,1,1,1,qty,days).entrySet().iterator();
        for(int i=0;iterator.hasNext();i++){
            Map.Entry<Double,Hotel> pair = iterator.next();
            if(i>=3)
                TopDownF3.put(pair.getKey(),pair.getValue());
            //System.out.println(pair.getValue().getName());
        }
        Map<Double,Hotel> TopDown3=new TreeMap<>(TopDownF3).descendingMap();
        return TopDown3;
    }


}
