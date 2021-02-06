import React from 'react';
import { useCart } from '../providers/CartProvider';

export default function LineaCarrito({ producto, cantidad }) {
  const cart = useCart();

  const handleOnChange = (event) => {
    cart.modificarCarrito(producto.id, parseInt(event.target.value));
  };

  return (
    <li className="flex-flex-start flex-flex-wrap margin-bottom-1 flex-align-stretch">
      <div className="width-15">{producto.nombre}</div>
      <div className="width-5 text-align-right">
        <form>
          <input
            className="width-5"
            type="number"
            min={0}
            value={cantidad}
            onChange={handleOnChange}
          />
        </form>
      </div>
      <div className="width-5 text-align-right">
        $ {producto.precio.toFixed(2)}
      </div>
      <div className="width-5 text-align-right">
        $ {(producto.precio * cantidad).toFixed(2)}
      </div>
    </li>
  );
}
