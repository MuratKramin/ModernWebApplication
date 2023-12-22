package com.spring.backend.controllers;


import com.spring.backend.models.Hotel;
import com.spring.backend.repository.HotelRepository;
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
        List<Hotel> hotels = hotelRepository.findAll();
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
}
