import React, { useState } from 'react';
import { Redirect } from 'react-router-dom';
import { useAuth } from '../providers/AuthProvider';

export default function Logout() {
  const [control, setControl] = useState(true);
  const auth = useAuth();
  if (auth.estaAutenticado() && control) {
    auth.cerrarSesion().then(console.log('Logged out'));
    setControl(false);
  }

  return <Redirect to="/" />;
}
