import axios from 'axios';

const API_URL = 'http://localhost:8080/api/residenceHistories'; // Измените URL на ваш базовый URL, если он отличается

class ResidenceHistoryService {
    getAllResidenceHistories() {
        return axios.get(API_URL);
    }

    getResidenceHistoryById(id) {
        return axios.get(`${API_URL}/${id}`);
    }

    createResidenceHistory(data) {
        return axios.post(API_URL, data);
    }

    updateResidenceHistory(id, data) {
        return axios.put(`${API_URL}/${id}`, data);
    }

    deleteResidenceHistory(id) {
        return axios.delete(`${API_URL}/${id}`);
    }

    insertResidenceHistory(formData) {
        const params = new URLSearchParams();
        params.append('checkInDate', formData.checkInDate);
        params.append('checkOutDate', formData.checkOutDate);
        params.append('totalCost', formData.totalCost);
        params.append('review', formData.review);
        params.append('grade', formData.grade);
        params.append('userId', formData.userId);
        params.append('hotelId', formData.hotelId);
        return axios.post(`${API_URL}/insert`, params);
    }

    getRatings() {
        return axios.get(`${API_URL}/ratings`);
    }

    getMaxIdHotel() {
        return axios.get(`${API_URL}/maxIdHotel`);
    }

    getResidenceHistoriesByHotelId(hotelId) {
        return axios.get(`${API_URL}/byHotel/${hotelId}`);
    }
}

export default new ResidenceHistoryService();
