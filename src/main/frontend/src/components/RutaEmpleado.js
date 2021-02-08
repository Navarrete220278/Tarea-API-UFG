import React from 'react';
import { useAuth } from '../providers/AuthProvider';
import { Route, Redirect } from 'react-router-dom';

export default function RutaEmpleado({ children, ...rest }) {
  let auth = useAuth();
  return (
    <Route
      {...rest}
      render={({ location }) =>
        auth.estaAutenticado() && auth.usuario.esEmpleado ? (
          children
        ) : (
          <Redirect
            to={{
              pathname: '/iniciar-sesion',
              state: { from: location },
            }}
          />
        )
      }
    />
  );
}
