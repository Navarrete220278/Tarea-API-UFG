import React from 'react';
import { useCart } from '../providers/CartProvider';

export default function LineaCarrito({ producto, cantidad }) {
  const cart = useCart();

  const handleOnChange = (event) => {
    if (event.target.value)
      cart.modificarCarrito(producto.id, parseInt(event.target.value));
  };

  return (
    <tr>
      <td>{producto.nombre}</td>
      <td>
        <form>
          <input
            className="width-5"
            type="number"
            min={0}
            value={cantidad}
            onChange={handleOnChange}
          />
        </form>
      </td>
      <td>$ {producto.precio.toFixed(2)}</td>
      <td>$ {(producto.precio * cantidad).toFixed(2)}</td>
    </tr>
  );
}
