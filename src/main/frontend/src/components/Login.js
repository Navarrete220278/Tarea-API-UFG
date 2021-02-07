import React, { useState } from 'react';
import { Formik, Field, Form, ErrorMessage } from 'formik';
import * as Yup from 'yup';
import { Link, useHistory, useLocation } from 'react-router-dom';
import { useAuth } from '../providers/AuthProvider';

export default function Login() {
  const history = useHistory();
  const location = useLocation();
  const auth = useAuth();

  const [errorLogin, setErrorLogin] = useState('');

  const { from } = location.state || { from: { pathname: '/' } };

  return (
    <>
      <h1>Iniciar sesión</h1>
      {from.pathname !== '/' ? (
        <p>Debe iniciar sesión para ver la página solicitada</p>
      ) : null}
      <Formik
        initialValues={{ email: '', password: '' }}
        validationSchema={Yup.object({
          email: Yup.string()
            .email('Correo electrónico no válido')
            .required('Requerido'),
          password: Yup.string()
            .min(6, 'Debe tener al menos 6 caracteres')
            .required('Requerido'),
        })}
        onSubmit={async (values, { setSubmitting }) => {
          try {
            await auth.iniciarSesion(values);
            setSubmitting(false);
            history.replace(from);
          } catch (error) {
            setErrorLogin('Credenciales no válidas');
            setSubmitting(false);
          }
        }}
      >
        <Form>
          <p>
            <label htmlFor="email" className="margin-right-1">
              Correo electrónico
            </label>
            <Field name="email" type="email" className="margin-right-1" />
            <ErrorMessage name="email" />
          </p>

          <p>
            <label htmlFor="password" className="margin-right-1">
              Contraseña
            </label>
            <Field name="password" type="password" className="margin-right-1" />
            <ErrorMessage name="password" />
          </p>

          {errorLogin ? <p>{errorLogin}</p> : null}
          <button type="submit">Iniciar sesión</button>
        </Form>
      </Formik>
      <p>
        ¿Aún no tiene una cuenta? <Link to="/registro">Registrarse</Link>
      </p>
    </>
  );
}
