package modelo;

public class TorneoAmistoso extends Torneo {

    private double bonoDescuento; // porcentaje, ej: 0.15 = 15 % de descuento

    public TorneoAmistoso(String nombre, DiaSemana dia, JuegoMesa juego,
                          int cuposTotales, double bonoDescuento)
    {
        super(nombre, dia, juego, cuposTotales);
        this.bonoDescuento = bonoDescuento;
    }

    // ── Premio ────────────────────────────────────────────────────────────

    @Override
    public String obtenerDescripcionPremio()
    {
        int porcentaje = (int) (bonoDescuento * 100);
        return "Bono de descuento del " + porcentaje + "% en tu próxima compra";
    }

    @Override
    public void otorgarPremio(Usuario ganador)
    {
        if (ganador instanceof Cliente)
        {
            Cliente cliente = (Cliente) ganador;
            cliente.recibirBonoDescuento(bonoDescuento);
        }
        else if (ganador instanceof Empleado)
        {
            Empleado empleado = (Empleado) ganador;
            empleado.recibirBonoDescuento(bonoDescuento);
        }
    }

    // ── Getters y setters ─────────────────────────────────────────────────

    public double getBonoDescuento()                       { return bonoDescuento; }
    public void   setBonoDescuento(double bonoDescuento)   { this.bonoDescuento = bonoDescuento; }
}