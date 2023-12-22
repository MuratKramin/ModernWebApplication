import api from "./api";

const getPublicContent = () => {
  return api.get("/test/all");
};

const getUserBoard = () => {
  return api.get("/test/user");
};

const getModeratorBoard = () => {
  return api.get("/users");
};

const getAdminBoard = () => {
  return api.get("/users");
};
const updateUser = (id, data) => {
  return api.put(`/users/${id}`, data);
}

const UserService = {
  getPublicContent,
  getUserBoard,
  getModeratorBoard,
  getAdminBoard,
  updateUser
};

export default UserService;
