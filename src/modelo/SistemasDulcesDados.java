package modelo;

import java.util.ArrayList;
import java.util.List;
import modelo.InscripcionTorneo;
import modelo.Torneo;
import modelo.DiaSemana;

import persistencia.PersistenciaSistema;

public class SistemasDulcesDados
{
    private Cafe cafe;
    private List<Usuario> usuarios;
    private PersistenciaSistema persistencia;
    private Usuario sesionActual;
    private List<SugerenciaPlatillo> sugerencias;

    public SistemasDulcesDados(Cafe cafe, PersistenciaSistema persistencia)
    {
        this.cafe = cafe;
        this.usuarios = new ArrayList<Usuario>();
        this.persistencia = persistencia;
        this.sesionActual = null;
        this.sugerencias = new ArrayList<>();
    }

    public void inicializarSistema()
    {
        cargarDatos();
        asegurarEstructuraMinima();
    }

    public void cargarDatos()
    {
        if (persistencia == null)
        {
            asegurarEstructuraMinima();
            return;
        }

        List<Usuario> usuariosCargados = persistencia.cargarUsuarios();
        Cafe cafeCargado = persistencia.cargarCafe();
        List<SugerenciaPlatillo> sugsGuardadas = persistencia.cargarSugerencias();
        if (sugsGuardadas != null) {
            this.sugerencias = sugsGuardadas;
        }

        if (usuariosCargados != null)
        {
            usuarios = usuariosCargados;
        }
        else
        {
            usuarios = new ArrayList<Usuario>();
        }

        if (cafeCargado != null)
        {
            cafe = cafeCargado;
        }

        asegurarEstructuraMinima();
    }

    public void guardarDatos()
    {
        if (persistencia != null)
        {
            persistencia.guardarUsuarios(usuarios);
            persistencia.guardarCafe(cafe);
            persistencia.guardarTorneos(cafe.getTorneos());
            List<Torneo> torneosGuardados = persistencia.cargarTorneos(cafe, usuarios);
            cafe.setTorneos(torneosGuardados);
        }
    }

    private void asegurarEstructuraMinima()
    {
        if (cafe == null)
        {
            cafe = new Cafe(50);
        }

        if (usuarios == null)
        {
            usuarios = new ArrayList<Usuario>();
        }
    }

    public Usuario autenticarUsuario(String login, String password)
    {
        if (login == null || password == null)
        {
            return null;
        }

        Usuario usuario = buscarUsuarioPorLogin(login);

        if (usuario != null && usuario.validarPassword(password))
        {
            sesionActual = usuario;
            return usuario;
        }

        return null;
    }

    public void cerrarSesion()
    {
        sesionActual = null;
    }

    public boolean haySesionIniciada()
    {
        return sesionActual != null;
    }

    public Usuario buscarUsuarioPorLogin(String login)
    {
        if (login == null || usuarios == null)
        {
            return null;
        }

        for (Usuario usuario : usuarios)
        {
            if (usuario.getLogin() != null && usuario.getLogin().equals(login))
            {
                return usuario;
            }
        }
        return null;
    }

    public boolean agregarUsuario(Usuario usuario)
    {
        if (usuario == null)
        {
            return false;
        }

        if (usuario.getLogin() == null || usuario.getLogin().trim().isEmpty())
        {
            return false;
        }

        if (buscarUsuarioPorLogin(usuario.getLogin()) != null)
        {
            return false;
        }

        usuarios.add(usuario);
        return true;
    }

    public boolean eliminarUsuarioPorLogin(String login)
    {
        Usuario usuario = buscarUsuarioPorLogin(login);

        if (usuario == null)
        {
            return false;
        }

        if (sesionActual != null && sesionActual == usuario)
        {
            sesionActual = null;
        }

        return usuarios.remove(usuario);
    }

    public Cafe obtenerCafe()
    {
        return cafe;
    }

    public Cafe getCafe()
    {
        return cafe;
    }

    public void setCafe(Cafe cafe)
    {
        if (cafe != null)
        {
            this.cafe = cafe;
        }
    }

    public List<Usuario> getUsuarios()
    {
        return usuarios;
    }

    public void setUsuarios(List<Usuario> usuarios)
    {
        if (usuarios != null)
        {
            this.usuarios = usuarios;
        }
        else
        {
            this.usuarios = new ArrayList<Usuario>();
        }
    }

    public PersistenciaSistema getPersistencia()
    {
        return persistencia;
    }

    public void setPersistencia(PersistenciaSistema persistencia)
    {
        this.persistencia = persistencia;
    }

    public Usuario getSesionActual()
    {
        return sesionActual;
    }

    public void setSesionActual(Usuario sesionActual)
    {
        this.sesionActual = sesionActual;
    }
    public void agregarSugerencia(SugerenciaPlatillo sugerencia) {
        if (sugerencia != null) {
            sugerencias.add(sugerencia);
        }
    }

    public List<SugerenciaPlatillo> getSugerencias() {
        return sugerencias;
    }
 // ── Torneos ───────────────────────────────────────────────────────────

    public Torneo crearTorneoAmistoso(String nombre, DiaSemana dia,
                                      JuegoMesa juego, int cupos, double bono)
    {
        if (!(sesionActual instanceof Administrador))
        {
            return null; // solo el administrador puede crear torneos
        }
        Administrador admin = (Administrador) sesionActual;
        return admin.crearTorneoAmistoso(nombre, dia, juego, cupos, bono, cafe);
    }

    public Torneo crearTorneoCompetitivo(String nombre, DiaSemana dia,
                                         JuegoMesa juego, int cupos, double tarifa)
    {
        if (!(sesionActual instanceof Administrador))
        {
            return null;
        }
        Administrador admin = (Administrador) sesionActual;
        return admin.crearTorneoCompetitivo(nombre, dia, juego, cupos, tarifa, cafe);
    }

    public List<Torneo> consultarTorneos()
    {
        return cafe.getTorneos();
    }

    public List<Torneo> consultarTorneosDisponibles()
    {
        return cafe.consultarTorneosDisponibles();
    }

    public InscripcionTorneo inscribirEnTorneo(Torneo torneo, int numeroCupos)
    {
        if (sesionActual == null || torneo == null)
        {
            return null;
        }
        return torneo.inscribir(sesionActual, numeroCupos);
    }

    public boolean desinscribirDeTorneo(Torneo torneo)
    {
        if (sesionActual == null || torneo == null)
        {
            return false;
        }
        return torneo.desinscribir(sesionActual);
    }

    public boolean aplicarBonoATorneo(Venta venta)
    {
        if (sesionActual instanceof Cliente)
        {
            return ((Cliente) sesionActual).aplicarBonoAVenta(venta);
        }
        if (sesionActual instanceof Empleado)
        {
            return ((Empleado) sesionActual).aplicarBonoAVenta(venta);
        }
        return false;
    }
}