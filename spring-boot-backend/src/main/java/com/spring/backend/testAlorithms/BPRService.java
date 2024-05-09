package com.spring.backend.testAlorithms;

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
public class BPRService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private HotelRepository hotelRepository;
    @Autowired
    private ResidenceHistoryRepository historyRepository;

    private Map<Long, Map<Long, Double>> userItemLatentFactors;
    private Map<Long, Map<Long, Double>> itemLatentFactors;
    private final Random random = new Random();
    private static final int LATENT_FACTORS_COUNT = 10;
    private static final double LEARNING_RATE = 0.01;
    private static final double REGULARIZATION = 0.01;

    public void trainModel() {
        List<User> users = userRepository.findAll();
        List<Hotel> hotels = hotelRepository.findAll();

        initializeLatentFactors(users, hotels);

        // Загружаем все истории посещений и формируем начальные данные
        Map<Long, Set<Long>> userPositiveInteractions = new HashMap<>();
        for (ResidenceHistory history : historyRepository.findAll()) {
            userPositiveInteractions.computeIfAbsent(history.getUsers_rev().getId(), k -> new HashSet<>()).add(Integer.toUnsignedLong(history.getHotel_rev().getId()));
        }

        // Обучение модели BPR
        for (int epoch = 0; epoch < 100; epoch++) { // Настройка количества эпох
            for (User user : users) {
                Set<Long> positives = userPositiveInteractions.getOrDefault(user.getId(), new HashSet<>());
                for (Long positiveItemId : positives) {
                    Long negativeItemId = selectNegativeItem(positives, hotels);
                    updateLatentFactors(user.getId(), positiveItemId, negativeItemId);
                }
            }
        }
    }

    private void initializeLatentFactors(List<User> users, List<Hotel> hotels) {
        userItemLatentFactors = new HashMap<>();
        itemLatentFactors = new HashMap<>();
        for (User user : users) {
            userItemLatentFactors.put(user.getId(), randomLatentFactors());
        }
        for (Hotel hotel : hotels) {
            itemLatentFactors.put(Integer.toUnsignedLong(hotel.getId()), randomLatentFactors());
        }
    }

    private Map<Long, Double> randomLatentFactors() {
        return random.doubles(LATENT_FACTORS_COUNT, 0, 0.1).boxed().collect(Collectors.toMap(
                i -> i.longValue(),
                d -> d
        ));
    }

    private Long selectNegativeItem(Set<Long> positives, List<Hotel> hotels) {
        Long negativeItem;
        do {
            negativeItem = Integer.toUnsignedLong(hotels.get(random.nextInt(hotels.size())).getId());
        } while (positives.contains(negativeItem));
        return negativeItem;
    }

    private void updateLatentFactors(Long userId, Long positiveItemId, Long negativeItemId) {
        Map<Long, Double> userFactors = userItemLatentFactors.get(userId);
        Map<Long, Double> positiveItemFactors = itemLatentFactors.get(positiveItemId);
        Map<Long, Double> negativeItemFactors = itemLatentFactors.get(negativeItemId);

        double x_uij = dotProduct(userFactors, positiveItemFactors) - dotProduct(userFactors, negativeItemFactors);
        double sigmoid = sigmoid(x_uij);

        for (int k = 0; k < LATENT_FACTORS_COUNT; k++) {
            double userFactor = userFactors.get((long) k);
            double positiveFactor = positiveItemFactors.get((long) k);
            double negativeFactor = negativeItemFactors.get((long) k);

            userFactors.put((long) k, updateFactor(userFactor, sigmoid, positiveFactor, negativeFactor));
            positiveItemFactors.put((long) k, updateFactor(positiveFactor, sigmoid, userFactor, 0));
            negativeItemFactors.put((long) k, updateFactor(negativeFactor, sigmoid, 0, userFactor));
        }
    }

    private double updateFactor(double factor, double sigmoid, double positiveFactor, double negativeFactor) {
        return factor + LEARNING_RATE * (sigmoid * (positiveFactor - negativeFactor) - REGULARIZATION * factor);
    }

    private double dotProduct(Map<Long, Double> factors1, Map<Long, Double> factors2) {
        return factors1.entrySet().stream()
                .mapToDouble(e -> e.getValue() * factors2.getOrDefault(e.getKey(), 0.0))
                .sum();
    }

    private double sigmoid(double x) {
        return 1 / (1 + Math.exp(-x));
    }

    public List<Hotel> recommendHotels() {
        User user = userRepository.getReferenceById(Integer.toUnsignedLong(1));

        // Реализация рекомендации отелей для пользователя на основе обученной модели BPR
        List<Hotel> rec=hotelRepository.findAll().stream()
                .sorted(Comparator.comparingDouble(hotel -> -dotProduct(userItemLatentFactors.get(user.getId()), itemLatentFactors.get(hotel.getId()))))
                .collect(Collectors.toList());
        System.out.println(rec);
        return rec;
    }
}
