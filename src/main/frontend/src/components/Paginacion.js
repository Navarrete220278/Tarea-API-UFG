import React from 'react';

export default function Paginacion({
  numeroPagina,
  paginasTotales,
  avanzarPaginaHandler,
  retrocederPaginaHandler,
}) {
  return (
    <div className="margin-bottom-1">
      <button
        name="anterior"
        onClick={retrocederPaginaHandler}
        disabled={numeroPagina < 1}
      >
        &lt;- Anterior
      </button>{' '}
      Página <strong>{numeroPagina + 1}</strong> de{' '}
      <strong>{paginasTotales}</strong>{' '}
      <button
        name="siguiente"
        onClick={avanzarPaginaHandler}
        disabled={numeroPagina + 1 >= paginasTotales}
      >
        Siguiente -&gt;
      </button>
    </div>
  );
}
