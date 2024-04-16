import api from "./api";

const getHotel = () => {
    return api.get("/hotels");
};

const getHotelById = (id) => {
    return api.get(`/hotels/${id}`);
};

const getStats = () => {
    return api.get("/hotels/stats");
};

const HotelService = {
    getHotel,
    getHotelById,
    getStats
};

export default HotelService;
