package com.spring.backend.testAlorithms;

import java.util.*;
import java.util.stream.Collectors;

import com.spring.backend.models.Hotel;
import com.spring.backend.models.ResidenceHistory;
import com.spring.backend.models.User;
import com.spring.backend.repository.HotelRepository;
import com.spring.backend.repository.ResidenceHistoryRepository;
import com.spring.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
public class RecommendationService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ResidenceHistoryRepository residenceHistoryRepository;

    public List<Hotel> recommendHotels() {
        int topN=5;
        User user = userRepository.getReferenceById(Integer.toUnsignedLong(1));
        List<User> users = userRepository.findAll();
        Map<User, Double> similarities = new HashMap<>();

        for (User other : users) {
            if (!other.equals(user)) {
                double similarity = calculateSimilarity(user, other);
                if (similarity > 0) {
                    similarities.put(other, similarity);
                }
            }
        }

        Map<Hotel, Double> hotelScores = new HashMap<>();
        similarities.forEach((similarUser, similarity) -> {
            List<ResidenceHistory> theirHistory = residenceHistoryRepository.findByUserId(similarUser.getId());
            theirHistory.forEach(history -> {
                hotelScores.merge(history.getHotel_rev(), history.getGrade() * similarity, Double::sum);
            });
        });

        return hotelScores.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(topN)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private double calculateSimilarity(User user1, User user2) {
        List<ResidenceHistory> history1 = residenceHistoryRepository.findByUserId(user1.getId());
        List<ResidenceHistory> history2 = residenceHistoryRepository.findByUserId(user2.getId());

        Map<Integer, Integer> ratings1 = history1.stream()
                .collect(Collectors.toMap(h -> h.getHotel_rev().getId(), ResidenceHistory::getGrade));
        Map<Integer, Integer> ratings2 = history2.stream()
                .collect(Collectors.toMap(h -> h.getHotel_rev().getId(), ResidenceHistory::getGrade));

        return calculatePearsonCorrelation(ratings1, ratings2);
    }

    public double calculatePearsonCorrelation(Map<Integer, Integer> ratings1, Map<Integer, Integer> ratings2) {
        double sum1 = 0;
        double sum2 = 0;
        double sum1Sq = 0;
        double sum2Sq = 0;
        double pSum = 0;
        int n = 0;

        for (Integer key : ratings1.keySet()) {
            if (ratings2.containsKey(key)) {
                n++;
                int x = ratings1.get(key);
                int y = ratings2.get(key);
                sum1 += x;
                sum2 += y;
                sum1Sq += x * x;
                sum2Sq += y * y;
                pSum += x * y;
            }
        }

        if (n == 0) return 0;
        double num = pSum - (sum1 * sum2 / n);
        double den = Math.sqrt((sum1Sq - sum1 * sum1 / n) * (sum2Sq - sum2 * sum2 / n));
        if (den == 0) return 0;
        return num / den;
    }

}
