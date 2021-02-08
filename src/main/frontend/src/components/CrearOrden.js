import React, { useState } from 'react';
import { useCart } from '../providers/CartProvider';
import 'react-datepicker/dist/react-datepicker.css';
import DatePicker from 'react-datepicker';
import { Link, Redirect } from 'react-router-dom';
import { useAuth } from '../providers/AuthProvider';
import ordenService from '../services/ordenService';

export default function CrearOrden() {
  const cart = useCart();
  const auth = useAuth();
  const [opcionElegida, setOpcionElegida] = useState('default');
  const [fechaSolicitada, setFechaSolicitada] = useState(null);
  const [submitted, setSubmitted] = useState(false);

  const handleOnChangeOpcion = (event) => {
    setOpcionElegida(event.target.value);
  };

  const filterPassedTime = (time) => {
    const currentDate = new Date();
    currentDate.setMinutes(currentDate.getMinutes() + 30);
    const selectedDate = new Date(time);

    return currentDate.getTime() < selectedDate.getTime();
  };

  const handleCrearOrden = async (event) => {
    event.preventDefault();
    setSubmitted(true);
    const idUsuario = auth.usuario.id;
    const orden = {};
    orden.lineas = cart.carrito.map((linea) => ({
      idProducto: linea.producto.id,
      cantidad: linea.cantidad,
    }));
    if (opcionElegida === 'custom' && !!fechaSolicitada) {
      fechaSolicitada.setHours(fechaSolicitada.getHours() - 6); // Ajuste por zona horaria
      orden.fechaSolicitada = fechaSolicitada;
    }

    try {
      const data = await ordenService.crearOrden(idUsuario, orden);
      cart.limpiarCarrito();
      console.log(`Orden ${data.id} creada!!!`);
    } catch (error) {
      console.log('Error al crear la orden: ' + error);
    }
  };

  return cart.carrito.length > 0 ? (
    <>
      <h1>Crear orden</h1>
      <form>
        <p>¿Cuándo desea recibir su orden?</p>
        <p>
          <input
            type="radio"
            value="default"
            checked={opcionElegida === 'default'}
            onChange={handleOnChangeOpcion}
          />{' '}
          <label>Tan pronto como sea posible</label>
        </p>
        <p className="margin-bottom-3">
          <input
            type="radio"
            value="custom"
            checked={opcionElegida === 'custom'}
            onChange={handleOnChangeOpcion}
          />{' '}
          <label>
            En una fecha específica{' '}
            {opcionElegida === 'custom' ? (
              <DatePicker
                selected={fechaSolicitada}
                minDate={new Date()}
                onChange={(fecha) => setFechaSolicitada(fecha)}
                filterTime={filterPassedTime}
                placeholderText="Elija una fecha"
                showTimeSelect
                dateFormat="Pp"
              />
            ) : null}
          </label>
        </p>

        {cart.carrito.length > 0 &&
        (opcionElegida === 'custom'
          ? !!fechaSolicitada &&
            fechaSolicitada.getTime() > new Date().getTime()
          : !!opcionElegida) ? (
          <>
            <p className="margin-bottom-3">
              El total a pagar es de{' '}
              <strong>$ {cart.obtenerTotal().toFixed(2)}</strong>. Si está
              seguro(a) de los detalles de su orden, haga clic en el botón
              "Colocar mi orden" o haga clic en{' '}
              <Link to="/carrito">este enlace</Link> para modificarla.
            </p>

            <button disabled={submitted} onClick={handleCrearOrden}>
              Colocar mi orden
            </button>
          </>
        ) : null}
      </form>
    </>
  ) : (
    <Redirect to="/mis-ordenes" />
  );
}
