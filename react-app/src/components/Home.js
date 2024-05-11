import React, { useState, useEffect } from "react";
import UserService from "../services/user.service";
import Hotel from "./Hotels";
import "./home.css";
import HotelService from "../services/hotels.service";
import { Link } from "react-router-dom";

const Home = () => {
  const [content, setContent] = useState("");
  const [content2, setContent2] = useState([]);

  const [formData, setFormData] = useState({
    qty: '0',
    days: '0',
    children: '0',
    activity: '0',
    safety: '0',
    comfort: '0',
    price: '0',
    distance: '0',
    campaign: '0',
    active_recreation_on_the_water: false,
    fishing: false,
    football: false,
    volleyball: false,
    table_tennis: false,
    tennis: false,
    cycling: false,
    distance_from_Kazan: '0',
    budget: '0'
  });

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

    // Загрузка начального списка отелей
    HotelService.getHotel().then(
        (response) => {

          setContent2(response.data);
          console.log(response.data)
        },
        (error) => {
          console.error('Failed to fetch hotels:', error);
        }
    );
  }, []);

  const handleChange = (event) => {
    const { name, value, type, checked } = event.target;
    if (type === "checkbox") {
      setFormData({
        ...formData,
        [name]: checked
      });
    } else {
      setFormData({
        ...formData,
        [name]: value
      });
    }
  };

  const handleSubmit = (event) => {
    event.preventDefault();
    const numericData = Object.fromEntries(
        Object.entries(formData).map(([key, value]) => {
          if (typeof value === "boolean") {
            return [key, value ? '1' : '0'];
          }
          return [key, value];
        })
    );
    HotelService.findHotels(numericData).then(
        (response) => {
          setContent2(response.data);
          console.log(response.status)
        },
        (error) => {
          const _content =
              (error.response && error.response.data) ||
              error.message ||
              error.toString();
          setContent2(_content);
        }
    );
  };


  return (
      <div className="container">
        {/*<header className="jumbotron">*/}
        {/*  <h3>{content}</h3>*/}
        {/*</header>*/}

        <div className="main">
          <form onSubmit={handleSubmit} className="form">
            <div>
              <label htmlFor="qty">Количество человек:</label>
              <input type="text" name="qty" id="qty" value={formData.qty} onChange={handleChange} />
            </div>
            <div>
              <label htmlFor="days">Количество дней:</label>
              <input type="text" name="days" id="days" value={formData.days} onChange={handleChange} />
            </div>
            <div>
              <label htmlFor="children">Количество детей:</label>
              <input type="text" name="children" id="children" value={formData.children} onChange={handleChange} />
            </div>
            <div>
              <label htmlFor="family">Семья:</label>
              <input type="radio" name="campaign" id="family" value="family" checked={formData.campaign === 'family'} onChange={handleChange} />
              <label htmlFor="the_youth">Молодёжь:</label>
              <input type="radio" name="campaign" id="the_youth" value="the_youth" checked={formData.campaign === 'the_youth'} onChange={handleChange} />
              <label htmlFor="old_friends">Старые друзья:</label>
              <input type="radio" name="campaign" id="old_friends" value="old_friends" checked={formData.campaign === 'old_friends'} onChange={handleChange} />
            </div>
            <div>
              <label htmlFor="activity">Уровень активности отдыха:</label>
              <input type="range" min="1" max="5" name="activity" value={formData.activity} onChange={handleChange} />
            </div>
            <div>
              <label htmlFor="safety">Уровень безопасности:</label>
              <input type="range" min="1" max="5" name="safety" value={formData.safety} onChange={handleChange} />
            </div>
            <div>
              <label htmlFor="comfort">Уровень комфорта:</label>
              <input type="range" min="1" max="5" name="comfort" value={formData.comfort} onChange={handleChange} />
            </div>
            <div>
              <label htmlFor="price">Стоимость:</label>
              <input type="range" min="1" max="5" name="price" value={formData.price} onChange={handleChange} />
            </div>
            <div>
              <label htmlFor="distance">Расстояние:</label>
              <input type="range" min="1" max="5" name="distance" value={formData.distance} onChange={handleChange} />
            </div>
            <div>
              <label>Активный отдых на воде:</label>
              <input type="checkbox" name="active_recreation_on_the_water" id="active_recreation_on_the_water" checked={formData.active_recreation_on_the_water} onChange={handleChange} />
              <label>Рыбалка:</label>
              <input type="checkbox" name="fishing" id="fishing" checked={formData.fishing} onChange={handleChange} />
              <label>Футбол:</label>
              <input type="checkbox" name="football" id="football" checked={formData.football} onChange={handleChange} />
              <label>Волейбол:</label>
              <input type="checkbox" name="volleyball" id="volleyball" checked={formData.volleyball} onChange={handleChange} />
              <label>Настольный теннис:</label>
              <input type="checkbox" name="table_tennis" id="table_tennis" checked={formData.table_tennis} onChange={handleChange} />
              <label>Большой теннис:</label>
              <input type="checkbox" name="tennis" id="tennis" checked={formData.tennis} onChange={handleChange} />
              <label>Велоспорт:</label>
              <input type="checkbox" name="cycling" id="cycling" checked={formData.cycling} onChange={handleChange} />
            </div>
            <div>
              <label htmlFor="distance_from_Kazan">Максимальное расстояние от Казани (км):</label>
              <input type="text" name="distance_from_Kazan" id="distance_from_Kazan" value={formData.distance_from_Kazan} onChange={handleChange} />
            </div>
            <div>
              <label htmlFor="budget">Максимальный бюджет (руб.):</label>
              <input type="text" name="budget" id="budget" value={formData.budget} onChange={handleChange} />
            </div>
            <input type="submit" value="Search Hotels" />
          </form>

          {/*<div>*/}
          {/*  {Array.isArray(content2) && content2.map(hotel => (*/}
          {/*      <div key={hotel.id}>*/}

          {/*        <Link to={`/hotel/${hotel.id}`}><h2>{hotel.name}</h2>*/}
          {/*          <img src={hotel.photoList[0] ? hotel.photoList[0].link : ''} alt={hotel.name} />*/}
          {/*        </Link>*/}

          {/*        <p style={{ whiteSpace: 'pre-line' }}>{hotel.description}</p>*/}

          {/*      </div>*/}
          {/*  )) || <div> <h3>Упс! Нет подходящих отелей </h3> </div>}*/}
          {/*</div>*/}

          <div>
            <div className="container mt-4">
              <h2>Места отдыха по вашим параметрам:</h2>
              <div className="row">
                {Array.isArray(content2) && content2.map((hotel, index) => (
                    <div className={`col-md-6 ${index < 3 ? 'highlighted' : ''}`} key={hotel.id}>
                      <div className="card mb-3" style={{width: '100%'}}>
                        <Link to={`/hotel/${hotel.id}`} >
                        <div className="card-body-number">{index + 1}</div>
                        <img src={hotel.photoList[0].link} className="card-img-top" alt={hotel.name} />
                        <div className="card-body">
                          <h5 className="card-title">{hotel.name}</h5>

                        </div>
                       </Link>
                      </div>
                    </div>
                ))}
              </div>
            </div>
          </div>
        </div>
      </div>
  );
};

export default Home;
