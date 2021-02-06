import axios from 'axios';

async function iniciarSesion({ email, password }) {
  const result = await axios.post('/api/v1/auth/login', { email, password });
  return result.data;
}

async function refrescarToken() {
  const result = await axios.post('/api/v1/auth/refresh');
  return result.data;
}

async function cerrarSesion(authHeader) {
  await axios.post('/api/v1/auth/logout', null, {
    headers: authHeader,
  });
}

const authService = {
  iniciarSesion,
  refrescarToken,
  cerrarSesion,
};

export default authService;
