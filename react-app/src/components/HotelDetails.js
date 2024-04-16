import React, { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import axios from 'axios';
import HotelService from '../services/hotels.service';

function HotelDetails() {
    const [hotel, setHotel] = useState(null);
    const { id } = useParams();

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
    }, [id]);

    if (!hotel) {
        return <div>Loading...</div>;
    }

    return (
        <div>
            <h2>{hotel.name}</h2>
            <img src={hotel.photoList[0].link} alt={hotel.name} />
            <p style={{ whiteSpace: 'pre-line' }}>{hotel.description}</p>
            <Link to="/home">Back to list</Link>
        </div>
    );
}

export default HotelDetails;
