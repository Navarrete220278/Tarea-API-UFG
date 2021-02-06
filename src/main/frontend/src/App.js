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
import Login from './components/Login';
import Logout from './components/Logout';

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
              <Login />
            </Route>
            <Route path="/carrito">
              <Carrito />
            </Route>
            <RutaCliente path="/mis-ordenes">
              <h1>Mis órdenes</h1>
            </RutaCliente>
            <RutaCliente path="/crear-orden">
              <h1>Crear orden</h1>
            </RutaCliente>
            <Route path="/cerrar-sesion">
              <Logout />
            </Route>
          </Switch>
        </Router>
      </CartProvider>
    </AuthProvider>
  );
}
