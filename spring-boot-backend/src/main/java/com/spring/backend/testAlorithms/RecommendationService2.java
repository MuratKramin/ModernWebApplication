package com.spring.backend.testAlorithms;

// RecommendationService.java

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
public class RecommendationService2 {

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ResidenceHistoryRepository residenceHistoryRepository;


    public void test(){
        List<User> users = userRepository.findAll();
        List<Hotel> hotels = hotelRepository.findAll();
        Map<Integer, Map<Integer, Integer>> ratingsMatrix = new HashMap<>();

        // Initialize matrix
        users.forEach(user -> ratingsMatrix.put(user.getId().intValue(), new HashMap<>()));
        hotels.forEach(hotel -> users.forEach(user -> ratingsMatrix.get(user.getId().intValue()).put(hotel.getId(), null)));

        // Populate matrix with actual ratings
        for (User user : users) {
            List<ResidenceHistory> histories = residenceHistoryRepository.findByUserId(user.getId());
//            for(ResidenceHistory i:residenceHistoryRepository.findAll()){
//                //System.out.println(i.toString());
//            }
//            histories.forEach(residenceHistory -> System.out.println("Grade:"+residenceHistory.getGrade()));
            histories.forEach(history ->
                    ratingsMatrix.get(user.getId().intValue()).put(history.getHotel_rev().getId(), history.getGrade()));

        }

        // Print the matrix
        System.out.println("Actual Ratings Matrix:");
        printMatrix(ratingsMatrix, hotels);

    }

    public void printMyMatrix(){
        System.out.println("My_matrix");
        for(User i: userRepository.findAll()){
            for(ResidenceHistory j:i.getResidenceHistoryList()){

                System.out.print(j.getGrade()+" ");
            }
            System.out.println();
        }
    }

    public List<Hotel> recommendHotels(Long userId, int topN) {
        //residenceHistoryRepository.findAll().forEach(residenceHistory -> System.out.println("grade:"+residenceHistory.getGrade()));
        List<User> users = userRepository.findAll();
        Map<User, List<ResidenceHistory>> userHistories = users.stream()
                .collect(Collectors.
                        toMap(user -> user, user -> residenceHistoryRepository.findByUserId(user.getId())));

        Map<User, Double> userMeanRatings = calculateMeanRatings(userHistories);
        normalizeRatings(userHistories, userMeanRatings);
        List<Hotel> hotels = hotelRepository.findAll();
        Map<Hotel, Map<Hotel, Double>> similarities = calculateCosineSimilarity(hotels, userHistories, userMeanRatings);

        return getTopNRecommendations(userRepository.findById(userId).orElse(null), hotels, similarities, topN);
    }

    private Map<User, Double> calculateMeanRatings(Map<User, List<ResidenceHistory>> userHistories) {
        Map<User, Double> meanRatings = new HashMap<>();
        for (Map.Entry<User, List<ResidenceHistory>> entry : userHistories.entrySet()) {
            List<ResidenceHistory> histories = entry.getValue();
            double mean = histories.stream().mapToInt(ResidenceHistory::getGrade).average().orElse(0);
            meanRatings.put(entry.getKey(), mean);
        }
        System.out.println(meanRatings);
        return meanRatings;
    }

    private void normalizeRatings(Map<User, List<ResidenceHistory>> userHistories, Map<User, Double> userMeanRatings) {
        for (Map.Entry<User, List<ResidenceHistory>> entry : userHistories.entrySet()) {
            User user = entry.getKey();
            List<ResidenceHistory> histories = entry.getValue();
            Double meanRating = userMeanRatings.get(user);
            histories.forEach(history -> {
                history.setGrade((int) (history.getGrade() - meanRating));
            });
        }

    }

    private Map<Hotel, Map<Hotel, Double>> calculateCosineSimilarity(List<Hotel> hotels, Map<User, List<ResidenceHistory>> userHistories, Map<User, Double> userMeanRatings) {
        Map<Hotel, Map<Hotel, Double>> similarities = new HashMap<>();

        // Initialize similarity map
        hotels.forEach(hotel -> similarities.put(hotel, new HashMap<>()));

        // Calculate similarities
        for (Hotel h1 : hotels) {
            for (Hotel h2 : hotels) {
                if (h1 == h2) continue;
                double numerator = 0, denominator1 = 0, denominator2 = 0;
                for (User user : userHistories.keySet()) {
                    Optional<ResidenceHistory> history1 = userHistories.get(user).stream().filter(h -> h.getHotel_rev().equals(h1)).findFirst();
                    Optional<ResidenceHistory> history2 = userHistories.get(user).stream().filter(h -> h.getHotel_rev().equals(h2)).findFirst();
                    if (history1.isPresent() && history2.isPresent()) {
                        double normalized1 = history1.get().getGrade(); // Already normalized
                        double normalized2 = history2.get().getGrade(); // Already normalized
                        numerator += normalized1 * normalized2;
                        denominator1 += Math.pow(normalized1, 2);
                        denominator2 += Math.pow(normalized2, 2);
                    }
                }
                double similarity = (denominator1 == 0 || denominator2 == 0) ? 0 : numerator / (Math.sqrt(denominator1) * Math.sqrt(denominator2));
                similarities.get(h1).put(h2, similarity);
            }
        }
        return similarities;
    }

    private List<Hotel> getTopNRecommendations
            (User user, List<Hotel> hotels, Map<Hotel, Map<Hotel, Double>> similarities, int topN) {
        Map<Hotel, Double> scores = new HashMap<>();
        List<ResidenceHistory> userHistories = residenceHistoryRepository.findByUserId(user.getId());

        for (Hotel hotel : hotels) {
            double score = 0;
            int count = 0;
            for (ResidenceHistory history : userHistories) {
                Hotel ratedHotel = history.getHotel_rev();
                if (similarities.containsKey(ratedHotel) && similarities.get(ratedHotel).containsKey(hotel)) {
                    double similarity = similarities.get(ratedHotel).get(hotel);
                    score += similarity * history.getGrade(); // Use normalized rating directly
                    count++;
                }
            }
            if (count > 0) scores.put(hotel, score / count);
        }

        return scores.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .map(Map.Entry::getKey)
                .limit(topN)
                .collect(Collectors.toList());
    }

    public void printRatingMatrix() {
        List<User> users = userRepository.findAll();
        List<Hotel> hotels = hotelRepository.findAll();
        Map<Integer, Map<Integer, Integer>> ratingsMatrix = new HashMap<>();

        // Initialize matrix
        users.forEach(user -> ratingsMatrix.put(user.getId().intValue(), new HashMap<>()));
        hotels.forEach(hotel -> users.forEach(user -> ratingsMatrix.get(user.getId().intValue()).put(hotel.getId(), null)));

        // Populate matrix with actual ratings
        for (User user : users) {
            List<ResidenceHistory> histories = residenceHistoryRepository.findByUserId(user.getId());
//            for(ResidenceHistory i:residenceHistoryRepository.findAll()){
//                //System.out.println(i.toString());
//            }
//            histories.forEach(residenceHistory -> System.out.println("Grade:"+residenceHistory.getGrade()));
            histories.forEach(history ->
                    ratingsMatrix.get(user.getId().intValue()).put(history.getHotel_rev().getId(), history.getGrade()));

        }

        // Print the matrix
        System.out.println("Actual Ratings Matrix:");
        printMatrix(ratingsMatrix, hotels);
    }

    public void printPredictedRatingMatrix() {
        List<User> users = userRepository.findAll();
        List<Hotel> hotels = hotelRepository.findAll();
        Map<Integer, Map<Integer, Integer>> ratingsMatrix = new HashMap<>();

        // Initialize matrix and populate with existing ratings
        users.forEach(user -> ratingsMatrix.put(user.getId().intValue(), new HashMap<>()));
        hotels.forEach(hotel -> users.forEach(user -> {
            List<ResidenceHistory> histories = residenceHistoryRepository.findByUserId(user.getId());
            Integer existingRating = histories.stream()
                    .filter(h -> Integer.valueOf(h.getHotel_rev().getId()).equals(hotel.getId()))
                    .findFirst()
                    .map(ResidenceHistory::getGrade)
                    .orElse(null);
            ratingsMatrix.get(user.getId().intValue()).put(hotel.getId(), existingRating);
        }));

        // Calculate predicted ratings for missing ratings
        Map<User, List<ResidenceHistory>> userHistories = users.stream()
                .collect(Collectors.toMap(user -> user, user -> residenceHistoryRepository.findByUserId(user.getId())));
        Map<User, Double> userMeanRatings = calculateMeanRatings(userHistories);
        Map<Hotel, Map<Hotel, Double>> similarities = calculateCosineSimilarity(hotels, userHistories, userMeanRatings);

        for (User user : users) {
            Map<Integer, Integer> userRatings = ratingsMatrix.get(user.getId().intValue());
            List<ResidenceHistory> histories = userHistories.get(user);
            Set<Integer> ratedHotels = histories.stream().map(h -> h.getHotel_rev().getId()).collect(Collectors.toSet());

            for (Hotel hotel : hotels) {
                if (!ratedHotels.contains(hotel.getId())) { // Only predict for hotels not rated by the user
                    Integer predictedRating = predictRating(user, hotel, similarities, histories, userMeanRatings);
                    userRatings.put(hotel.getId(), predictedRating);
                }
            }
        }

        // Print the matrix including both existing and predicted ratings
        System.out.println("Complete Ratings Matrix:");
        printMatrix(ratingsMatrix, hotels);
    }



    private void printMatrix(Map<Integer, Map<Integer, Integer>> matrix, List<Hotel> hotels) {
        final String ANSI_RESET = "\u001B[0m";
        final String ANSI_BLUE = "\u001B[34m";

        System.out.print("Us\\Hot:");
        hotels.forEach(hotel -> System.out.print("\t" + hotel.getId()));
        System.out.println();

        matrix.forEach((userId, ratings) -> {
            System.out.print("User " + userId+":");
            ratings.forEach((hotelId, rating) -> {
                if (rating == null) {
                    System.out.print("\t ");
                } else {
                    // Check if it is an existing rating or a predicted one
                    ResidenceHistory history = residenceHistoryRepository.findByUserId((long) userId)
                            .stream()
                            .filter(h -> Integer.valueOf(h.getHotel_rev().getId()).equals(hotelId))
                            .findFirst()
                            .orElse(null);

                    if (history != null) {
                        System.out.print("\t" + rating);
                    } else {
                        // This is a predicted rating
                        System.out.print("\t" + ANSI_BLUE + rating + ANSI_RESET);
                    }
                }
            });
            System.out.println();
        });
    }


    private Integer predictRating(User user, Hotel hotel, Map<Hotel, Map<Hotel, Double>> similarities, List<ResidenceHistory> userHistories, Map<User, Double> userMeanRatings) {
        double score = 0;
        double totalSimilarity = 0;
        for (ResidenceHistory history : userHistories) {
            Hotel ratedHotel = history.getHotel_rev();
            if (similarities.containsKey(ratedHotel) && similarities.get(ratedHotel).containsKey(hotel)) {
                double similarity = similarities.get(ratedHotel).get(hotel);
                score += similarity * (history.getGrade() - userMeanRatings.get(user));
                totalSimilarity += Math.abs(similarity);
            }
        }
        return totalSimilarity > 0 ? (int) Math.round(score / totalSimilarity + userMeanRatings.get(user)) : null;
    }

}

