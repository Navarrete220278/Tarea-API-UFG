import axios from 'axios';

const axiosInstance = axios.create();

// Add a request interceptor
axiosInstance.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (!!token) {
      config.headers['Authorization'] = 'Bearer ' + token;
    }
    config.baseURL = '/api/v1';
    // config.headers['Content-Type'] = 'application/json';
    return config;
  },
  (error) => {
    Promise.reject(error);
  }
);

// Add a response interceptor
axiosInstance.interceptors.response.use(
  (response) => {
    return response;
  },
  function (error) {
    const originalRequest = error.config;
    if (error.response.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;
      return axios.post('/api/v1/auth/refresh').then((res) => {
        if (res.status === 201) {
          // 1) put token to LocalStorage
          console.log('Se refresco el token exitosamente');
          localStorage.setItem('token', res.data.token);

          // 2) Change Authorization header
          axios.defaults.headers.common['Authorization'] =
            'Bearer ' + res.data.token;

          // 3) return originalRequest object with Axios.
          return axios(originalRequest);
        }
      });
    }
    // return Error object with Promise
    return Promise.reject(error);
  }
);

export default axiosInstance;
