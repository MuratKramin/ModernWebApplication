package com.spring.backend.controllers;

import com.spring.backend.models.ResidenceHistory;
import com.spring.backend.repository.HotelRepository;
import com.spring.backend.repository.ResidenceHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/residenceHistories")
public class ResidenceHistoryController {

    @Autowired
    private ResidenceHistoryRepository residenceHistoryRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @GetMapping("/byHotel/{hotelId}")
    public ResponseEntity<List<ResidenceHistory>> getResidenceHistoriesByHotelId(@PathVariable int hotelId) {
        List<ResidenceHistory> histories = hotelRepository.getReferenceById(hotelId).getResidenceHistoryList();
        if(histories != null && !histories.isEmpty()) {
            return ResponseEntity.ok(histories);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Получить список всех историй проживания
    @GetMapping
    public List<ResidenceHistory> getAllResidenceHistories() {
        return residenceHistoryRepository.findAll();
    }

    // Получить историю проживания по ID
    @GetMapping("/{id}")
    public ResponseEntity<ResidenceHistory> getResidenceHistoryById(@PathVariable(value = "id") int id) {
        Optional<ResidenceHistory> residenceHistory = residenceHistoryRepository.findById(id);
        if (residenceHistory.isPresent()) {
            return ResponseEntity.ok().body(residenceHistory.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Создать новую историю проживания
    @PostMapping
    public ResidenceHistory createResidenceHistory(@RequestBody ResidenceHistory residenceHistory) {
        System.out.println(residenceHistory);
        residenceHistory.setId(0);
        System.out.println(residenceHistory);
        return residenceHistoryRepository.save(residenceHistory);
    }

    // Обновить историю проживания
    @PutMapping("/{id}")
    public ResponseEntity<ResidenceHistory> updateResidenceHistory(@PathVariable(value = "id") int id,
                                                                   @RequestBody ResidenceHistory residenceHistoryDetails) {
        Optional<ResidenceHistory> residenceHistory = residenceHistoryRepository.findById(id);
        if (residenceHistory.isPresent()) {
            ResidenceHistory updatedResidenceHistory = residenceHistory.get();
            updatedResidenceHistory.setCheckInDate(residenceHistoryDetails.getCheckInDate());
            updatedResidenceHistory.setCheckOutDate(residenceHistoryDetails.getCheckOutDate());
            updatedResidenceHistory.setTotalCost(residenceHistoryDetails.getTotalCost());
            updatedResidenceHistory.setReview(residenceHistoryDetails.getReview());
            updatedResidenceHistory.setGrade(residenceHistoryDetails.getGrade());
            updatedResidenceHistory.setUsers_rev(residenceHistoryDetails.getUsers_rev());
            updatedResidenceHistory.setHotel_rev(residenceHistoryDetails.getHotel_rev());
            residenceHistoryRepository.save(updatedResidenceHistory);
            return ResponseEntity.ok(updatedResidenceHistory);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Удалить историю проживания
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteResidenceHistory(@PathVariable(value = "id") int id) {
        return residenceHistoryRepository.findById(id)
                .map(residenceHistory -> {
                    residenceHistoryRepository.delete(residenceHistory);
                    return ResponseEntity.ok().build();
                }).orElse(ResponseEntity.notFound().build());
    }

    // Дополнительный метод для вставки через native query
    @PostMapping("/insert")
    public void insertResidenceHistory(@RequestParam Date checkInDate,
                                       @RequestParam Date checkOutDate,
                                       @RequestParam double totalCost,
                                       @RequestParam String review,
                                       @RequestParam Integer grade,
                                       @RequestParam int userId,
                                       @RequestParam int hotelId) {
        residenceHistoryRepository.insert(checkInDate, checkOutDate, totalCost, review, grade, userId, hotelId);
    }

    // Получить рейтинги
    @GetMapping("/ratings")
    public List<Object[]> getRatings() {
        return residenceHistoryRepository.findRatings();
    }

    // Получить максимальный ID отеля
    @GetMapping("/maxIdHotel")
    public int getMaxIdHotel() {
        return residenceHistoryRepository.maxIdHotel();
    }
}
