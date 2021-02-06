import axios from 'axios';

axios.defaults.baseURL = '/api/v1/categorias';

async function obtenerCategorias(numeroPagina, filasPorPagina) {
  const result = await axios.get(
    `/?sort=id,asc${filasPorPagina ? '&size=' + filasPorPagina : ''}${
      numeroPagina ? '&page=' + numeroPagina : ''
    }`
  );
  return result.data;
}

async function obtenerCategoriaPorId(idCategoria) {
  const result = await axios.get(`/${idCategoria}`);
  return result.data;
}

async function obtenerProductosDeCategoria(
  idCategoria,
  numeroPagina,
  filasPorPagina
) {
  const result = await axios.get(
    `/${idCategoria}/productos?sort=id,asc${
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
