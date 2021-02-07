import React, { useEffect } from 'react';
import { Redirect } from 'react-router-dom';
import { useAuth } from '../providers/AuthProvider';

export default function Logout() {
  const auth = useAuth();
  useEffect(() => {
    auth.cerrarSesion();
  }, []); // eslint-disable-line react-hooks/exhaustive-deps

  if (auth.estaAutenticado())
    return <p>Espere un momento mientras cerramos su sesión...</p>;
  else return <Redirect to="/" />;
}
