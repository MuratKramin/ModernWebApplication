import React, { useState, useEffect } from "react";
//import { Switch, Route, Link } from "react-router-dom";
import "bootstrap/dist/css/bootstrap.min.css";
import "./App.css";

import AuthService from "./services/auth.service";

import Login from "./components/Login";
import Register from "./components/Register";
import Home from "./components/Home";
import Main from "./components/Main"
import Profile from "./components/Profile";
import BoardUser from "./components/BoardUser";
import BoardModerator from "./components/BoardModerator";
import BoardAdmin from "./components/BoardAdmin";
import Stats from "./components/Stats";

import EventBus from "./common/EventBus";

//import React, { useState, useEffect } from "react";
import { Switch, Route, Link, BrowserRouter as Router } from "react-router-dom";
import HotelDetails from './components/HotelDetails'; // Убедитесь, что импортировали этот компонент


const App = () => {
  const [showModeratorBoard, setShowModeratorBoard] = useState(false);
  const [showAdminBoard, setShowAdminBoard] = useState(false);
  const [currentUser, setCurrentUser] = useState(undefined);

  useEffect(() => {
    const user = AuthService.getCurrentUser();

    if (user) {
      setCurrentUser(user);
      setShowModeratorBoard(user.roles.includes("ROLE_MODERATOR"));
      setShowAdminBoard(user.roles.includes("ROLE_ADMIN"));
    }

    EventBus.on("logout", () => {
      logOut();
    });

    return () => {
      EventBus.remove("logout");
    };
  }, []);

  const logOut = () => {
    AuthService.logout();
    setShowModeratorBoard(false);
    setShowAdminBoard(false);
    setCurrentUser(undefined);
  };

  return (
      <div style={{
        backgroundImage: "url('https://sun9-61.userapi.com/impg/b2iC32PN4B7tSwckVwH74Rd2pCqihNfzOugQaw/pfC2ONrXUpU.jpg?size=1442x2160&quality=96&sign=30d851392093eddc42ccf1e5b753662d&type=album')",
        backgroundSize: 'cover',
        backgroundAttachment: 'fixed',
        minHeight: '100vh', // Убедитесь, что минимальная высота контейнера достаточна для заполнения экрана
        width: '100%'
      }}>
        <nav className="navbar navbar-expand navbar-dark bg-dark">
          <Link to={"/home"} className="navbar-brand">
            Nature_Navigator
          </Link>
          <div className="navbar-nav mr-auto">

            <li className="nav-item">
              <Link to={"/main"} className="nav-link">
                Main
              </Link>
            </li>

            <li className="nav-item">
              <Link to={"/home"} className="nav-link">
                Choose
              </Link>
            </li>



            {showModeratorBoard && (
                <li className="nav-item">
                  <Link to={"/mod"} className="nav-link">
                    Moderator Board
                  </Link>
                </li>
            )}

            {showAdminBoard && (
                <li className="nav-item">
                  <Link to={"/admin"} className="nav-link">
                    Admin Board
                  </Link>
                </li>
            )}

            {currentUser && (
                <li className="nav-item">
                  <Link to={"/user"} className="nav-link">
                    User
                  </Link>
                </li>
            )}
            {currentUser && (
                <li className="nav-item">
                  <Link to={"/stats"} className="nav-link">
                    Stats
                  </Link>
                </li>
            )}
          </div>

          {currentUser ? (
              <div className="navbar-nav ml-auto">
                <li className="nav-item">
                  <Link to={"/profile"} className="nav-link">
                    {currentUser.username}
                  </Link>
                </li>
                <li className="nav-item">
                  <a href="/login" className="nav-link" onClick={logOut}>
                    LogOut
                  </a>
                </li>
              </div>
          ) : (
              <div className="navbar-nav ml-auto">
                <li className="nav-item">
                  <Link to={"/login"} className="nav-link">
                    Login
                  </Link>
                </li>

                <li className="nav-item">
                  <Link to={"/register"} className="nav-link">
                    Sign Up
                  </Link>
                </li>
              </div>
          )}
        </nav>

        <div className="container mt-3">
          <Switch>
            <Route exact path={[ "/main"]} component={Main} />
            <Route exact path={["/", "/home"]} component={Home} />
            <Route exact path="/login" component={Login} />
            <Route exact path="/register" component={Register} />
            <Route exact path="/profile" component={Profile} />
            <Route path="/user" component={BoardUser} />
            <Route path="/mod" component={BoardModerator} />
            <Route path="/admin" component={BoardAdmin} />
            <Route path="/stats" component={Stats} />


            <Route path="/hotel/:id" component={HotelDetails} />
          </Switch>
        </div>
      </div>

  );
};

export default App;
