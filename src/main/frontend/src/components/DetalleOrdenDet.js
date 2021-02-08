import React from 'react';

export default function DetalleOrdenDet({ detalles }) {
  return (
    <table>
      <thead>
        <tr>
          <th>Producto</th>
          <th>Cantidad</th>
          <th>Precio</th>
          <th>Total</th>
        </tr>
      </thead>
      <tbody>
        {detalles.map((detalle) => (
          <tr key={detalle.id}>
            <td>{detalle.producto.nombre}</td>
            <td>{detalle.cantidad}</td>
            <td>$ {detalle.precio.toFixed(2)}</td>
            <td>$ {(detalle.cantidad * detalle.precio).toFixed(2)}</td>
          </tr>
        ))}
        <tr>
          <td colSpan={3}>
            <strong>Total general</strong>
          </td>
          <td>
            <strong>
              ${' '}
              {detalles
                .reduce((acc, val) => acc + val.cantidad * val.precio, 0)
                .toFixed(2)}
            </strong>
          </td>
        </tr>
      </tbody>
    </table>
  );
}
