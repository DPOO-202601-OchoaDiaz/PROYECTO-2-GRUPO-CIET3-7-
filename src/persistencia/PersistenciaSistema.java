package persistencia;
import modelo.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import modelo.DiaSemana;
import modelo.InscripcionTorneo;
import modelo.Torneo;
import modelo.TorneoAmistoso;
import modelo.TorneoCompetitivo;


public class PersistenciaSistema
{
    private String rutaDatos;

    private boolean cargado = false;
    private List<Usuario> usuariosCacheados = null;
    private Cafe cafeCacheado = null;
    private List<SugerenciaPlatillo> sugerenciasCacheadas = null;

    public PersistenciaSistema(String rutaDatos)
    {
        this.rutaDatos = rutaDatos;
        crearCarpetaSiNoExiste();
    }

    // =========================================================
    // API publica
    // =========================================================
    public List<SugerenciaPlatillo> cargarSugerencias()
    {
        if (!cargado)
        {
            cargarTodo();
        }
        return sugerenciasCacheadas;
    }
    public List<Usuario> cargarUsuarios()
    {
        if (!cargado)
        {
            cargarTodo();
        }
        return usuariosCacheados;
    }

    public Cafe cargarCafe()
    {
        if (!cargado)
        {
            cargarTodo();
        }
        return cafeCacheado;
    }

    public void guardarUsuarios(List<Usuario> usuarios)
    {
        escribirUsuariosBasico(usuarios);
        escribirTurnos(usuarios);
        escribirJuegosConocidosMesero(usuarios);
        escribirJuegosFavoritos(usuarios);
        escribirSolicitudesTurno(usuarios);
        escribirSugerencias();
        invalidarCache();
    }

    public void guardarCafe(Cafe cafe)
    {
        escribirCafeBasico(cafe);
        escribirEmpleadosCafe(cafe);
        escribirAdministradorCafe(cafe);
        escribirJuegosMesa(cafe);
        escribirCopiasPrestamo(cafe);
        escribirJuegosVenta(cafe);
        escribirMenu(cafe);
        escribirReservas(cafe);
        escribirPrestamos(cafe);
        escribirVentas(cafe);
        invalidarCache();
    }

    public List<Venta> cargarVentas()
    {
        if (!cargado)
        {
            cargarTodo();
        }
        if (cafeCacheado != null)
        {
            return cafeCacheado.getVentas();
        }
        return new ArrayList<Venta>();
    }

    public void guardarVentas(List<Venta> ventas)
    {
        // Se maneja desde guardarCafe(cafe)
    }

    public List<Prestamo> cargarPrestamos()
    {
        if (!cargado)
        {
            cargarTodo();
        }
        if (cafeCacheado != null)
        {
            return cafeCacheado.getPrestamos();
        }
        return new ArrayList<Prestamo>();
    }

    public void guardarPrestamos(List<Prestamo> prestamos)
    {
        // Se maneja desde guardarCafe(cafe)
    }

    public List<Turno> cargarTurnos()
    {
        return new ArrayList<Turno>();
    }

    public void guardarTurnos(List<Turno> turnos)
    {
        // Se maneja desde guardarUsuarios(usuarios)
    }

    // =========================================================
    // Carga completa
    // =========================================================

    private void cargarTodo()
    {
        if (cargado)
        {
            return;
        }

        usuariosCacheados = new ArrayList<Usuario>();
        sugerenciasCacheadas = new ArrayList<SugerenciaPlatillo>();
        leerUsuariosBasico();

        cafeCacheado = leerCafeBasico();
        if (cafeCacheado == null)
        {
            cafeCacheado = new Cafe(30);
        }

        leerEmpleadosCafe();
        leerAdministradorCafe();
        leerJuegosMesa();
        leerCopiasPrestamo();
        leerJuegosVenta();
        leerMenu();
        leerTurnos();
        leerJuegosConocidosMesero();
        leerJuegosFavoritos();
        leerSolicitudesTurno();
        leerSugerencias();
        leerReservas();
        leerPrestamos();
        leerVentas();

        cargado = true;
    }

    // =========================================================
    // Lectura
    // =========================================================

    private void leerUsuariosBasico()
    {
        File archivo = path("usuarios.txt");
        if (!archivo.exists())
        {
            return;
        }

        try (BufferedReader br = abrir(archivo))
        {
            String linea;
            while ((linea = br.readLine()) != null)
            {
                linea = linea.trim();
                if (linea.isEmpty())
                {
                    continue;
                }

                String[] p = linea.split(";", -1);

                if ("CLIENTE".equals(p[0]) && p.length >= 7)
                {
                    Cliente c = new Cliente(p[1], p[2], p[3], p[4], p[5]);
                    c.setPuntosFidelidad(Integer.parseInt(p[6]));
                    usuariosCacheados.add(c);
                }
                else if ("MESERO".equals(p[0]) && p.length >= 7)
                {
                    usuariosCacheados.add(new Mesero(p[1], p[2], p[3], p[4], p[5], p[6]));
                }
                else if ("COCINERO".equals(p[0]) && p.length >= 7)
                {
                    usuariosCacheados.add(new Cocinero(p[1], p[2], p[3], p[4], p[5], p[6]));
                }
                else if ("ADMIN".equals(p[0]) && p.length >= 6)
                {
                    usuariosCacheados.add(new Administrador(p[1], p[2], p[3], p[4], p[5]));
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private Cafe leerCafeBasico()
    {
        File archivo = path("cafe.txt");
        if (!archivo.exists())
        {
            return null;
        }

        try (BufferedReader br = abrir(archivo))
        {
            String linea = br.readLine();
            if (linea == null)
            {
                return null;
            }

            Cafe cafe = new Cafe(Integer.parseInt(linea.trim()));

            String mesaLinea;
            while ((mesaLinea = br.readLine()) != null)
            {
                mesaLinea = mesaLinea.trim();
                if (!mesaLinea.isEmpty())
                {
                    cafe.agregarMesa(new Mesa(Integer.parseInt(mesaLinea)));
                }
            }

            return cafe;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    private void leerEmpleadosCafe()
    {
        File archivo = path("empleados_cafe.txt");
        if (!archivo.exists())
        {
            return;
        }

        try (BufferedReader br = abrir(archivo))
        {
            String linea;
            while ((linea = br.readLine()) != null)
            {
                linea = linea.trim();
                if (linea.isEmpty())
                {
                    continue;
                }

                Usuario u = usuarioPorLogin(linea);
                if (u instanceof Empleado)
                {
                    cafeCacheado.agregarEmpleado((Empleado) u);
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void leerAdministradorCafe()
    {
        File archivo = path("admin_cafe.txt");
        if (!archivo.exists())
        {
            return;
        }

        try (BufferedReader br = abrir(archivo))
        {
            String linea = br.readLine();
            if (linea != null && !linea.trim().isEmpty())
            {
                Usuario u = usuarioPorLogin(linea.trim());
                if (u instanceof Administrador)
                {
                    cafeCacheado.setAdministrador((Administrador) u);
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void leerJuegosMesa()
    {
        File archivo = path("juegos_mesa.txt");
        if (!archivo.exists())
        {
            return;
        }

        try (BufferedReader br = abrir(archivo))
        {
            String linea;
            while ((linea = br.readLine()) != null)
            {
                linea = linea.trim();
                if (linea.isEmpty())
                {
                    continue;
                }

                String[] p = linea.split(";", -1);
                if (p.length >= 8)
                {
                    JuegoMesa juego = new JuegoMesa(
                        p[0],
                        Integer.parseInt(p[1]),
                        p[2],
                        Integer.parseInt(p[3]),
                        Integer.parseInt(p[4]),
                        Integer.parseInt(p[5]),
                        Boolean.parseBoolean(p[6]),
                        CategoriaJuego.valueOf(p[7])
                    );
                    cafeCacheado.agregarJuegoCatalogo(juego);
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void leerCopiasPrestamo()
    {
        File archivo = path("copias_prestamo.txt");
        if (!archivo.exists())
        {
            return;
        }

        try (BufferedReader br = abrir(archivo))
        {
            String linea;
            while ((linea = br.readLine()) != null)
            {
                linea = linea.trim();
                if (linea.isEmpty())
                {
                    continue;
                }

                String[] p = linea.split(";", -1);
                if (p.length >= 3)
                {
                    JuegoMesa juego = juegoMesaPorNombre(p[2]);
                    if (juego != null)
                    {
                        CopiaJuegoPrestamo copia = new CopiaJuegoPrestamo(
                            EstadoJuego.valueOf(p[0]),
                            Boolean.parseBoolean(p[1]),
                            juego
                        );
                        juego.agregarCopia(copia);
                    }
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void leerJuegosVenta()
    {
        File archivo = path("juegos_venta.txt");
        if (!archivo.exists())
        {
            return;
        }

        try (BufferedReader br = abrir(archivo))
        {
            String linea;
            while ((linea = br.readLine()) != null)
            {
                linea = linea.trim();
                if (linea.isEmpty())
                {
                    continue;
                }

                String[] p = linea.split(";", -1);
                if (p.length >= 4)
                {
                    cafeCacheado.agregarJuegoVenta(
                        new JuegoVenta(
                            p[0],
                            Double.parseDouble(p[1]),
                            Boolean.parseBoolean(p[2]),
                            Integer.parseInt(p[3])
                        )
                    );
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void leerMenu()
    {
        File archivo = path("menu.txt");
        if (!archivo.exists())
        {
            return;
        }

        try (BufferedReader br = abrir(archivo))
        {
            String linea;
            while ((linea = br.readLine()) != null)
            {
                linea = linea.trim();
                if (linea.isEmpty())
                {
                    continue;
                }

                String[] p = linea.split(";", -1);

                if ("BEBIDA".equals(p[0]) && p.length >= 6)
                {
                    cafeCacheado.agregarProductoMenu(
                        new Bebida(
                            p[1],
                            Double.parseDouble(p[2]),
                            Boolean.parseBoolean(p[3]),
                            Boolean.parseBoolean(p[4]),
                            Boolean.parseBoolean(p[5])
                        )
                    );
                }
                else if ("PASTELERIA".equals(p[0]) && p.length >= 4)
                {
                    Pasteleria pas = new Pasteleria(
                        p[1],
                        Double.parseDouble(p[2]),
                        Boolean.parseBoolean(p[3])
                    );

                    if (p.length >= 5 && !p[4].trim().isEmpty())
                    {
                        String[] alergenos = p[4].split(",");
                        for (String al : alergenos)
                        {
                            al = al.trim();
                            if (!al.isEmpty())
                            {
                                pas.agregarAlergeno(Alergeno.valueOf(al));
                            }
                        }
                    }

                    cafeCacheado.agregarProductoMenu(pas);
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void leerTurnos()
    {
        File archivo = path("turnos.txt");
        if (!archivo.exists())
        {
            return;
        }

        try (BufferedReader br = abrir(archivo))
        {
            String linea;
            while ((linea = br.readLine()) != null)
            {
                linea = linea.trim();
                if (linea.isEmpty())
                {
                    continue;
                }

                String[] p = linea.split(";", -1);
                if (p.length >= 4)
                {
                    Usuario u = usuarioPorLogin(p[0]);
                    if (u instanceof Empleado)
                    {
                        ((Empleado) u).getTurnos().add(
                            new Turno(
                                DiaSemana.valueOf(p[1]),
                                p[2],
                                p[3]
                            )
                        );
                    }
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void leerJuegosConocidosMesero()
    {
        File archivo = path("juegos_conocidos.txt");
        if (!archivo.exists())
        {
            return;
        }

        try (BufferedReader br = abrir(archivo))
        {
            String linea;
            while ((linea = br.readLine()) != null)
            {
                linea = linea.trim();
                if (linea.isEmpty())
                {
                    continue;
                }

                String[] p = linea.split(";", -1);
                if (p.length >= 2)
                {
                    Usuario u = usuarioPorLogin(p[0]);
                    JuegoMesa j = juegoMesaPorNombre(p[1]);

                    if (u instanceof Mesero && j != null)
                    {
                        ((Mesero) u).registrarJuegoConocido(j);
                    }
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void leerJuegosFavoritos()
    {
        File archivo = path("juegos_favoritos.txt");
        if (!archivo.exists())
        {
            return;
        }

        try (BufferedReader br = abrir(archivo))
        {
            String linea;
            while ((linea = br.readLine()) != null)
            {
                linea = linea.trim();
                if (linea.isEmpty())
                {
                    continue;
                }

                String[] p = linea.split(";", -1);
                if (p.length >= 2)
                {
                    Usuario u = usuarioPorLogin(p[0]);
                    JuegoMesa j = juegoMesaPorNombre(p[1]);

                    if (u instanceof Cliente && j != null)
                    {
                        ((Cliente) u).agregarJuegoFavorito(j);
                    }
                    else if (u instanceof Empleado && j != null)
                    {
                        ((Empleado) u).agregarJuegoFavorito(j);
                    }
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void leerSolicitudesTurno()
    {
        File archivo = path("solicitudes_turno.txt");
        if (!archivo.exists())
        {
            return;
        }

        try (BufferedReader br = abrir(archivo))
        {
            String linea;
            while ((linea = br.readLine()) != null)
            {
                linea = linea.trim();
                if (linea.isEmpty())
                {
                    continue;
                }

                String[] p = linea.split(";", -1);
                if (p.length >= 11)
                {
                    Usuario uSol = usuarioPorLogin(p[0]);
                    if (!(uSol instanceof Empleado))
                    {
                        continue;
                    }

                    TipoSolicitud tipo = TipoSolicitud.valueOf(p[1]);
                    String fechaHora = fromNull(p[2]);
                    String estado = fromNull(p[3]);

                    Usuario uDest = usuarioPorLogin(p[4]);
                    Empleado destino = (uDest instanceof Empleado) ? (Empleado) uDest : null;

                    Turno turnoOriginal = turnoDeEmpleado((Empleado) uSol, p[5], p[6], p[7]);
                    Turno turnoPropuesto = null;

                    if (!p[8].isEmpty())
                    {
                        turnoPropuesto = new Turno(
                            DiaSemana.valueOf(p[8]),
                            p[9],
                            p[10]
                        );
                    }

                    SolicitudCambioTurno solicitud = new SolicitudCambioTurno(
                        tipo,
                        fechaHora,
                        estado,
                        (Empleado) uSol,
                        turnoOriginal,
                        turnoPropuesto,
                        destino
                    );

                    ((Empleado) uSol).getSolicitudesCambioTurno().add(solicitud);
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void leerSugerencias()
    {
        File archivo = path("sugerencias.txt");
        if (!archivo.exists())
        {
            return;
        }

        try (BufferedReader br = abrir(archivo))
        {
            String linea;
            while ((linea = br.readLine()) != null)
            {
                linea = linea.trim();
                if (linea.isEmpty())
                {
                    continue;
                }

                // loginEmpleado;nombrePropuesto;categoria;fechaHora;estado;loginAdmin
                String[] p = linea.split(";", -1);
                if (p.length >= 6)
                {
                    Usuario uEmp = usuarioPorLogin(p[0]);
                    Usuario uAdmin = usuarioPorLogin(p[5]);

                    if (uEmp instanceof Empleado)
                    {
                        Empleado empleado = (Empleado) uEmp;
                        Administrador admin = (uAdmin instanceof Administrador) ? (Administrador) uAdmin : null;

                        SugerenciaPlatillo sugerencia = new SugerenciaPlatillo(
                        	    p[1],
                        	    CategoriaPropuesta.valueOf(p[2]),
                        	    fromNull(p[3]),
                        	    p[4].isEmpty() ? null : EstadoSugerencia.valueOf(p[4]),
                        	    empleado,
                        	    admin
                        	);

                        // No hay lista de sugerencias dentro de Empleado/Cafe,
                        // entonces solo se reconstruye el objeto si luego decides usarlo.
                      
                    }
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void leerReservas()
    {
        File archivo = path("reservas.txt");
        if (!archivo.exists())
        {
            return;
        }

        try (BufferedReader br = abrir(archivo))
        {
            String linea;
            while ((linea = br.readLine()) != null)
            {
                linea = linea.trim();
                if (linea.isEmpty())
                {
                    continue;
                }

                String[] p = linea.split(";", -1);
                if (p.length >= 7)
                {
                    String fechaHora = fromNull(p[0]);
                    int numeroPersonas = Integer.parseInt(p[1]);
                    boolean hayNinos = Boolean.parseBoolean(p[2]);
                    boolean hayMenores = Boolean.parseBoolean(p[3]);
                    EstadoReserva estado = EstadoReserva.valueOf(p[4]);
                    Usuario u = usuarioPorLogin(p[5]);
                    Mesa mesa = mesaPorNumero(Integer.parseInt(p[6]));

                    if (u instanceof Cliente && mesa != null)
                    {
                        ReservaMesa reserva = new ReservaMesa(
                            fechaHora,
                            numeroPersonas,
                            hayNinos,
                            hayMenores,
                            (Cliente) u,
                            mesa
                        );
                        reserva.setEstadoReserva(estado);
                        mesa.asignarReserva(reserva);
                    }
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void leerPrestamos()
    {
        File archivoPrestamos = path("prestamos.txt");
        if (!archivoPrestamos.exists())
        {
            return;
        }

        List<Prestamo> indicePrestamos = new ArrayList<Prestamo>();

        try (BufferedReader br = abrir(archivoPrestamos))
        {
            String linea;
            while ((linea = br.readLine()) != null)
            {
                linea = linea.trim();
                if (linea.isEmpty())
                {
                    continue;
                }

                String[] p = linea.split(";", -1);
                if (p.length >= 5)
                {
                    String fechaInicio = fromNull(p[0]);
                    String fechaFin = fromNull(p[1]);
                    boolean advertencia = Boolean.parseBoolean(p[2]);
                    Usuario usuario = usuarioPorLogin(p[3]);
                    Mesa mesa = "NULL".equals(p[4]) ? null : mesaPorNumero(Integer.parseInt(p[4]));

                    Prestamo prestamo = new Prestamo(fechaInicio, fechaFin, advertencia, usuario, mesa);
                    indicePrestamos.add(prestamo);
                    cafeCacheado.registrarPrestamo(prestamo);
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        File archivoDetalles = path("detalles_prestamo.txt");
        if (!archivoDetalles.exists())
        {
            return;
        }

        try (BufferedReader br = abrir(archivoDetalles))
        {
            String linea;
            while ((linea = br.readLine()) != null)
            {
                linea = linea.trim();
                if (linea.isEmpty())
                {
                    continue;
                }

                String[] p = linea.split(";", -1);
                if (p.length >= 5)
                {
                    int indicePrestamo = Integer.parseInt(p[0]);
                    String nombreJuego = p[1];
                    int indiceCopia = Integer.parseInt(p[2]);
                    String fechaAsignacion = fromNull(p[3]);
                    String fechaDevolucion = fromNull(p[4]);

                    if (indicePrestamo < indicePrestamos.size())
                    {
                        JuegoMesa juego = juegoMesaPorNombre(nombreJuego);
                        if (juego != null && indiceCopia >= 0 && indiceCopia < juego.getCopias().size())
                        {
                            CopiaJuegoPrestamo copia = juego.getCopias().get(indiceCopia);
                            DetallePrestamo detalle = new DetallePrestamo(copia);
                            detalle.setFechaAsignacion(fechaAsignacion);
                            detalle.setFechaDevolucion(fechaDevolucion);
                            indicePrestamos.get(indicePrestamo).getDetalles().add(detalle);
                        }
                    }
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void leerVentas()
    {
        File archivoVentas = path("ventas.txt");
        if (!archivoVentas.exists())
        {
            return;
        }

        List<Venta> indiceVentas = new ArrayList<Venta>();

        try (BufferedReader br = abrir(archivoVentas))
        {
            String linea;
            while ((linea = br.readLine()) != null)
            {
                linea = linea.trim();
                if (linea.isEmpty())
                {
                    continue;
                }

                String[] p = linea.split(";", -1);
                if (p.length >= 6)
                {
                    String fechaHora = fromNull(p[0]);
                    TipoVenta tipoVenta = TipoVenta.valueOf(p[1]);
                    double descuento = Double.parseDouble(p[2]);
                    double propina = Double.parseDouble(p[3]);
                    Usuario comprador = usuarioPorLogin(p[4]);

                    Mesero mesero = null;
                    if (!"NULL".equals(p[5]))
                    {
                        Usuario u = usuarioPorLogin(p[5]);
                        if (u instanceof Mesero)
                        {
                            mesero = (Mesero) u;
                        }
                    }

                    Venta venta = new Venta(fechaHora, tipoVenta, descuento, propina, comprador, mesero);
                    indiceVentas.add(venta);
                    cafeCacheado.registrarVenta(venta);
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        File archivoItems = path("items_venta.txt");
        if (!archivoItems.exists())
        {
            return;
        }

        try (BufferedReader br = abrir(archivoItems))
        {
            String linea;
            while ((linea = br.readLine()) != null)
            {
                linea = linea.trim();
                if (linea.isEmpty())
                {
                    continue;
                }

                String[] p = linea.split(";", -1);
                if (p.length >= 5)
                {
                    int indiceVenta = Integer.parseInt(p[0]);
                    int cantidad = Integer.parseInt(p[1]);
                    double precio = Double.parseDouble(p[2]);
                    String nombreProducto = p[3];
                    String tipoProducto = p[4];

                    if (indiceVenta < indiceVentas.size())
                    {
                        ProductoVendible producto = null;

                        if ("MENU".equals(tipoProducto))
                        {
                            producto = productoMenuPorNombre(nombreProducto);
                        }
                        else if ("JUEGO_VENTA".equals(tipoProducto))
                        {
                            producto = juegoVentaPorNombre(nombreProducto);
                        }

                        if (producto != null)
                        {
                            ItemVenta item = new ItemVenta(cantidad, precio, producto);
                            item.setPrecioUnitario(precio);
                            indiceVentas.get(indiceVenta).agregarItem(item);
                        }
                    }
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    // =========================================================
    // Escritura
    // =========================================================

    private void escribirUsuariosBasico(List<Usuario> usuarios)
    {
        try (FileWriter fw = crear("usuarios.txt"))
        {
            for (Usuario u : usuarios)
            {
                if (u instanceof Cliente)
                {
                    Cliente c = (Cliente) u;
                    fw.write("CLIENTE;" + safe(c.getDocumentoIdentidad()) + ";" + safe(c.getNombre()) + ";"
                            + safe(c.getCorreoElectronico()) + ";" + safe(c.getLogin()) + ";"
                            + safe(c.getPassword()) + ";" + c.getPuntosFidelidad() + "\n");
                }
                else if (u instanceof Mesero)
                {
                    Mesero m = (Mesero) u;
                    fw.write("MESERO;" + safe(m.getDocumentoIdentidad()) + ";" + safe(m.getNombre()) + ";"
                            + safe(m.getCorreoElectronico()) + ";" + safe(m.getLogin()) + ";"
                            + safe(m.getPassword()) + ";" + safe(m.getCodigoEmpleado()) + "\n");
                }
                else if (u instanceof Cocinero)
                {
                    Cocinero c = (Cocinero) u;
                    fw.write("COCINERO;" + safe(c.getDocumentoIdentidad()) + ";" + safe(c.getNombre()) + ";"
                            + safe(c.getCorreoElectronico()) + ";" + safe(c.getLogin()) + ";"
                            + safe(c.getPassword()) + ";" + safe(c.getCodigoEmpleado()) + "\n");
                }
                else if (u instanceof Administrador)
                {
                    Administrador a = (Administrador) u;
                    fw.write("ADMIN;" + safe(a.getDocumentoIdentidad()) + ";" + safe(a.getNombre()) + ";"
                            + safe(a.getCorreoElectronico()) + ";" + safe(a.getLogin()) + ";"
                            + safe(a.getPassword()) + "\n");
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void escribirTurnos(List<Usuario> usuarios)
    {
        try (FileWriter fw = crear("turnos.txt"))
        {
            for (Usuario u : usuarios)
            {
                if (u instanceof Empleado)
                {
                    Empleado emp = (Empleado) u;
                    for (Turno t : emp.getTurnos())
                    {
                        fw.write(emp.getLogin() + ";" + t.getDia() + ";" + t.getHoraInicio() + ";" + t.getHoraFin() + "\n");
                    }
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void escribirJuegosConocidosMesero(List<Usuario> usuarios)
    {
        try (FileWriter fw = crear("juegos_conocidos.txt"))
        {
            for (Usuario u : usuarios)
            {
                if (u instanceof Mesero)
                {
                    Mesero m = (Mesero) u;
                    for (JuegoMesa j : m.getJuegosConocidos())
                    {
                        fw.write(m.getLogin() + ";" + safe(j.getNombre()) + "\n");
                    }
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void escribirJuegosFavoritos(List<Usuario> usuarios)
    {
        try (FileWriter fw = crear("juegos_favoritos.txt"))
        {
            for (Usuario u : usuarios)
            {
                List<JuegoMesa> favoritos = null;

                if (u instanceof Cliente)
                {
                    favoritos = ((Cliente) u).consultarJuegosFavoritos();
                }
                else if (u instanceof Empleado)
                {
                    favoritos = ((Empleado) u).consultarJuegosFavoritos();
                }

                if (favoritos != null)
                {
                    for (JuegoMesa j : favoritos)
                    {
                        fw.write(u.getLogin() + ";" + safe(j.getNombre()) + "\n");
                    }
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void escribirSolicitudesTurno(List<Usuario> usuarios)
    {
        try (FileWriter fw = crear("solicitudes_turno.txt"))
        {
            for (Usuario u : usuarios)
            {
                if (u instanceof Empleado)
                {
                    Empleado emp = (Empleado) u;

                    for (SolicitudCambioTurno s : emp.getSolicitudesCambioTurno())
                    {
                        Turno original = s.getTurnoOriginal();
                        Turno propuesto = s.getTurnoPropuesto();
                        String loginDestino = (s.getEmpleadoDestino() != null) ? s.getEmpleadoDestino().getLogin() : "NULL";

                        String diaOrig = (original != null) ? original.getDia().name() : "";
                        String horaIniOrig = (original != null) ? original.getHoraInicio() : "";
                        String horaFinOrig = (original != null) ? original.getHoraFin() : "";

                        String diaProp = (propuesto != null) ? propuesto.getDia().name() : "";
                        String horaIniProp = (propuesto != null) ? propuesto.getHoraInicio() : "";
                        String horaFinProp = (propuesto != null) ? propuesto.getHoraFin() : "";

                        fw.write(emp.getLogin() + ";" + s.getTipoSolicitud() + ";" + toNull(s.getFechaHora()) + ";"
                                + toNull(s.getEstado()) + ";" + loginDestino + ";"
                                + diaOrig + ";" + horaIniOrig + ";" + horaFinOrig + ";"
                                + diaProp + ";" + horaIniProp + ";" + horaFinProp + "\n");
                    }
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void escribirSugerencias()
    {
        try (FileWriter fw = crear("sugerencias.txt"))
        {
            if (sugerenciasCacheadas != null)
            {
                for (SugerenciaPlatillo s : sugerenciasCacheadas)
                {
                    String loginEmpleado = s.getEmpleado() != null
                            ? s.getEmpleado().getLogin()
                            : "NULL";

                    String loginAdmin = s.getRevisadaPor() != null
                            ? s.getRevisadaPor().getLogin()
                            : "NULL";

                    fw.write(loginEmpleado + ";"
                            + safe(s.getNombrePropuesto()) + ";"
                            + s.getCategoriaPropuesta() + ";"
                            + toNull(s.getFechaHora()) + ";"
                            + (s.getEstado() != null ? s.getEstado() : "") + ";"
                            + loginAdmin + "\n");
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    
    }

    private void escribirCafeBasico(Cafe cafe)
    {
        try (FileWriter fw = crear("cafe.txt"))
        {
            if (cafe != null)
            {
                fw.write(cafe.getCapacidadMaximaClientes() + "\n");

                for (Mesa mesa : cafe.getMesas())
                {
                    fw.write(mesa.getNumeroMesa() + "\n");
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void escribirEmpleadosCafe(Cafe cafe)
    {
        try (FileWriter fw = crear("empleados_cafe.txt"))
        {
            if (cafe != null)
            {
                for (Empleado empleado : cafe.getEmpleados())
                {
                    fw.write(empleado.getLogin() + "\n");
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void escribirAdministradorCafe(Cafe cafe)
    {
        try (FileWriter fw = crear("admin_cafe.txt"))
        {
            if (cafe != null && cafe.getAdministrador() != null)
            {
                fw.write(cafe.getAdministrador().getLogin() + "\n");
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void escribirJuegosMesa(Cafe cafe)
    {
        try (FileWriter fw = crear("juegos_mesa.txt"))
        {
            if (cafe != null)
            {
                for (JuegoMesa juego : cafe.getCatalogoJuegos())
                {
                    fw.write(safe(juego.getNombre()) + ";" + juego.getAnioPublicacion() + ";"
                            + safe(juego.getEmpresaMatriz()) + ";" + juego.getMinJugadores() + ";"
                            + juego.getMaxJugadores() + ";" + juego.getEdadMinima() + ";"
                            + juego.isDificil() + ";" + juego.getCategoria() + "\n");
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void escribirCopiasPrestamo(Cafe cafe)
    {
        try (FileWriter fw = crear("copias_prestamo.txt"))
        {
            if (cafe != null)
            {
                for (JuegoMesa juego : cafe.getCatalogoJuegos())
                {
                    for (CopiaJuegoPrestamo copia : juego.getCopias())
                    {
                        fw.write(copia.getEstado() + ";" + copia.isDisponible() + ";" + safe(juego.getNombre()) + "\n");
                    }
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void escribirJuegosVenta(Cafe cafe)
    {
        try (FileWriter fw = crear("juegos_venta.txt"))
        {
            if (cafe != null)
            {
                for (JuegoVenta juego : cafe.getInventarioVenta())
                {
                    fw.write(safe(juego.getNombre()) + ";" + juego.getPrecio() + ";" + juego.isDisponible() + ";"
                            + juego.getStockDisponible() + "\n");
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void escribirMenu(Cafe cafe)
    {
        try (FileWriter fw = crear("menu.txt"))
        {
            if (cafe != null)
            {
                for (ProductoMenu producto : cafe.getMenu())
                {
                    if (producto instanceof Bebida)
                    {
                        Bebida bebida = (Bebida) producto;
                        fw.write("BEBIDA;" + safe(bebida.getNombre()) + ";" + bebida.getPrecio() + ";"
                                + bebida.isDisponible() + ";" + bebida.esAlcoholica() + ";"
                                + bebida.esCaliente() + "\n");
                    }
                    else if (producto instanceof Pasteleria)
                    {
                        Pasteleria pasteleria = (Pasteleria) producto;
                        String alergenos = "";

                        List<Alergeno> lista = pasteleria.obtenerAlergenos();
                        for (int i = 0; i < lista.size(); i++)
                        {
                            if (i > 0)
                            {
                                alergenos += ",";
                            }
                            alergenos += lista.get(i).name();
                        }

                        fw.write("PASTELERIA;" + safe(pasteleria.getNombre()) + ";" + pasteleria.getPrecio() + ";"
                                + pasteleria.isDisponible() + ";" + alergenos + "\n");
                    }
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void escribirReservas(Cafe cafe)
    {
        try (FileWriter fw = crear("reservas.txt"))
        {
            if (cafe != null)
            {
                for (Mesa mesa : cafe.getMesas())
                {
                    for (ReservaMesa reserva : mesa.getReservas())
                    {
                        if (reserva.getCliente() != null)
                        {
                            fw.write(toNull(reserva.getFechaHora()) + ";" + reserva.getNumeroPersonas() + ";"
                                    + reserva.isHayNinosMenores5() + ";" + reserva.isHayMenoresEdad() + ";"
                                    + reserva.getEstadoReserva() + ";" + reserva.getCliente().getLogin() + ";"
                                    + mesa.getNumeroMesa() + "\n");
                        }
                    }
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void escribirPrestamos(Cafe cafe)
    {
        try (FileWriter fwPrestamos = crear("prestamos.txt");
             FileWriter fwDetalles = crear("detalles_prestamo.txt"))
        {
            if (cafe != null)
            {
                List<Prestamo> prestamos = cafe.getPrestamos();

                for (int i = 0; i < prestamos.size(); i++)
                {
                    Prestamo prestamo = prestamos.get(i);

                    if (prestamo.getUsuario() != null)
                    {
                        String mesaTexto = "NULL";
                        if (prestamo.getMesa() != null)
                        {
                            mesaTexto = String.valueOf(prestamo.getMesa().getNumeroMesa());
                        }

                        fwPrestamos.write(toNull(prestamo.getFechaInicio()) + ";" + toNull(prestamo.getFechaFin()) + ";"
                                + prestamo.isAdvertenciaSinMesero() + ";" + prestamo.getUsuario().getLogin() + ";"
                                + mesaTexto + "\n");

                        for (DetallePrestamo detalle : prestamo.getDetalles())
                        {
                            CopiaJuegoPrestamo copia = detalle.getCopiaJuego();
                            if (copia != null && copia.getJuegoMesa() != null)
                            {
                                JuegoMesa juego = copia.getJuegoMesa();
                                int indiceCopia = juego.getCopias().indexOf(copia);

                                fwDetalles.write(i + ";" + safe(juego.getNombre()) + ";" + indiceCopia + ";"
                                        + toNull(detalle.getFechaAsignacion()) + ";"
                                        + toNull(detalle.getFechaDevolucion()) + "\n");
                            }
                        }
                    }
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void escribirVentas(Cafe cafe)
    {
        try (FileWriter fwVentas = crear("ventas.txt");
             FileWriter fwItems = crear("items_venta.txt"))
        {
            if (cafe != null)
            {
                List<Venta> ventas = cafe.getVentas();

                for (int i = 0; i < ventas.size(); i++)
                {
                    Venta venta = ventas.get(i);

                    if (venta.getComprador() != null)
                    {
                        String loginMesero = "NULL";
                        if (venta.getRegistradaPor() != null)
                        {
                            loginMesero = venta.getRegistradaPor().getLogin();
                        }

                        fwVentas.write(toNull(venta.getFechaHora()) + ";" + venta.getTipoVenta() + ";"
                                + venta.getDescuentoAplicado() + ";" + venta.getPropina() + ";"
                                + venta.getComprador().getLogin() + ";" + loginMesero + "\n");

                        for (ItemVenta item : venta.getItems())
                        {
                            if (item.getProducto() != null)
                            {
                                String tipoProducto = "JUEGO_VENTA";
                                if (item.getProducto() instanceof ProductoMenu)
                                {
                                    tipoProducto = "MENU";
                                }

                                fwItems.write(i + ";" + item.getCantidad() + ";" + item.getPrecioUnitario() + ";"
                                        + safe(item.getProducto().getNombre()) + ";" + tipoProducto + "\n");
                            }
                        }
                    }
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    // =========================================================
    // Utilidades privadas
    // =========================================================

    private Usuario usuarioPorLogin(String login)
    {
        if (login == null || usuariosCacheados == null)
        {
            return null;
        }

        for (Usuario usuario : usuariosCacheados)
        {
            if (usuario.getLogin() != null && usuario.getLogin().equals(login))
            {
                return usuario;
            }
        }
        return null;
    }

    private JuegoMesa juegoMesaPorNombre(String nombre)
    {
        if (nombre == null || cafeCacheado == null)
        {
            return null;
        }

        for (JuegoMesa juego : cafeCacheado.getCatalogoJuegos())
        {
            if (juego.getNombre() != null && juego.getNombre().equals(nombre))
            {
                return juego;
            }
        }
        return null;
    }

    private Mesa mesaPorNumero(int numero)
    {
        if (cafeCacheado == null)
        {
            return null;
        }

        for (Mesa mesa : cafeCacheado.getMesas())
        {
            if (mesa.getNumeroMesa() == numero)
            {
                return mesa;
            }
        }
        return null;
    }

    private ProductoMenu productoMenuPorNombre(String nombre)
    {
        if (nombre == null || cafeCacheado == null)
        {
            return null;
        }

        for (ProductoMenu producto : cafeCacheado.getMenu())
        {
            if (producto.getNombre() != null && producto.getNombre().equals(nombre))
            {
                return producto;
            }
        }
        return null;
    }

    private JuegoVenta juegoVentaPorNombre(String nombre)
    {
        if (nombre == null || cafeCacheado == null)
        {
            return null;
        }

        for (JuegoVenta juego : cafeCacheado.getInventarioVenta())
        {
            if (juego.getNombre() != null && juego.getNombre().equals(nombre))
            {
                return juego;
            }
        }
        return null;
    }

    private Turno turnoDeEmpleado(Empleado empleado, String dia, String horaInicio, String horaFin)
    {
        if (empleado == null || dia == null || dia.isEmpty())
        {
            return null;
        }

        for (Turno turno : empleado.getTurnos())
        {
            if (turno.getDia() == DiaSemana.valueOf(dia)
                    && turno.getHoraInicio().equals(horaInicio)
                    && turno.getHoraFin().equals(horaFin))
            {
                return turno;
            }
        }

        return null;
    }

    private String toNull(String texto)
    {
        if (texto == null)
        {
            return "NULL";
        }
        return texto;
    }

    private String fromNull(String texto)
    {
        if ("NULL".equals(texto))
        {
            return null;
        }
        return texto;
    }

    private String safe(String texto)
    {
        if (texto == null)
        {
            return "";
        }
        return texto.replace(";", ",");
    }

    private File path(String nombreArchivo)
    {
        return new File(rutaDatos + File.separator + nombreArchivo);
    }

    private FileWriter crear(String nombreArchivo) throws IOException
    {
        return new FileWriter(path(nombreArchivo), false);
    }

    private BufferedReader abrir(File archivo) throws IOException
    {
        return new BufferedReader(new FileReader(archivo));
    }

    private void crearCarpetaSiNoExiste()
    {
        File carpeta = new File(rutaDatos);
        if (!carpeta.exists())
        {
            carpeta.mkdirs();
        }
    }

    private void invalidarCache()
    {
        cargado = false;
        usuariosCacheados = null;
        cafeCacheado = null;
    }

    public String getRutaDatos()
    {
        return rutaDatos;
    }

    public void setRutaDatos(String rutaDatos)
    {
        this.rutaDatos = rutaDatos;
        crearCarpetaSiNoExiste();
        invalidarCache();
    }
 // ── Torneos ───────────────────────────────────────────────────────────

    public void guardarTorneos(List<Torneo> torneos)
    {
        try (FileWriter fw = crear("torneos.txt"))
        {
            for (Torneo t : torneos)
            {
                String tipo = (t instanceof TorneoCompetitivo) ? "COMPETITIVO" : "AMISTOSO";
                String extra;
                if (t instanceof TorneoCompetitivo)
                {
                    extra = String.valueOf(((TorneoCompetitivo) t).getTarifaEntrada());
                }
                else
                {
                    extra = String.valueOf(((TorneoAmistoso) t).getBonoDescuento());
                }

                fw.write(tipo + "|" + t.getNombre() + "|" + t.getDia()
                         + "|" + t.getJuego().getNombre()
                         + "|" + t.getCuposTotales() + "|" + extra + "\n");

                for (InscripcionTorneo ins : t.getInscripciones())
                {
                    fw.write("INS|" + ins.getUsuario().getLogin()
                             + "|" + ins.getNumeroCupos()
                             + "|" + ins.isUsoCupoFanatico() + "\n");
                }
            }
        }
        catch (IOException e)
        {
            System.err.println("Error guardando torneos: " + e.getMessage());
        }
    }

    public List<Torneo> cargarTorneos(Cafe cafe, List<Usuario> usuarios)
    {
        List<Torneo> torneos = new ArrayList<>();
        File archivo = path("torneos.txt");

        if (!archivo.exists())
        {
            return torneos;
        }

        try (BufferedReader br = abrir(archivo))
        {
            String linea;
            Torneo torneoActual = null;

            while ((linea = br.readLine()) != null)
            {
                String[] partes = linea.split("\\|");

                if (partes[0].equals("AMISTOSO"))
                {
                    JuegoMesa juego = cafe.buscarJuegoMesaPorNombre(partes[3]);
                    DiaSemana dia   = DiaSemana.valueOf(partes[2]);
                    int cupos       = Integer.parseInt(partes[4]);
                    double bono     = Double.parseDouble(partes[5]);
                    torneoActual    = new TorneoAmistoso(partes[1], dia, juego, cupos, bono);
                    torneos.add(torneoActual);
                }
                else if (partes[0].equals("COMPETITIVO"))
                {
                    JuegoMesa juego = cafe.buscarJuegoMesaPorNombre(partes[3]);
                    DiaSemana dia   = DiaSemana.valueOf(partes[2]);
                    int cupos       = Integer.parseInt(partes[4]);
                    double tarifa   = Double.parseDouble(partes[5]);
                    torneoActual    = new TorneoCompetitivo(partes[1], dia, juego, cupos, tarifa);
                    torneos.add(torneoActual);
                }
                else if (partes[0].equals("INS") && torneoActual != null)
                {
                    // Buscar el usuario en la lista recibida como parámetro
                    Usuario usuario = null;
                    for (Usuario u : usuarios)
                    {
                        if (u.getLogin().equals(partes[1]))
                        {
                            usuario = u;
                            break;
                        }
                    }

                    if (usuario != null)
                    {
                        int numeroCupos    = Integer.parseInt(partes[2]);
                        boolean esFanatico = Boolean.parseBoolean(partes[3]);
                        torneoActual.getInscripciones().add(
                            new InscripcionTorneo(usuario, numeroCupos, esFanatico));
                    }
                }
            }
        }
        catch (IOException e)
        {
            System.err.println("Error cargando torneos: " + e.getMessage());
        }

        return torneos;
    }
}