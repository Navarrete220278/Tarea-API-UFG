import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import categoriaService from '../services/categoriaService';
import Paginacion from './Paginacion';
import Producto from './Producto';

export default function Productos() {
  let { idCategoria } = useParams();
  const [categoria, setCategoria] = useState({ nombre: '' });
  const [productos, setProductos] = useState([]);
  const [numeroPagina, setNumeroPagina] = useState(0);
  const [paginasTotales, setPaginasTotales] = useState(0);

  const avanzarPaginaHandler = () => {
    setNumeroPagina(numeroPagina + 1);
  };

  const retrocederPaginaHandler = () => {
    setNumeroPagina(numeroPagina - 1);
  };

  useEffect(() => {
    const obtenerDetalleCategoria = async () => {
      console.log('Actualizando categorias...');
      const data = await categoriaService.obtenerCategoriaPorId(idCategoria);
      setCategoria(data);
    };

    obtenerDetalleCategoria();
  }, [idCategoria]);

  useEffect(() => {
    const obtenerProductosDeCategoria = async () => {
      try {
        const data = await categoriaService.obtenerProductosDeCategoria(
          idCategoria,
          numeroPagina
        );
        setProductos(data.content);
        setPaginasTotales(data.totalPages);
      } catch (error) {
        console.log(error);
      }
    };

    obtenerProductosDeCategoria();
  }, [idCategoria, numeroPagina]);

  return (
    <div>
      <h1>{categoria.nombre}</h1>
      {productos.length > 0 ? (
        <Paginacion
          numeroPagina={numeroPagina}
          paginasTotales={paginasTotales}
          avanzarPaginaHandler={avanzarPaginaHandler}
          retrocederPaginaHandler={retrocederPaginaHandler}
        />
      ) : (
        <p>No se encontraron productos para esta categoría.</p>
      )}

      <ul className="list-none padding-zero">
        {productos.map((producto) => (
          <Producto key={producto.id} producto={producto} />
        ))}
      </ul>
    </div>
  );
}
