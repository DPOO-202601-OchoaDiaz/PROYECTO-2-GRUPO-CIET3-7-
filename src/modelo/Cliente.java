package modelo;

import java.util.ArrayList;
import java.util.List;

public class Cliente extends Usuario {
	private int puntosFidelidad;
    private List<JuegoMesa> juegosFavoritos;
    private double bonoDescuentoGanado; // 0.0 si no tiene bono activo
    private double premioMonetarioPendiente; // monto ganado en torneo competitivo

    public Cliente(String documentoIdentidad, String nombre, String correoElectronico, String login, String password)
    {
        super(documentoIdentidad, nombre, correoElectronico, login, password);
        this.puntosFidelidad = 0;
        this.juegosFavoritos = new ArrayList<JuegoMesa>();
        this.bonoDescuentoGanado        = 0;
        this.premioMonetarioPendiente   = 0;
    }

    public ReservaMesa crearReserva(Mesa mesa, String fechaHora, int numeroPersonas, boolean hayNinosMenores5, boolean hayMenoresEdad)
    {
        return new ReservaMesa(fechaHora, numeroPersonas, hayNinosMenores5, hayMenoresEdad, this, mesa);
    }

    public Prestamo solicitarPrestamo(Mesa mesa, List<CopiaJuegoPrestamo> copias)
    {
        return new Prestamo(null, null, false, this, mesa);
    }

    public void devolverPrestamo(Prestamo prestamo)
    {
        if (prestamo != null)
        {
            prestamo.finalizarPrestamo();
        }
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

    /**
     * Aplica el bono de descuento ganado en un torneo amistoso a una venta.
     * El bono se consume al aplicarse — no es acumulable.
     * Retorna true si había bono activo y se aplicó, false si no había ninguno.
     */
    public boolean aplicarBonoAVenta(Venta venta)
    {
        if (venta == null || !tieneBonoActivo())
        {
            return false;
        }

        double porcentaje = usarBonoDescuento(); // obtiene y pone en 0
        venta.aplicarDescuentoPorcentaje(porcentaje);
        return true;
    }

    /**
     * Reemplaza el stub anterior.
     * Permite aplicar el bono usando un código de descuento de empleado
     * o el bono ganado en torneo. En esta entrega solo aplica el bono
     * de torneo si el código coincide con "BONO_TORNEO".
     */
    public void aplicarCodigoDescuento(String codigo, Venta venta)
    {
        if (codigo == null || venta == null)
        {
            return;
        }

        if (codigo.equalsIgnoreCase("BONO_TORNEO") && tieneBonoActivo())
        {
            aplicarBonoAVenta(venta);
        }
    }

    public void usarPuntosFidelidad(double valor)
    {
        puntosFidelidad -= (int) valor;
    }

    public void acumularPuntos(double valorCompra)
    {
        puntosFidelidad += (int) valorCompra;
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

    public void registrarPremioMonetario(double monto)
    {
        premioMonetarioPendiente += monto;
    }

    public double getPremioMonetarioPendiente()        { return premioMonetarioPendiente; }
    public void   setPremioMonetarioPendiente(double m){ this.premioMonetarioPendiente = m; }

    public double getBonoDescuentoGanado()             { return bonoDescuentoGanado; }
    public void   setBonoDescuentoGanado(double bono)  { this.bonoDescuentoGanado = bono; }

    public int consultarPuntosFidelidad()
    {
        return puntosFidelidad;
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

    public int getPuntosFidelidad()
    {
        return puntosFidelidad;
    }

    public void setPuntosFidelidad(int puntosFidelidad)
    {
        this.puntosFidelidad = puntosFidelidad;
    }
    
}
