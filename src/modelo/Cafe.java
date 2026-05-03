package modelo;

import java.util.ArrayList;
import java.util.List;

public class Cafe {
	private int capacidadMaximaClientes;
    private List<Mesa> mesas;
    private List<Empleado> empleados;
    private Administrador administrador;
    private List<JuegoMesa> catalogoJuegos;
    private List<JuegoVenta> inventarioVenta;
    private List<ProductoMenu> menu;
    private List<Venta> ventas;
    private List<Prestamo> prestamos;
    private List<Torneo> torneos;

    public Cafe(int capacidadMaximaClientes)
    {
        this.capacidadMaximaClientes = capacidadMaximaClientes;
        this.mesas = new ArrayList<Mesa>();
        this.empleados = new ArrayList<Empleado>();
        this.catalogoJuegos = new ArrayList<JuegoMesa>();
        this.inventarioVenta = new ArrayList<JuegoVenta>();
        this.menu = new ArrayList<ProductoMenu>();
        this.ventas = new ArrayList<Venta>();
        this.prestamos = new ArrayList<Prestamo>();
        this.torneos = new ArrayList<Torneo>();
    }
    
    public int calcularPersonasActuales() {
        int total = 0;

        for (Mesa mesa : mesas) {
            ReservaMesa reserva = mesa.getReservaActiva();
            if (reserva != null) {
                total += reserva.getNumeroPersonas();
            }
        }

        return total;
    }
    public boolean verificarCapacidadDisponible(int personasNuevas) {
        return (calcularPersonasActuales() + personasNuevas)
                <= capacidadMaximaClientes;
    }

    public Mesa buscarMesaDisponible(int numeroPersonas, String fechaHora)
    {
        for (Mesa mesa : mesas)
        {
            if (mesa.getReservaActiva() == null)
            {
                return mesa;
            }
        }
        return null;
    }

    public void agregarMesa(Mesa mesa)
    {
        if (mesa != null)
        {
            mesas.add(mesa);
        }
    }

    public void agregarEmpleado(Empleado empleado)
    {
        if (empleado != null)
        {
            empleados.add(empleado);
        }
    }

    public void agregarJuegoCatalogo(JuegoMesa juego)
    {
        if (juego != null)
        {
            catalogoJuegos.add(juego);
        }
    }

    public void agregarJuegoVenta(JuegoVenta juego)
    {
        if (juego != null)
        {
            inventarioVenta.add(juego);
        }
    }

    public void agregarProductoMenu(ProductoMenu producto)
    {
        if (producto != null)
        {
            menu.add(producto);
        }
    }

    public List<JuegoMesa> consultarCatalogoJuegos()
    {
        return catalogoJuegos;
    }

    public List<ProductoMenu> consultarMenu()
    {
        return menu;
    }

    public List<CopiaJuegoPrestamo> consultarInventarioPrestamo()
    {
        List<CopiaJuegoPrestamo> copias = new ArrayList<CopiaJuegoPrestamo>();
        for (JuegoMesa juego : catalogoJuegos)
        {
            copias.addAll(juego.getCopias());
        }
        return copias;
    }

    public List<JuegoVenta> consultarInventarioVenta()
    {
        return inventarioVenta;
    }

    public void registrarVenta(Venta venta)
    {
        if (venta != null)
        {
            ventas.add(venta);
        }
    }

    public void registrarPrestamo(Prestamo prestamo)
    {
        if (prestamo != null)
        {
            prestamos.add(prestamo);
        }
    }
    
 // ── Gestión de torneos ────────────────────────────────────────────────

    /**
     * Valida si es posible crear un torneo con el número de participantes
     * indicado. La validación principal: las copias disponibles del juego
     * deben alcanzar para todos los participantes simultáneos.
     *
     * Regla del enunciado: el número de participantes puede superar el
     * máximo de jugadores del juego, siempre que haya copias suficientes.
     * Ejemplo: Catan (max 4 jugadores) con 3 copias disponibles admite
     * hasta 12 participantes en el torneo (4 x 3).
     */
    public boolean validarCreacionTorneo(JuegoMesa juego, int cuposTotales)
    {
        if (juego == null || cuposTotales <= 0)
        {
            return false;
        }

        int copiasDisponibles = juego.obtenerCopiasDisponibles().size();

        if (copiasDisponibles == 0)
        {
            return false;
        }

        int capacidadTotal = juego.getMaxJugadores() * copiasDisponibles;
        return cuposTotales <= capacidadTotal;
    }

    public void agregarTorneo(Torneo torneo)
    {
        if (torneo != null)
        {
            torneos.add(torneo);
        }
    }

    public void eliminarTorneo(Torneo torneo)
    {
        torneos.remove(torneo);
    }

    public List<Torneo> getTorneos()
    {
        return torneos;
    }

    public void setTorneos(List<Torneo> torneos)
    {
        this.torneos = torneos;
    }

    public List<Torneo> consultarTorneosDisponibles()
    {
        List<Torneo> disponibles = new ArrayList<Torneo>();
        for (Torneo torneo : torneos)
        {
            if (!torneo.estaLleno())
            {
                disponibles.add(torneo);
            }
        }
        return disponibles;
    }

    public Torneo buscarTorneoPorNombre(String nombre)
    {
        for (Torneo torneo : torneos)
        {
            if (torneo.getNombre().equalsIgnoreCase(nombre))
            {
                return torneo;
            }
        }
        return null;
    }
    
    public boolean hayClientesPorAtender()
    {
        for (Mesa mesa : mesas)
        {
            if (mesa.getReservaActiva() != null)
            {
                return true;
            }
        }
        return false;
    }

    public Mesero buscarMeseroCapacitado(JuegoMesa juego)
    {
        for (Empleado empleado : empleados)
        {
            if (empleado instanceof Mesero)
            {
                Mesero mesero = (Mesero) empleado;
                if (mesero.puedeExplicarJuego(juego))
                {
                    return mesero;
                }
            }
        }
        return null;
    }

    public List<Venta> generarInformeVentas(String granularidad, TipoVenta tipoVenta)
    {
        List<Venta> resultado = new ArrayList<Venta>();
        for (Venta venta : ventas)
        {
            if (tipoVenta == null || venta.getTipoVenta() == tipoVenta)
            {
                resultado.add(venta);
            }
        }
        return resultado;
    }

    public int getCapacidadMaximaClientes()
    {
        return capacidadMaximaClientes;
    }

    public void setCapacidadMaximaClientes(int capacidadMaximaClientes)
    {
        this.capacidadMaximaClientes = capacidadMaximaClientes;
    }

    public List<Mesa> getMesas()
    {
        return mesas;
    }

    public void setMesas(List<Mesa> mesas)
    {
        this.mesas = mesas;
    }

    public List<Empleado> getEmpleados()
    {
        return empleados;
    }

    public void setEmpleados(List<Empleado> empleados)
    {
        this.empleados = empleados;
    }

    public Administrador getAdministrador()
    {
        return administrador;
    }

    public void setAdministrador(Administrador administrador)
    {
        this.administrador = administrador;
    }

    public List<JuegoMesa> getCatalogoJuegos()
    {
        return catalogoJuegos;
    }

    public void setCatalogoJuegos(List<JuegoMesa> catalogoJuegos)
    {
        this.catalogoJuegos = catalogoJuegos;
    }

    public List<JuegoVenta> getInventarioVenta()
    {
        return inventarioVenta;
    }

    public void setInventarioVenta(List<JuegoVenta> inventarioVenta)
    {
        this.inventarioVenta = inventarioVenta;
    }

    public List<ProductoMenu> getMenu()
    {
        return menu;
    }

    public void setMenu(List<ProductoMenu> menu)
    {
        this.menu = menu;
    }

    public List<Venta> getVentas()
    {
        return ventas;
    }

    public void setVentas(List<Venta> ventas)
    {
        this.ventas = ventas;
    }

    public List<Prestamo> getPrestamos()
    {
        return prestamos;
    }

    public void setPrestamos(List<Prestamo> prestamos)
    {
        this.prestamos = prestamos;
    }
    public JuegoMesa buscarJuegoMesaPorNombre(String nombre) {
        for (JuegoMesa juego : catalogoJuegos) {
            if (juego.getNombre().equalsIgnoreCase(nombre)) {
                return juego;
            }
        }
        return null;
    }
}
