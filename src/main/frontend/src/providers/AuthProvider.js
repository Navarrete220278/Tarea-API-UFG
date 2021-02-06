import React, { createContext, useContext, useState } from 'react';
import authService from '../services/authService';

const AuthContext = createContext();

function getFromLocalStorage(key) {
  const value = localStorage.getItem(key);
  if (!value) return null;

  if (value.startsWith('{') || value.startsWith('[')) {
    // it might be an object or an array
    try {
      const parsedValue = JSON.parse(value);
      return parsedValue;
    } catch (error) {
      console.log('Error when parsing JSON (local storage): ' + error);
    }
  }

  return value;
}

function saveToLocalStorage(key, value) {
  let isJson = false;
  let stringifiedValue = null;
  if (value !== null && typeof value === 'object') {
    try {
      stringifiedValue = JSON.stringify(value);
      isJson = true;
    } catch (error) {
      console.log('Error when stringifying object (local storage): ' + error);
    }
  }

  localStorage.setItem(key, isJson ? stringifiedValue : value);
}

function removeFromLocalStorage(key) {
  localStorage.removeItem(key);
}

function AuthProvider({ children }) {
  const [autenticado, setAutenticado] = useState(
    Boolean(getFromLocalStorage('autenticado')) || false
  );
  const [usuario, setUsuario] = useState(getFromLocalStorage('usuario'));
  const [token, setToken] = useState(getFromLocalStorage('token'));

  const getAuthorizationHeader = () => ({ Authorization: 'Bearer ' + token });

  const iniciarSesion = async ({ email, password }) => {
    try {
      const data = await authService.iniciarSesion({ email, password });
      setUsuario(data.usuario);
      setToken(data.token);
      setAutenticado(true);
      saveToLocalStorage('usuario', data.usuario);
      saveToLocalStorage('token', data.token);
      saveToLocalStorage('autenticado', true);
    } catch (error) {
      console.log(`Error al iniciar sesión: ${error}`);
    }
  };

  const refrescarToken = async () => {
    try {
      const data = await authService.refrescarToken();
      setUsuario(data.usuario);
      setToken(data.token);
      setAutenticado(true);
      saveToLocalStorage('usuario', data.usuario);
      saveToLocalStorage('token', data.token);
      saveToLocalStorage('autenticado', true);
    } catch (error) {
      console.log(`Error al refrescar token: ${error}`);
    }
  };

  const cerrarSesion = async () => {
    try {
      await authService.cerrarSesion(getAuthorizationHeader());
      setAutenticado(false);
      setUsuario(null);
      setToken(null);
      removeFromLocalStorage('usuario');
      removeFromLocalStorage('token');
      removeFromLocalStorage('autenticado');
    } catch (error) {
      console.log('Error when logging out ' + error);
    }
  };

  const esEmpleado = () => {
    if (usuario && usuario.esEmpleado) return true;
    else return false;
  };

  const actualizarUsuario = (usuario) => {
    if (estaAutenticado()) {
      setUsuario(usuario);
      saveToLocalStorage('usuario', usuario);
    }
  };

  const ejecutarConAuthHeader = async (cb) => {
    try {
      return await cb(getAuthorizationHeader());
    } catch (error) {
      if (error.response) {
        if (error.response.status === 401) {
          await refrescarToken();
          return await cb(getAuthorizationHeader());
        }
      }
    }
  };

  const estaAutenticado = () => {
    return autenticado;
  };

  const authHelper = {
    usuario,
    token,
    iniciarSesion,
    cerrarSesion,
    refrescarToken,
    ejecutarConAuthHeader,
    actualizarUsuario, // para usar cuando el usuario modifica su perfil
    estaAutenticado,
    esEmpleado,
  };

  return (
    <AuthContext.Provider value={authHelper}>{children}</AuthContext.Provider>
  );
}

function useAuth() {
  return useContext(AuthContext);
}

export { AuthProvider, useAuth };
