package ui;

import java.util.ArrayList;
import java.util.List;

import modelo.Administrador;
import modelo.Cafe;
import modelo.CategoriaJuego;
import modelo.Cliente;
import modelo.CopiaJuegoPrestamo;
import modelo.DiaSemana;
import modelo.Empleado;
import modelo.EstadoJuego;
import modelo.InscripcionTorneo;
import modelo.JuegoMesa;
import modelo.Mesero;
import modelo.Torneo;
import modelo.TorneoCompetitivo;
import modelo.Turno;

public class PruebaTorneos
{
    public static void main(String[] args)
    {
        System.out.println("=================================");
        System.out.println("       PRUEBA DE TORNEOS");
        System.out.println("=================================");

        // ── Escenario base ────────────────────────────────────────────────
        Cafe cafe = new Cafe(50);
        Administrador admin = new Administrador("900", "Admin", "admin@mail.com",
                                                "admin", "admin123");

        JuegoMesa catan = new JuegoMesa("Catan", 1995, "Kosmos",
                                        3, 4, 10, false, CategoriaJuego.TABLERO);
        catan.agregarCopia(new CopiaJuegoPrestamo(EstadoJuego.NUEVO, true, catan));
        catan.agregarCopia(new CopiaJuegoPrestamo(EstadoJuego.NUEVO, true, catan));
        cafe.agregarJuegoCatalogo(catan);

        // Catan: max 4 jugadores, 2 copias → capacidad = 8 cupos
        Torneo torneo = admin.crearTorneoCompetitivo(
            "Gran Torneo Catan", DiaSemana.SABADO, catan, 8, 15000, cafe);

        System.out.println("\n--- Estado inicial del torneo ---");
        System.out.println("Cupos totales:    " + torneo.getCuposTotales());
        System.out.println("Cupos fanáticos:  " + torneo.getCuposFanaticos()); // ceil(8*0.20)=2
        System.out.println("Cupos disponibles:" + torneo.getTotalCuposDisponibles());
        verificar("¿Cupos fanáticos = 2?", torneo.getCuposFanaticos() == 2);

        // ── CASO 1: Creación inválida (cupos > capacidad real) ────────────
        System.out.println("\n--- CASO 1: Torneo con cupos excesivos ---");
        Torneo torneoInvalido = admin.crearTorneoCompetitivo(
            "Torneo Imposible", DiaSemana.LUNES, catan, 100, 10000, cafe);
        verificar("¿Rechaza torneo con 100 cupos (capacidad=8)?",
                  torneoInvalido == null);

        // ── CASO 2: Inscripción normal ────────────────────────────────────
        System.out.println("\n--- CASO 2: Inscripción normal ---");
        Cliente cliente1 = new Cliente("101", "Ana", "ana@mail.com", "ana", "1234");
        InscripcionTorneo ins1 = torneo.inscribir(cliente1, 2);
        verificar("¿Cliente inscribe 2 cupos?", ins1 != null);
        verificar("¿Cupos disponibles = 6?", torneo.getTotalCuposDisponibles() == 6);

        // ── CASO 3: No puede inscribirse dos veces ────────────────────────
        System.out.println("\n--- CASO 3: Doble inscripción ---");
        InscripcionTorneo ins1b = torneo.inscribir(cliente1, 1);
        verificar("¿Rechaza segunda inscripción del mismo usuario?",
                  ins1b == null);

        // ── CASO 4: Más de 3 cupos ────────────────────────────────────────
        System.out.println("\n--- CASO 4: Más de 3 cupos ---");
        Cliente cliente2 = new Cliente("102", "Bob", "bob@mail.com", "bob", "1234");
        InscripcionTorneo ins2 = torneo.inscribir(cliente2, 4);
        verificar("¿Rechaza 4 cupos (máximo 3)?", ins2 == null);

        // ── CASO 5: Fanático usa cupo especial ────────────────────────────
        System.out.println("\n--- CASO 5: Cliente fanático ---");
        Cliente fanatico = new Cliente("103", "Carlos", "carlos@mail.com", "carlos", "1234");
        fanatico.agregarJuegoFavorito(catan); // lo hace fanático de Catan
        verificar("¿Es fanático?", torneo.esFanatico(fanatico));

        InscripcionTorneo insFan = torneo.inscribir(fanatico, 1);
        verificar("¿Fanático se inscribe?", insFan != null);
        verificar("¿Usó cupo de fanático?", insFan.isUsoCupoFanatico());
        verificar("¿Cupos fanáticos restantes = 1?",
                  torneo.getCuposFanaticosDisponibles() == 1);

        // ── CASO 6: Desinscripción devuelve cupos al pool correcto ────────
        System.out.println("\n--- CASO 6: Desinscripción ---");
        int cuposAntes = torneo.getTotalCuposDisponibles();
        int fanaticosAntes = torneo.getCuposFanaticosDisponibles();

        boolean desins = torneo.desinscribir(fanatico);
        verificar("¿Desinscripción exitosa?", desins);
        verificar("¿Cupos totales restaurados (+1)?",
                  torneo.getTotalCuposDisponibles() == cuposAntes + 1);
        verificar("¿Cupos fanáticos restaurados (+1)?",
                  torneo.getCuposFanaticosDisponibles() == fanaticosAntes + 1);

        // Desinscribir a alguien que no estaba inscrito
        Cliente extraño = new Cliente("999", "X", "x@mail.com", "x", "x");
        boolean desinsNula = torneo.desinscribir(extraño);
        verificar("¿Rechaza desinscripción de no inscrito?", !desinsNula);

        // ── CASO 7: Empleado con turno ese día no puede inscribirse ───────
        System.out.println("\n--- CASO 7: Empleado con turno ---");
        Mesero mesero = new Mesero("201", "Diego", "diego@mail.com",
                                   "diego", "1234", "M01");
        Turno turnoSabado = new Turno(DiaSemana.SABADO, "08:00", "16:00");
        mesero.getTurnos().add(turnoSabado);

        InscripcionTorneo insMesero = torneo.inscribir(mesero, 1);
        verificar("¿Rechaza empleado con turno el sábado?", insMesero == null);

        // ── CASO 8: Empleado SIN turno ese día puede inscribirse ──────────
        System.out.println("\n--- CASO 8: Empleado sin turno ---");
        Mesero meseroLibre = new Mesero("202", "Elena", "elena@mail.com",
                                        "elena", "1234", "M02");
        Turno turnoDomingo = new Turno(DiaSemana.DOMINGO, "08:00", "16:00");
        meseroLibre.getTurnos().add(turnoDomingo); // turno en OTRO día

        InscripcionTorneo insMeseroLibre = torneo.inscribir(meseroLibre, 1);
        verificar("¿Empleado libre ese día puede inscribirse?",
                  insMeseroLibre != null);

        // ── CASO 9: Premio competitivo no llega a empleados ───────────────
        System.out.println("\n--- CASO 9: Premio competitivo ---");
        TorneoCompetitivo torneoComp = (TorneoCompetitivo) torneo;

        // Solo cliente1 (2 cupos) pagó tarifa — meseroLibre no paga
        double premioEsperado = 2 * 15000; // solo cupos de clientes
        double premioCalculado = torneoComp.calcularPremioMonetario();
        verificar("¿Premio = $30.000 (solo cupos de clientes)?",
                  premioCalculado == premioEsperado);
        System.out.println("Premio calculado: $" + (int) premioCalculado);

        // El empleado gana — no recibe dinero
        double premioAntesDeGanar = meseroLibre.getBonoDescuentoGanado();
        torneoComp.otorgarPremio(meseroLibre);
        verificar("¿Empleado no recibe premio monetario?",
                  premioAntesDeGanar == meseroLibre.getBonoDescuentoGanado());

        // ── CASO 10: Torneo lleno ─────────────────────────────────────────
        System.out.println("\n--- CASO 10: Torneo lleno ---");

        // Torneo pequeño: 2 cupos totales
        JuegoMesa dados = new JuegoMesa("Dados", 2000, "Genérico",
                                        1, 2, 0, false, CategoriaJuego.ACCION);
        dados.agregarCopia(new CopiaJuegoPrestamo(EstadoJuego.NUEVO, true, dados));
        cafe.agregarJuegoCatalogo(dados);

        Torneo torneoChico = admin.crearTorneoCompetitivo(
            "Torneo Dados", DiaSemana.LUNES, dados, 2, 5000, cafe);

        Cliente c1 = new Cliente("301", "P1", "p1@mail.com", "p1", "1234");
        Cliente c2 = new Cliente("302", "P2", "p2@mail.com", "p2", "1234");
        Cliente c3 = new Cliente("303", "P3", "p3@mail.com", "p3", "1234");

        torneoChico.inscribir(c1, 1);
        torneoChico.inscribir(c2, 1);

        verificar("¿Torneo está lleno?", torneoChico.estaLleno());

        InscripcionTorneo insExtra = torneoChico.inscribir(c3, 1);
        verificar("¿Rechaza inscripción en torneo lleno?", insExtra == null);

        // ── Resumen ───────────────────────────────────────────────────────
        System.out.println("\n=================================");
        System.out.println("      FIN PRUEBA DE TORNEOS");
        System.out.println("=================================");
    }

    private static void verificar(String descripcion, boolean condicion)
    {
        String resultado = condicion ? "OK" : "FALLÓ";
        System.out.println(descripcion + " → " + resultado);
    }
}