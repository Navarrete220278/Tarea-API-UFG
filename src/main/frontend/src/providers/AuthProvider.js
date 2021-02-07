import React, { useState, createContext, useContext } from 'react';
import authService from '../services/authService';

const AuthContext = createContext();

const useAuth = () => useContext(AuthContext);

const useProvideAuth = () => {
  const [usuario, setUsuario] = useState(
    () => JSON.parse(localStorage.getItem('usuario')) || null
  );

  const iniciarSesion = async ({ email, password }) => {
    try {
      const data = await authService.iniciarSesion({ email, password });
      setUsuario(data.usuario);
      localStorage.setItem('usuario', JSON.stringify(data.usuario));
      localStorage.setItem('token', data.token);
    } catch (error) {
      console.log('Error al iniciar sesión ' + error.message);
    }
  };

  const estaAutenticado = () => !!usuario;

  const cerrarSesion = async () => {
    try {
      await authService.cerrarSesion();
      setUsuario(null);
      localStorage.removeItem('usuario');
      localStorage.removeItem('token');
    } catch (error) {
      console.log('Error al cerrar sesión ' + error.message);
    }
  };

  return {
    usuario,
    iniciarSesion,
    cerrarSesion,
    estaAutenticado,
  };
};

function AuthProvider({ children }) {
  const auth = useProvideAuth();
  return <AuthContext.Provider value={auth}>{children}</AuthContext.Provider>;
}

export { useAuth, AuthProvider };
