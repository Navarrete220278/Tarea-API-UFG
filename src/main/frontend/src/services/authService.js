import axiosInstance from './axiosInstance';

async function iniciarSesion({ email, password }) {
  const result = await axiosInstance.post('/auth/login', { email, password });
  return result.data;
}

async function cerrarSesion() {
  await axiosInstance.post('/auth/logout');
}

const authService = {
  iniciarSesion,
  cerrarSesion,
};

export default authService;
