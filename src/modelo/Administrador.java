package modelo;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Administrador extends Usuario {
	public Administrador(String documentoIdentidad, String nombre, String correoElectronico, String login, String password)
    {
        super(documentoIdentidad, nombre, correoElectronico, login, password);
    }

    public List<CopiaJuegoPrestamo> consultarInventarioPrestamo()
    {
        return new ArrayList<CopiaJuegoPrestamo>();
    }

    public List<CopiaJuegoPrestamo> consultarInventarioPrestamo(Cafe cafe) {
        return cafe.consultarInventarioPrestamo();
    }

    public List<Prestamo> consultarHistorialPrestamos()
    {
        return new ArrayList<Prestamo>();
    }

    public void moverJuegoVentaAPrestamo(JuegoVenta juegoVenta, int cantidad, Cafe cafe) {
        if (juegoVenta == null || cantidad <= 0) {
            return;
        }

        if (juegoVenta.hayStock(cantidad)) {
            JuegoMesa juegoMesa = cafe.buscarJuegoMesaPorNombre(juegoVenta.getNombre());

            if (juegoMesa != null) {
                for (int i = 0; i < cantidad; i++) {
                    juegoMesa.agregarCopia(
                        new CopiaJuegoPrestamo(
                            EstadoJuego.BUENO,
                            true,
                            juegoMesa
                        )
                    );
                }

                juegoVenta.reducirStock(cantidad);
            }
        }
    }

    public void reabastecerJuegoVenta(JuegoVenta juego, int cantidad)
    {
        if (juego != null)
        {
            juego.aumentarStock(cantidad);
        }
    }

    public void reabastecerCopiaPrestamo(JuegoMesa juego, int cantidad)
    {
        if (juego == null || cantidad <= 0)
        {
            return;
        }

        for (int i = 0; i < cantidad; i++)
        {
            CopiaJuegoPrestamo nuevaCopia = new CopiaJuegoPrestamo(EstadoJuego.NUEVO, true, juego);
            juego.agregarCopia(nuevaCopia);
        }
    }

    public void repararJuego(CopiaJuegoPrestamo copia, JuegoVenta reemplazo)
    {
        if (copia != null)
        {
            copia.cambiarEstado(EstadoJuego.BUENO);
        }
    }

    public void marcarJuegoDesaparecido(CopiaJuegoPrestamo copia)
    {
        if (copia != null)
        {
            copia.marcarDesaparecido();
        }
    }

    public void agregarProductoMenu(ProductoMenu producto)
    {
        // Sin referencia al Cafe no es posible agregar al menú.
        // Usar la versión con Cafe: agregarProductoMenu(producto, cafe).
    }

    public void agregarProductoMenu(ProductoMenu producto, Cafe cafe)
    {
        if (producto == null || cafe == null)
        {
            return;
        }

        cafe.agregarProductoMenu(producto);
    }

    public void aprobarSugerencia(SugerenciaPlatillo sugerencia)
    {
        if (sugerencia != null)
        {
            sugerencia.aprobar();
        }
    }

    public void rechazarSugerencia(SugerenciaPlatillo sugerencia)
    {
        if (sugerencia != null)
        {
            sugerencia.rechazar();
        }
    }

    public void asignarTurno(Empleado empleado, Turno turno)
    {
        if (empleado != null && turno != null)
        {
            empleado.getTurnos().add(turno);
        }
    }

    public void modificarTurno(Empleado empleado, Turno turnoAnterior, Turno nuevoTurno)
    {
        if (empleado != null && turnoAnterior != null && nuevoTurno != null)
        {
            List<Turno> turnos = empleado.getTurnos();
            int posicion = turnos.indexOf(turnoAnterior);
            if (posicion != -1)
            {
                turnos.set(posicion, nuevoTurno);
            }
        }
    }

    public void aprobarSolicitudCambio(SolicitudCambioTurno solicitud)
    {
        if (solicitud != null)
        {
            solicitud.aprobar();
        }
    }

    public void rechazarSolicitudCambio(SolicitudCambioTurno solicitud)
    {
        if (solicitud != null)
        {
            solicitud.rechazar();
        }
    }

    public List<Venta> generarInformeVentas(String granularidad, TipoVenta tipoVenta)
    {
        // Sin referencia al Cafe no hay ventas que filtrar.
        // Usar la versión con Cafe: generarInformeVentas(granularidad, tipoVenta, cafe).
        return new ArrayList<Venta>();
    }

    public List<Venta> generarInformeVentas(String granularidad, TipoVenta tipoVenta, Cafe cafe)
    {
        if (cafe == null)
        {
            return new ArrayList<Venta>();
        }

        List<Venta> todasLasVentas = cafe.generarInformeVentas(null, tipoVenta);

        if (granularidad == null || granularidad.equalsIgnoreCase("GENERAL"))
        {
            return todasLasVentas;
        }

        LocalDate hoy = LocalDate.now();
        List<Venta> resultado = new ArrayList<Venta>();

        for (Venta venta : todasLasVentas)
        {
            String fechaHora = venta.getFechaHora();

            if (fechaHora == null || fechaHora.isEmpty())
            {
                continue;
            }

            // fechaHora tiene formato LocalDateTime.toString(): "2026-04-05T19:00:00.123..."
            // Extraemos solo la parte de fecha (los primeros 10 caracteres: yyyy-MM-dd)
            if (fechaHora.length() < 10)
            {
                continue;
            }

            String partesFecha = fechaHora.substring(0, 10); // "yyyy-MM-dd"

            try
            {
                LocalDate fechaVenta = LocalDate.parse(partesFecha);

                if (granularidad.equalsIgnoreCase("DIARIA"))
                {
                    if (fechaVenta.equals(hoy))
                    {
                        resultado.add(venta);
                    }
                }
                else if (granularidad.equalsIgnoreCase("SEMANAL"))
                {
                    WeekFields semana = WeekFields.of(Locale.getDefault());
                    boolean mismoAnio = fechaVenta.getYear() == hoy.getYear();
                    boolean mismaSemana = fechaVenta.get(semana.weekOfWeekBasedYear())
                            == hoy.get(semana.weekOfWeekBasedYear());

                    if (mismoAnio && mismaSemana)
                    {
                        resultado.add(venta);
                    }
                }
                else if (granularidad.equalsIgnoreCase("MENSUAL"))
                {
                    boolean mismoAnio = fechaVenta.getYear() == hoy.getYear();
                    boolean mismoMes  = fechaVenta.getMonthValue() == hoy.getMonthValue();

                    if (mismoAnio && mismoMes)
                    {
                        resultado.add(venta);
                    }
                }
            }
            catch (Exception e)
            {
                // Si la fecha no tiene el formato esperado, se omite esa venta.
            }
        }

        return resultado;
    }

    public List<Venta> consultarVentasPorRubro(String granularidad)
    {
        // Sin referencia al Cafe no hay ventas.
        // Usar generarInformeVentas(granularidad, null, cafe) para todas las ventas sin filtro de tipo.
        return new ArrayList<Venta>();
    }

    public List<Venta> consultarVentasPorRubro(String granularidad, Cafe cafe)
    {
        return generarInformeVentas(granularidad, null, cafe);
    }
    
 // ── Gestión de torneos ────────────────────────────────────────────────

    /**
     * Crea un torneo amistoso y lo registra en el café.
     * Retorna null si los cupos superan la capacidad real del juego
     * según las copias disponibles.
     *
     * @param nombre        nombre del torneo
     * @param dia           día de la semana en que se realiza
     * @param juego         juego que se usará
     * @param cuposTotales  número de participantes
     * @param bonoDescuento porcentaje de descuento para el ganador (ej: 0.15 = 15%)
     * @param cafe          referencia al café para validar y registrar
     */
    public Torneo crearTorneoAmistoso(String nombre, DiaSemana dia, JuegoMesa juego,
                                      int cuposTotales, double bonoDescuento, Cafe cafe)
    {
        if (!cafe.validarCreacionTorneo(juego, cuposTotales))
        {
            return null;
        }

        TorneoAmistoso torneo = new TorneoAmistoso(nombre, dia, juego,
                                                   cuposTotales, bonoDescuento);
        cafe.agregarTorneo(torneo);
        return torneo;
    }

    /**
     * Crea un torneo competitivo y lo registra en el café.
     * Retorna null si los cupos superan la capacidad real del juego
     * según las copias disponibles.
     *
     * @param nombre        nombre del torneo
     * @param dia           día de la semana en que se realiza
     * @param juego         juego que se usará
     * @param cuposTotales  número de participantes
     * @param tarifaEntrada costo de inscripción por participante (clientes)
     * @param cafe          referencia al café para validar y registrar
     */
    public Torneo crearTorneoCompetitivo(String nombre, DiaSemana dia, JuegoMesa juego,
                                         int cuposTotales, double tarifaEntrada, Cafe cafe)
    {
        if (!cafe.validarCreacionTorneo(juego, cuposTotales))
        {
            return null;
        }

        TorneoCompetitivo torneo = new TorneoCompetitivo(nombre, dia, juego,
                                                         cuposTotales, tarifaEntrada);
        cafe.agregarTorneo(torneo);
        return torneo;
    }

    /**
     * Elimina un torneo del café.
     * Útil si el administrador decide cancelarlo.
     */
    public void cancelarTorneo(Torneo torneo, Cafe cafe)
    {
        if (torneo != null && cafe != null)
        {
            cafe.eliminarTorneo(torneo);
        }
    }
}