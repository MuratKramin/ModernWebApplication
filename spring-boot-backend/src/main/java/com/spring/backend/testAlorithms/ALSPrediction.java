package com.spring.backend.testAlorithms;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.ml.recommendation.ALS;
import org.apache.spark.ml.recommendation.ALSModel;
import org.apache.spark.mllib.recommendation.MatrixFactorizationModel;
import org.apache.spark.mllib.recommendation.Rating;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.ml.recommendation.ALS;
import org.apache.spark.ml.recommendation.ALSModel;
import org.apache.spark.sql.*;
import org.apache.spark.sql.types.*;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.LogManager;

public class ALSPrediction {
    private static final double[][] ratingsMatrix = {
            {1,0,3,0,0,5,0,0,5,0,4,0},
            {0,0,5,4,0,0,4,0,0,2,1,3},
            {2,4,0,1,2,0,3,0,4,3,5,0},
            {0,2,4,0,5,0,0,4,0,0,2,0},
            {0,0,4,3,4,2,0,0,0,0,2,5},
            {1,0,3,0,3,0,0,2,0,0,4,0}

    };

    public static void main(String[] args) {
        SparkSession spark = SparkSession.builder()
                .appName("ALS Example")
                .config("spark.master", "local")
                .getOrCreate();

        //LogManager.getLogManager().reset();

        Logger.getLogger("org.apache.spark").setLevel(Level.OFF);
        Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF);
        Logger.getLogger("io.netty").setLevel(Level.OFF);

        //JavaSparkContext javaSparkContext = new JavaSparkContext();


        List<Row> dataList = new ArrayList<>();
        for (int i = 0; i < ratingsMatrix.length; i++) {
            for (int j = 0; j < ratingsMatrix[i].length; j++) {
                if (ratingsMatrix[i][j] != 0) {
                    dataList.add(RowFactory.create(i, j, ratingsMatrix[i][j]));
                    System.out.println(ratingsMatrix[i][j]);
                }
            }
        }

        StructType schema = new StructType(new StructField[]{
                new StructField("user", DataTypes.IntegerType, false, Metadata.empty()),
                new StructField("item", DataTypes.IntegerType, false, Metadata.empty()),
                new StructField("rating", DataTypes.DoubleType, false, Metadata.empty())
        });

        List<Rating> ratingsList = Arrays.asList(
                new Rating(0, 0, 4.0),
                new Rating(0, 1, 2.0),
                new Rating(1, 0, 3.0),
                new Rating(1, 1, 4.0),
                new Rating(2, 0, 1.0),
                new Rating(2, 1, 5.0)
        );

// Преобразование в DataFrame
        //Dataset<Row> ratingsDF = spark.createDataFrame(ratingsList, Rating.class).withColumn("user",new Column("user"));


        Dataset<Row> ratings = spark.createDataFrame(dataList, schema);
        //Dataset<Row> ratings = spark.createDataFrame(ratingsRDD, Rating.class);

        ratings.printSchema();
        ALS als = new ALS()
                .setRank(2)
                .setAlpha(1.5)
                .setImplicitPrefs(true)
                .setMaxIter(5)
                .setRegParam(0.01)
                .setUserCol("user")
                .setItemCol("item")
                .setRatingCol("rating")
                .setColdStartStrategy("nan");

        ALSModel model = als.fit(ratings);




        //System.out.println(Arrays.deepToString(matrixFactorization.getPredictedRatings()));
        Dataset<Row> itemFactors = model.itemFactors();
        itemFactors.show(1500);
        Dataset<Row> userFactors = model.userFactors();
        System.out.println("userFactors");
        userFactors.show();
        Dataset<Row> predictions = model.transform(ratings);

        // Создаем пустую матрицу для результатов предсказаний
        double[][] predictedMatrix = new double[ratingsMatrix.length][ratingsMatrix[0].length];
        for (int i = 0; i < ratingsMatrix.length; i++) {
            for (int j = 0; j < ratingsMatrix[i].length; j++) {
                predictedMatrix[i][j] = 0;  // Копирование исходных рейтингов
            }
        }

        // Заполнение матрицы предсказаниями
        predictions.collectAsList().forEach(row -> {
            int user = row.getInt(0);
            int item = row.getInt(1);
            double rating = row.getDouble(2);
            predictedMatrix[user][item] = rating;
        });

        // Вывод результата в консоль
        System.out.println("Predicted Ratings Matrix:");
        for (double[] row : predictedMatrix) {
            for (double value : row) {
                System.out.printf("%5.2f ", value);
            }
            System.out.println();
        }


        // Создание полного списка всех пар пользователь-предмет
        List<Row> allUserItemPairs = new ArrayList<>();
        for (int user = 0; user < ratingsMatrix.length; user++) {
            for (int item = 0; item < ratingsMatrix[0].length; item++) {
                allUserItemPairs.add(RowFactory.create(user, item));
            }
        }

// Определение схемы для нового DataFrame
        StructType userItemSchema = new StructType(new StructField[]{
                new StructField("user", DataTypes.IntegerType, false, Metadata.empty()),
                new StructField("item", DataTypes.IntegerType, false, Metadata.empty())
        });

// Создание DataFrame со всеми комбинациями пользователь-предмет
        Dataset<Row> allUserItemDF = spark.createDataFrame(allUserItemPairs, userItemSchema);

// Получение предсказаний для всех комбинаций
        Dataset<Row> allPredictions = model.transform(allUserItemDF);

// Показать предсказания
        allPredictions.show();

        model.recommendForAllUsers(2).show();


//        Dataset<Row> testData = spark.createDataFrame(Arrays.asList(RowFactory.create(1, 4, 0), schema));
//        Dataset<Row> predictions2 = model.transform(testData);
//        predictions2.show();

        spark.stop();


    }
}
