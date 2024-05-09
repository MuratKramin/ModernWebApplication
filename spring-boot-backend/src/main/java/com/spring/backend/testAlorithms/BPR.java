package com.spring.backend.testAlorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class BPR {
    private static final int FACTORS = 10; // Количество скрытых факторов
    private static final int EPOCHS = 100; // Количество эпох обучения
    private static final double LEARNING_RATE = 0.01; // Скорость обучения
    private static final double REGULARIZATION = 0.01; // Коэффициент регуляризации

    private double[][] userFactors;
    private double[][] itemFactors;
    private int numUsers;
    private int numItems;

    public BPR(int numUsers, int numItems) {
        this.numUsers = numUsers;
        this.numItems = numItems;
        this.userFactors = new double[numUsers][FACTORS];
        this.itemFactors = new double[numItems][FACTORS];
        initialize();
    }

    private void initialize() {
        Random random = new Random();
        for (int u = 0; u < numUsers; u++) {
            for (int f = 0; f < FACTORS; f++) {
                userFactors[u][f] = random.nextGaussian() * 0.1;
            }
        }
        for (int i = 0; i < numItems; i++) {
            for (int f = 0; f < FACTORS; f++) {
                itemFactors[i][f] = random.nextGaussian() * 0.1;
            }
        }
    }

    public void train(int[][] matrix) {
        Random random = new Random();
        for (int epoch = 0; epoch < EPOCHS; epoch++) {
            for (int u = 0; u < numUsers; u++) {
                int[] positiveItems = new int[numItems];
                int positiveItemCount = 0;
                int[] negativeItems = new int[numItems];
                int negativeItemCount = 0;

                // Собираем индексы позитивных и негативных итемов
                for (int i = 0; i < numItems; i++) {
                    if (matrix[i][u] > 0) {
                        positiveItems[positiveItemCount++] = i;
                    } else {
                        negativeItems[negativeItemCount++] = i;
                    }
                }

                for (int idx = 0; idx < positiveItemCount; idx++) {
                    int i = positiveItems[idx];
                    if (negativeItemCount > 0) {
                        int j = negativeItems[random.nextInt(negativeItemCount)];

                        double x_uij = predict(u, i) - predict(u, j);
                        double gradient = -1.0 / (1.0 + Math.exp(x_uij));

                        for (int f = 0; f < FACTORS; f++) {
                            double uF = userFactors[u][f];
                            double iF = itemFactors[i][f];
                            double jF = itemFactors[j][f];

                            userFactors[u][f] -= LEARNING_RATE * (gradient * (iF - jF) + REGULARIZATION * uF);
                            itemFactors[i][f] -= LEARNING_RATE * (gradient * uF + REGULARIZATION * iF);
                            itemFactors[j][f] -= LEARNING_RATE * (-gradient * uF + REGULARIZATION * jF);
                        }
                    }
                }
            }
        }
    }


    private double predict(int u, int i) {
        double score = 0;
        for (int f = 0; f < FACTORS; f++) {
            score += userFactors[u][f] * itemFactors[i][f];
        }
        return score;
    }

//    public static void main(String[] args) {
//        int[][] ratings = {
//                {1,0,3,0,0,5,0,0,5,0,4,0},
//                {0,0,5,4,0,0,4,0,0,2,1,3},
//                {2,4,0,1,2,0,3,0,4,3,5,0},
//                {0,2,4,0,5,0,0,4,0,0,2,0},
//                {0,0,4,3,4,2,0,0,0,0,2,5},
//                {1,0,3,0,3,0,0,2,0,0,4,0}
//        };
//        BPR bpr = new BPR(12, 6);
//        bpr.train(ratings);
//        System.out.println("Predicted rating for user 4, item 1: " + bpr.predict(4, 1));
//    }

    public class ItemRating implements Comparable<ItemRating> {
        public int itemId;
        public double rating;

        public ItemRating(int itemId, double rating) {
            this.itemId = itemId;
            this.rating = rating;
        }

        @Override
        public int compareTo(ItemRating other) {
            return Double.compare(other.rating, this.rating); // Сортировка по убыванию
        }
    }

    public List<ItemRating> recommendItems(int[][] matrix, int userIndex, int topN) {
        List<ItemRating> itemRatings = new ArrayList<>();
        for (int i = 0; i < numItems; i++) {
            if (matrix[i][userIndex] == 0) { // Товар не оценен пользователем
                double rating = predict(userIndex, i);
                itemRatings.add(new ItemRating(i, rating));
            }
        }
        Collections.sort(itemRatings);
        return itemRatings.subList(0, Math.min(topN, itemRatings.size())); // Возвращает топ-N предметов
    }

    public static void main(String[] args) {
        int[][] ratings = {
                {1,0,3,0,0,5,0,0,5,0,4,0},
                {0,0,5,4,0,0,4,0,0,2,1,3},
                {2,4,0,1,2,0,3,0,4,3,5,0},
                {0,2,4,0,5,0,0,4,0,0,2,0},
                {0,0,4,3,4,2,0,0,0,0,2,5},
                {1,0,3,0,3,0,0,2,0,0,4,0}
        };
        BPR bpr = new BPR(12, 6);
        bpr.train(ratings);
        List<ItemRating> recommendations = bpr.recommendItems(ratings, 4, 5);
        System.out.println("Top 5 recommendations for user 0:");
        for (ItemRating ir : recommendations) {
            System.out.println("Item " + ir.itemId + " with predicted rating: " + ir.rating);
        }
    }
}
