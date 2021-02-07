import React from 'react';

export default function Orden({ orden }) {
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
    </tr>
  );
}
