import React from 'react';
import { useCart } from '../providers/CartProvider';
import LineaCarrito from './LineaCarrito';

export default function Carrito() {
  const cart = useCart();

  return (
    <div>
      <h1>Carrito de Compras</h1>
      <ul>
        {cart.carrito.map((linea) => (
          <LineaCarrito key={linea.producto.id} {...linea} />
        ))}
      </ul>
    </div>
  );
}
