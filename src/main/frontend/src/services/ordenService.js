import axiosInstance from './axiosInstance';

async function crearOrden(idUsuario, orden) {
  const result = await axiosInstance.post(
    `/usuarios/${idUsuario}/ordenes`,
    orden
  );
  return result.data;
}

async function obtenerOrdenes(idUsuario, numeroPagina, filasPorPagina) {
  const result = await axiosInstance.get(
    `/usuarios/${idUsuario}/ordenes?sort=id,desc${
      !!filasPorPagina ? '&size=' + filasPorPagina : ''
    }${!!numeroPagina ? '&page=' + numeroPagina : ''}`
  );
  return result.data;
}

async function cancelarOrden(idUsuario, idOrden) {
  await axiosInstance.delete(`/usuarios/${idUsuario}/ordenes/${idOrden}`);
}

async function obtenerOrdenPorUsuarioYId(idUsuario, idOrden) {
  const result = await axiosInstance.get(
    `/usuarios/${idUsuario}/ordenes/${idOrden}`
  );
  return result.data;
}

const ordenService = {
  crearOrden,
  obtenerOrdenes,
  cancelarOrden,
  obtenerOrdenPorUsuarioYId,
};

export default ordenService;
