package modelo;

public class TorneoCompetitivo extends Torneo {

    private double tarifaEntrada; // costo por participante (clientes)

    public TorneoCompetitivo(String nombre, DiaSemana dia, JuegoMesa juego,
                             int cuposTotales, double tarifaEntrada)
    {
        super(nombre, dia, juego, cuposTotales);
        this.tarifaEntrada = tarifaEntrada;
    }

    // ── Premio ────────────────────────────────────────────────────────────

    /**
     * Calcula el premio monetario como la suma de tarifas pagadas
     * únicamente por clientes. Los empleados no aportan al pozo.
     */
    public double calcularPremioMonetario()
    {
        int cuposClientes = 0;

        for (InscripcionTorneo ins : getInscripciones())
        {
            if (ins.getUsuario() instanceof Cliente)
            {
                cuposClientes += ins.getNumeroCupos();
            }
        }

        return cuposClientes * tarifaEntrada;
    }

    @Override
    public String obtenerDescripcionPremio()
    {
        return "Premio en metálico: $" + (int) calcularPremioMonetario();
    }

    /**
     * Otorga el premio al ganador.
     * Si el ganador es un empleado, no recibe el premio monetario.
     */
    @Override
    public void otorgarPremio(Usuario ganador)
    {
        if (ganador instanceof Empleado)
        {
            // Regla del enunciado: empleados no reciben el premio en metálico
            return;
        }

        // Solo los clientes reciben el premio
        // El pago físico está tercerizado — el sistema solo registra el monto
        if (ganador instanceof Cliente)
        {
            Cliente cliente = (Cliente) ganador;
            cliente.registrarPremioMonetario(calcularPremioMonetario());
        }
    }

    /**
     * Determina si un usuario debe pagar la tarifa de entrada.
     * Los empleados siempre participan gratis en torneos competitivos.
     */
    public double calcularTarifaParaUsuario(Usuario usuario)
    {
        if (usuario instanceof Empleado)
        {
            return 0;
        }
        return tarifaEntrada;
    }

    // ── Getters y setters ─────────────────────────────────────────────────

    public double getTarifaEntrada()                     { return tarifaEntrada; }
    public void   setTarifaEntrada(double tarifaEntrada) { this.tarifaEntrada = tarifaEntrada; }
}