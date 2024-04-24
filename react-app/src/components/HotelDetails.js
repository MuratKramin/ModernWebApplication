import React, { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import axios from 'axios';
import HotelService from '../services/hotels.service';
import AuthService from "../services/auth.service";


function HotelDetails() {
    const [hotel, setHotel] = useState(null);
    const { id } = useParams();

    const [histories, setHistories] = useState(null);
    const [newReview, setNewReview] = useState("");
    const [newGrade, setNewGrade] = useState(1);

    useEffect(() => {
        HotelService.getHotelById(id).then(
            (response) => {
                setHotel(response.data);
            },
            (error) => {
                const _content =
                    (error.response && error.response.data) ||
                    error.message ||
                    error.toString();
                setHotel(_content);
            }
        );
        HotelService.getResidenceHistory(id).then(
            response => {
                console.log(response)
                setHistories(response.data);
                console.log(response.data)
            },
            error => {
                console.log("Error fetching residence histories:", error);
            }
        );
        //console.log(AuthService.getCurrentUser());
    }, [id]);

    if (!hotel) {
        return <div>Loading...</div>;
    }

    return (

        <div>
            hi
            <h2>{hotel.name}</h2>
            <img src={hotel.photoList[0].link} alt={hotel.name} />
            <p style={{ whiteSpace: 'pre-line' }}>{hotel.description}</p>
            <Link to="/home">Back to list</Link>
            {Array.isArray(histories) && histories.map(history => (
                <div key={history.id}>
                    <p>User: {history.users_rev.username} {history.review}  Grade: {history.grade} </p>
                </div>
            ))



            }
        </div>
    );
}

export default HotelDetails;
