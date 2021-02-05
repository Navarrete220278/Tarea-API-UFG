import React, { useEffect, useState } from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import {
  faHome,
  faSignInAlt,
  faShoppingCart,
  faPlusSquare,
} from '@fortawesome/free-solid-svg-icons';
import {
  BrowserRouter as Router,
  Switch,
  Link,
  Route,
  useParams,
  useHistory,
  useLocation,
} from 'react-router-dom';
import './App.css';
import axios from 'axios';
import './food.png';

function App() {
  const [carrito, setCarrito] = useState(
    JSON.parse(localStorage.getItem('carrito') || '[]')
  );

  const agregarAlCarrito = (producto, cantidad) => {
    // Variable para guardar el carrito mutado
    let nuevoCarrito;

    // ¿El producto ya está en el carrito?
    const indice = carrito.findIndex(
      (item) => item.producto.id === producto.id
    );
    if (indice === -1) {
      nuevoCarrito = [...carrito, { producto: producto, cantidad: cantidad }];
    } else {
      nuevoCarrito = carrito.map((idx, item) => {
        if (idx === indice)
          return { producto: producto, cantidad: item.cantidad + cantidad };
        else return item;
      });
    }

    setCarrito(nuevoCarrito);
    localStorage.setItem('carrito', JSON.stringify(nuevoCarrito));
  };

  return (
    <Router>
      <Navbar
        totalCarrito={carrito.reduce(
          (acc, cur) => acc + cur.producto.precio * cur.cantidad,
          0
        )}
      />
      <div className="wrapper">
        <Switch>
          <Route exact path="/">
            <Categorias />
          </Route>
          <Route path="/categorias/:idCategoria">
            <ProductosCategoria agregarAlCarrito={agregarAlCarrito} />
          </Route>
          <Route path="/iniciar-sesion">
            <Login />
          </Route>
        </Switch>
      </div>
    </Router>
  );
}

function Navbar({ totalCarrito = 0 }) {
  return (
    <div className="top-navbar">
      <header>
        <div id="logo">
          <Link to="/">
            Ordena <span>en línea</span>
          </Link>
        </div>
      </header>
      <nav>
        <ul>
          <li>
            <Link to="/">
              <FontAwesomeIcon icon={faHome} /> Página principal
            </Link>
          </li>
          <li>
            <Link to="/iniciar-sesion">
              <FontAwesomeIcon icon={faSignInAlt} /> Iniciar sesión
            </Link>
          </li>
          <li>
            <Link to="/carrito">
              <FontAwesomeIcon icon={faShoppingCart} /> Carrito (${' '}
              {totalCarrito.toFixed(2)})
            </Link>
          </li>
        </ul>
      </nav>
    </div>
  );
}

function Categorias() {
  const [categorias, setCategorias] = useState([]);
  const [numeroPagina, setNumeroPagina] = useState(0);
  const [paginasTotales, setPaginasTotales] = useState(null);

  useEffect(() => {
    const obtenerCategorias = async () => {
      const result = await axios.get(
        `/api/v1/categorias?sort=id,asc&page=${numeroPagina}`
      );
      setCategorias(result.data.content);
      setPaginasTotales(result.data.totalPages);
    };
    obtenerCategorias();
  }, [numeroPagina]);

  const incrementarNumeroPagina = () => {
    setNumeroPagina(numeroPagina + 1);
  };

  const disminuirNumeroPagina = () => {
    setNumeroPagina(numeroPagina - 1);
  };

  return (
    <>
      <h1>Categorías</h1>
      {/* Lista de categorias */}
      <ul className="categoria">
        {categorias.map((categoria) => (
          <li key={categoria.id} className="categoria-item">
            <Link to={`/categorias/${categoria.id}`}>
              <img
                src={`/api/v1/categorias/${categoria.id}/imagen`}
                alt={categoria.nombre}
                onError={(ev) => (ev.target.src = 'food.png')}
              />
              <p>{categoria.nombre}</p>
            </Link>
          </li>
        ))}
      </ul>
      <Paginacion
        numeroPagina={numeroPagina}
        paginasTotales={paginasTotales}
        funcionIncrementar={incrementarNumeroPagina}
        funcionDisminuir={disminuirNumeroPagina}
      />
    </>
  );
}

function ProductosCategoria({ agregarAlCarrito }) {
  let { idCategoria } = useParams();
  const [categoria, setCategoria] = useState({});
  const [productos, setProductos] = useState([]);
  const [numeroPagina, setNumeroPagina] = useState(0);
  const [paginasTotales, setPaginasTotales] = useState(null);

  useEffect(() => {
    axios
      .get(`/api/v1/categorias/${idCategoria}`)
      .then((res) => setCategoria(res.data));

    axios.get(`/api/v1/categorias/${idCategoria}/productos`).then((res) => {
      setProductos(res.data.content);
      setPaginasTotales(res.data.totalPages);
    });
  }, [numeroPagina, idCategoria]);

  const incrementarNumeroPagina = () => {
    setNumeroPagina(numeroPagina + 1);
  };

  const disminuirNumeroPagina = () => {
    setNumeroPagina(numeroPagina - 1);
  };

  return (
    <>
      <h1>{categoria.nombre}</h1>
      {productos.length > 0 ? (
        <>
          <div class="lista-productos">
            {productos.map((producto) => (
              <div key={producto.id} className="producto">
                <div className="producto-img">
                  <img
                    src={`/api/v1/productos/${producto.id}/imagen`}
                    alt={producto.nombre}
                    onError={(ev) => (ev.target.src = 'food.png')}
                  />
                </div>
                <div className="producto-info">
                  <p className="producto-nombre">{producto.nombre}</p>
                  <p className="producto-precio">
                    $ {producto.precio.toFixed(2)}
                  </p>
                </div>
                <div className="agregar-orden">
                  <button onClick={() => agregarAlCarrito(producto, 1)}>
                    <FontAwesomeIcon icon={faPlusSquare} /> Agregar al carrito
                  </button>
                </div>
              </div>
            ))}
          </div>
          <Paginacion
            numeroPagina={numeroPagina}
            paginasTotales={paginasTotales}
            funcionIncrementar={incrementarNumeroPagina}
            funcionDisminuir={disminuirNumeroPagina}
          />
        </>
      ) : (
        <p className="espacio-antes">No hay productos en esta categoría.</p>
      )}
    </>
  );
}

function Paginacion({
  numeroPagina,
  paginasTotales,
  funcionIncrementar,
  funcionDisminuir,
}) {
  return (
    <>
      {paginasTotales > 1 ? (
        <div class="paginacion">
          <nav>
            <button
              name="anterior"
              onClick={funcionDisminuir}
              disabled={numeroPagina <= 0}
            >
              &lt;- Anterior
            </button>
            <span>
              Página <strong>{numeroPagina + 1}</strong> de{' '}
              <strong>{paginasTotales}</strong>
            </span>
            <button
              name="siguiente"
              onClick={funcionIncrementar}
              disabled={numeroPagina + 1 >= paginasTotales}
              value={'->'}
            >
              Siguiente -&gt;
            </button>
          </nav>
        </div>
      ) : null}
    </>
  );
}

function Login() {
  const [usuario, setUsuario] = useState({
    usuario: '',
    password: '',
  });

  let history = useHistory();
  let location = useLocation();
  let { from } = location.state || { from: { pathname: '/' } };

  const handleInputChange = (event) => {
    const { name, value } = event.target;

    setUsuario({ ...usuario, [name]: value });
  };

  const handleAuth = (event) => {
    event.preventDefault();
    axios.post('/api/v1/auth/login', usuario).then((res) => {
      localStorage.setItem('token', res.data.token);
      history.replace(from);
    });
  };

  return (
    <>
      <h1>Inicio de sesión</h1>
      <form>
        <label>Correo electrónico</label>
        <input
          type="text"
          name="usuario"
          value={usuario.usuario}
          onChange={handleInputChange}
        />
        <label>Contraseña</label>
        <input type="password" name="password" onChange={handleInputChange} />
        <button type="submit" onClick={handleAuth}>
          Acceder
        </button>
        <span>
          ¿Aún no tiene una cuenta? <Link to="/registro">Regístrese aquí</Link>
        </span>
      </form>
    </>
  );
}

export default App;
