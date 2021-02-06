import React from 'react';
import './App.css';
import { BrowserRouter as Router, Switch, Route } from 'react-router-dom';

import { AuthProvider } from './providers/AuthProvider';
import { CartProvider } from './providers/CartProvider';
import Navegacion from './components/Navegacion';
import RutaCliente from './components/RutaCliente';
import Categorias from './components/Categorias';
import Productos from './components/Productos';
import Carrito from './components/Carrito';

export default function App() {
  return (
    <AuthProvider>
      <CartProvider>
        <Router>
          <Navegacion />
          <Switch>
            <Route exact path="/">
              <Categorias />
            </Route>
            <Route path="/categorias/:idCategoria">
              <Productos />
            </Route>
            <Route path="/iniciar-sesion">
              <h1>Iniciar sesión</h1>
            </Route>
            <Route path="/carrito">
              <Carrito />
            </Route>
            <RutaCliente path="/mis-ordenes">
              <h1>Mis órdenes</h1>
            </RutaCliente>
          </Switch>
        </Router>
      </CartProvider>
    </AuthProvider>
  );
}
