/**
 * Copyright Universidad de Valladolid 2024
 */
package eda2425a;

/**
 * Esta clase simula unas iteraciones con una cantidad de goticulas diferentes para ver
 * como funciona la clase Simulador y se comprobara el numero de operaciones elementales.
 * @author javcalv
 */

public class DependenciaOperaciones {
    public static void main(String[] args) {
        int[] tamaniosGoticulas = {2000, 3000, 4000, 5000, 6000, 6500, 7000, 7500, 8000, 8500};
        int repeticiones = 1;
        System.out.println("N�mero de got�culas - Operaciones promedio");
        comprobarGoticulas(tamaniosGoticulas,repeticiones);
    }
    
    /**
     * Metodo que sirve para comprobar la media del numero de operaciones elementales realizadas por cada
     * numero de goticulas diferentes.
     * @param tamaniosGoticulas Lista con los diferentes numeros de goticulas
     * @param repeticiones las repeticiones que se realizan para cada numero de goticulas
     * @return Devuelve mediante la consola el numero de goticulas y su numero de operaciones
     */
    public static void comprobarGoticulas(int[] tamaniosGoticulas, int repeticiones) {
    	for (int n : tamaniosGoticulas) {
            long totalOperaciones = 0;

            for (int i = 0; i < repeticiones; i++) {
                Simulador sim = new Simulador(n);
                sim.PasoSimulacion();
                totalOperaciones += sim.obtenerContadorOperaciones();
            }

            long promedioOperaciones = totalOperaciones / repeticiones;
            System.out.println(n + "\t" + promedioOperaciones);
        }
    }
}
