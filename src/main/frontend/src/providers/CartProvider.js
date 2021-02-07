import React, { useState, createContext, useContext } from 'react';

const CartContext = createContext();

const useCart = () => useContext(CartContext);

const useProvideCart = () => {
  const [carrito, setCarrito] = useState(
    () => JSON.parse(localStorage.getItem('carrito')) || []
  );

  const agregarAlCarrito = (producto, cantidad) => {
    const indice = carrito.findIndex(
      (linea) => !!linea.producto && linea.producto.id === producto.id
    );
    let nuevoCarrito;
    if (indice === -1) nuevoCarrito = [...carrito, { producto, cantidad }];
    else
      nuevoCarrito = carrito.map((linea) =>
        !!linea.producto && linea.producto.id === producto.id
          ? { producto, cantidad: linea.cantidad + cantidad }
          : linea
      );
    setCarrito(nuevoCarrito);
    localStorage.setItem('carrito', JSON.stringify(nuevoCarrito));
  };

  const quitarDelCarrito = (idProducto) => {
    const nuevoCarrito = carrito.filter(
      (linea) => !!linea.producto && linea.producto.id !== idProducto
    );
    setCarrito(nuevoCarrito);
    localStorage.setItem('carrito', JSON.stringify(nuevoCarrito));
  };

  const modificarCarrito = (idProducto, cantidad) => {
    if (cantidad === 0) return quitarDelCarrito(idProducto);

    const nuevoCarrito = carrito.map((linea) =>
      !!linea.producto && linea.producto.id === idProducto
        ? { producto: linea.producto, cantidad }
        : linea
    );

    setCarrito(nuevoCarrito);
    localStorage.setItem('carrito', JSON.stringify(nuevoCarrito));
  };

  const obtenerTotal = () => {
    return carrito.reduce(
      (acc, val) => acc + val.producto.precio * val.cantidad,
      0
    );
  };

  const limpiarCarrito = () => {
    setCarrito([]);
    localStorage.removeItem('carrito');
  };

  return {
    carrito,
    agregarAlCarrito,
    modificarCarrito,
    quitarDelCarrito,
    limpiarCarrito,
    obtenerTotal,
  };
};

function CartProvider({ children }) {
  const cart = useProvideCart();
  return <CartContext.Provider value={cart}>{children}</CartContext.Provider>;
}

export { useCart, CartProvider };
