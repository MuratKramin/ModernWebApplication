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

const findHotels = ({
                        qty = "1",
                        days = "1",
                        children = "0",
                        activity = "3",
                        safety = "3",
                        comfort = "3",
                        price = "3",
                        distance = "3",
                        distance_from_Kazan = "0",
                        cost_of_stay_per_day = "0",
                        campaign = "0",
                        active_recreation_on_the_water = "0",
                        fishing = "0",
                        football = "0",
                        volleyball = "0",
                        table_tennis = "0",
                        tennis = "0",
                        cycling = "0"
                    } = {}) => {
    const params = {
        qty,
        days,
        children,
        activity,
        safety,
        comfort,
        price,
        distance,
        distance_from_Kazan,
        cost_of_stay_per_day,
        campaign,
        active_recreation_on_the_water,
        fishing,
        football,
        volleyball,
        table_tennis,
        tennis,
        cycling
    };
    console.log(params);
    return api.get("/hotels/findHotels", { params });
};

const HotelService = {
    getHotel,
    getHotelById,
    getStats,
    findHotels
};

export default HotelService;
