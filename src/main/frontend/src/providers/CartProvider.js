import React, { useState, createContext, useContext } from 'react';

const CartConext = createContext();

function loadCartFromLocalStorage() {
  const cart = localStorage.getItem('carrito');
  if (cart) {
    try {
      const parsedCart = JSON.parse(cart);
      return parsedCart;
    } catch (error) {
      console.log('Error when loading shopping cart from disk ' + error);
    }
  }

  return [];
}

function saveCartToLocalStorage(cart) {
  if (cart !== null && typeof cart === 'object') {
    try {
      const stringifiedCart = JSON.stringify(cart);
      localStorage.setItem('carrito', stringifiedCart);
    } catch (error) {
      console.log('Error when saving shopping cart to disk ' + error);
    }
  }
}

function CartProvider({ children }) {
  const [carrito, setCarrito] = useState(loadCartFromLocalStorage());

  const agregarAlCarrito = (producto, cantidad) => {
    let nuevoCarrito;

    // ¿Existe el producto en el carrito?
    const indice = carrito.findIndex(
      (linea) => linea.producto.id === producto.id
    );

    if (indice !== -1) {
      nuevoCarrito = carrito.map((linea) =>
        linea.producto.id === producto.id
          ? { producto, cantidad: linea.cantidad + cantidad }
          : linea
      );
    } else {
      nuevoCarrito = [...carrito, { producto, cantidad }];
    }

    setCarrito(nuevoCarrito);
    saveCartToLocalStorage(nuevoCarrito);
  };

  const quitarDelCarrito = (idProducto) => {
    // ¿Existe el producto en el carrito?
    const indice = carrito.findIndex(
      (linea) => linea.producto.id === idProducto
    );

    // Si el producto no existe, ignorar la petición
    if (indice === -1) return;

    const nuevoCarrito = carrito.filter(
      (linea) => linea.producto.id !== idProducto
    );

    setCarrito(nuevoCarrito);
    saveCartToLocalStorage(nuevoCarrito);
  };

  const modificarCarrito = (idProducto, cantidad) => {
    // ¿Existe el producto en el carrito?
    const indice = carrito.findIndex(
      (linea) => linea.producto.id === idProducto
    );

    // Si el producto no existe, ignorar la petición
    if (indice === -1) return;

    // Si la nueva cantidad es cero, quitar el producto del carrito
    if (cantidad === 0) return quitarDelCarrito(idProducto);

    // Modificar el carrito
    const nuevoCarrito = carrito.map((linea) =>
      linea.producto.id === idProducto
        ? { producto: linea.producto, cantidad }
        : linea
    );

    setCarrito(nuevoCarrito);
    saveCartToLocalStorage(nuevoCarrito);
  };

  const obtenerTotal = () =>
    carrito.reduce((acc, val) => acc + val.producto.precio * val.cantidad, 0);

  const shoppingCart = {
    carrito,
    agregarAlCarrito,
    quitarDelCarrito,
    modificarCarrito,
    obtenerTotal,
  };

  return (
    <CartConext.Provider value={shoppingCart}>{children}</CartConext.Provider>
  );
}

function useCart() {
  return useContext(CartConext);
}

export { CartProvider, useCart };
