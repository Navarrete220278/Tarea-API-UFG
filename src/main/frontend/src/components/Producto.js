import React from 'react';
import food from '../food.png';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faCartPlus } from '@fortawesome/free-solid-svg-icons';
import { useState } from 'react';
import { useCart } from '../providers/CartProvider';

export default function Producto({ producto }) {
  const cart = useCart();
  const [cantidad, setCantidad] = useState(1);

  const onChangeHandler = (event) => {
    setCantidad(parseInt(event.target.value));
  };

  const onClickHandler = (event) => {
    event.preventDefault(); // previene comportamiento por defecto
    cart.agregarAlCarrito(producto, cantidad);
  };

  return (
    <li className="flex-flex-start margin-bottom-1">
      <img
        className="img-thumbnail rounded-borders margin-right-1"
        src={producto ? `/api/v1/productos/${producto.id}/imagen` : food}
        onError={(e) => (e.target.src = food)}
        alt={producto.nombre}
      />
      <div>
        <p>{producto.nombre}</p>
        <h2>$ {producto.precio.toFixed(2)}</h2>
        <form>
          <p>
            <input
              type="number"
              value={cantidad}
              onChange={onChangeHandler}
              min={0}
            />
          </p>
          <p>
            <button
              type="submit"
              disabled={cantidad < 1}
              onClick={onClickHandler}
            >
              <FontAwesomeIcon icon={faCartPlus} /> Agregar al carrito
            </button>
          </p>
        </form>
      </div>
    </li>
  );
}
