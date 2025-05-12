function getToken() {
  return localStorage.getItem("token");
}

function saveToken(token) {
  localStorage.setItem("token", token);
}

function logoutUser() {
  localStorage.removeItem("token");
  window.location.href = "login.html";
}

function isAuthenticated() {
  return !!getToken();
}