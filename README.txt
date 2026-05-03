DULCES Y DADOS - PROYECTO2

Descripción:
Este proyecto implementa el sistema de gestión del café Dulces y Dados.
Permite gestionar usuarios, reservas, préstamos de juegos, ventas, turnos y persistencia de datos.

----------------------------------------
EJECUCIÓN DE LOS PROGRAMAS DE PRUEBA
--------------------------------------
Cada prueba se ejecuta de forma independiente.
No se requieren argumentos de entrada.

- ui.PruebaReservas       → Reservas de mesa
- ui.PruebaPrestamos      → Préstamo y devolución de juegos
- ui.PruebaVentas         → Ventas, impuestos y descuentos
- ui.PruebaTurnos         → Asignación y cambio de turnos
- ui.PruebaAdministrador  → Gestión de inventario
- ui.PruebaAutenticacion  → Login correcto e incorrecto
- ui.PruebasPersistencia  → Guardado y carga de datos

----------------------------------------
PERSISTENCIA
----------------------------------------

Los datos se guardan en archivos dentro de la carpeta configurada en PersistenciaSistema.

El sistema:
- Carga los datos automáticamente al iniciar.
- Guarda los datos al finalizar o cuando se invoque guardarDatos().

----------------------------------------
PRUEBAS
----------------------------------------

Las pruebas se encuentran en el paquete:

pruebas

Cada una se ejecuta de forma independiente.

Pruebas disponibles:

- PruebaPersistencia
  Verifica guardado y carga de datos.

- PruebaReservas
  Verifica creación, activación y cierre de reservas.

- PruebaPrestamos
  Verifica préstamo de juegos y devolución.

- PruebaVentas
  Verifica cálculo de subtotal, impuestos, descuentos y total.

- PruebaTurnos
  Verifica asignación de turnos y solicitudes de cambio.

- PruebaAdministrador
  Verifica gestión de inventario y juegos.

- PruebaAutenticacion
  Verifica inicio de sesión correcto e incorrecto.

Para ejecutar una prueba:
1. Ir a la clase en el paquete pruebas.
2. Ejecutar el método main.

----------------------------------------
ESTRUCTURA DEL PROYECTO
----------------------------------------

modelo     -> lógica del sistema
ui         -> consola y pruebas
datos      -> archivos de persistencia

# Manual básico de uso (consola)

## Inicio
Cada rol tiene su clase principal:
- `ui.MainAdministrador`
- `ui.MainCliente`
- `ui.MainEmpleado`

Al iniciar, el sistema carga automáticamente la información desde `src/datos`.
Al salir, guarda automáticamente usuarios y torneos.

## Flujo Administrador
1. Iniciar sesión con credenciales de administrador.
2. Opciones disponibles:
   - Crear torneo amistoso.
   - Crear torneo competitivo.
   - Registrar cliente.
   - Registrar empleado (Mesero/Cocinero).
   - Listar torneos.
   - Listar usuarios.

## Flujo Cliente
1. Elegir entre iniciar sesión o registrarse.
2. Si se registra, crear cuenta con documento, nombre, correo, login y password.
3. Opciones disponibles:
   - Consultar torneos disponibles.
   - Inscribirse a torneo (1 a 3 cupos).
   - Desinscribirse de torneo.

## Flujo Empleado
1. Iniciar sesión con usuario de tipo empleado.
2. Opciones disponibles:
   - Consultar torneos disponibles.
   - Inscribirse a torneo (1 cupo).

## Validación de entradas
Todas las opciones numéricas y campos obligatorios pasan por validaciones para:
- Evitar valores vacíos.
- Controlar rangos de menú.
- Asegurar números válidos.
