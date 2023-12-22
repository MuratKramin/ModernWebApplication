import api from "./api";

const getHotel = () => {
    return api.get("/hotels");
};

const getStats = () => {
  return api.get("/hotels/stats");
};


const HotelService = {
    getHotel,
    getStats
  };
  
export default HotelService;