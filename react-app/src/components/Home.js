import React, { useState, useEffect } from "react";

import UserService from "../services/user.service";
import Hotel from "./hotel";
import "./hotel.css";
import HotelService from "../services/hotels.service";

const Home = () => {
  const [content, setContent] = useState("");
  const [content2, setContent2] = useState("");

  useEffect(() => {
    UserService.getPublicContent().then(
      (response) => {
        setContent(response.data);
      },
      (error) => {
        const _content =
          (error.response && error.response.data) ||
          error.message ||
          error.toString();

        setContent(_content);
      }
    );

    HotelService.getHotel().then(
      (response) => {
        setContent2(response.data);
      },
      (error) => {
        const _content =
          (error.response && error.response.data) ||
          error.message ||
          error.toString();

        setContent2(_content);
      }
    );
  }, []);

  return (

    <div className="container">
      <header className="jumbotron">
        <h3>{content}</h3>
      </header>

      
      <div className="main">
      <div className="form">
        <form action="@{/findHotels}" method="GET">

          <div>
            <label data-toggle="tooltip" data-placement="top" title="">Кол-во человек</label>
            <input type="text" name="qty" id="qty" />
          </div>

          <div>
            <label >Кол-во дней</label>
            <input type="text" name="days" id="days" />
          </div>


          <div>
            <label>Семья</label>
            <input type="radio" name="campaign" id="family" value="family" />
          </div>
          <div>
            <label>Кол-во детей</label>
            <input type="text" name="children" id="children" />
          </div>

          <div>
            <label>Молодёжь</label>
            <input type="radio" name="campaign" id="the_youth" value="the_youth" />
          </div>
          <div>
            <label>Старые друзья</label>
            <input type="radio" name="campaign" id="old_friends" value="old_friends" />
          </div>








          <b>Необходимая выраженность фактора:</b>
          <div>
            <label>Уровень активности отдыха</label>
            <input type="range" min="1" max="5" name="activity" />
          </div>
          <div>
            <label>Уровень безопасности</label>
            <input type="range" min="1" max="5" name="safety" />
          </div>
          <div>
            <label>Уровень комфорта</label>
            <input type="range" min="1" max="5" name="comfort" />
          </div>

          <b>Чувствительность к фактору:</b>
          <button type="button" class="btn btn-light btn-sm" data-toggle="tooltip" data-placement="top" title=" Крайне правое положение - нежелание много платить или далеко ехать (дороговизна и дальние поездки очень напрягают). Крайне левое положение - цена не имеет значенние (готов ехать на край света).">
            ?
          </button>









          <div>
            <label>Стоимость</label>

            <input type="range" min="1" max="5" name="price" />
          </div>
          <div>
            <label>Расстояние</label>

            <input type="range" min="1" max="5" name="distance" />
          </div>


          <div>
            <label>Активный отдых на воде</label>
            <input className="form-check-input" type="checkbox" name="active_recreation_on_the_water" id="active_recreation_on_the_water" value="1" />
          </div>
          <div>
            <label>Рыбалка</label>
            <input className="form-check-input" type="checkbox" name="fishing" id="fishing" value="1" />
          </div>
          <div>
            <label>Футбол</label>
            <input className="form-check-input" type="checkbox" name="football" id="football" value="1" />
          </div>
          <div>
            <label>Волейбол</label>
            <input className="form-check-input" type="checkbox" name="volleyball" id="volleyball" value="1" />
          </div>
          <div>
            <label>Настольный теннис</label>
            <input className="form-check-input" type="checkbox" name="table_tennis" id="table_tennis" value="1" />
          </div>
          <div>
            <label>Большой теннис</label>
            <input className="form-check-input" type="checkbox" name="tennis" id="tennis" value="1" />
          </div>
          <div>
            <label>Велоспорт</label>
            <input className="form-check-input" type="checkbox" name="cycling" id="cycling" value="1" />
          </div>

          <div>
            <label>Максимальное расстояние от Казани (км)</label>

            <input className="" type="text" name="distance_from_Kazan" id="distance_from_Kazan" />
          </div>
          <div>
            <label>Максимальный бюджет (руб.)</label>
            <input className="" type="text" name="cost_of_stay_per_day" id="cost_of_stay_per_day" />
          </div>

          <input type="submit" />
        </form>
      </div>

      <div className="results">
        <Hotel />
      </div>

      </div>
    </div>
  );
};

export default Home;
