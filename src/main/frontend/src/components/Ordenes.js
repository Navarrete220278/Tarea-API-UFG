import React, { useState, useEffect } from 'react';
import { useAuth } from '../providers/AuthProvider';
import ordenService from '../services/ordenService';
import Paginacion from './Paginacion';
import Orden from './Orden';

export default function Ordenes() {
  const auth = useAuth();
  const [ordenes, setOrdenes] = useState([]);
  const [numeroPagina, setNumeroPagina] = useState(0);
  const [paginasTotales, setPaginasTotales] = useState(0);

  const avanzarPaginaHandler = () => {
    setNumeroPagina(numeroPagina + 1);
  };

  const retrocederPaginaHandler = () => {
    setNumeroPagina(numeroPagina - 1);
  };

  useEffect(() => {
    const obtenerOrdenes = async () => {
      try {
        const data = await ordenService.obtenerOrdenes(
          auth.usuario.id,
          numeroPagina
        );
        setOrdenes(data.content);
        setPaginasTotales(data.totalPages);
      } catch (error) {
        console.log(error);
      }
    };

    obtenerOrdenes();
  }, [numeroPagina, auth]);

  return (
    <>
      <h1>Mis órdenes</h1>
      {ordenes.length > 0 ? (
        <Paginacion
          numeroPagina={numeroPagina}
          paginasTotales={paginasTotales}
          avanzarPaginaHandler={avanzarPaginaHandler}
          retrocederPaginaHandler={retrocederPaginaHandler}
        />
      ) : (
        <p>Aún no se han creado categorías.</p>
      )}
      <table>
        <thead>
          <tr>
            <th>No.</th>
            <th>Fecha creada</th>
            <th>Fecha solicitada</th>
            <th>Estado</th>
            <th>Monto</th>
          </tr>
        </thead>
        <tbody>
          {ordenes.map((orden) => (
            <Orden key={orden.id} orden={orden} />
          ))}
        </tbody>
      </table>
    </>
  );
}
