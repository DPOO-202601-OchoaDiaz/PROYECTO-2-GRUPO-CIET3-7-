package ui;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import modelo.Administrador;
import modelo.Bebida;
import modelo.Cafe;
import modelo.CategoriaJuego;
import modelo.Cliente;
import modelo.CopiaJuegoPrestamo;
import modelo.DiaSemana;
import modelo.EstadoJuego;
import modelo.InscripcionTorneo;
import modelo.ItemVenta;
import modelo.JuegoMesa;
import modelo.JuegoVenta;
import modelo.Pasteleria;
import modelo.ProductoMenu;
import modelo.TipoVenta;
import modelo.Torneo;
import modelo.Venta;

public class PruebaAdministrador
{
    public static void main(String[] args)
    {
        System.out.println("=================================");
        System.out.println("     PRUEBA DE ADMINISTRADOR");
        System.out.println("=================================");

        Cafe cafe = new Cafe(50);
        Administrador admin = new Administrador("900", "Admin", "admin@mail.com", "admin", "admin123");

        // ─── Datos iniciales ─────────────────────────────────────────────────

        JuegoMesa catanMesa = new JuegoMesa("Catan", 1995, "Kosmos", 3, 4, 10, true, CategoriaJuego.TABLERO);
        CopiaJuegoPrestamo copia1 = new CopiaJuegoPrestamo(EstadoJuego.FALTA_PIEZA, true, catanMesa);
        catanMesa.agregarCopia(copia1);
        cafe.agregarJuegoCatalogo(catanMesa);

        JuegoVenta catanVenta = new JuegoVenta("Catan", 180000, true, 5);
        cafe.agregarJuegoVenta(catanVenta);

        System.out.println("\n--- Inventario inicial ---");
        System.out.println("Stock venta Catan:        " + catanVenta.getStockDisponible());
        System.out.println("Copias préstamo Catan:    " + catanMesa.getCopias().size());
        System.out.println("Estado copia préstamo:    " + copia1.getEstado());

        // ─── 1. reabastecerJuegoVenta ────────────────────────────────────────

        admin.reabastecerJuegoVenta(catanVenta, 3);
        System.out.println("\n--- Después de reabastecerJuegoVenta(+3) ---");
        System.out.println("Stock venta Catan: " + catanVenta.getStockDisponible());

        // ─── 2. reabastecerCopiaPrestamo ─────────────────────────────────────

        admin.reabastecerCopiaPrestamo(catanMesa, 2);
        System.out.println("\n--- Después de reabastecerCopiaPrestamo(+2) ---");
        System.out.println("Copias préstamo Catan: " + catanMesa.getCopias().size());

        long copiasnuevas = catanMesa.getCopias().stream()
                .filter(c -> c.getEstado() == EstadoJuego.NUEVO)
                .count();
        System.out.println("Copias con estado NUEVO: " + copiasnuevas);

        boolean resultado = copiasnuevas == 2;
        System.out.println("¿Se agregaron 2 copias NUEVO? " + (resultado ? "OK" : "FALLÓ"));

        // ─── 3. repararJuego y marcarJuegoDesaparecido ───────────────────────

        admin.repararJuego(copia1, null);
        System.out.println("\n--- Después de repararJuego ---");
        System.out.println("Estado copia1: " + copia1.getEstado());

        admin.marcarJuegoDesaparecido(copia1);
        System.out.println("\n--- Después de marcarJuegoDesaparecido ---");
        System.out.println("Estado copia1:    " + copia1.getEstado());
        System.out.println("Disponible:       " + copia1.estaDisponible());

        // ─── 4. moverJuegoVentaAPrestamo ─────────────────────────────────────

        int copiasPrevias = catanMesa.getCopias().size();
        admin.moverJuegoVentaAPrestamo(catanVenta, 1, cafe);
        System.out.println("\n--- Después de moverJuegoVentaAPrestamo(1) ---");
        System.out.println("Stock venta Catan:     " + catanVenta.getStockDisponible());
        System.out.println("Copias préstamo Catan: " + catanMesa.getCopias().size());
        System.out.println("¿Se movió 1 copia? " + (catanMesa.getCopias().size() == copiasPrevias + 1 ? "OK" : "FALLÓ"));

        // ─── 5. agregarProductoMenu ──────────────────────────────────────────

        System.out.println("\n--- Antes de agregarProductoMenu ---");
        System.out.println("Productos en menú: " + cafe.getMenu().size());

        Bebida capuchino = new Bebida("Capuchino", 7000, true, false, true);
        admin.agregarProductoMenu(capuchino, cafe);

        Pasteleria muffin = new Pasteleria("Muffin de arándano", 8500, true);
        admin.agregarProductoMenu(muffin, cafe);

        System.out.println("--- Después de agregarProductoMenu (x2) ---");
        System.out.println("Productos en menú: " + cafe.getMenu().size());

        for (ProductoMenu p : cafe.consultarMenu())
        {
            System.out.println("  · " + p.getNombre() + " - $" + (int) p.getPrecio());
        }

        boolean menuOk = cafe.getMenu().size() == 2;
        System.out.println("¿Se agregaron ambos productos? " + (menuOk ? "OK" : "FALLÓ"));

        // ─── 6. generarInformeVentas ─────────────────────────────────────────

        // Registramos ventas de prueba con fecha de hoy
        String ahora = LocalDateTime.now().toString();
        String hoyMesAnterior = LocalDateTime.now().minusMonths(1).toString();

        Venta ventaHoy1 = new Venta(ahora, TipoVenta.CAFETERIA, 0, 1000, null, null);
        ventaHoy1.agregarItem(new ItemVenta(2, capuchino.getPrecio(), capuchino));
        cafe.registrarVenta(ventaHoy1);

        Venta ventaHoy2 = new Venta(ahora, TipoVenta.TIENDA_JUEGOS, 0, 0, null, null);
        ventaHoy2.agregarItem(new ItemVenta(1, catanVenta.getPrecio(), catanVenta));
        cafe.registrarVenta(ventaHoy2);

        Venta ventaMesAnterior = new Venta(hoyMesAnterior, TipoVenta.CAFETERIA, 0, 0, null, null);
        ventaMesAnterior.agregarItem(new ItemVenta(1, muffin.getPrecio(), muffin));
        cafe.registrarVenta(ventaMesAnterior);

        System.out.println("\n--- generarInformeVentas ---");

        List<Venta> general = admin.generarInformeVentas("GENERAL", null, cafe);
        System.out.println("GENERAL (todas):           " + general.size() + " ventas");
        System.out.println("¿Son 3? " + (general.size() == 3 ? "OK" : "FALLÓ"));

        List<Venta> diaria = admin.generarInformeVentas("DIARIA", null, cafe);
        System.out.println("DIARIA (hoy):              " + diaria.size() + " ventas");
        System.out.println("¿Son 2? " + (diaria.size() == 2 ? "OK" : "FALLÓ"));

        List<Venta> mensual = admin.generarInformeVentas("MENSUAL", null, cafe);
        System.out.println("MENSUAL (mes actual):      " + mensual.size() + " ventas");
        System.out.println("¿Son 2? " + (mensual.size() == 2 ? "OK" : "FALLÓ"));

        List<Venta> soloTienda = admin.generarInformeVentas("GENERAL", TipoVenta.TIENDA_JUEGOS, cafe);
        System.out.println("GENERAL solo TIENDA:       " + soloTienda.size() + " venta");
        System.out.println("¿Es 1? " + (soloTienda.size() == 1 ? "OK" : "FALLÓ"));

        double totalGeneral = 0;
        for (Venta v : general)
        {
            totalGeneral += v.calcularTotal();
        }
        System.out.println("\nTotal acumulado (general): $" + (int) totalGeneral);
        
        
     // ── Verificación del ciclo completo de premios ────────────────────────

        System.out.println("\n--- Ciclo completo de premios ---");

        // Crear juego y copias
        JuegoMesa ajedrez = new JuegoMesa("Ajedrez", 1475, "FIDE", 2, 2, 6, false,
                                           CategoriaJuego.TABLERO);
        ajedrez.agregarCopia(new CopiaJuegoPrestamo(EstadoJuego.NUEVO, true, ajedrez));
        ajedrez.agregarCopia(new CopiaJuegoPrestamo(EstadoJuego.NUEVO, true, ajedrez));
        cafe.agregarJuegoCatalogo(ajedrez);

        // Admin crea un torneo amistoso (bono 20%)
        Torneo torneoAjedrez = admin.crearTorneoAmistoso(
            "Copa Ajedrez", DiaSemana.SABADO, ajedrez, 4, 0.20, cafe);

        System.out.println("Torneo creado: " + (torneoAjedrez != null ? "OK" : "FALLÓ"));
        System.out.println("Premio: " + torneoAjedrez.obtenerDescripcionPremio());

        // Cliente se inscribe
        Cliente jugador = new Cliente("202", "Laura", "laura@mail.com", "laura", "1234");
        InscripcionTorneo ins = torneoAjedrez.inscribir(jugador, 1);
        System.out.println("Inscripción: " + (ins != null ? "OK" : "FALLÓ"));
        System.out.println("Cupos disponibles tras inscripción: "
                           + torneoAjedrez.getTotalCuposDisponibles());

        // El jugador gana el torneo
        torneoAjedrez.otorgarPremio(jugador);
        System.out.println("¿Tiene bono activo? " + jugador.tieneBonoActivo());
        System.out.println("Bono ganado: " + (int)(jugador.getBonoDescuentoGanado() * 100) + "%");

        // El jugador usa el bono en una compra
        Bebida jugo = new Bebida("Jugo", 8000, true, false, false);
        List<ItemVenta> items = new ArrayList<>();
        items.add(new ItemVenta(1, jugo.getPrecio(), jugo));
        Venta compra = jugador.comprarProductos(items, 0);
        compra.setTipoVenta(TipoVenta.CAFETERIA);

        System.out.println("Subtotal antes de bono: $" + (int) compra.calcularSubtotal());
        boolean bonoAplicado = jugador.aplicarBonoAVenta(compra);
        System.out.println("¿Bono aplicado? " + bonoAplicado);
        System.out.println("Total con descuento 20%: $" + (int) compra.calcularTotal());
        System.out.println("¿Bono consumido? " + !jugador.tieneBonoActivo());
        

        System.out.println("\n=================================");
        System.out.println("   FIN PRUEBA DE ADMINISTRADOR");
        System.out.println("=================================");
        
    }

}