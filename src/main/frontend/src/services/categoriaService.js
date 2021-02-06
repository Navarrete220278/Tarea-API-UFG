import axios from 'axios';

async function obtenerCategorias(numeroPagina, filasPorPagina) {
  const result = await axios.get(
    `/api/v1/categorias?sort=id,asc${
      filasPorPagina ? '&size=' + filasPorPagina : ''
    }${numeroPagina ? '&page=' + numeroPagina : ''}`
  );
  return result.data;
}

async function obtenerCategoriaPorId(idCategoria) {
  const result = await axios.get(`/api/v1/categorias/${idCategoria}`);
  return result.data;
}

async function obtenerProductosDeCategoria(
  idCategoria,
  numeroPagina,
  filasPorPagina
) {
  const result = await axios.get(
    `/api/v1/categorias/${idCategoria}/productos?sort=id,asc${
      filasPorPagina ? '&size=' + filasPorPagina : ''
    }${numeroPagina ? '&page=' + numeroPagina : ''}`
  );
  return result.data;
}

const categoriaService = {
  obtenerCategorias,
  obtenerCategoriaPorId,
  obtenerProductosDeCategoria,
};

export default categoriaService;
