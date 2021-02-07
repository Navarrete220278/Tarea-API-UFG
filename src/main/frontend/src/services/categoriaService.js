import axiosInstance from './axiosInstance';

async function obtenerCategorias(numeroPagina, filasPorPagina) {
  const result = await axiosInstance.get(
    `/categorias?sort=id,asc${
      !!filasPorPagina ? '&size=' + filasPorPagina : ''
    }${!!numeroPagina ? '&page=' + numeroPagina : ''}`
  );
  return result.data;
}

async function obtenerCategoriaPorId(idCategoria) {
  const result = await axiosInstance.get(`/categorias/${idCategoria}`);
  return result.data;
}

async function obtenerProductosDeCategoria(
  idCategoria,
  numeroPagina,
  filasPorPagina
) {
  const result = await axiosInstance.get(
    `/categorias/${idCategoria}/productos?sort=id,asc${
      !!filasPorPagina ? '&size=' + filasPorPagina : ''
    }${!!numeroPagina ? '&page=' + numeroPagina : ''}`
  );
  return result.data;
}

const categoriaService = {
  obtenerCategorias,
  obtenerCategoriaPorId,
  obtenerProductosDeCategoria,
};

export default categoriaService;
