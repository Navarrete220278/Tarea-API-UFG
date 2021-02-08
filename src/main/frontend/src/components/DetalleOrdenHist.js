import React from 'react';

export default function DetalleOrdenHist({ historial }) {
  return (
    <table>
      <thead>
        <tr>
          <th>Fecha</th>
          <th>Estado</th>
          <th>Comentarios</th>
        </tr>
      </thead>
      <tbody>
        {historial.map((entrada) => (
          <tr key={entrada.id}>
            <td>{entrada.fecha}</td>
            <td>{entrada.estado}</td>
            <td>{entrada.comentarios}</td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}
