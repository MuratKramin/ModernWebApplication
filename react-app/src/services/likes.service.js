import axios from 'axios';

const API_URL = 'http://localhost:8080/api/likes'; // URL вашего API, измените порт и базовый URL по необходимости

class LikesService {
    // Получение всех записей
    getAllLikes() {
        return axios.get(API_URL);
    }

    // Получение одной записи по ID
    getLikeById(id) {
        return axios.get(`${API_URL}/${id}`);
    }

    // Создание новой записи
    createLike(like) {
        return axios.post(API_URL, like);
    }

    createLikeById(userId, hotelId) {
        const params = new URLSearchParams();
        params.append('user_id', userId);
        params.append('hotel_id', hotelId);
        return axios.post(`${API_URL}/byId`, null, { params });
    }

    // Обновление записи
    updateLike(id, like) {
        return axios.put(`${API_URL}/${id}`, like);
    }

    // Удаление записи по ID
    deleteLike(id) {
        return axios.delete(`${API_URL}/${id}`);
    }

    // Удаление всех записей
    deleteAllLikes() {
        return axios.delete(API_URL);
    }
}

export default new LikesService();
