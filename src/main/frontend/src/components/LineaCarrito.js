import React from 'react';

export default function LineaCarrito({ producto, cantidad }) {
  return (
    <li>
      Producto: {producto.nombre}, Cantidad: {cantidad}, Precio:{' '}
      {producto.precio}, Total: $ {(producto.precio * cantidad).toFixed(2)}
    </li>
  );
}
