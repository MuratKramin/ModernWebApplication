import React, { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import HotelService from '../services/hotels.service';
import ResidenceHistoryService from '../services/residenceHistoryService';
import AuthService from "../services/auth.service";
import "./HotelDetails.css"; // Убедитесь, что путь к файлу CSS корректен



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
    const mainImages = hotel.photoList?.slice(0, 1);
    const otherImages = hotel.photoList?.slice(1);

    return (
        <div className="container">
            <div className="header">
                {mainImages && mainImages.length > 0 && (
                    <div className="images-sidebar">
                        {mainImages.map((img, index) => (
                            <img key={index} src={img.link} alt={`Hotel View ${index + 1}`} />
                        ))}
                    </div>
                )}
                <div className="header-info">
                    <h2>{hotel.name}</h2>
                    <p style={{ whiteSpace: 'pre-line' }}>{hotel.description}</p>
                    <Link to="/home">Back to list</Link>
                </div>
            </div>
            {otherImages && otherImages.length > 0 && (
                <div className="main-images">
                    {otherImages.map((img, index) => (
                        <img key={index} src={img.link} alt={`Hotel View ${index + 3}`} />
                    ))}
                </div>
            )}
            <div className="review-container">
                <h3>Reviews</h3>
                {histories.map(history => (
                    <div key={history.id} className="review">
                        <div className="review-details">
                            <p className="review-text">{history.review}</p>
                        </div>
                        <div className="review-grade">
                            {history.grade}
                        </div>
                    </div>
                ))}
            </div>
            {currentUser && (
                <div className="add-review-form">
                    <h4>Add your review:</h4>
                    <div className="form-group">
                        <label className="form-label">Check-in:</label>
                        <input type="date" className="form-input" value={checkInDate} onChange={e => setCheckInDate(e.target.value)} />
                    </div>
                    <div className="form-group">
                        <label className="form-label">Check-out:</label>
                        <input type="date" className="form-input" value={checkOutDate} onChange={e => setCheckOutDate(e.target.value)} />
                    </div>
                    <div className="form-group">
                        <label className="form-label">Total cost:</label>
                        <input type="number" className="form-input" value={totalCost} onChange={e => setTotalCost(e.target.value)} min="0" />
                    </div>
                    <div className="form-group">
                        <label className="form-label">Review:</label>
                        <textarea className="form-textarea" value={newReview} onChange={e => setNewReview(e.target.value)} />
                    </div>
                    <div className="form-group">
                        <label className="form-label">Grade:</label>
                        <input type="range" className="form-input" value={newGrade} min="1" max="5" onChange={e => setNewGrade(e.target.value)} />
                    </div>
                    <button className="form-button" onClick={handleSubmitReview}>Submit Review</button>
                </div>
            )}
        </div>
    );
}

export default HotelDetails;

