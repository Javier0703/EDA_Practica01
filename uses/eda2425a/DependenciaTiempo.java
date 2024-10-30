/**
 * Copyright Universidad de Valladolid 2024
 */
package eda2425a;
//import javax.swing.SwingUtilities;

/**
 * Esta clase simula unas iteraciones con una cantidad de goticulas diferentes para ver
 * como funciona la clase Simulador y se comprobara el tiempo que tarda con ellas
 * @author javcalv
 */
public class DependenciaTiempo {

	public static void main(String[] args) {
        int[] tamanosGoticula = {3000, 4000, 5000, 6000, 7000, 8000, 8500, 9000, 9500, 10000};  
        int repeticiones = 1;
        System.out.println("Gotículas - Tiempo Promedio (ms)");
        comprobacionGoticulas(tamanosGoticula, repeticiones);   
    }
	
	/**
	 * Metodo para realizar la comprobacion de las Simulaciones para diversos tamaños
	 * de las gotículas donde devuelve la media del tiempo para cada comprobacion de goticulas
	 * @param tamanosGoticula array con el numero de goticulas
	 * @param repeticiones Las repeticiones por cada goticula que se hace
	 * @return tiempo promedio por cada numero de goticulas utilizado
	 */
	public static void comprobacionGoticulas(int[] tamanosGoticula, int repeticiones) { 
		for (int n : tamanosGoticula) {
            long tiempoTotal = 0;
            for (int i = 0; i < repeticiones; i++) {
                Simulador sim = new Simulador(n);
                long inicio = System.nanoTime();
                sim.PasoSimulacion();
                long fin = System.nanoTime();
                tiempoTotal += (fin - inicio);
            }
            double tiempoPromedio = (tiempoTotal / repeticiones) / 1e6;
            System.out.printf("%d\t%.2f\n", n, tiempoPromedio);
        }
    }
}
