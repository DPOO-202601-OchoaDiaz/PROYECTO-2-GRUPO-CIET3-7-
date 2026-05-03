package modelo;

import java.util.ArrayList;
import java.util.List;

public abstract class Empleado extends Usuario {
	private String codigoEmpleado;
    private boolean enTurno;
    private List<Turno> turnos;
    private List<SolicitudCambioTurno> solicitudesCambioTurno;
    private List<JuegoMesa> juegosFavoritos;
    private double bonoDescuentoGanado; // 0.0 si no tiene bono activo
    public Empleado(String documentoIdentidad, String nombre, String correoElectronico, String login, String password, String codigoEmpleado)
    {
        super(documentoIdentidad, nombre, correoElectronico, login, password);
        this.codigoEmpleado = codigoEmpleado;
        this.enTurno = false;
        this.turnos = new ArrayList<Turno>();
        this.solicitudesCambioTurno = new ArrayList<SolicitudCambioTurno>();
        this.juegosFavoritos = new ArrayList<JuegoMesa>();
        this.bonoDescuentoGanado = 0;
    }

    public List<Turno> consultarTurnos()
    {
        return turnos;
    }

    public SolicitudCambioTurno solicitarCambioTurno(Turno turnoOriginal, TipoSolicitud tipo, Turno turnoPropuesto, Empleado empleadoDestino)
    {
        SolicitudCambioTurno solicitud = new SolicitudCambioTurno(tipo, null, null, this, turnoOriginal, turnoPropuesto, empleadoDestino);
        solicitudesCambioTurno.add(solicitud);
        return solicitud;
    }

    public Prestamo solicitarPrestamoEmpleado(List<CopiaJuegoPrestamo> copias)
    {
        return new Prestamo(null, null, false, this, null);
    }

    public Venta comprarProductos(List<ItemVenta> items, double propina)
    {
        Venta venta = new Venta(null, TipoVenta.CAFETERIA, 0, propina, this, null);
        if (items != null)
        {
            for (ItemVenta item : items)
            {
                venta.agregarItem(item);
            }
        }
        return venta;
    }

    public double obtenerDescuentoEmpleado()
    {
        return 0.20;
        
    }
    
    public void recibirBonoDescuento(double porcentaje)
    {
        // El bono no es acumulable: solo se guarda si no hay uno activo
        if (bonoDescuentoGanado == 0)
        {
            bonoDescuentoGanado = porcentaje;
        }
    }

    public boolean tieneBonoActivo()
    {
        return bonoDescuentoGanado > 0;
    }

    public double usarBonoDescuento()
    {
        double bono = bonoDescuentoGanado;
        bonoDescuentoGanado = 0; // se consume al usarse
        return bono;
    }

    public double getBonoDescuentoGanado()             { return bonoDescuentoGanado; }
    public void   setBonoDescuentoGanado(double bono)  { this.bonoDescuentoGanado = bono; }
    
    /**
     * Aplica el bono de descuento ganado en un torneo amistoso a una venta.
     * El bono se consume al aplicarse — no es acumulable.
     */
    public boolean aplicarBonoAVenta(Venta venta)
    {
        if (venta == null || !tieneBonoActivo())
        {
            return false;
        }

        double porcentaje = usarBonoDescuento();
        venta.aplicarDescuentoPorcentaje(porcentaje);
        return true;
    }

    public SugerenciaPlatillo crearSugerenciaPlatillo(String nombre, CategoriaPropuesta categoria)
    {
        return new SugerenciaPlatillo(nombre, categoria, null, null, this, null);
    }

    public List<Venta> consultarHistorialCompras()
    {
        return new ArrayList<Venta>();
    }

    public void agregarJuegoFavorito(JuegoMesa juego)
    {
        if (juego != null && !juegosFavoritos.contains(juego))
        {
            juegosFavoritos.add(juego);
        }
    }

    public void eliminarJuegoFavorito(JuegoMesa juego)
    {
        juegosFavoritos.remove(juego);
    }

    public List<JuegoMesa> consultarJuegosFavoritos()
    {
        return juegosFavoritos;
    }

    public String getCodigoEmpleado()
    {
        return codigoEmpleado;
    }

    public void setCodigoEmpleado(String codigoEmpleado)
    {
        this.codigoEmpleado = codigoEmpleado;
    }

    public boolean isEnTurno()
    {
        return enTurno;
    }

    public void setEnTurno(boolean enTurno)
    {
        this.enTurno = enTurno;
    }

    public List<Turno> getTurnos()
    {
        return turnos;
    }

    public void setTurnos(List<Turno> turnos)
    {
        this.turnos = turnos;
    }

    public List<SolicitudCambioTurno> getSolicitudesCambioTurno()
    {
        return solicitudesCambioTurno;
    }

    public void setSolicitudesCambioTurno(List<SolicitudCambioTurno> solicitudesCambioTurno)
    {
        this.solicitudesCambioTurno = solicitudesCambioTurno;
    }
}
