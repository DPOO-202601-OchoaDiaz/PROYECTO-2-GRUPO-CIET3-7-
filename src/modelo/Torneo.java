package modelo;

import java.util.ArrayList;
import java.util.List;

public abstract class Torneo {

    // ── Identidad del torneo ──────────────────────────────────────────────
    private String nombre;
    private DiaSemana dia;
    private JuegoMesa juego;

    // ── Gestión de cupos ──────────────────────────────────────────────────
    private int cuposTotales;
    private int cuposFanaticos;       // 20 % de cuposTotales, redondeado arriba
    private int cuposFanaticosUsados;
    private int cuposRegularesUsados;

    // ── Inscripciones ─────────────────────────────────────────────────────
    private List<InscripcionTorneo> inscripciones;

    // ── Constructor ───────────────────────────────────────────────────────
    public Torneo(String nombre, DiaSemana dia, JuegoMesa juego, int cuposTotales)
    {
        this.nombre               = nombre;
        this.dia                  = dia;
        this.juego                = juego;
        this.cuposTotales         = cuposTotales;
        this.cuposFanaticos       = (int) Math.ceil(cuposTotales * 0.20);
        this.cuposFanaticosUsados = 0;
        this.cuposRegularesUsados = 0;
        this.inscripciones        = new ArrayList<InscripcionTorneo>();
    }

    // ── Consultas de estado ───────────────────────────────────────────────

    public int getCuposFanaticosDisponibles()
    {
        return cuposFanaticos - cuposFanaticosUsados;
    }

    public int getCuposRegularesDisponibles()
    {
        int cuposRegularesTotales = cuposTotales - cuposFanaticos;
        return cuposRegularesTotales - cuposRegularesUsados;
    }

    public int getTotalCuposDisponibles()
    {
        return cuposTotales - cuposFanaticosUsados - cuposRegularesUsados;
    }

    public boolean estaLleno()
    {
        return getTotalCuposDisponibles() == 0;
    }

    public boolean usuarioYaInscrito(Usuario usuario)
    {
        for (InscripcionTorneo ins : inscripciones)
        {
            if (ins.getUsuario().equals(usuario))
            {
                return true;
            }
        }
        return false;
    }

    // ── Validaciones de inscripción ───────────────────────────────────────

    public boolean esFanatico(Usuario usuario)
    {
        if (usuario instanceof Cliente)
        {
            Cliente cliente = (Cliente) usuario;
            return cliente.consultarJuegosFavoritos().contains(juego);
        }
        if (usuario instanceof Empleado)
        {
            Empleado empleado = (Empleado) usuario;
            return empleado.consultarJuegosFavoritos().contains(juego);
        }
        return false;
    }

    public boolean empleadoTieneTurnoEseDia(Empleado empleado)
    {
        for (Turno turno : empleado.getTurnos())
        {
            if (turno.coincideConDia(dia))
            {
                return true;
            }
        }
        return false;
    }

    public boolean puedeInscribirse(Usuario usuario, int numeroCupos)
    {
        // Regla: máximo 3 cupos por usuario en un mismo torneo
        if (numeroCupos < 1 || numeroCupos > 3)
        {
            return false;
        }

        // Regla: no puede inscribirse dos veces
        if (usuarioYaInscrito(usuario))
        {
            return false;
        }

        // Regla: empleado no puede inscribirse si tiene turno ese día
        if (usuario instanceof Empleado)
        {
            Empleado empleado = (Empleado) usuario;
            if (empleadoTieneTurnoEseDia(empleado))
            {
                return false;
            }
        }

        // Regla: deben quedar cupos suficientes en total
        if (getTotalCuposDisponibles() < numeroCupos)
        {
            return false;
        }

        return true;
    }

    // ── Inscripción y desinscripción ──────────────────────────────────────

    public InscripcionTorneo inscribir(Usuario usuario, int numeroCupos)
    {
        if (!puedeInscribirse(usuario, numeroCupos))
        {
            return null;
        }

        boolean esFan = esFanatico(usuario);
        boolean usoCupoFanatico = false;

        // Distribuir los cupos: primero pool de fanáticos si aplica
        if (esFan && getCuposFanaticosDisponibles() >= numeroCupos)
        {
            cuposFanaticosUsados += numeroCupos;
            usoCupoFanatico = true;
        }
        else
        {
            // Fanático que no alcanzó cupo especial, o usuario regular
            cuposRegularesUsados += numeroCupos;
            usoCupoFanatico = false;
        }

        InscripcionTorneo inscripcion = new InscripcionTorneo(usuario, numeroCupos, usoCupoFanatico);
        inscripciones.add(inscripcion);
        return inscripcion;
    }

    public boolean desinscribir(Usuario usuario)
    {
        InscripcionTorneo aEliminar = null;

        for (InscripcionTorneo ins : inscripciones)
        {
            if (ins.getUsuario().equals(usuario))
            {
                aEliminar = ins;
                break;
            }
        }

        if (aEliminar == null)
        {
            return false;   // no estaba inscrito
        }

        // Devolver los cupos al pool correcto
        if (aEliminar.isUsoCupoFanatico())
        {
            cuposFanaticosUsados -= aEliminar.getNumeroCupos();
        }
        else
        {
            cuposRegularesUsados -= aEliminar.getNumeroCupos();
        }

        inscripciones.remove(aEliminar);
        return true;
    }

    // ── Métodos abstractos (cada subtipo los implementa) ─────────────────

    public abstract String obtenerDescripcionPremio();

    public abstract void otorgarPremio(Usuario ganador);

    // ── Getters y setters ─────────────────────────────────────────────────

    public String getNombre()                        { return nombre; }
    public void   setNombre(String nombre)           { this.nombre = nombre; }

    public DiaSemana getDia()                        { return dia; }
    public void      setDia(DiaSemana dia)           { this.dia = dia; }

    public JuegoMesa getJuego()                      { return juego; }
    public void      setJuego(JuegoMesa juego)       { this.juego = juego; }

    public int getCuposTotales()                     { return cuposTotales; }
    public void setCuposTotales(int cuposTotales)    { this.cuposTotales = cuposTotales; }

    public int getCuposFanaticos()                   { return cuposFanaticos; }

    public List<InscripcionTorneo> getInscripciones() { return inscripciones; }
    public void setInscripciones(List<InscripcionTorneo> inscripciones)
    {
        this.inscripciones = inscripciones;
    }
}