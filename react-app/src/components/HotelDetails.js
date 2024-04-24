import React, { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import HotelService from '../services/hotels.service';
import ResidenceHistoryService from '../services/residenceHistoryService';
import AuthService from "../services/auth.service";


function HotelDetails() {
    const { id } = useParams();
    const [hotel, setHotel] = useState(null);
    const [histories, setHistories] = useState([]);
    const [newReview, setNewReview] = useState("");
    const [newGrade, setNewGrade] = useState(1);
    const [checkInDate, setCheckInDate] = useState("");
    const [checkOutDate, setCheckOutDate] = useState("");
    const [totalCost, setTotalCost] = useState(0);
    const currentUser = AuthService.getCurrentUser();

    useEffect(() => {
        HotelService.getHotelById(id).then(
            response => setHotel(response.data),
            error => console.log("Error fetching hotel details:", error)
        );

        ResidenceHistoryService.getResidenceHistoriesByHotelId(id).then(
            response => setHistories(response.data),
            error => console.log("Error fetching residence histories:", error)
        );
        console.log(histories);
    }, [id]);

    const handleSubmitReview = () => {
        if (!currentUser) return;

        const formData = {
            checkInDate,
            checkOutDate,
            totalCost,
            review: newReview,
            grade: parseInt(newGrade, 10),

            hotel_rev: {id:parseInt(id, 10)},
            user_rev: {id: parseInt(currentUser.id)}
        };

        ResidenceHistoryService.createResidenceHistory(formData).then(
            response => {
                // Предполагаем, что response.data содержит добавленный отзыв
                // Обновляем список отзывов добавляя новый отзыв
                setHistories(prevHistories => [...prevHistories, response.data]);
                // Сброс формы
                setNewReview("");
                setNewGrade(1);
                setCheckInDate("");
                setCheckOutDate("");
                setTotalCost(0);
            },
            error => {
                console.log("Error posting review:", error);
            }
        );
    };

    if (!hotel) {
        return <div>Loading...</div>;
    }

    return (
        <div>
            <h2>{hotel.name}</h2>
            <img src={hotel.photoList[0]?.link} alt={hotel.name} />
            <p style={{ whiteSpace: 'pre-line' }}>{hotel.description}</p>
            <Link to="/home">Back to list</Link>
            <div>
                <h3>Reviews</h3>
                {


                    histories.map(history => (
                    <div key={history.id}>
                        <p>{history.review}</p>
                        <p>Grade: {history.grade}</p>
                    </div>
                ))}
            </div>
            {currentUser && (
                <div>
                    <h4>Add your review:</h4>
                    <label>
                        Check-in:
                        <input type="date" value={checkInDate} onChange={e => setCheckInDate(e.target.value)} />
                    </label>
                    <label>
                        Check-out:
                        <input type="date" value={checkOutDate} onChange={e => setCheckOutDate(e.target.value)} />
                    </label>
                    <label>
                        total cost:
                        <input type="number" value={totalCost} onChange={e => setTotalCost(e.target.value)} min="0" />
                    </label>
                    <label>
                        Отзыв:
                        <textarea value={newReview} onChange={e => setNewReview(e.target.value)} />
                    </label>

                    {/*<label>*/}
                    {/*    Grade:*/}
                    {/*    <input type="number" value={newGrade} min="1" max="5" onChange={e => setNewGrade(e.target.value)} />*/}
                    {/*</label>*/}
                    <label>
                        Grade:
                        <input type="range" value={newGrade} min="1" max="5" onChange={e => setNewGrade(e.target.value)} />
                    </label>
                    <button onClick={handleSubmitReview}>Submit Review</button>
                </div>
            )}
        </div>
    );
}

export default HotelDetails;

