package com.spring.backend.testAlorithms;

import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.ml.recommendation.ALS;
import org.apache.spark.ml.recommendation.ALSModel;

import java.io.Serializable;
import java.util.Arrays;

public class ALSPrediction2 {
    public static void main(String[] args) {
        // Инициализация Spark
        SparkSession spark = SparkSession.builder().appName("ALS Example").master("local").getOrCreate();

        // Подготовка данных
        Dataset<Row> data = spark.createDataFrame(Arrays.asList(
                new Rating(0, 2, 3),
                new Rating(0, 5, 5),
                new Rating(0, 8, 5),
                new Rating(0, 10, 4),
                new Rating(1, 2, 5),
                new Rating(1, 3, 4),
                new Rating(1, 6, 4),
                new Rating(1, 9, 2),
                new Rating(1, 10, 1),
                new Rating(1, 11, 3),
                new Rating(2, 0, 2),
                new Rating(2, 1, 4),
                new Rating(2, 3, 1),
                new Rating(2, 4, 2),
                new Rating(2, 6, 3),
                new Rating(2, 8, 4),
                new Rating(2, 9, 3),
                new Rating(2, 10, 5),
                new Rating(3, 1, 2),
                new Rating(3, 2, 4),
                new Rating(3, 4, 5),
                new Rating(3, 7, 4),
                new Rating(3, 10, 2),
                new Rating(4, 2, 4),
                new Rating(4, 3, 3),
                new Rating(4, 4, 4),
                new Rating(4, 5, 2),
                new Rating(4, 10, 2),
                new Rating(4, 11, 5),
                new Rating(5, 0, 1),
                new Rating(5, 2, 3),
                new Rating(5, 4, 3),
                new Rating(5, 7, 2),
                new Rating(5, 10, 4)
        ), Rating.class);

        // Настройка модели ALS
        ALS als = new ALS()
                .setMaxIter(1)
                .setRank(3)
                .setRegParam(0.01)
                .setUserCol("user")
                .setItemCol("item")
                .setRatingCol("rating");

        // Обучение модели
        ALSModel model = als.fit(data);

        // Предсказание оценки
        Dataset<Row> testData = spark.createDataFrame(Arrays.asList(new Rating(1, 4, 0)), Rating.class);
        Dataset<Row> predictions = model.transform(testData);

        // Вывод предсказания
        predictions.show();

        // Закрытие Spark
        spark.stop();
    }

    public static class Rating implements Serializable {
        private int user;
        private int item;
        private float rating;

        public Rating(int user, int item, float rating) {
            this.user = user;
            this.item = item;
            this.rating = rating;
        }

        public int getUser() {
            return user;
        }

        public void setUser(int user) {
            this.user = user;
        }

        public int getItem() {
            return item;
        }

        public void setItem(int item) {
            this.item = item;
        }

        public float getRating() {
            return rating;
        }

        public void setRating(float rating) {
            this.rating = rating;
        }
    }

}
