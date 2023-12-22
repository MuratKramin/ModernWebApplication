import React, { useState, useEffect } from "react";
import { Modal, Button, Form } from 'react-bootstrap';
import UserService from "../services/user.service";
import EventBus from "../common/EventBus";

const BoardAdmin = () => {
  const [content, setContent] = useState("");
  const [showModal, setShowModal] = useState(false);
  const [selectedUser, setSelectedUser] = useState({});
  const [formData, setFormData] = useState({ email: "", first_name: "" });

  useEffect(() => {
    UserService.getAdminBoard().then(
      (response) => {
        setContent(response.data);
      },
      (error) => {
        const _content =
          (error.response &&
            error.response.data &&
            error.response.data.message) ||
          error.message ||
          error.toString();

        setContent(_content);

        if (error.response && error.response.status === 403) {
          EventBus.dispatch("logout");
        }
      }
    );
  }, []);

  const handleEditClick = (user) => {
    setSelectedUser(user);
    setShowModal(true);
    setFormData({
      id: user.id,
      username: user.username || "",
      password: user.password || "",
      email: user.email || "",
      first_name: user.first_name || "",
      second_name: user.second_name || "",
      otchestvo: user.otchestvo || "",
      phone_num: user.phone_num || ""
    });
  };

  const handleModalClose = () => {
    setShowModal(false);
    setFormData({ email: "", first_name: "" });
  };

  const handleFormSubmit = () => {
    const { id } = selectedUser;
    const updatedUserData = {
      id,
      username: formData.username,
      password: formData.password,
      email: formData.email,
      first_name: formData.first_name,
      second_name: formData.second_name,
      otchestvo: formData.otchestvo,
      phone_num: formData.phone_num
    };

    // Отправка PUT-запроса с использованием updateUser
    UserService.updateUser(id, updatedUserData)
      .then(() => {
        // Обновление данных пользователя в таблице
        setContent((prevContent) => {
          const updatedContent = prevContent.map((user) =>
            user.id === id ? { ...user, ...updatedUserData } : user
          );
          return updatedContent;
        });
        console.log("User updated successfully");
      })
      .catch((error) => {
        // Обработка ошибок при отправке запроса
        console.error("Error updating user:", error);
      })
      .finally(() => {
        // Закрываем модальное окно в любом случае
        setShowModal(false);
        setFormData({
          id: "",
          username: "",
          password: "",
          email: "",
          first_name: "",
          second_name: "",
          otchestvo: "",
          phone_num: ""
        });
      });
  };

  if (content=="") {
    return <div>Loading...</div>;
  }
  else

  return (
    <div className="container">
      <header className="jumbotron">
        {content.length > 0 ? (
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>Username</th>
                <th>Email</th>
                <th>Password</th>
                <th>Roles</th>
                <th>Action</th>
              </tr>
            </thead>
            <tbody>
              {content.map((user) => (
                <tr key={user.id}>
                  <td>{user.id}</td>
                  <td>{user.username}</td>
                  <td>{user.email}</td>
                  <td>{user.password}</td>
                  <td>{user.roles.length > 0
                      ? user.roles.map((role) => role.name).join(", ")
                      : "No roles"}</td>
                  <td>
                    <Button variant="primary" onClick={() => handleEditClick(user)}>
                      Edit
                    </Button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        ) : (
          <p>No data available.</p>
        )}

        {/* Modal for editing user */}
      <Modal show={showModal} onHide={handleModalClose}>
        <Modal.Header closeButton>
          <Modal.Title>Edit User</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Form>
            <Form.Group controlId="formUsername">
              <Form.Label>Username</Form.Label>
              <Form.Control
                type="text"
                placeholder="Enter username"
                value={formData.username}
                onChange={(e) => setFormData({ ...formData, username: e.target.value })}
              />
            </Form.Group>
            <Form.Group controlId="formPassword">
              <Form.Label>Password</Form.Label>
              <Form.Control
                type="password"
                placeholder="Enter password"
                value={formData.password}
                onChange={(e) => setFormData({ ...formData, password: e.target.value })}
              />
            </Form.Group>
            <Form.Group controlId="formEmail">
              <Form.Label>Email</Form.Label>
              <Form.Control
                type="text"
                placeholder="Enter email"
                value={formData.email}
                onChange={(e) => setFormData({ ...formData, email: e.target.value })}
              />
            </Form.Group>
            <Form.Group controlId="formFirstName">
              <Form.Label>First Name</Form.Label>
              <Form.Control
                type="text"
                placeholder="Enter first name"
                value={formData.first_name}
                onChange={(e) => setFormData({ ...formData, first_name: e.target.value })}
              />
            </Form.Group>
            <Form.Group controlId="formSecondName">
              <Form.Label>Second Name</Form.Label>
              <Form.Control
                type="text"
                placeholder="Enter second name"
                value={formData.second_name}
                onChange={(e) => setFormData({ ...formData, second_name: e.target.value })}
              />
            </Form.Group>
            <Form.Group controlId="formOtchestvo">
              <Form.Label>Otchestvo</Form.Label>
              <Form.Control
                type="text"
                placeholder="Enter otchestvo"
                value={formData.otchestvo}
                onChange={(e) => setFormData({ ...formData, otchestvo: e.target.value })}
              />
            </Form.Group>
            <Form.Group controlId="formPhoneNum">
              <Form.Label>Phone Number</Form.Label>
              <Form.Control
                type="text"
                placeholder="Enter phone number"
                value={formData.phone_num}
                onChange={(e) => setFormData({ ...formData, phone_num: e.target.value })}
              />
            </Form.Group>
          </Form>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={handleModalClose}>
            Close
          </Button>
          <Button variant="primary" onClick={handleFormSubmit}>
            Save Changes
          </Button>
        </Modal.Footer>
      </Modal>
      </header>
    </div>
  );
};

export default BoardAdmin;
