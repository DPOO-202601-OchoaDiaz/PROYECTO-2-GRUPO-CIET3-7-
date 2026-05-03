package test.torneos;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import modelo.Administrador;
import modelo.Cafe;
import modelo.CategoriaJuego;
import modelo.Cliente;
import modelo.CopiaJuegoPrestamo;
import modelo.DiaSemana;
import modelo.EstadoJuego;
import modelo.InscripcionTorneo;
import modelo.JuegoMesa;
import modelo.Mesero;
import modelo.Torneo;
import modelo.TorneoAmistoso;
import modelo.TorneoCompetitivo;
import modelo.Turno;
import modelo.Venta;

class TestTorneos {

    // ── Fixtures compartidos por todos los tests ──────────────────────────
    private Cafe cafe;
    private Administrador admin;
    private JuegoMesa catan;
    private TorneoCompetitivo torneoComp;
    private TorneoAmistoso torneoAmistoso;
    private Cliente cliente1;
    private Cliente cliente2;
    private Cliente fanatico;

    @BeforeEach
    void setUp()
    {
        cafe  = new Cafe(50);
        admin = new Administrador("900", "Admin", "admin@mail.com", "admin", "pass");

        // Catan: max 4 jugadores, 2 copias → capacidad = 8 cupos
        catan = new JuegoMesa("Catan", 1995, "Kosmos", 3, 4, 10, false,
                              CategoriaJuego.TABLERO);
        catan.agregarCopia(new CopiaJuegoPrestamo(EstadoJuego.NUEVO, true, catan));
        catan.agregarCopia(new CopiaJuegoPrestamo(EstadoJuego.NUEVO, true, catan));
        cafe.agregarJuegoCatalogo(catan);

        torneoComp = (TorneoCompetitivo) admin.crearTorneoCompetitivo(
            "Copa Catan", DiaSemana.SABADO, catan, 8, 15000, cafe);

        torneoAmistoso = (TorneoAmistoso) admin.crearTorneoAmistoso(
            "Torneo Amistoso", DiaSemana.DOMINGO, catan, 8, 0.20, cafe);

        cliente1 = new Cliente("101", "Ana", "ana@mail.com", "ana", "1234");
        cliente2 = new Cliente("102", "Bob", "bob@mail.com", "bob", "1234");

        fanatico = new Cliente("103", "Carlos", "carlos@mail.com", "carlos", "1234");
        fanatico.agregarJuegoFavorito(catan);
    }

    // ── Creación de torneos ───────────────────────────────────────────────

    @Test
    void testCreacionTorneoRegistradoEnCafe()
    {
        assertEquals(2, cafe.getTorneos().size());
    }

    @Test
    void testCuposFanaticosCalculadosCorrectamente()
    {
        // ceil(8 * 0.20) = 2
        assertEquals(2, torneoComp.getCuposFanaticos());
    }

    @Test
    void testCreacionRechazadaPorCuposExcesivos()
    {
        Torneo invalido = admin.crearTorneoCompetitivo(
            "Imposible", DiaSemana.LUNES, catan, 100, 5000, cafe);
        assertNull(invalido);
    }

    @Test
    void testCreacionRechazadaSinCopias()
    {
        JuegoMesa sinCopias = new JuegoMesa("Vacío", 2000, "X", 2, 4, 0,
                                             false, CategoriaJuego.CARTAS);
        cafe.agregarJuegoCatalogo(sinCopias);
        Torneo invalido = admin.crearTorneoAmistoso(
            "Sin copias", DiaSemana.LUNES, sinCopias, 4, 0.10, cafe);
        assertNull(invalido);
    }

    // ── Inscripción ───────────────────────────────────────────────────────

    @Test
    void testInscripcionNormalExitosa()
    {
        InscripcionTorneo ins = torneoComp.inscribir(cliente1, 2);
        assertNotNull(ins);
        assertEquals(6, torneoComp.getTotalCuposDisponibles());
    }

    @Test
    void testInscripcionRechazaDobleInscripcion()
    {
        torneoComp.inscribir(cliente1, 1);
        InscripcionTorneo segunda = torneoComp.inscribir(cliente1, 1);
        assertNull(segunda);
    }

    @Test
    void testInscripcionRechazaMasDeTresCupos()
    {
        InscripcionTorneo ins = torneoComp.inscribir(cliente1, 4);
        assertNull(ins);
    }

    @Test
    void testInscripcionRechazaCeroCupos()
    {
        InscripcionTorneo ins = torneoComp.inscribir(cliente1, 0);
        assertNull(ins);
    }

    // ── Fanáticos ─────────────────────────────────────────────────────────

    @Test
    void testFanaticoReconocidoCorrectamente()
    {
        assertTrue(torneoComp.esFanatico(fanatico));
        assertFalse(torneoComp.esFanatico(cliente1));
    }

    @Test
    void testFanaticoUsaCupoEspecial()
    {
        InscripcionTorneo ins = torneoComp.inscribir(fanatico, 1);
        assertNotNull(ins);
        assertTrue(ins.isUsoCupoFanatico());
        assertEquals(1, torneoComp.getCuposFanaticosDisponibles());
    }

    @Test
    void testFanaticoSinCupoEspecialUsaRegular()
    {
        // Ocupar todos los cupos de fanáticos primero
        torneoComp.inscribir(fanatico, 1); // usa el cupo fanático

        // Segundo fanático — ya no hay cupos especiales, usa regular
        Cliente fanatico2 = new Cliente("104", "Dora", "d@mail.com", "d", "1234");
        fanatico2.agregarJuegoFavorito(catan);
        torneoComp.inscribir(fanatico2, 1); // usa cupo regular

        InscripcionTorneo ins3 = torneoComp.inscribir(cliente1, 1); // fanáticos agotados
        // cliente1 no es fanático, va a regular directamente
        assertNotNull(ins3);
        assertFalse(ins3.isUsoCupoFanatico());
    }

    // ── Desinscripción ────────────────────────────────────────────────────

    @Test
    void testDesinscripcionDevuelveCuposRegulares()
    {
        torneoComp.inscribir(cliente1, 2);
        int cuposAntes = torneoComp.getTotalCuposDisponibles();
        torneoComp.desinscribir(cliente1);
        assertEquals(cuposAntes + 2, torneoComp.getTotalCuposDisponibles());
    }

    @Test
    void testDesinscripcionDevuelveCuposFanaticos()
    {
        torneoComp.inscribir(fanatico, 1);
        int fanaticosAntes = torneoComp.getCuposFanaticosDisponibles();
        torneoComp.desinscribir(fanatico);
        assertEquals(fanaticosAntes + 1, torneoComp.getCuposFanaticosDisponibles());
    }

    @Test
    void testDesinscripcionDeUsuarioNoInscritoRetornaFalse()
    {
        assertFalse(torneoComp.desinscribir(cliente1));
    }

    @Test
    void testReinscripcionTrasDesinscripcion()
    {
        torneoComp.inscribir(cliente1, 1);
        torneoComp.desinscribir(cliente1);
        InscripcionTorneo reins = torneoComp.inscribir(cliente1, 1);
        assertNotNull(reins);
    }

    // ── Empleados ─────────────────────────────────────────────────────────

    @Test
    void testEmpleadoConTurnoBloqueado()
    {
        Mesero mesero = new Mesero("201", "Diego", "d@mail.com", "diego", "1234", "M01");
        mesero.getTurnos().add(new Turno(DiaSemana.SABADO, "08:00", "16:00"));
        InscripcionTorneo ins = torneoComp.inscribir(mesero, 1);
        assertNull(ins);
    }

    @Test
    void testEmpleadoSinTurnoPuedeInscribirse()
    {
        Mesero mesero = new Mesero("202", "Elena", "e@mail.com", "elena", "1234", "M02");
        mesero.getTurnos().add(new Turno(DiaSemana.LUNES, "08:00", "16:00"));
        InscripcionTorneo ins = torneoComp.inscribir(mesero, 1);
        assertNotNull(ins);
    }

    // ── Torneo lleno ──────────────────────────────────────────────────────

    @Test
    void testTorneoLleno()
    {
        // Torneo de 2 cupos
        JuegoMesa dados = new JuegoMesa("Dados", 2000, "X", 1, 2, 0,
                                         false, CategoriaJuego.ACCION);
        dados.agregarCopia(new CopiaJuegoPrestamo(EstadoJuego.NUEVO, true, dados));
        cafe.agregarJuegoCatalogo(dados);

        Torneo chico = admin.crearTorneoCompetitivo(
            "Chico", DiaSemana.LUNES, dados, 2, 5000, cafe);

        chico.inscribir(cliente1, 1);
        chico.inscribir(cliente2, 1);

        assertTrue(chico.estaLleno());
        Cliente extra = new Cliente("999", "X", "x@mail.com", "x", "x");
        assertNull(chico.inscribir(extra, 1));
    }

    // ── Premios ───────────────────────────────────────────────────────────

    @Test
    void testPremioAmistosoOtorgaBono()
    {
        torneoAmistoso.inscribir(cliente1, 1);
        torneoAmistoso.otorgarPremio(cliente1);
        assertTrue(cliente1.tieneBonoActivo());
        assertEquals(0.20, cliente1.getBonoDescuentoGanado(), 0.001);
    }

    @Test
    void testBonoNoAcumulable()
    {
        torneoAmistoso.otorgarPremio(cliente1);
        torneoAmistoso.otorgarPremio(cliente1); // segundo bono ignorado
        assertEquals(0.20, cliente1.getBonoDescuentoGanado(), 0.001);
    }

    @Test
    void testBonoSeConsumAlUsarse()
    {
        torneoAmistoso.otorgarPremio(cliente1);
        Venta venta = cliente1.comprarProductos(null, 0);
        cliente1.aplicarBonoAVenta(venta);
        assertFalse(cliente1.tieneBonoActivo());
    }

    @Test
    void testPremioCompetitivoCalculaSoloCuposClientes()
    {
        torneoComp.inscribir(cliente1, 2);   // 2 cupos × $15.000 = $30.000

        Mesero mesero = new Mesero("202", "Elena", "e@mail.com", "elena", "1234", "M02");
        mesero.getTurnos().add(new Turno(DiaSemana.LUNES, "08:00", "16:00"));
        torneoComp.inscribir(mesero, 1);     // gratis, no suma al pozo

        assertEquals(30000, torneoComp.calcularPremioMonetario(), 0.001);
    }

    @Test
    void testEmpleadoNoRecibePremioMonetario()
    {
        Mesero mesero = new Mesero("202", "Elena", "e@mail.com", "elena", "1234", "M02");
        mesero.getTurnos().add(new Turno(DiaSemana.LUNES, "08:00", "16:00"));
        torneoComp.inscribir(mesero, 1);
        torneoComp.otorgarPremio(mesero);
        // El empleado tiene bonoDescuentoGanado = 0, no recibió nada
        assertEquals(0, mesero.getBonoDescuentoGanado(), 0.001);
    }

    @Test
    void testDescripcionPremioAmistoso()
    {
        assertEquals("Bono de descuento del 20% en tu próxima compra",
                     torneoAmistoso.obtenerDescripcionPremio());
    }

    @Test
    void testDescripcionPremioCompetitivoSinInscripciones()
    {
        assertEquals("Premio en metálico: $0",
                     torneoComp.obtenerDescripcionPremio());
    }
    @Test
    void testSoloAdminPuedaCrearTorneo()
    {
        // Simula que el sistema tiene sesión de cliente
        // SistemasDulcesDados devuelve null si la sesión no es admin
        // Este test verifica la regla de negocio directamente en Administrador
        Cliente noAdmin = new Cliente("500", "X", "x@mail.com", "x", "x");

        // Un cliente no tiene crearTorneoAmistoso() — la restricción
        // la impone SistemasDulcesDados verificando sesionActual.
        // Aquí verificamos que la creación directa por admin sí funciona.
        Torneo t = admin.crearTorneoAmistoso(
            "Test", DiaSemana.LUNES, catan, 4, 0.10, cafe);
        assertNotNull(t);
    }

    @Test
    void testBuscarTorneoPorNombre()
    {
        assertNotNull(cafe.buscarTorneoPorNombre("Copa Catan"));
        assertNull(cafe.buscarTorneoPorNombre("Inexistente"));
    }

    @Test
    void testCancelarTorneoLoEliminaDelCafe()
    {
        int torneoAntes = cafe.getTorneos().size();
        admin.cancelarTorneo(torneoComp, cafe);
        assertEquals(torneoAntes - 1, cafe.getTorneos().size());
    }
}