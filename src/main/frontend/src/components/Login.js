import React, { useState } from 'react';
import { useHistory, useLocation } from 'react-router-dom';
import { useAuth } from '../providers/AuthProvider';

export default function Login() {
  const history = useHistory();
  const location = useLocation();
  const auth = useAuth();

  const credencialesIniciales = {
    email: '',
    password: '',
  };
  const [credenciales, setCredenciales] = useState(credencialesIniciales);

  const [errorLogin, setErrorLogin] = useState('');

  const handleOnInputChange = (event) => {
    const name = event.target.name;
    const value = event.target.value;
    setCredenciales({ ...credenciales, [name]: value });
  };

  const { from } = location.state || { from: { pathname: '/' } };
  const iniciarSesion = async () => {
    try {
      await auth.iniciarSesion(credenciales);
      history.replace(from);
    } catch (error) {
      setErrorLogin('Credenciales no válidas');
      setCredenciales(credencialesIniciales);
    }
  };

  return (
    <div>
      <h1>Iniciar sesión</h1>
      {from.pathname !== '/' ? (
        <p>Debe iniciar sesión para ver la página solicitada</p>
      ) : null}
      <form>
        <p>
          <label className="margin-right-1">Usuario:</label>
          <input type="email" name="email" onChange={handleOnInputChange} />
        </p>
        <p>
          <label className="margin-right-1">Contraseña:</label>
          <input
            type="password"
            name="password"
            onChange={handleOnInputChange}
          />
        </p>
        {errorLogin ? <p>{errorLogin}</p> : null}
      </form>
      <button onClick={iniciarSesion}>Log in</button>
    </div>
  );
}
