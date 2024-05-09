package com.spring.backend.testAlorithms;

import java.util.Arrays;

public class MatrixFactorization {
    private double[][] userMatrix;
    private double[][] hotelMatrix;
    private double[][] predictedRatings;
    private int numUsers;
    private int numHotels;
    private int numFeatures;
    private int maxIterations;
    private double learningRate;
    private double regularization;

    public MatrixFactorization(double[][] ratings, int numFeatures, int maxIterations, double learningRate, double regularization) {
        this.userMatrix = ratings;
        this.numUsers = ratings.length;
        this.numHotels = ratings[0].length;
        this.numFeatures = numFeatures;
        this.maxIterations = maxIterations;
        this.learningRate = learningRate;
        this.regularization = regularization;

        initializeMatrices();
    }

    private void initializeMatrices() {
        hotelMatrix = new double[numHotels][numFeatures];
        predictedRatings = new double[numUsers][numHotels];

        for (int i = 0; i < numHotels; i++) {
            for (int j = 0; j < numFeatures; j++) {
                hotelMatrix[i][j] = Math.random(); // Random initialization of hotel matrix
            }
        }
    }

    public void train() {
        for (int iter = 0; iter < maxIterations; iter++) {
            for (int i = 0; i < numUsers; i++) {
                for (int j = 0; j < numHotels; j++) {
                    if (userMatrix[i][j] > 0) {
                        double error = userMatrix[i][j] - predictedRatings[i][j];
                        for (int k = 0; k < numFeatures; k++) {
                            double userFeature = predictedRatings[i][k];
                            double hotelFeature = hotelMatrix[j][k];
                            predictedRatings[i][j] += learningRate * (error * hotelFeature - regularization * userFeature);
                            hotelMatrix[j][k] += learningRate * (error * userFeature - regularization * hotelFeature);
                        }
                    }
                }
            }
        }
    }

    public double[][] getPredictedRatings() {
        return predictedRatings;
    }

    public static void main(String[] args) {
        double[][] actualRatings = {
                {1, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0},
                {5, 0, 0, 0, 0, 0, 4, 0, 0, 0, 0, 0},
                {1, 4, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 5, 5, 0, 0, 3, 0, 0, 0, 1, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
        };

        int numFeatures = 7;
        int maxIterations = 200;
        double learningRate = 0.1;
        double regularization = 0.2;

        MatrixFactorization mf = new MatrixFactorization(actualRatings, numFeatures, maxIterations, learningRate, regularization);
        mf.train();
        double[][] predictedRatings = mf.getPredictedRatings();

        System.out.println(Arrays.deepToString(predictedRatings));

        // Print predicted ratings matrix
        for (int i = 0; i < predictedRatings.length; i++) {
            System.out.println("User " + (i+1) + ": " + Arrays.toString(predictedRatings[i]));
        }
    }
}
