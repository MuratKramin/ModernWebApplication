import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import HotelService from '../services/hotels.service';
import AuthService from '../services/auth.service';
import './main.css'; // Импорт стилей

function Main() {
    const [popularHotels, setPopularHotels] = useState([]);
    const [recommendedHotels, setRecommendedHotels] = useState([]);
    const [currentUser, setCurrentUser] = useState(null);

    useEffect(() => {
        // Получаем список популярных отелей
        HotelService.getPopular().then(response => {
            setPopularHotels(response.data);
        });

        // Проверяем, авторизован ли пользователь
        const user = AuthService.getCurrentUser();
        if (user) {
            setCurrentUser(user);
            // Получаем список рекомендованных отелей для авторизованного пользователя
            HotelService.getRecommendedByUserId(user.id).then(response => {
                setRecommendedHotels(response.data);
            });
        }
    }, []);

    return (
        <div >
            <div className="container mt-4">
                <h2>Популярные места отдыха</h2>
                <div className="d-flex overflow-auto" >
                    {popularHotels.map(hotel => (
                        <div className="card-pop" key={hotel.id}>
                            <Link to={`/hotel/${hotel.id}`} >
                            <img src={hotel.photoList[0].link} className="card-img-top" alt={hotel.name} />
                            <div className="card-body">
                                <h5 className="card-title">{hotel.name}</h5>
                                {/*<Link to={`/hotel/${hotel.id}`} className="btn btn-primary">Посмотреть детали</Link>*/}
                            </div>
                            </Link>
                        </div>
                    ))}
                </div>
            </div>

            {currentUser && (
                <div className="container mt-4">
                    <h2>Рекомендованные Вам места отдыха</h2>
                    <div className="row">
                        {recommendedHotels.map((hotel, index) => (
                            <div className={`col-md-4 ${index < 3 ? 'highlighted' : ''}`} key={hotel.id}>
                                <div className="card mb-3">
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
            )}
        </div>
    );
}

export default Main;
