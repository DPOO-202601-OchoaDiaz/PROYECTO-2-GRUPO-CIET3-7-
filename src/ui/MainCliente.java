package ui;

import java.util.List;
import java.util.Scanner;

import modelo.Cafe;
import modelo.Cliente;
import modelo.InscripcionTorneo;
import modelo.SistemasDulcesDados;
import modelo.Torneo;
import modelo.Usuario;
import persistencia.PersistenciaSistema;

public class MainCliente
{
    public static void main(String[] args)
    {
        SistemasDulcesDados sistema = new SistemasDulcesDados(new Cafe(50), new PersistenciaSistema("src/datos"));
        sistema.inicializarSistema();

        Scanner scanner = new Scanner(System.in);
        try
        {
            menuInicioCliente(scanner, sistema);
        }
        finally
        {
            sistema.guardarDatos();
            scanner.close();
        }
    }

    private static void menuInicioCliente(Scanner scanner, SistemasDulcesDados sistema)
    {
        System.out.println("=== CLIENTE ===");
        System.out.println("1. Iniciar sesión");
        System.out.println("2. Registrarse");
        int opcion = ValidadorEntradaConsola.leerEnteroEnRango(scanner, "Seleccione opción: ", 1, 2);

        if (opcion == 2)
        {
            registrarse(scanner, sistema);
        }

        if (!iniciarSesion(scanner, sistema))
        {
            System.out.println("Credenciales inválidas o usuario no es cliente.");
            return;
        }

        boolean continuar = true;
        while (continuar)
        {
            System.out.println("\n=== MENÚ CLIENTE ===");
            System.out.println("1. Consultar torneos");
            System.out.println("2. Inscribirse a torneo");
            System.out.println("3. Desinscribirse de torneo");
            System.out.println("0. Salir");
            int opcionMenu = ValidadorEntradaConsola.leerEnteroEnRango(scanner, "Seleccione opción: ", 0, 3);

            switch (opcionMenu)
            {
                case 1:
                    listarTorneos(sistema);
                    break;
                case 2:
                    inscribir(scanner, sistema);
                    break;
                case 3:
                    desinscribir(scanner, sistema);
                    break;
                case 0:
                    continuar = false;
                    break;
                default:
                    break;
            }
        }
    }

    private static void registrarse(Scanner scanner, SistemasDulcesDados sistema)
    {
        String doc = ValidadorEntradaConsola.leerTextoNoVacio(scanner, "Documento: ");
        String nombre = ValidadorEntradaConsola.leerTextoNoVacio(scanner, "Nombre: ");
        String correo = ValidadorEntradaConsola.leerTextoNoVacio(scanner, "Correo: ");
        String login = ValidadorEntradaConsola.leerTextoNoVacio(scanner, "Login: ");
        String pass = ValidadorEntradaConsola.leerTextoNoVacio(scanner, "Password: ");

        boolean agregado = sistema.agregarUsuario(new Cliente(doc, nombre, correo, login, pass));
        System.out.println(agregado ? "Registro exitoso." : "No se pudo registrar (login duplicado).");
    }

    private static boolean iniciarSesion(Scanner scanner, SistemasDulcesDados sistema)
    {
        String login = ValidadorEntradaConsola.leerTextoNoVacio(scanner, "Login: ");
        String pass = ValidadorEntradaConsola.leerTextoNoVacio(scanner, "Password: ");
        Usuario usuario = sistema.autenticarUsuario(login, pass);
        return usuario instanceof Cliente;
    }

    private static void listarTorneos(SistemasDulcesDados sistema)
    {
        List<Torneo> torneos = sistema.consultarTorneosDisponibles();
        if (torneos.isEmpty())
        {
            System.out.println("No hay torneos disponibles.");
            return;
        }

        for (int i = 0; i < torneos.size(); i++)
        {
            Torneo t = torneos.get(i);
            System.out.println((i + 1) + ". " + t.getNombre() + " (día: " + t.getDia() + ", cupos: " + t.getTotalCuposDisponibles() + ")");
        }
    }

    private static void inscribir(Scanner scanner, SistemasDulcesDados sistema)
    {
        List<Torneo> torneos = sistema.consultarTorneosDisponibles();
        if (torneos.isEmpty())
        {
            System.out.println("No hay torneos disponibles.");
            return;
        }
        listarTorneos(sistema);
        int indice = ValidadorEntradaConsola.leerEnteroEnRango(scanner, "Seleccione torneo: ", 1, torneos.size()) - 1;
        int cupos = ValidadorEntradaConsola.leerEnteroEnRango(scanner, "Número de cupos: ", 1, 3);
        InscripcionTorneo inscripcion = sistema.inscribirEnTorneo(torneos.get(indice), cupos);
        System.out.println(inscripcion != null ? "Inscripción exitosa." : "No fue posible inscribir.");
    }

    private static void desinscribir(Scanner scanner, SistemasDulcesDados sistema)
    {
        List<Torneo> torneos = sistema.consultarTorneos();
        if (torneos.isEmpty())
        {
            System.out.println("No hay torneos.");
            return;
        }

        for (int i = 0; i < torneos.size(); i++)
        {
            Torneo t = torneos.get(i);
            System.out.println((i + 1) + ". " + t.getNombre() + " (día: " + t.getDia() + ")");
        }

        int indice = ValidadorEntradaConsola.leerEnteroEnRango(scanner, "Seleccione torneo: ", 1, torneos.size()) - 1;
        boolean ok = sistema.desinscribirDeTorneo(torneos.get(indice));
        System.out.println(ok ? "Desinscripción exitosa." : "No estabas inscrito o la operación falló.");
    }
}