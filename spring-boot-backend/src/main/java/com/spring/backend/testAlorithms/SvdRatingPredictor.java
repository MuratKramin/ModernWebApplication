package com.spring.backend.testAlorithms;

import com.spring.backend.models.Hotel;
import com.spring.backend.models.User;
import com.spring.backend.repository.HotelRepository;
import com.spring.backend.repository.ResidenceHistoryRepository;
import com.spring.backend.repository.UserRepository;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.SingularValueDecomposition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SvdRatingPredictor {

    @Autowired
    private final HotelRepository hotelRepository;
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final ResidenceHistoryRepository residenceHistoryRepository;

    public SvdRatingPredictor(UserRepository userRepository, HotelRepository hotelRepository, ResidenceHistoryRepository residenceHistoryRepository) {
        this.userRepository = userRepository;
        this.hotelRepository = hotelRepository;
        this.residenceHistoryRepository = residenceHistoryRepository;
    }

    public double predictRating(Long userId, Long hotelId) {
        // Получение и построение матрицы рейтингов
        Map<Long, Integer> userIndexMap = new HashMap<>();
        Map<Long, Integer> hotelIndexMap = new HashMap<>();
        List<User> users = userRepository.findAll();
        List<Hotel> hotels = hotelRepository.findAll();
        double[][] ratings = new double[users.size()][hotels.size()];

        for(int i=0;i<users.size();i++){
            for(int j=0;j<hotels.size();j++){
                Random random = new Random();
                int randomNumber = random.nextInt(5) + 1; // Генерация числа от 0 до 4 и добавление 1 для получения числа от 1 до 5
                ratings[i][j]=randomNumber;

            }
        }

        int userIndex = 0;
        for (User user : users) {
            userIndexMap.put(user.getId(), userIndex++);
        }
        int hotelIndex = 0;
        for (Hotel hotel : hotels) {
            hotelIndexMap.put(Integer.toUnsignedLong(hotel.getId()), hotelIndex++);
        }

        residenceHistoryRepository.findAll().forEach(history -> {
            Integer uIndex = userIndexMap.get(history.getUsers_rev().getId());
            Integer hIndex = hotelIndexMap.get(history.getHotel_rev().getId());
            if (uIndex != null && hIndex != null) {
                ratings[uIndex][hIndex] = history.getGrade();
            }
        });

        System.out.println("ratings:"+ Arrays.deepToString(ratings));

        // Применение SVD
        SingularValueDecomposition svd = new SingularValueDecomposition(new Array2DRowRealMatrix(ratings));
        double[][] estimatedRatings = svd.getSolver().getInverse().multiply(new Array2DRowRealMatrix(ratings)).getData();
        System.out.println("SvdClass:"+Arrays.deepToString(estimatedRatings));

        return estimatedRatings[userIndexMap.get(userId)][hotelIndexMap.get(hotelId)];
    }
}
