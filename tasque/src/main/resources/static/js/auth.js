function getToken() {
  return localStorage.getItem("token");
}

function saveToken(token) {
  localStorage.setItem("token", token);
}

function logoutUser() {
  localStorage.removeItem("token");
  window.location.href = "auth.html";
}

function isAuthenticated() {
  return !!getToken();
}