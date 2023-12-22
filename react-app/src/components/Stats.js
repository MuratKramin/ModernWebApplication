import React, { useState, useEffect } from "react";
import Chart from "chart.js/auto"; 

import UserService from "../services/user.service";
import EventBus from "../common/EventBus";
import HotelService from "../services/hotels.service";

const BoardUser = () => {
  const [content, setContent] = useState("");

  useEffect(() => {
    UserService.getUserBoard().then(
      (response) => {
        setContent(response.data);
      },
      (error) => {
        const _content =
          (error.response &&
            error.response.data &&
            error.response.data.message) ||
          error.message ||
          error.toString();

        setContent(_content);

        if (error.response && error.response.status === 403) {
          EventBus.dispatch("logout");
        }
      }
    );

    HotelService.getStats().then(
      (response) => {
        setContent(response.data);
        drawChart(response.data); // Вызываем функцию отрисовки диаграммы
      }
    );
  }, []);

  const drawChart = (data) => {
    const labels = data.map((item) => item[0]);
    const values = data.map((item) => parseInt(item[1], 10));

    const ctx = document.getElementById("hotelChart");

    new Chart(ctx, {
      type: "bar",
      data: {
        labels: labels,
        datasets: [
          {
            label: "Количество посещений",
            data: values,
            backgroundColor: "rgba(75,192,192,0.2)",
            borderColor: "rgba(75,192,192,1)",
            borderWidth: 1,
          },
        ],
      },
      options: {
        scales: {
          y: {
            beginAtZero: true,
          },
        },
      },
    });
  };

  return (
    <div className="container">
      <header className="jumbotron">
        {/* <h3>{content}</h3> */}
        <h3>Статистика посещений каждого отеля</h3>
        <canvas id="hotelChart" width="400" height="200"></canvas>
      </header>
    </div>
  );
};

export default BoardUser;
