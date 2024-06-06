package com.spring.backend.services;

import com.spring.backend.models.Hotel;
import com.spring.backend.models.ResidenceHistory;
import com.spring.backend.models.User;
import com.spring.backend.repository.HotelRepository;
import com.spring.backend.repository.ResidenceHistoryRepository;
import com.spring.backend.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class HybridRecommendationService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private HotelRepository hotelRepository;
    @Autowired
    private ResidenceHistoryRepository residenceHistoryRepository;

    @Autowired
    private CollaborativeFilteringService collaborativeFilteringService;

    @Autowired
    private ContentFilteringService contentFilteringService;

    public List<Hotel> recommendHotels(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return Collections.emptyList();
        Set<Integer> ratedHotelIds = getRatedHotelIds(userId);
        List<Hotel> allHotels = hotelRepository.findAll().stream()
                .filter(hotel -> !ratedHotelIds.contains(hotel.getId()))
                .collect(Collectors.toList());

        Map<Integer, Double> hotelScores = new HashMap<>();
        Map<Integer, Double> hotelsPredictedScores= collaborativeFilteringService.getMapForUserId(userId.intValue());
        //userProfile

        for (Hotel hotel : allHotels) {
            double featureSimilarity = (contentFilteringService.getFeatureSimilarity(hotel,userId)-0.9)*10;
            double descriptionSimilarity = (contentFilteringService.getTextSimilarity(hotel,userId))*10;
            double colaborativeScore = 0;

            for (Map.Entry<Integer, Double> entry : hotelsPredictedScores.entrySet()) {
                if (entry.getKey() == hotel.getId()) {
                    colaborativeScore = entry.getValue();
                    break; // Для остановки цикла после нахождения соответствующего значения
                }
            }

            double combinedScore = 0.7 * featureSimilarity+ 0.3 * descriptionSimilarity+colaborativeScore;
            System.out.println(hotel.getName()+":");
            System.out.println("featureSimilarity:"+featureSimilarity);
            System.out.println("descriptionSimilarity:"+descriptionSimilarity);
            hotelScores.put(hotel.getId(), combinedScore);
        }

        return allHotels.stream()
                .sorted(Comparator.comparing(hotel -> hotelScores.get(hotel.getId()), Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }


    private Set<Integer> getRatedHotelIds(Long userId) {
        List<ResidenceHistory> histories = residenceHistoryRepository.findByUserId(userId);
        return histories.stream()
                .map(history -> history.getHotel_rev().getId())
                .collect(Collectors.toSet());
    }
}
