import React, { useState, useEffect } from 'react';
import categoriaService from '../services/categoriaService';
import Categoria from './Categoria';
import Paginacion from './Paginacion';

export default function Categorias() {
  const [categorias, setCategorias] = useState([]);
  const [numeroPagina, setNumeroPagina] = useState(0);
  const [paginasTotales, setPaginasTotales] = useState(0);

  const avanzarPaginaHandler = () => {
    setNumeroPagina(numeroPagina + 1);
  };

  const retrocederPaginaHandler = () => {
    setNumeroPagina(numeroPagina - 1);
  };

  useEffect(() => {
    const obtenerCategorias = async () => {
      try {
        const data = await categoriaService.obtenerCategorias(numeroPagina);
        setCategorias(data.content);
        setPaginasTotales(data.totalPages);
      } catch (error) {
        console.log(error);
      }
    };

    obtenerCategorias();
  }, [numeroPagina]);

  return (
    <div>
      <h1>Categorías</h1>

      {categorias.length > 0 ? (
        <Paginacion
          numeroPagina={numeroPagina}
          paginasTotales={paginasTotales}
          avanzarPaginaHandler={avanzarPaginaHandler}
          retrocederPaginaHandler={retrocederPaginaHandler}
        />
      ) : (
        <p>Aún no se han creado categorías.</p>
      )}
      <ul className="list-none padding-zero">
        {categorias.map((categoria) => (
          <Categoria key={categoria.id} categoria={categoria} />
        ))}
      </ul>
    </div>
  );
}
