package com.spring.backend.services;

import com.spring.backend.models.Hotel;
import com.spring.backend.models.ResidenceHistory;
import com.spring.backend.models.User;
import com.spring.backend.repository.HotelRepository;
import com.spring.backend.repository.ResidenceHistoryRepository;
import com.spring.backend.repository.UserRepository;
import com.spring.backend.testAlorithms.SvdRatingPredictor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.SingularValueDecomposition;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class HotelRecommendationService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private HotelRepository hotelRepository;
    @Autowired
    private ResidenceHistoryRepository residenceHistoryRepository;







    private Map<Integer, Map<Integer, Double>> createUserHotelMatrix() {
        List<ResidenceHistory> allHistories = residenceHistoryRepository.findAll();
        Map<Integer, Map<Integer, Double>> userHotelMatrix = new HashMap<>();

        for (ResidenceHistory history : allHistories) {
            int userId = history.getUsers_rev().getId().intValue();
            int hotelId = history.getHotel_rev().getId();
            double grade = Optional.ofNullable(history.getGrade()).orElse(0);

            userHotelMatrix.putIfAbsent(userId, new HashMap<>());
            userHotelMatrix.get(userId).put(hotelId, grade);
        }

        System.out.println("userHotelMatrix:");
        System.out.println(userHotelMatrix);

        return userHotelMatrix;
    }


    public List<Hotel> recommendHotels(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return Collections.emptyList();

        Set<Integer> ratedHotelIds = getRatedHotelIds(userId);
        String userDescription = buildUserDescription(userId);
        Map<String, Double> idfScores = calculateIdfScores(hotelRepository.findAll());

        List<Hotel> allHotels = hotelRepository.findAll().stream()
                .filter(hotel -> !ratedHotelIds.contains(hotel.getId()))
                .collect(Collectors.toList());

        Map<Integer, Double> hotelScores = new HashMap<>();

        for (Hotel hotel : allHotels) {
            Double[] hotelProfile = getHotelProfile(hotel);
            double featureSimilarity = cosineSimilarity(buildUserProfile(userId), hotelProfile);
            double descriptionSimilarity = cosineSimilarityText(idfScores, userDescription, hotel.getDescription());
            double combinedScore = 0.7 * featureSimilarity + 0.3 * descriptionSimilarity;
            System.out.println(hotel.getName()+":");
            System.out.println("featureSimilarity:"+featureSimilarity);
            System.out.println("descriptionSimilarity:"+descriptionSimilarity);
            hotelScores.put(hotel.getId(), combinedScore);
        }

        return allHotels.stream()
                .sorted(Comparator.comparing(hotel -> hotelScores.get(hotel.getId()), Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }

//    private double getAverageRatingForHotel(int hotelId, Long userId) {
//        // Получаем историю отзывов пользователя для данного отеля
//        List<ResidenceHistory> histories = residenceHistoryRepository.findByUserIdAndHotelId(userId, hotelId);
//        if (histories.isEmpty()) return 1.0; // Нет отзывов, нейтральный множитель
//        return histories.stream()
//                .mapToInt(ResidenceHistory::getGrade)
//                .average()
//                .orElse(1.0); // По умолчанию, если нет оценок, используем нейтральный множитель
//    }

    private Set<Integer> getRatedHotelIds(Long userId) {
        List<ResidenceHistory> histories = residenceHistoryRepository.findByUserId(userId);
        return histories.stream()
                .map(history -> history.getHotel_rev().getId())
                .collect(Collectors.toSet());
    }



    // Строим "виртуальное описание" пользователя на основе его отзывов и оценок
    private String buildUserDescription(Long userId) {
        List<ResidenceHistory> userHistories = residenceHistoryRepository.findByUserId(userId);
        StringBuilder weightedDescription = new StringBuilder();
        for (ResidenceHistory history : userHistories) {
            if (history.getGrade() != null) {
                String description = history.getHotel_rev().getDescription();
                int weight = history.getGrade(); // Оценка от 1 до 5
                for (int i = 0; i < weight; i++) {
                    weightedDescription.append(description).append(" ");
                }
            }
        }
        return weightedDescription.toString();
    }

    // Вычисляем IDF-оценки для всех отелей
    private Map<String, Double> calculateIdfScores(List<Hotel> hotels) {
        Map<String, Integer> docFreq = new HashMap<>();
        int totalDocs = hotels.size();
        for (Hotel hotel : hotels) {
            Set<String> encountered = new HashSet<>();
            for (String word : hotel.getDescription().toLowerCase().split("\\s+")) {
                if (!encountered.contains(word)) {
                    docFreq.put(word, docFreq.getOrDefault(word, 0) + 1);
                    encountered.add(word);
                }
            }
        }
        Map<String, Double> idfScores = new HashMap<>();
        for (Map.Entry<String, Integer> entry : docFreq.entrySet()) {
            idfScores.put(entry.getKey(), Math.log((double) totalDocs / entry.getValue()));
        }
        return idfScores;
    }

    // Рассчитываем косинусное сходство для текстовых описаний
    private double cosineSimilarityText(Map<String, Double> idfScores, String description1, String description2) {
        Map<String, Double> tfidf1 = computeTfidfVector(description1, idfScores);
        Map<String, Double> tfidf2 = computeTfidfVector(description2, idfScores);
        double dotProduct = 0.0, norm1 = 0.0, norm2 = 0.0;
        Set<String> allWords = new HashSet<>(tfidf1.keySet());
        allWords.addAll(tfidf2.keySet());
        for (String word : allWords) {
            double v1 = tfidf1.getOrDefault(word, 0.0);
            double v2 = tfidf2.getOrDefault(word, 0.0);
            dotProduct += v1 * v2;
            norm1 += v1 * v1;
            norm2 += v2 * v2;
        }
        return (norm1 == 0.0 || norm2 == 0.0) ? 0.0 : dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }

    // Рассчитываем TF-IDF вектора
    private Map<String, Double> computeTfidfVector(String text, Map<String, Double> idfScores) {
        Map<String, Double> tfidf = new HashMap<>();
        Map<String, Integer> termFreq = new HashMap<>();
        String[] words = text.toLowerCase().split("\\s+");
        for (String word : words) {
            termFreq.put(word, termFreq.getOrDefault(word, 0) + 1);
        }
        int totalTerms = words.length;
        for (Map.Entry<String, Integer> entry : termFreq.entrySet()) {
            double tf = entry.getValue() / (double) totalTerms;
            double idf = idfScores.getOrDefault(entry.getKey(), 0.0);
            tfidf.put(entry.getKey(), tf * idf);
        }
        return tfidf;
    }

    // Рассчитываем профиль отеля на основе его числовых характеристик
    private Double[] getHotelProfile(Hotel hotel) {
        return new Double[]{
                (double) hotel.getFamily(),
                (double) hotel.getChildren(),
                (double) hotel.getTheYouth(),
                (double) hotel.getOldFriends(),
                ( double) hotel.getComfort(),
                (double) hotel.getDistance(),
                (double) hotel.getPrice(),
                (double) hotel.getActivity(),
                (double) hotel.getSafety(),
                (double) hotel.getActiveRecreationOnTheWater(),
                (double) hotel.getFishing(),
                (double) hotel.getFootball(),
                (double) hotel.getVolleyball(),
                (double) hotel.getTableTennis(),
                (double) hotel.getTennis(),
                (double) hotel.getCycling()
        };
    }

    // Строим профиль пользователя на основе его истории пребывания
    private Double[] buildUserProfile(Long userId) {
        List<ResidenceHistory> userHistories = residenceHistoryRepository.findByUserId(userId);
        Map<Integer, Double[]> attributeScores = new HashMap<>();
        Map<Integer, Integer> attributeWeights = new HashMap<>();

        // Собираем все параметры и веса
        for (ResidenceHistory history : userHistories) {
            Hotel hotel = history.getHotel_rev();
            int weight = history.getGrade() != null ? history.getGrade() : 1; // Используем оценку как вес
            updateAttributeScores(attributeScores, attributeWeights, 0, hotel.getFamily(), weight);
            updateAttributeScores(attributeScores, attributeWeights, 1, hotel.getChildren(), weight);
            updateAttributeScores(attributeScores, attributeWeights, 2, hotel.getTheYouth(), weight);
            updateAttributeScores(attributeScores, attributeWeights, 3, hotel.getOldFriends(), weight);
            updateAttributeScores(attributeScores, attributeWeights, 4, hotel.getComfort(), weight);
            updateAttributeScores(attributeScores, attributeWeights, 5, hotel.getDistance(), weight);
            updateAttributeScores(attributeScores, attributeWeights, 6, hotel.getPrice(), weight);
            updateAttributeScores(attributeScores, attributeWeights, 7, hotel.getActivity(), weight);
            updateAttributeScores(attributeScores, attributeWeights, 8, hotel.getSafety(), weight);
            updateAttributeScores(attributeScores, attributeWeights, 9, hotel.getActiveRecreationOnTheWater(), weight);
            updateAttributeScores(attributeScores, attributeWeights, 10, hotel.getFishing(), weight);
            updateAttributeScores(attributeScores, attributeWeights, 11, hotel.getFootball(), weight);
            updateAttributeScores(attributeScores, attributeWeights, 12, hotel.getVolleyball(), weight);
            updateAttributeScores(attributeScores, attributeWeights, 13, hotel.getTableTennis(), weight);
            updateAttributeScores(attributeScores, attributeWeights, 14, hotel.getTennis(), weight);
            updateAttributeScores(attributeScores, attributeWeights, 15, hotel.getCycling(), weight);
        }

        // Рассчитываем взвешенное среднее для каждого атрибута
        Double[] userProfile = new Double[16];
        for (int i = 0; i < userProfile.length; i++) {
            if (attributeWeights.get(i) != null && attributeWeights.get(i) > 0) {
                userProfile[i] = attributeScores.get(i)[0] / attributeWeights.get(i);
            } else {
                userProfile[i] = 0.0;
            }
        }
        return userProfile;
    }

    private void updateAttributeScores(Map<Integer, Double[]> scores, Map<Integer, Integer> weights, int index, double value, int weight) {
        if (!scores.containsKey(index)) {
            scores.put(index, new Double[]{0.0});
            weights.put(index, 0);
        }
        scores.get(index)[0] += value * weight;
        weights.put(index, weights.get(index) + weight);
    }


    // Рассчитываем косинусное сходство между числовыми профилями
    private double cosineSimilarity(Double[] userProfile, Double[] hotelProfile) {
        double dotProduct = 0.0, normA = 0.0, normB = 0.0;
        for (int i = 0; i < userProfile.length; i++) {
            dotProduct += userProfile[i] * hotelProfile[i];
            normA += Math.pow(userProfile[i], 2);
            normB += Math.pow(hotelProfile[i], 2);
        }
        return (normA == 0.0 || normB == 0.0) ? 0.0 : dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}
