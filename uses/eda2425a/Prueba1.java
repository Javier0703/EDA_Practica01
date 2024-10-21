/**
 * Copyright Universidad de Valladolid 2024
 */
package eda2425a;

/**
 * Esta clase simula unas iteraciones con una cantidad de goticulas diferentes para ver
 * como funciona la clase Simulador.
 * @author javcalv
 */
public class Prueba1 {

	public static void main(String[] args) {
        int[] tamanosGoticula = {150, 300, 450, 600, 750, 900, 1050};  
        int repeticiones = 10;  
        System.out.println("Tamaño de Gotículas\tTiempo Promedio (ms)");
        comprobacionGoticulas(tamanosGoticula, repeticiones);   
    }
	
	/**
	 * Metodo para realizar la comprobacion de las Simulaciones para diversos tamaños
	 * de las gotículas
	 * @param tamanosGoticula array con el numero de goticulas
	 * @param repeticiones Las repeticiones por cada goticula que se hace
	 * @return tiempo promedio por cada numero de goticulas utilizado
	 */
	public static void comprobacionGoticulas(int[] tamanosGoticula, int repeticiones) {
		for (int n : tamanosGoticula) {
            long tiempoTotal = 0;
            
            //Realizamos 
            for (int i = 0; i < repeticiones; i++) {
                Simulador sim = new Simulador(n);
                long inicio = System.nanoTime();
                sim.PasoSimulacion();
                long fin = System.nanoTime();
                tiempoTotal += (fin - inicio);
            }

            // Calculamos el tiempo promedio en milisegundos
            double tiempoPromedio = (tiempoTotal / repeticiones) / 1e6;
            System.out.printf("%d\t\t\t\t%.2f\n", n, tiempoPromedio);
        }
    }
}
