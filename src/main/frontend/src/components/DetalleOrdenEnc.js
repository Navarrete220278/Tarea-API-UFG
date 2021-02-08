import React from 'react';

export default function DetalleOrdenEnc({
  usuario,
  fechaCreada,
  fechaSolicitada,
  estado,
}) {
  return (
    <table>
      <tbody>
        <tr>
          <td>
            <strong>Cliente:</strong>
          </td>
          <td>
            {usuario.nombres} {usuario.apellidos}
          </td>
        </tr>
        <tr>
          <td>
            <strong>Fecha creada:</strong>
          </td>
          <td>{fechaCreada}</td>
        </tr>
        <tr>
          <td>
            <strong>Fecha solicitada:</strong>
          </td>
          <td>{fechaSolicitada}</td>
        </tr>
        <tr>
          <td>
            <strong>Estado actual:</strong>
          </td>
          <td>{estado}</td>
        </tr>
      </tbody>
    </table>
  );
}
