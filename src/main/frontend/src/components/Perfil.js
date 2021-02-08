import React, { useState, useCallback } from 'react';
import { useAuth } from '../providers/AuthProvider';
import person from '../person.png';
import { useDropzone } from 'react-dropzone';

function MyDropzone() {
  const onDrop = useCallback((acceptedFiles) => {
    // Do something with the files
  }, []);
  const { getRootProps, getInputProps, isDragActive } = useDropzone({ onDrop });

  return (
    <div {...getRootProps()}>
      <input {...getInputProps()} />
      {isDragActive ? (
        <p>Drop the files here ...</p>
      ) : (
        <p>Drag 'n' drop some files here, or click to select files</p>
      )}
    </div>
  );
}

export default function Perfil() {
  const auth = useAuth();
  const usuario = {
    id: auth.usuario.id,
    nombres: auth.usuario.nombres,
    apellidos: auth.usuario.apellidos,
    email: auth.usuario.email,
    direccion: auth.usuario.direccion,
    telefono: auth.usuario.telefono,
  };

  const [cambioFoto, setCambioFoto] = useState(false);

  return (
    <>
      <h1>Perfil</h1>
      {cambioFoto ? (
        <MyDropzone />
      ) : (
        <img
          src={`/api/v1/usuarios/${usuario.id}/imagen`}
          alt="Imagen de perfil"
          onError={(e) => (e.target.src = person)}
        />
      )}
      <br />
      <button onClick={(e) => e.preventDefault() || setCambioFoto(!cambioFoto)}>
        Cambiar imagen de perfil
      </button>
      <br />
      <br />
      <table>
        <tr>
          <td>Nombres</td>
          <td>{usuario.nombres}</td>
        </tr>
        <tr>
          <td>Apellidos</td>
          <td>{usuario.apellidos}</td>
        </tr>
        <tr>
          <td>Email</td>
          <td>{usuario.email}</td>
        </tr>
        <tr>
          <td>Dirección</td>
          <td>{usuario.direccion}</td>
        </tr>
        <tr>
          <td>Teléfono</td>
          <td>{usuario.telefono}</td>
        </tr>
      </table>
    </>
  );
}
