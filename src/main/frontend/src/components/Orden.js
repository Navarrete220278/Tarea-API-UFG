import React from 'react';
import { useHistory } from 'react-router-dom';

export default function Orden({ orden, cancelarOrden }) {
  const history = useHistory();
  return (
    <tr>
      <td>{orden.id}</td>
      <td>{orden.fechaCreada}</td>
      <td>{orden.fechaSolicitada}</td>
      <td>{orden.estado}</td>
      <td>
        ${' '}
        {orden.detalles
          .reduce((acc, val) => acc + val.cantidad * val.precio, 0)
          .toFixed(2)}
      </td>
      <td>
        <form>
          <button
            onClick={(event) => {
              event.preventDefault();
              history.push(`/ordenes/${orden.id}`);
            }}
          >
            Detalles
          </button>
          {orden.estado === 'PENDIENTE' ? (
            <button
              onClick={async (event) => {
                event.preventDefault();
                await cancelarOrden(orden.id);
              }}
            >
              Cancelar
            </button>
          ) : null}
        </form>
      </td>
    </tr>
  );
}
