import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { useAuth } from '../providers/AuthProvider';
import ordenService from '../services/ordenService';
import DetalleOrdenEnc from './DetalleOrdenEnc';
import DetalleOrdenHist from './DetalleOrdenHist';
import DetalleOrdenDet from './DetalleOrdenDet';
import { Link } from 'react-router-dom';

export default function DetalleOrden() {
  const { idOrden } = useParams();
  const auth = useAuth();
  const [orden, setOrden] = useState({});
  const [isLoading, setIsLoading] = useState(true);
  const [isError, setIsError] = useState(false);

  useEffect(() => {
    const obtenerDetallesOrden = async () => {
      setIsLoading(true);
      try {
        const data = await ordenService.obtenerOrdenPorUsuarioYId(
          auth.usuario.id,
          idOrden
        );
        setOrden(data);
        setIsLoading(false);
      } catch (error) {
        console.log(
          'Error when fetching order details: ' + JSON.stringify(error.config)
        );
        setIsError(true);
      }
    };

    obtenerDetallesOrden();
  }, [idOrden, auth.usuario.id]);

  if (isLoading) {
    return <p>Cargando detalles de la orden...</p>;
  } else if (isError) {
    return <p>Se produjo un error al recuperar los detalles de la orden</p>;
  } else {
    return (
      <>
        <h1>Orden # {idOrden}</h1>
        <h2>Datos generales</h2>
        <DetalleOrdenEnc {...orden} />

        <h2>Historial</h2>
        <DetalleOrdenHist {...orden} />

        <h2>Detalle</h2>
        <DetalleOrdenDet {...orden} />

        <p>
          <Link to="/mis-ordenes">Volver al listado de órdenes</Link>
        </p>
      </>
    );
  }
}
