import React from "react";
import { Navigate, Link } from "react-router-dom";

function AuthRoute({ authenticated, component: Component }) {
  console.log("authRoute");
  return authenticated ? (
    Component
  ) : (
    <Navigate to="/" {...alert("로그인 후 이용하세요")} />
  );
}

export default AuthRoute;
