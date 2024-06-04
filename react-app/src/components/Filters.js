import React, { useState, useEffect } from "react";
import UserService from "../services/user.service";

import "./filters.css";
import HotelService from "../services/hotels.service";
import { Link } from "react-router-dom";
import LikesService from "../services/likes.service";
import AuthService from "../services/auth.service";

const Filters = () => {
  const [content, setContent] = useState("");
  const [content2, setContent2] = useState([]);
  const [currentUser, setCurrentUser] = useState(null);
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

    HotelService.getHotel().then(
        (response) => {
          setContent2(response.data);
          console.log(response.data);
        },
        (error) => {
          console.error("Failed to fetch hotels:", error);
        }
    );

    const user = AuthService.getCurrentUser();
    if (user) {
      setCurrentUser(user);
    }
  }, []);

  const handleChange = (event) => {
    const { name, value, type, checked } = event.target;
    if (type === "checkbox") {
      setFormData({
        ...formData,
        [name]: checked,
      });
    } else {
      setFormData({
        ...formData,
        [name]: value,
      });
    }
  };

  const handleSubmit = (event) => {
    event.preventDefault();
    const numericData = Object.fromEntries(
        Object.entries(formData).map(([key, value]) => {
          if (typeof value === "boolean") {
            return [key, value ? "1" : "0"];
          }
          return [key, value];
        })
    );
    HotelService.findHotels(numericData).then(
        (response) => {
          setContent2(response.data);
          console.log(response.status);
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

  function handleLike(hotelId) {
    console.log(hotelId);
    const userId = currentUser && currentUser.id;
    LikesService.createLikeById(userId, hotelId)
        .then((response) => {
          console.log("Like added:", response.data);
        })
        .catch((error) => {
          console.error("Failed to add like:", error);
        });
  }

  return (
      <div className="container">
        <div className="main">
          <form onSubmit={handleSubmit} className="form">
            <div className="form-group">
              <label htmlFor="qty">Количество человек:</label>
              <input
                  type="text"
                  className="form-control"
                  name="qty"
                  id="qty"
                  value={formData.qty}
                  onChange={handleChange}
              />
            </div>
            <div className="form-group">
              <label htmlFor="days">Количество дней:</label>
              <input
                  type="text"
                  className="form-control"
                  name="days"
                  id="days"
                  value={formData.days}
                  onChange={handleChange}
              />
            </div>
            <div className="form-group">
              <label htmlFor="children">Количество детей:</label>
              <input
                  type="text"
                  className="form-control"
                  name="children"
                  id="children"
                  value={formData.children}
                  onChange={handleChange}
              />
            </div>
            <div className="form-group">
              <label>Семья:</label>
              <div className="form-check">
                <input
                    type="radio"
                    className="form-check-input"
                    name="campaign"
                    id="family"
                    value="family"
                    checked={formData.campaign === "family"}
                    onChange={handleChange}
                />
                <label className="form-check-label" htmlFor="family">
                  Семья
                </label>
              </div>
              <div className="form-check">
                <input
                    type="radio"
                    className="form-check-input"
                    name="campaign"
                    id="the_youth"
                    value="the_youth"
                    checked={formData.campaign === "the_youth"}
                    onChange={handleChange}
                />
                <label className="form-check-label" htmlFor="the_youth">
                  Молодёжь
                </label>
              </div>
              <div className="form-check">
                <input
                    type="radio"
                    className="form-check-input"
                    name="campaign"
                    id="old_friends"
                    value="old_friends"
                    checked={formData.campaign === "old_friends"}
                    onChange={handleChange}
                />
                <label className="form-check-label" htmlFor="old_friends">
                  Старые друзья
                </label>
              </div>
            </div>
            <div className="form-group">
              <label htmlFor="activity">Уровень активности отдыха:</label>
              <input
                  type="range"
                  className="form-control"
                  min="1"
                  max="5"
                  name="activity"
                  value={formData.activity}
                  onChange={handleChange}
              />
            </div>
            <div className="form-group">
              <label htmlFor="safety">Уровень безопасности:</label>
              <input
                  type="range"
                  className="form-control"
                  min="1"
                  max="5"
                  name="safety"
                  value={formData.safety}
                  onChange={handleChange}
              />
            </div>
            <div className="form-group">
              <label htmlFor="comfort">Уровень комфорта:</label>
              <input
                  type="range"
                  className="form-control"
                  min="1"
                  max="5"
                  name="comfort"
                  value={formData.comfort}
                  onChange={handleChange}
              />
            </div>
            <div className="form-group">
              <label htmlFor="price">Стоимость:</label>
              <input
                  type="range"
                  className="form-control"
                  min="1"
                  max="5"
                  name="price"
                  value={formData.price}
                  onChange={handleChange}
              />
            </div>
            <div className="form-group">
              <label htmlFor="distance">Расстояние:</label>
              <input
                  type="range"
                  className="form-control"
                  min="1"
                  max="5"
                  name="distance"
                  value={formData.distance}
                  onChange={handleChange}
              />
            </div>
            <div className="form-group">
              <label>Активный отдых на воде:</label>
              <div className="form-check">
                <input
                    type="checkbox"
                    className="form-check-input"
                    name="active_recreation_on_the_water"
                    id="active_recreation_on_the_water"
                    checked={formData.active_recreation_on_the_water}
                    onChange={handleChange}
                />
                <label
                    className="form-check-label"
                    htmlFor="active_recreation_on_the_water"
                >
                  Активный отдых на воде
                </label>
              </div>
              <div className="form-check">
                <input
                    type="checkbox"
                    className="form-check-input"
                    name="fishing"
                    id="fishing"
                    checked={formData.fishing}
                    onChange={handleChange}
                />
                <label className="form-check-label" htmlFor="fishing">
                  Рыбалка
                </label>
              </div>
              <div className="form-check">
                <input
                    type="checkbox"
                    className="form-check-input"
                    name="football"
                    id="football"
                    checked={formData.football}
                    onChange={handleChange}
                />
                <label className="form-check-label" htmlFor="football">
                  Футбол
                </label>
              </div>
              <div className="form-check">
                <input
                    type="checkbox"
                    className="form-check-input"
                    name="volleyball"
                    id="volleyball"
                    checked={formData.volleyball}
                    onChange={handleChange}
                />
                <label className="form-check-label" htmlFor="volleyball">
                  Волейбол
                </label>
              </div>
              <div className="form-check">
                <input
                    type="checkbox"
                    className="form-check-input"
                    name="table_tennis"
                    id="table_tennis"
                    checked={formData.table_tennis}
                    onChange={handleChange}
                />
                <label className="form-check-label" htmlFor="table_tennis">
                  Настольный теннис
                </label>
              </div>
              <div className="form-check">
                <input
                    type="checkbox"
                    className="form-check-input"
                    name="tennis"
                    id="tennis"
                    checked={formData.tennis}
                    onChange={handleChange}
                />
                <label className="form-check-label" htmlFor="tennis">
                  Большой теннис
                </label>
              </div>
              <div className="form-check">
                <input
                    type="checkbox"
                    className="form-check-input"
                    name="cycling"
                    id="cycling"
                    checked={formData.cycling}
                    onChange={handleChange}
                />
                <label className="form-check-label" htmlFor="cycling">
                  Велоспорт
                </label>
              </div>
            </div>
            <div className="form-group">
              <label htmlFor="distance_from_Kazan">
                Максимальное расстояние от Казани (км):
              </label>
              <input
                  type="text"
                  className="form-control"
                  name="distance_from_Kazan"
                  id="distance_from_Kazan"
                  value={formData.distance_from_Kazan}
                  onChange={handleChange}
              />
            </div>
            <div className="form-group">
              <label htmlFor="budget">Максимальный бюджет (руб.):</label>
              <input
                  type="text"
                  className="form-control"
                  name="budget"
                  id="budget"
                  value={formData.budget}
                  onChange={handleChange}
              />
            </div>
            <button type="submit" className="btn btn-primary">
              Search Hotels
            </button>
          </form>

          <div>
            <div className="container mt-4">
              <h2>Места отдыха по вашим параметрам:</h2>
              <div className="row">
                {Array.isArray(content2) &&
                    content2.map((hotel, index) => (
                        <div
                            className={`col-md-6 ${index < 3 ? "highlighted" : ""}`}
                            key={hotel.id}
                        >
                          <div className="card mb-3" style={{ width: "100%" }}>
                            {currentUser && (
                                <button className="heart-btn" onClick={() => handleLike(hotel.id)}>
                                  &#10084;
                                </button>
                            )}
                            <Link to={`/hotel/${hotel.id}`}>
                              <div className="card-body-number">{index + 1}</div>
                              <img
                                  src={hotel.photoList[0].link}
                                  className="card-img-top"
                                  alt={hotel.name}
                              />
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

export default Filters;
