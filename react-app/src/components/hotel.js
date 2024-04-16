import React, { useEffect, useState } from 'react';
import axios from 'axios';
import HotelService from '../services/hotels.service';

import { Link } from 'react-router-dom';

function HotelDetails() {
  const [hotel, setHotel] = useState(null);

  useEffect(() => {
    HotelService.getHotel().then(
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
  });

  if (!hotel) {
    return <div>Loading...</div>;
  }
  else{
    return (
        <div>
          {hotel.map(hotel => (
              <div key={hotel.id}>
                <h2>{hotel.name}</h2>
                <img src={hotel.photoList[0].link} alt={hotel.name} />
                <p style={{ whiteSpace: 'pre-line' }}>{hotel.description}</p>
                <Link to={`/hotel/${hotel.id}`}>View Details</Link>
              </div>
          ))}
        </div>
    );
  }

}

export default HotelDetails;
