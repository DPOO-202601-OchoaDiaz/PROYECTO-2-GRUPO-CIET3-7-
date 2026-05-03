package ui;

import java.util.Scanner;

public class ValidadorEntradaConsola
{
    private ValidadorEntradaConsola()
    {
    }

    public static String leerTextoNoVacio(Scanner scanner, String mensaje)
    {
        while (true)
        {
            System.out.print(mensaje);
            String valor = scanner.nextLine();
            if (valor != null && !valor.trim().isEmpty())
            {
                return valor.trim();
            }
            System.out.println("Entrada inválida. Intente de nuevo.");
        }
    }

    public static int leerEnteroEnRango(Scanner scanner, String mensaje, int minimo, int maximo)
    {
        while (true)
        {
            System.out.print(mensaje);
            String valor = scanner.nextLine();
            try
            {
                int numero = Integer.parseInt(valor.trim());
                if (numero >= minimo && numero <= maximo)
                {
                    return numero;
                }
            }
            catch (NumberFormatException e)
            {
                // Se maneja con mensaje de error abajo
            }
            System.out.println("Número inválido. Debe estar entre " + minimo + " y " + maximo + ".");
        }
    }

    public static double leerDoublePositivo(Scanner scanner, String mensaje)
    {
        while (true)
        {
            System.out.print(mensaje);
            String valor = scanner.nextLine();
            try
            {
                double numero = Double.parseDouble(valor.trim());
                if (numero >= 0)
                {
                    return numero;
                }
            }
            catch (NumberFormatException e)
            {
                // Se maneja con mensaje de error abajo
            }
            System.out.println("Valor inválido. Debe ser un número positivo.");
        }
    }
}
