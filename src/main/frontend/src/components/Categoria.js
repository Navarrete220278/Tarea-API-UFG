import React from 'react';
import food from '../food.png';
import { Link } from 'react-router-dom';

export default function Categoria({ categoria }) {
  return (
    <li className="flex-flex-start margin-bottom-1">
      <Link to={`/categorias/${categoria.id}`}>
        <img
          className="img-thumbnail rounded-borders margin-right-1"
          src={categoria ? `/api/v1/categorias/${categoria.id}/imagen` : food}
          onError={(e) => (e.target.src = food)}
          alt={categoria.nombre}
        />
        {categoria.nombre}
      </Link>
    </li>
  );
}
