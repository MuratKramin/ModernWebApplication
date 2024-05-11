package com.spring.backend.services;

import com.spring.backend.models.ResidenceHistory;
import com.spring.backend.repository.HotelRepository;
import com.spring.backend.repository.ResidenceHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationService3 {

    @Autowired
    private ResidenceHistoryRepository residenceHistoryRepository;

    @Autowired
    private HotelRepository hotelRepository;

    public Map<Integer, Map<Integer, Double>> generateRecommendations() {
        // Загрузка историй проживаний
        List<ResidenceHistory> histories = residenceHistoryRepository.findAll();
        Map<Integer, Map<Integer, Integer>> ratings = new HashMap<>();

        // Построение матрицы оценок
        for (ResidenceHistory history : histories) {
            if (history.getGrade() != null) {
                ratings.computeIfAbsent(history.getHotel_rev().getId(), k -> new HashMap<>())
                        .put(history.getUsers_rev().getId().intValue(), history.getGrade());
            }
        }

        // Нормализация оценок
        Map<Integer, Double[]> normalizedRatings = normalizeRatings(ratings);

        // Вычисление косинусного сходства
        Map<Integer, List<Integer>> similarHotels = findSimilarHotels(normalizedRatings);

        // Предсказание оценок
        return predictRatings(ratings, normalizedRatings, similarHotels);
    }

    private Map<Integer, Double[]> normalizeRatings(Map<Integer, Map<Integer, Integer>> ratings) {
        Map<Integer, Double[]> normalizedRatings = new HashMap<>();
        ratings.forEach((hotelId, userRatings) -> {
            double average = userRatings.values().stream().mapToInt(v -> v).average().orElse(0.0);
            Double[] normalized = userRatings.values().stream()
                    .mapToInt(v -> v)
                    .mapToDouble(v -> v - average)
                    .boxed()
                    .toArray(Double[]::new);
            normalizedRatings.put(hotelId, normalized);
        });
        return normalizedRatings;
    }

    private Map<Integer, List<Integer>> findSimilarHotels(Map<Integer, Double[]> normalizedRatings) {
        Map<Integer, List<Integer>> similarHotels = new HashMap<>();
        normalizedRatings.keySet().forEach(hotelId -> {
            Map<Integer, Double> similarities = new HashMap<>();
            normalizedRatings.forEach((otherHotelId, otherRatings) -> {
                if (!hotelId.equals(otherHotelId)) {
                    double similarity = cosineSimilarity(normalizedRatings.get(hotelId), normalizedRatings.get(otherHotelId));
                    similarities.put(otherHotelId, similarity);
                }
            });
            List<Integer> topSimilar = similarities.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .map(Map.Entry::getKey)
                    .limit(2)
                    .collect(Collectors.toList());
            similarHotels.put(hotelId, topSimilar);
        });
        return similarHotels;
    }

    private Map<Integer, Map<Integer, Double>> predictRatings(Map<Integer, Map<Integer, Integer>> ratings, Map<Integer, Double[]> normalizedRatings, Map<Integer, List<Integer>> similarHotels) {
        Map<Integer, Map<Integer, Double>> predictedRatings = new HashMap<>();
        ratings.forEach((hotelId, userRatings) -> {
            Map<Integer, Double> predictions = new HashMap<>();
            userRatings.forEach((userId, rating) -> {
                if (rating == null) {
                    double sumSim = 0.0;
                    double weightedSum = 0.0;
                    for (Integer similarHotel : similarHotels.get(hotelId)) {
                        Double similarRating = Double.valueOf(ratings.get(similarHotel).get(userId));
                        if (similarRating != null) {
                            double similarity = cosineSimilarity(normalizedRatings.get(hotelId), normalizedRatings.get(similarHotel));
                            weightedSum += similarity * similarRating;
                            sumSim += Math.abs(similarity);
                        }
                    }
                    double predictedRating = weightedSum / sumSim;
                    predictions.put(userId, predictedRating);
                } else {
                    predictions.put(userId, (double) rating);
                }
            });
            predictedRatings.put(hotelId, predictions);
        });
        return predictedRatings;
    }

    private double cosineSimilarity(Double[] vectorA, Double[] vectorB) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (int i = 0; i < vectorA.length; i++) {
            dotProduct += vectorA[i] * vectorB[i];
            normA += Math.pow(vectorA[i], 2);
            normB += Math.pow(vectorB[i], 2);
        }
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}
