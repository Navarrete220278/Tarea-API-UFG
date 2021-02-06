import React from 'react';
import { Link } from 'react-router-dom';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import {
  faHome,
  faFileInvoice,
  faSignInAlt,
  faSignOutAlt,
  faShoppingCart,
} from '@fortawesome/free-solid-svg-icons';

import { useAuth } from '../providers/AuthProvider';
import { useCart } from '../providers/CartProvider';

export default function Navegacion() {
  const auth = useAuth();
  const cart = useCart();
  return (
    <header className="margin-bottom-3">
      <Link to="/">
        <h1>Ordena en línea</h1>
      </Link>
      <nav>
        <ul>
          <li>
            <Link to="/">
              <FontAwesomeIcon icon={faHome} /> Página principal
            </Link>
          </li>
          {auth.estaAutenticado() ? (
            <li>
              <Link to="/mis-ordenes">
                <FontAwesomeIcon icon={faFileInvoice} /> Mis órdenes
              </Link>
            </li>
          ) : null}
          {auth.estaAutenticado() ? (
            <li>
              <Link to="/cerrar-sesion">
                <FontAwesomeIcon icon={faSignOutAlt} /> Cerrar sesión
              </Link>
            </li>
          ) : (
            <li>
              <Link to="/iniciar-sesion">
                <FontAwesomeIcon icon={faSignInAlt} /> Iniciar sesión
              </Link>
            </li>
          )}
          <li>
            <Link to="/carrito">
              <FontAwesomeIcon icon={faShoppingCart} /> Carrito (${' '}
              {cart.obtenerTotal().toFixed(2)})
            </Link>
          </li>
        </ul>
      </nav>
    </header>
  );
}
