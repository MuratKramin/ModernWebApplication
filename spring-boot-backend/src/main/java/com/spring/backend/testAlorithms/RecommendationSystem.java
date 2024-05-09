package com.spring.backend.testAlorithms;

import com.spring.backend.models.Hotel;
import com.spring.backend.models.ResidenceHistory;
import com.spring.backend.models.User;
import com.spring.backend.repository.HotelRepository;
import com.spring.backend.repository.ResidenceHistoryRepository;
import com.spring.backend.repository.UserRepository;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.SingularValueDecomposition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RecommendationSystem {

    @Autowired
    private HotelRepository hotelRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ResidenceHistoryRepository residenceHistoryRepository;

    public RecommendationSystem(HotelRepository hotelRepository, UserRepository userRepository, ResidenceHistoryRepository residenceHistoryRepository) {
        this.hotelRepository = hotelRepository;
        this.userRepository = userRepository;
        this.residenceHistoryRepository = residenceHistoryRepository;
    }

    public RealMatrix createRatingMatrix() {
        List<User> users = userRepository.findAll();
        List<Hotel> hotels = hotelRepository.findAll();
        Map<Integer, Integer> userIndex = new HashMap<>();
        Map<Integer, Integer> hotelIndex = new HashMap<>();
        for (int i = 0; i < users.size(); i++) {
            userIndex.put(users.get(i).getId().intValue(), i);
        }
        for (int j = 0; j < hotels.size(); j++) {
            hotelIndex.put(hotels.get(j).getId(), j);
        }

        double[][] data = new double[users.size()][hotels.size()];

        for(int i=0;i<users.size();i++){
            for(int j=0;j<hotels.size();j++){
                Random random = new Random();
                int randomNumber = random.nextInt(5) + 1; // Генерация числа от 0 до 4 и добавление 1 для получения числа от 1 до 5
                data[i][j]=randomNumber;
            }
        }
        for (ResidenceHistory history : residenceHistoryRepository.findAll()) {
            int userIdx = userIndex.get(history.getUsers_rev().getId().intValue());
            int hotelIdx = hotelIndex.get(history.getHotel_rev().getId());
            data[userIdx][hotelIdx] = history.getGrade();
        }
        System.out.println(Arrays.deepToString(data));

        return new Array2DRowRealMatrix(data);
    }

    public void recommendHotels() {
        RealMatrix ratings = createRatingMatrix();
        SingularValueDecomposition svd = new SingularValueDecomposition(ratings);
        RealMatrix u = svd.getU();
        RealMatrix s = svd.getS();
        RealMatrix v = svd.getVT();

        // Построение приближенной матрицы оценок
        RealMatrix approximatedRatings = u.multiply(s).multiply(v);

        System.out.println(approximatedRatings.toString());
        // Используйте approximatedRatings для получения рекомендаций
    }
}
