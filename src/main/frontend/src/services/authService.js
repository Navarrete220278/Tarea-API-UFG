import axios from 'axios';

axios.defaults.baseURL = '/api/v1/auth';

async function iniciarSesion({ email, password }) {
  const result = await axios.post('/login', { email, password });
  return result.data;
}

async function refrescarToken() {
  const result = await axios.post('/refresh');
  return result.data;
}

async function cerrarSesion(authHeader) {
  await axios.post('/logout', null, {
    headers: authHeader,
  });
}

const authService = {
  iniciarSesion,
  refrescarToken,
  cerrarSesion,
};

export default authService;
