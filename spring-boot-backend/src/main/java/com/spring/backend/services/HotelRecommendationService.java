package com.spring.backend.services;

import com.spring.backend.models.Hotel;
import com.spring.backend.models.ResidenceHistory;
import com.spring.backend.models.User;
import com.spring.backend.repository.HotelRepository;
import com.spring.backend.repository.ResidenceHistoryRepository;
import com.spring.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    // Получение списка рекомендуемых отелей
    public List<Hotel> recommendHotels(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return Collections.emptyList();

        // Получаем описание профиля пользователя на основе его истории и оценок
        String userDescription = buildUserDescription(userId);
        Map<String, Double> idfScores = calculateIdfScores(hotelRepository.findAll());

        // Получаем список всех отелей и их профили
        List<Hotel> allHotels = hotelRepository.findAll();
        Map<Integer, Double> hotelScores = new HashMap<>();

        // Оцениваем каждый отель
        for (Hotel hotel : allHotels) {
            Double[] hotelProfile = getHotelProfile(hotel);
            double featureSimilarity = cosineSimilarity(buildUserProfile(userId), hotelProfile);
            double descriptionSimilarity = cosineSimilarityText(idfScores, userDescription, hotel.getDescription());
            double combinedScore = 0.7 * featureSimilarity + 0.3 * descriptionSimilarity;
            hotelScores.put(hotel.getId(), combinedScore);
        }

        return allHotels.stream()
                .sorted(Comparator.comparing(hotel -> hotelScores.get(hotel.getId()), Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }

    // Строим "виртуальное описание" пользователя на основе его отзывов и оценок
    private String buildUserDescription(Long userId) {
        List<ResidenceHistory> userHistories = residenceHistoryRepository.findByUserId(userId);
        StringBuilder weightedDescription = new StringBuilder();
        for (ResidenceHistory history : userHistories) {
            if (history.getGrade() != null) {
                String description = history.getHotel_rev().getDescription();
                for (int i = 0; i < history.getGrade(); i++) {
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
        Double[] userProfile = new Double[16];
        Arrays.fill(userProfile, 0.0);
        userHistories.forEach(history -> {
            Hotel hotel = history.getHotel_rev();
            userProfile[0] += hotel.getFamily();
            userProfile[1] += hotel.getChildren();
            userProfile[2] += hotel.getTheYouth();
            userProfile[3] += hotel.getOldFriends();
            userProfile[4] += hotel.getComfort();
            userProfile[5] += hotel.getDistance();
            userProfile[6] += hotel.getPrice();
            userProfile[7] += hotel.getActivity();
            userProfile[8] += hotel.getSafety();
            userProfile[9] += hotel.getActiveRecreationOnTheWater();
            userProfile[10] += hotel.getFishing();
            userProfile[11] += hotel.getFootball();
            userProfile[12] += hotel.getVolleyball();
            userProfile[13] += hotel.getTableTennis();
            userProfile[14] += hotel.getTennis();
            userProfile[15] += hotel.getCycling();
        });
        return userProfile;
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
