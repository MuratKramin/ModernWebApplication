package com.spring.backend.controllers;


import com.spring.backend.models.Hotel;
import com.spring.backend.models.ResidenceHistory;
import com.spring.backend.repository.HotelRepository;
import com.spring.backend.repository.ResidenceHistoryRepository;
import com.spring.backend.services.*;
import com.spring.backend.testAlorithms.BPRService;
import com.spring.backend.testAlorithms.RecommendationService;
import com.spring.backend.testAlorithms.RecommendationService2;
import com.spring.backend.testAlorithms.RecommendationSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/hotels")
public class HotelController {

    @Autowired
    private HotelRepository hotelRepository;
    @Autowired
    private HotelRecommendationService hotelRecommendationService;
    @Autowired
    private RecommendationSystem recommendationSystem;
    @Autowired
    private BPRService bprService;

    @Autowired
    private RecommendationService recommendationService;

    @Autowired
    private RecommendationService2 recommendationService2;

    @Autowired
    private RecommendationService3 recommendationService3;
    @Autowired
    private RecommendationService4 recommendationService4;

    // Создание нового отеля
    @PostMapping
    public ResponseEntity<Hotel> createHotel(@Valid @RequestBody Hotel hotel) {
        try {
            Hotel newHotel = hotelRepository.save(hotel);
            return new ResponseEntity<>(newHotel, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Получение всех отелей
    @GetMapping
    public ResponseEntity<List<Hotel>> getAllHotels() {
        List<Hotel> hotels = hotelRepository.AllHotelsSortById();
        if (hotels.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(hotels, HttpStatus.OK);
    }

    @GetMapping("/getRecom")
    public ResponseEntity<List<Hotel>> getRecom() {

        //residenceHistoryRepository.findAll().forEach(residenceHistory -> System.out.println(residenceHistory.getGrade()));
        List<Hotel> hotels = hotelRecommendationService.recommendHotels(Integer.toUnsignedLong(1));
        recommendationSystem.recommendHotels();
        //bprService.recommendHotels();
        //List<Hotel> rec =recommendationService.recommendHotels(Integer.toUnsignedLong(1),5);
//        for(Hotel hotel:rec){
//            System.out.println(hotel.getName());
//        }
        //recommendationService.recommendHotels();
        //recommendationService2.printRatingMatrix();
        //recommendationService2.printPredictedRatingMatrix();
        //recommendationService2.printMyMatrix();
        //recommendationService2.recommendHotels(Integer.toUnsignedLong(1),5).forEach(hotel -> System.out.println("Hotel_id"+hotel.getId()));

        //recommendationService3.generateRecommendations();

        recommendationService4.generateRecommendations();
        if (hotels.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(hotels, HttpStatus.OK);
    }

    @GetMapping("/stats")
    public ResponseEntity<List<List<String>>> getStat() {
        List<List<String>> stringList = hotelRepository.getStats();
        System.out.println(stringList);
        return new ResponseEntity<>(stringList, HttpStatus.OK);
    }



    // Получение отеля по ID
    @GetMapping("/{id}")
    public ResponseEntity<Hotel> getHotelById(@PathVariable int id) {
        Optional<Hotel> hotel = hotelRepository.findById(id);
        return hotel.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Обновление отеля по ID
    @PutMapping("/{id}")
    public ResponseEntity<Hotel> updateHotel(@PathVariable int id, @RequestBody Hotel hotel) {
        Optional<Hotel> hotelData = hotelRepository.findById(id);

        if (hotelData.isPresent()) {
            Hotel existingHotel = hotelData.get();
            existingHotel.setName(hotel.getName());
            existingHotel.setCountry(hotel.getCountry());
            existingHotel.setRegion(hotel.getRegion());
            existingHotel.setCity(hotel.getCity());
            existingHotel.setStreet(hotel.getStreet());
            existingHotel.setHouse(hotel.getHouse());
            existingHotel.setDescription(hotel.getDescription());
            existingHotel.setPhone_num(hotel.getPhone_num());
            existingHotel.setEmail(hotel.getEmail());
            existingHotel.setWeb_site(hotel.getWeb_site());
            existingHotel.setShort_description(hotel.getShort_description());
            existingHotel.setMain_feature(hotel.getMain_feature());
            existingHotel.setPhotoList(hotel.getPhotoList());
            existingHotel.setResidenceHistoryList(hotel.getResidenceHistoryList());

            return new ResponseEntity<>(hotelRepository.save(existingHotel), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Удаление отеля по ID
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteHotel(@PathVariable int id) {
        try {
            hotelRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @Autowired
    private ResidenceHistoryRepository residenceHistoryRepository;

    @GetMapping("/{id}/residenceHistory")
    public ResponseEntity<List<ResidenceHistory>> getAllHotelsReviews(@PathVariable int id) {
        List<ResidenceHistory> residenceHistoryList = hotelRepository.getReferenceById(id).getResidenceHistoryList();
        if (residenceHistoryList.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(residenceHistoryList, HttpStatus.OK);
    }

    @Autowired
    private HotelService hotelService;

    @GetMapping("/findHotels")
    public ResponseEntity<List<Hotel>> getAllHotels(@RequestParam(value = "qty",required = false,defaultValue = "0") int qty,
                                                    @RequestParam(value = "days",required = false,defaultValue = "0") int days,
                                                    @RequestParam(value = "campaign",required = false) String campaign,
                                                    @RequestParam(value = "children",required = false,defaultValue = "0") int children ,

                                                    @RequestParam(value = "comfort",required = false,defaultValue = "0") int comfort ,
                                                    @RequestParam(value = "distance",required = false,defaultValue = "0") int distance ,
                                                    @RequestParam(value = "price",required = false,defaultValue = "0") int price ,
                                                    @RequestParam(value = "activity",required = false,defaultValue = "0") int activity ,
                                                    @RequestParam(value = "safety",required = false,defaultValue = "0") int safety ,

                                                    @RequestParam(value = "active_recreation_on_the_water",required = false,defaultValue = "0") int active_recreation_on_the_water,
                                                    @RequestParam(value = "fishing",required = false,defaultValue = "0") int fishing,
                                                    @RequestParam(value = "football",required = false,defaultValue = "0") int football,
                                                    @RequestParam(value = "volleyball",required = false,defaultValue = "0") int volleyball,
                                                    @RequestParam(value = "table_tennis",required = false,defaultValue = "0") int table_tennis,
                                                    @RequestParam(value = "tennis",required = false,defaultValue = "0") int tennis,
                                                    @RequestParam(value = "cycling",required = false,defaultValue = "0") int cycling,

                                                    @RequestParam(value = "distance_from_Kazan",required = false,defaultValue = "100000") int distance_from_Kazan,
                                                    @RequestParam(value = "budget",required = false,defaultValue = "100000") int budget
    ) {

        System.out.println(budget);
        System.out.println(distance_from_Kazan);
        System.out.println(days);

        if(tennis!=0 || tennis!=1) tennis=0;

        int family  = (campaign!=null&&campaign.equals("family")) ? 1:0;
        int the_youth  = (campaign!=null&&campaign.equals("the_youth")) ? 1:0;
        int old_friends  = (campaign!=null&&campaign.equals("old_friends")) ? 1:0;
        List<Hotel> hotels=hotelService.getBestHotels(family,
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

        System.out.println("Идёт запрос");

//        if (hotels.isEmpty()) {
//            hotels = hotelRepository.AllHotelsSortById();
//
//        }

        for (Hotel i: hotels){
            System.out.println(i.getName());
        }
        if (hotels.isEmpty()) {
            System.out.println("неееет");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        }
        return new ResponseEntity<>(hotels, HttpStatus.OK);
    }


}
