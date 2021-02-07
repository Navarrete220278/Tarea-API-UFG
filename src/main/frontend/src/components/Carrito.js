import React from 'react';
import { Link } from 'react-router-dom';
import { useCart } from '../providers/CartProvider';
import LineaCarrito from './LineaCarrito';

export default function Carrito() {
  const cart = useCart();

  return (
    <div>
      <h1>Carrito de Compras</h1>
      {cart.obtenerTotal() ? (
        <>
          <ul className="padding-zero margin-bottom-3">
            <li className="flex-flex-start flex-flex-wrap margin-bottom-1 flex-align-stretch">
              <div className="width-15">
                <strong>Producto</strong>
              </div>
              <div className="width-5 text-align-right">
                <strong>Cantidad</strong>
              </div>
              <div className="width-5 text-align-right">
                <strong>Precio</strong>
              </div>
              <div className="width-5 text-align-right">
                <strong>Total</strong>
              </div>
            </li>
            {cart.carrito.map((linea) => (
              <LineaCarrito key={linea.producto.id} {...linea} />
            ))}
          </ul>
          <Link className="button-link margin-right-1" to="/">
            Seguir comprando
          </Link>
          <Link className="button-link" to="/crear-orden">
            Enviar mi orden
          </Link>
        </>
      ) : (
        <p>
          Su carrito de compras está vacío.{' '}
          <Link to="/">Seguir comprando.</Link>
        </p>
      )}
    </div>
  );
}
