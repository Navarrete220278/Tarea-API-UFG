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
import CrearOrden from './components/CrearOrden';
import Ordenes from './components/Ordenes';
import Registro from './components/Registro';
import DetalleOrden from './components/DetalleOrden';

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
              <Ordenes idUsuario />
            </RutaCliente>
            <RutaCliente path="/crear-orden">
              <CrearOrden />
            </RutaCliente>
            <RutaCliente path="/cerrar-sesion">
              <Logout />
            </RutaCliente>
            <Route path="/registro">
              <Registro />
            </Route>
            <RutaCliente path="/ordenes/:idOrden">
              <DetalleOrden />
            </RutaCliente>
          </Switch>
        </Router>
      </CartProvider>
    </AuthProvider>
  );
}
