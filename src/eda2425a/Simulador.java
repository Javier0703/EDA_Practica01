/**
 * Clase para simular un líquido suponiendo que está compuesto por n gotículas.
 */
package eda2425a;

public class Simulador {
    
    /**
     * Representa una gotícula. Se usa únicamente para almacenar información
     * sobre la gotícula, por eso no se definen getters ni setters, salvo en
     * el caso especial de la posición.
     */
	
    public class Goticula {

        public float x, y;     // Posición
        public float xa, ya;   // Posición anterior
        public float vx, vy;   // Velocidad
        public float fpx, fpy; // Fuerza debida al diferencial de presión
        public float fux, fuy; // Fuerza debida a la interacción del usuario
        public float operacionesGoticula = 0; //Operaciones que hace cada porpia goticula
        

        /**
         * Actualiza la posición de una gotícula. Se asegura de que la posición
         * de las gotículas se encuentre en el rango [0..lx, 0..ly]
         * @param xn Nueva posición x
         * @param yn Nueva posición y
         * @param lx Valor máximo posición x
         * @param ly Valor máximo posición y
         */
        
        public void SetPos(float xn, float yn, int lx, int ly) {
            x = xn; y = yn;
            // El sobrepasar los límites se trata como una colisión con las
            // paredes, por eso se cambia de signo la velocidad
            if (x < 0) { x = 0; vx = -vx; this.operacionesGoticula++;}
            if (y < 0) { y = 0; vy = -vy; this.operacionesGoticula++;}
            if (x >= lx) { x = lx-0.00001f; vx = -vx; this.operacionesGoticula+=2;}
            if (y >= ly) { y = ly-0.00001f; vy = -vy; this.operacionesGoticula+=2;}
        }

        @Override
        public String toString() {
            return String.format("pos:[%.3f;%.3f] vel:[%.3f;%.3f]",x,y,vx,vy);
        }
    }
        
    // Parámetros de la simulación (no tocar!)
    static float DT = 0.005f;              // Paso de integración
    static float D0 = 6.0f;                // Densidad objetivo
    static float KP = 1000.0f;             // Conversión fuerza-presión
    static float GX = 0.0f;                // Gravedad (x)
    static float GY = -9.81f;              // Gravedad (y)      
    static float KK = (float) (6/Math.PI); // Normalización del Kernel
    
    public Goticula[] gotas; // Las gotículas del sistema
    public int n;            // Número de gotículas (igual a vec.length)
    public int lx;           // Longitud del tanque
    public int ly;           // Altura del tanque
    public double tpo;       // Tiempo de la última ejecución (milisegundos)
    
    // Interacción del usuario
    public float xu, yu;     // Posición del ratón (xu < 0 si no se aplica)
    public float ru;         // Radio fuerza usuario
    public float ku;         // Intensidad fuerza usuario
    
    //MODIFICACION : INICIALIZACION DEL CONTADOR
    private long contadorOperaciones; //Contador de operaciones Artim�tricas
    
    /**
     * Crea un nuevo simulador con el número de gotículas indicado.
     * Establece las posiciones iniciales de las gotículas
     * @param n Número de gotículas
     */
    public Simulador(int n) {
        this.n = n;
        contadorOperaciones = 0; //Inicializamos el contador en 0
        // Calculamos las dimensiones adecuadas del tanque
        lx = (int) (2*Math.ceil(Math.sqrt(n/D0)));
        ly = (9*lx)/16;
        xu = -1;   // No hay interacción usuario
        ru = ly/6f; //1 operacion
        this.contadorOperaciones++;
        // Creamos y colocamos las gotículas
        gotas = new Goticula[n];
        for(int i = 0; i < n; i++) {
        	gotas[i] = new Goticula(); 
        }
        ColocaGoticulasEnCuadrado();
    }
    
    /**
     * Coloca las gotículas segun un cuadrado con la densidad objetivo
     */
    private void ColocaGoticulasEnCuadrado() {
        float sep = (float) Math.sqrt(1/D0); //2 operaciones
        this.contadorOperaciones +=2;
        int nfil = (int) Math.floor(Math.sqrt(n));
        int ncol = (n-1)/nfil + 1;
        for(int i = 0; i < n; i++) {
            float x = sep*(i % ncol + 1); //1 operacion
            float y = sep*(i / ncol + 1); //1 operacion
            this.contadorOperaciones +=2;
            gotas[i].SetPos(x, y, lx, ly);
        }
    }
    
    /**
     * Devuelve la distancia entre los puntos (x1,y1) y (x2,y2)
     * @param x1    La posición x del primer punto
     * @param y1    La posición y del primer punto
     * @param x2    La posición x del segundo punto
     * @param y2    La posición y del segundo punto
     * @return  La distancia euclidea entre los puntos
     */
    private static float Dist(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1)); //8 Operaciones
    }
    
    /**
     * Función de ponderación (kernel)
     * @param d Distancia
     * @return  El valor de ponderación a esa distancia
     */
    private static float Kernel(float d) {
        return d > 1 ? 0 : KK*(1-d)*(1-d); //d<=1 --> 4 operaciones
    }
    
    /**
     * Derivada de la función de ponderación
     * @param d Distancia
     * @return  La derivada de la función a esa distancia
     */
    private static float DKernel(float d) {
        return d > 1 ? 0 : KK*2*(d-1); //d<=1 --> 3 operaciones
    }
    
    /**
     * Calcula la densidad en los alrededores del punto (x,y).
     * Recorre todas las gotículas y suma su masa (que es 1 por definición) 
     * ponderada según su distancia al punto (x,y) usando el Kernel.
     * Las gotículas más cercanas influiran más que las lejanas, y las que
     * estén a una distancia mayor que 1 no influiran.
     * @param x Posición x del punto
     * @param y Posición y del punto
     * @return  La densidad alrededor de ese punto
     */
    private float CalcDensidad(float x, float y) {
        float densidad = 0;
        for(Goticula g: gotas) {
            // Distancia de la cada gota al punto (x,y)
            float d = Dist(x, y, g.x, g.y);
            this.contadorOperaciones +=8; //Llamada a Dist = 8 operaciones
            // Sumamos la masa (=1) de la gota g ponderada por el Kernel
            // Nota: Fijaros que si la distancia de la gota es mayor que 1 su
            // contribución será nula (el kernel devuelve 0 para d > 1)
            if(d<=1)this.contadorOperaciones+=4;
            densidad += Kernel(d);
            this.contadorOperaciones++;
        }
        return densidad;
    }
    
    /**
     * Calcula la fuerza debida a diferenciales de presión.
     * Atención: La densidad se calcula con las posiciones actualizadas y
     * la presión con la posición original de las partículas (para que la 
     * simulación sea más estable)
     * @param gi La gotícula sobre la que se calcula la fuerza
     * @return La fuerza (fx,fy) que se realiza sobre la gotícula. Se devuelve
     * en un array con dos elementos (fx y fy).
     */
    private float[] CalcPresion(Goticula gi) {
        float fx = 0, fy = 0;
        // Diferencial de densidad en la partícula objetivo
        float di = CalcDensidad(gi.x, gi.y);
        // Se acumula el efecto del resto de gotículas
        for(Goticula gj: gotas) {
            if(gi == gj) { continue; }
            // Distancia entre goticulas (atención: se usa la posición salvada)
            float dist = Dist(gi.xa, gi.ya, gj.xa, gj.ya);
            this.contadorOperaciones +=8;
            // Si las partículas estan muy juntas puede dar problemas numéricos
            if(dist < 0.0001f) { continue; }
            // Diferencial de densidad en la otra partícula
            float dj = CalcDensidad(gj.x, gj.y);   
            if(dist<=1)this.contadorOperaciones+=3;
            float fr = KP*0.5f*((di-D0)+(dj-D0))*DKernel(dist)/dj; // 7 Operaciones
            // Acumulamos la fuerza
            fx += fr*(gj.xa-gi.xa)/dist; //3 operaciones
            fy += fr*(gj.ya-gi.ya)/dist; //3 operaciones
            this.contadorOperaciones += 13;
            
        }        
        return new float[] { fx/di, fy/di };
    }
    
    /**
     * Calcula la fuerza debida a la interacción del usuario.
     * @param g La gotícula sobre la que se calcula la fuerza
     * @return La fuerza (fx,fy) que se realiza sobre la gotícula. Se devuelve
     * en un array con dos elementos (fx y fy).
     */
    private float[] CalcInteraccion(Goticula g) {
        float fx = 0, fy = 0;
        if(xu >= 0) {
            // Distancia entre la goticula y el punto marcado por el usuario
            float dist = Dist(xu, yu, g.x, g.y); //8 operaciones
            this.contadorOperaciones +=8;
            float dx = 0, dy = 0; // Dirección al punto, normalizada
            if(dist > 0.0001f) { 
                dx = (xu - g.x)/dist; //2 operaciones
                dy = (yu - g.y)/dist; //2 operaciones 
                this.contadorOperaciones +=4;
            }
            float fr = 1 - dist/ru; //1 operacion
            fx = fr*(ku*dx - g.vx); //3 operaciones
            fy = fr*(ku*dy - g.vy); //3 operaciones
            this.contadorOperaciones +=7;
        }
        return new float[] { fx, fy };
    }
  
    /**
     * Realiza un paso de la simulación, integrando el sistema un intervalo
     * de tiempo DT. Se actualizan las posiciones y velocidades de las 
     * gotículas (array gotas).
     * Se utiliza el método de Euler con algún que otro truco para mejorar la
     * estabilidad de la simulación.
     */
    
    public final void PasoSimulacion() {
        long t0 = System.nanoTime();
        // Aplicamos la fuerza de la gravedad y predecimos la nueva posición
        for(Goticula g: gotas) {
            // Salvamos la posición anterior
            g.xa = g.x; g.ya = g.y;
            // Aplicamos la gravedad
            g.vx += DT*GX; g.vy += DT*GY; //4 operaciones
            this.contadorOperaciones += 4;
            // Actualizamos la posición
            g.SetPos(g.x + DT*g.vx, g.y + DT*g.vy, lx, ly);
            
        }
        // Calculamos la fuerza debida a los diferenciales de presión y
        // a la interacción del usuario
        for(Goticula g: gotas) {
            float[] fp = CalcPresion(g);
            g.fpx = fp[0]; g.fpy = fp[1];
            fp = CalcInteraccion(g);
            g.fux = fp[0]; g.fuy = fp[1];
        }
        // Aplicamos las fuerzas calculadas y almacenadas en el bucle anterior
        for(Goticula g: gotas) {
            g.vx += DT*(g.fpx + g.fux); //3 operaciones
            g.vy += DT*(g.fpy + g.fuy); //3 operaciones
            this.contadorOperaciones +=6;
            // Actualizamos la posición (usamos la posición original)
            g.SetPos(g.xa + DT*g.vx, g.ya + DT*g.vy, lx, ly);
            
        }
        tpo = 1e-6*(System.nanoTime()-t0);
    }

    /**
     * Indica que el usuario ha pulsado o arrastrado el ratón a esa posición.
     * @param x Posición del ratón
     * @param y Posición del ratón
     * @param intensidad    Intensidad
     */
    public final void Click(float x, float y, float intensidad) {
        xu = x; yu = y; ku = intensidad;
    }
    
    /**
     * Indica que el usuario ha dejado de pulsar el ratón.
     */
    public final void NoClick() {
        xu = -1;
    }
    
    /**
     * Metodo que devuelve el numro de operaciones aritm�tricas de tipo float y las que realiza cada propia goticula
     * @return numero de aritmetricas
     */
	public long obtenerContadorOperaciones() {
		long operacionesGoticulasTotal = 0;
		for (int i = 0; i < this.gotas.length; i++) {
			operacionesGoticulasTotal += this.gotas[i].operacionesGoticula;
		}
		return this.contadorOperaciones + operacionesGoticulasTotal;
		
	}   
    
    
}
