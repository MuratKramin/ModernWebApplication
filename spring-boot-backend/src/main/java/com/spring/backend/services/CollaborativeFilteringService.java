package com.spring.backend.services;

import com.spring.backend.models.Hotel;
import com.spring.backend.models.ResidenceHistory;
import com.spring.backend.repository.HotelRepository;
import com.spring.backend.repository.ResidenceHistoryRepository;
import com.spring.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class CollaborativeFilteringService {

    @Autowired
    private ResidenceHistoryRepository residenceHistoryRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private UserRepository userRepository;

    Map<Integer, Map<Integer, Double>> predictedRatings = new HashMap<>();

    public List<Hotel> getRecommendationForUserId(Integer userId){
        if (!predictedRatings.containsKey(userId)) {
            return Collections.emptyList();  // Если нет предсказаний для данного пользователя, возвращаем пустой список
        }

        Map<Integer, Double> userRatings = predictedRatings.get(userId);
        List<Hotel> hotels = hotelRepository.findAll();
        // Фильтруем и сортируем отели на основе предсказанных оценок
        return hotels.stream()
                .filter(hotel -> userRatings.containsKey(hotel.getId()))
                .sorted(Comparator.comparing(hotel -> userRatings.get(hotel.getId()), Comparator.reverseOrder()))
                .collect(Collectors.toList());

    }

    public Map<Integer, Double> getMapForUserId(Integer userId){
        if (!predictedRatings.containsKey(userId)) {
            return Collections.emptyMap();  // Если нет предсказаний для данного пользователя, возвращаем пустой список
        }

        return predictedRatings.get(userId);
    }

    public Map<Integer, Map<Integer, Double>> generateRecommendations() {
        List<ResidenceHistory> histories = residenceHistoryRepository.findAll();
        List<Hotel> hotels = hotelRepository.findAll();

        // Создание матрицы оценок
        Map<Integer, Map<Integer, Double>> ratings = new HashMap<>();
        Map<Integer, Map<Integer, Double>> centeredRatings = new HashMap<>();
        for (ResidenceHistory history : histories) {
            if (history.getGrade() != null) {
                ratings.computeIfAbsent(history.getUsers_rev().getId().intValue(), k -> new HashMap<>())
                        .put(history.getHotel_rev().getId(), (double) history.getGrade());
            }
        }
        System.out.println("ratings:");
        printMatrix(ratings);

        // Центрирование оценок
        Map<Integer, Double> averages = new HashMap<>();
        ratings.forEach((userId, userRatings) -> {
            averages.put(userId, userRatings.values().stream().mapToDouble(d -> d).average().orElse(0.0));
            Map<Integer, Double> tempMap = new HashMap<>();
            userRatings.forEach((hotelId, rating) -> tempMap.put(hotelId, rating - averages.get(userId)));
            centeredRatings.put(userId, tempMap);
        });
        System.out.println("mean_ratings:");
        printMatrix(centeredRatings);

        // Вычисление косинусного сходства
        Map<Integer, List<Map.Entry<Integer, Double>>> similarities = new HashMap<>();
        for (int hotelId1 : hotels.stream().map(Hotel::getId).toList()) {
            if(!hotelRepository.getReferenceById(hotelId1).getResidenceHistoryList().isEmpty()){
                Map<Integer, Double> similarityScores = new HashMap<>();
                for (int hotelId2 : hotels.stream().map(Hotel::getId).toList()) {
                    if (hotelId1 != hotelId2 && !hotelRepository.getReferenceById(hotelId2).getResidenceHistoryList().isEmpty()) {
                        double similarity = calculateCosineSimilarity(centeredRatings,averages, hotelId1, hotelId2);
                        similarityScores.put(hotelId2, similarity);
                    }
                }
                List<Map.Entry<Integer, Double>> sortedSimilarities = new ArrayList<>(similarityScores.entrySet());
                sortedSimilarities.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));
                similarities.put(hotelId1, sortedSimilarities);
            }

        }

        // Предсказание оценок

        userRepository.findAll().forEach(user -> {
            Map<Integer, Double> userPredictions = new HashMap<>();
            Integer userId =user.getId().intValue();
            hotelRepository.findAll().forEach(hotel -> {
                Integer hotelId=hotel.getId();
                if(residenceHistoryRepository.findByUserIdandHotelId(userId,hotelId).isEmpty()&&
                !residenceHistoryRepository.findByUserId(userId.longValue()).isEmpty()){

                    List<Map.Entry<Integer, Double>> nearest = similarities.get(hotelId);
                    if (nearest!=null){
                        if (nearest.size() > 1) {

                            List<Map.Entry<Integer, Double>> maxElements = nearest.stream()
                                    .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()))
                                    .limit(2)
                                    .toList();


                            double topSimilaritiesSum = maxElements.get(0).getValue() + maxElements.get(1).getValue();
                            double weightedSum = 0.0;
                            for (int i = 0; i < 2; i++) {
                                Map.Entry<Integer, Double> entry = nearest.get(i);
                                weightedSum += ratings.get(userId).getOrDefault(entry.getKey(), averages.get(userId)) * entry.getValue();
                            }
                            userPredictions.put(hotelId, weightedSum / topSimilaritiesSum);
                        } else {
                            userPredictions.put(hotelId, averages.get(userId));
                        }
                    }

                }

            });
            predictedRatings.put(userId, userPredictions);
        });

        // Вывод матрицы оценок
        System.out.println("Final ratings matrix:");
        predictedRatings.forEach((userId, userRatings) -> {
            System.out.println("User " + userId + ": " + userRatings);
        });
        return predictedRatings;
    }

    private void printMatrix(Map<Integer, Map<Integer, Double>> ratings){
        System.out.println("Ratings matrix:");
        ratings.forEach((userId, userRatings) -> {
            System.out.println("User " + userId + ": " + userRatings);
        });
    }

    private double calculateCosineSimilarity(Map<Integer, Map<Integer, Double>> ratings,Map<Integer, Double> averages, int hotelId1, int hotelId2) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (Map<Integer, Double> userRatings : ratings.values()) {
            double rating1 = userRatings.getOrDefault(hotelId1, 0.0)-averages.getOrDefault(hotelId1,0.0);
            double rating2 = userRatings.getOrDefault(hotelId2, 0.0)-averages.getOrDefault(hotelId2,0.0);
            dotProduct += rating1 * rating2;
            normA += rating1 * rating1;
            normB += rating2 * rating2;
        }
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}
