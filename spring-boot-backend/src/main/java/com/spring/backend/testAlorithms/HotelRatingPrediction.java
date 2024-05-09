package com.spring.backend.testAlorithms;

import org.apache.commons.math3.linear.*;

import java.util.Arrays;

public class HotelRatingPrediction {

    // Функция для выполнения SVD и предсказания пустых ячеек
    public RealMatrix predictRatings(RealMatrix ratingsMatrix) {
        // Разложение матрицы оценок с помощью SVD
        SingularValueDecomposition svd = new SingularValueDecomposition(ratingsMatrix);

        // Получение матриц U, S и V
        RealMatrix U = svd.getU();
        RealMatrix S = MatrixUtils.createRealDiagonalMatrix(svd.getSingularValues());
        RealMatrix V = svd.getVT();

        // Уменьшение ранга S для аппроксимации
        int rank = Math.min(ratingsMatrix.getRowDimension(), ratingsMatrix.getColumnDimension());
        int k = rank / 2; // Выбор оптимального значения k (можно экспериментировать)
        RealMatrix Sk = S.getSubMatrix(0, k - 1, 0, k - 1);
        RealMatrix Uk = U.getSubMatrix(0, U.getRowDimension() - 1, 0, k - 1);
        RealMatrix Vk = V.getSubMatrix(0, V.getRowDimension() - 1, 0, k - 1);

        // Предсказание пустых ячеек
        RealMatrix predictedMatrix = Uk.multiply(Sk).multiply(Vk.transpose());

        return predictedMatrix;
    }

    public static void main(String[] args) {
        // Входные данные: матрица оценок отелей
        double[][] ratingsData = {
                {1, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0},
                {5, 0, 0, 0, 0, 0, 4, 0, 0, 0, 0, 0},
                {1, 4, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 5, 5, 0, 0, 3, 0, 0, 0, 1, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
        };

        // Создание объекта HotelRatingPrediction
        HotelRatingPrediction prediction = new HotelRatingPrediction();

        // Преобразование массива в матрицу
        RealMatrix ratingsMatrix = MatrixUtils.createRealMatrix(ratingsData);

        // Предсказание оценок для пустых ячеек
        RealMatrix predictedMatrix = prediction.predictRatings(ratingsMatrix);

        System.out.println(" Ratings Matrix:");
        System.out.println(Arrays.deepToString(ratingsData));

        // Вывод предсказанных оценок
        System.out.println("Predicted Ratings Matrix:");
        System.out.println(predictedMatrix);
    }
}
