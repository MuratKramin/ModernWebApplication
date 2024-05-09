package com.spring.backend.repository;

import com.spring.backend.models.Hotel;
import com.spring.backend.models.ResidenceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelRepository extends JpaRepository<Hotel,Integer> {

    @Query(value = "SELECT hotels.name, count(residence_history.id_hotel) as num_visit " +
            "from hotels inner join residence_history on hotels.id = residence_history.id_hotel group by hotels.id",nativeQuery = true)
    List<List<String>> getStats();

    @Query(value = "select * from hotels  order by id",nativeQuery = true)
    List<Hotel> AllHotelsSortById();



    @Query(value = "select sum(family * (?1) +" +
            "children * sqrt(?2)" +
            " +the_youth * (?3)" +
            " +old_friends * (?4)" +
            " +comfort * (?5) *(?19)" +
            "+distance * (?6) *(?20)" +
            "+price * (?7) *(?21)" +
            " +activity * (?8) *(?22)" +
            " +safety * (?9) *(?23)" +
            " +active_recreation_on_the_water * (?10) * 5 " +
            "+fishing * (?11) * 5 +football * (?12) * 5 " +
            "+volleyball * (?13) * 5 +table_tennis * (?14) * 5" +
            " +tennis * (?15) * 5 " +
            "+cycling * (?16) * 5" +
            "+0*(?17)+0*(?18)+0*(?24)+0*(?25)) from hotels where id=(?26)"            ,nativeQuery = true)
    double getTotal(int family,
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
    );

    //Hotel getHotelById();
    @Query(value = "select hotels.*, sum(" +
            "family * (?1) +" +
            "children * sqrt(?2) +" +
            "the_youth * (?3) +" +
            "old_friends * (?4) +" +

            "comfort * (?5) *(?19)+" +
            "distance * (?6) *(?20)+" +
            "price * (?7) *(?21) +" +
            "activity * (?8) *(?22) +" +
            "safety * (?9) *(?23) +" +

            "active_recreation_on_the_water * (?10) * 5 +" +
            "fishing * (?11) * 5 +" +
            "football * (?12) * 5 +" +
            "volleyball * (?13) * 5 +" +
            "table_tennis * (?14) * 5 +" +
            "tennis * (?15) * 5 +" +
            "cycling * (?16) * 5 " +
            ") as total from hotels " +
            "group by id " +
            "having distance_from_Kazan<=(?17) and (?24)*(?25)*cost_of_stay_per_day<=(?18)" +
            "order by total desc;",nativeQuery = true)
    List<Hotel> findBestHotels(int family,
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
                               int d9_safety, int qty,int days);
}
