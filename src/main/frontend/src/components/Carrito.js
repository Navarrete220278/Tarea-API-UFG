import React from 'react';
import { Link, useHistory } from 'react-router-dom';
import { useCart } from '../providers/CartProvider';
import LineaCarrito from './LineaCarrito';

export default function Carrito() {
  const cart = useCart();
  const history = useHistory();

  return (
    <>
      <h1>Carrito de Compras</h1>
      {cart.obtenerTotal() ? (
        <>
          <table>
            <thead>
              <tr>
                <th>Producto</th>
                <th>Cantidad</th>
                <th>Precio</th>
                <th>Total</th>
              </tr>
            </thead>
            <tbody>
              {cart.carrito.map((linea) => (
                <LineaCarrito key={linea.producto.id} {...linea} />
              ))}
            </tbody>
          </table>
          <p className="margin-bottom-3">
            El total de su orden es{' '}
            <strong>$ {cart.obtenerTotal().toFixed(2)}</strong>
          </p>
          <button
            onClick={(event) => {
              event.preventDefault();
              history.push('/');
            }}
          >
            Seguir comprando
          </button>
          <button
            onClick={(event) => {
              event.preventDefault();
              history.push('/crear-orden');
            }}
          >
            Enviar mi orden
          </button>
        </>
      ) : (
        <p>
          Su carrito de compras está vacío.{' '}
          <Link to="/">Seguir comprando.</Link>
        </p>
      )}
    </>
  );
}
