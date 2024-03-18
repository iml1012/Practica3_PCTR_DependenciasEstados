package src.p03.c01;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * La clase Parque implementa la interfaz IParque y gestiona el control de acceso
 * de personas a un parque. Realiza seguimiento del número total de personas en el parque
 * y de las personas que entran o salen por cada puerta.
 * 
 * Se asegura que el número de personas en el parque no exceda un máximo establecido
 * y que no sea negativo.
 */
public class Parque implements IParque{

	// Máximo número de personas permitidas en el parque
	private static final int PERSONAS_MAX = 50;
	// Contador total de personas en el parque
	private int contadorPersonasTotales;
	// Contadores de personas por puerta
	private Hashtable<String, Integer> contadoresPersonasPuerta;
	
	
	/**
     * Constructor de la clase Parque. Inicializa el contador total de personas en el parque
     * y el Hashtable que mantiene el conteo de personas por puerta.
     */
	public Parque() {
		contadorPersonasTotales = 0;
		contadoresPersonasPuerta = new Hashtable<String, Integer>();
	}

	
	/**
     * Método sincronizado para gestionar la entrada de una persona al parque a través de una puerta específica.
     * Aumenta el contador total de personas y el contador específico de la puerta utilizada.
     * Imprime el estado actual del parque tras la entrada.
     * 
     * @param puerta El identificador de la puerta por la que entra la persona.
     */
	@Override
	public synchronized void entrarAlParque(String puerta){
		// Inicialización del contador para una nueva puerta, si es necesario
		if (contadoresPersonasPuerta.get(puerta) == null) {
			contadoresPersonasPuerta.put(puerta, 0);
		}
		
		comprobarAntesDeEntrar();
		
		// Incremento de contadores
		contadorPersonasTotales++;		
		contadoresPersonasPuerta.put(puerta, contadoresPersonasPuerta.get(puerta)+1);
		
		imprimirInfo(puerta, "Entrada");
		checkInvariante();
		notifyAll();
		
	}
	
	/**
     * Método sincronizado para gestionar la salida de una persona del parque a través de una puerta específica.
     * Disminuye el contador total de personas y el contador específico de la puerta utilizada.
     * Imprime el estado actual del parque tras la salida.
     * 
     * @param puerta El identificador de la puerta por la que sale la persona.
     */
	@Override
	public synchronized void salirDelParque(String puerta) {
		// Inicialización del contador para una nueva puerta, si es necesario
		if (contadoresPersonasPuerta.get(puerta) == null) {
			contadoresPersonasPuerta.put(puerta, 0);
		}
		comprobarAntesDeSalir();
		contadorPersonasTotales--;
		contadoresPersonasPuerta.put(puerta, contadoresPersonasPuerta.get(puerta) - 1);
	
		imprimirInfo(puerta, "Salida");
		checkInvariante();
		notifyAll();	
		
	}
	
	
	/**
	 * Imprime información sobre el movimiento (entrada/salida) de personas en el parque
	 * y el estado actual de las personas dentro del parque, incluyendo el conteo por puerta.
	 * 
	 * @param puerta El identificador de la puerta implicada en el movimiento.
	 * @param movimiento El tipo de movimiento ("Entrada" o "Salida"), indicando si las personas están entrando o saliendo.
	 */
	private void imprimirInfo (String puerta, String movimiento){
		System.out.println(movimiento + " por puerta " + puerta);
		System.out.println("--> Personas en el parque " + contadorPersonasTotales); //+ " tiempo medio de estancia: "  + tmedio);
		
		// Iteramos por todas las puertas e imprimimos sus entradas
		for(String p: contadoresPersonasPuerta.keySet()){
			System.out.println("----> Por puerta " + p + " " + contadoresPersonasPuerta.get(p));
		}
		System.out.println(" ");
	}
	
	/**
	 * Calcula y devuelve la suma de todas las personas que han entrado al parque por cada puerta.
	 * Este método es necesario para verificar el invariante de la clase y asegurar la consistencia
	 * del número total de personas dentro del parque en comparación con los contadores individuales
	 * de cada puerta.
	 * 
	 * @return int La suma de las personas que han entrado por todas las puertas.
	 */
	private int sumarContadoresPuerta() {
		int sumaContadoresPuerta = 0;
			Enumeration<Integer> iterPuertas = contadoresPersonasPuerta.elements();
			while (iterPuertas.hasMoreElements()) {
				sumaContadoresPuerta += iterPuertas.nextElement();
			}
		return sumaContadoresPuerta;
	}
	
	/**
	 * Verifica el invariante de la clase Parque asegurándose de que el número total de personas
	 * en el parque sea igual a la suma de las personas contadas por cada puerta y que no se
	 * exceda el máximo de personas permitidas. Además, verifica que el número total de personas
	 * no sea negativo. Este método es fundamental para mantener la integridad de los datos
	 * del parque y evitar estados inconsistentes.
	 */
	protected void checkInvariante() {
		assert sumarContadoresPuerta() == contadorPersonasTotales : "INV: La suma de contadores de las puertas debe ser igual al valor del contador del parte";
		assert contadorPersonasTotales <= PERSONAS_MAX : "INV: El aforo maximo es de " + PERSONAS_MAX;
		assert contadorPersonasTotales >= 0 : "INV: El aforo minimo es de 0 / No quedan personas en el parque";
	}

	/**
	 * Asegura que no se exceda el número máximo de personas permitidas en el parque antes de permitir
	 * la entrada de una persona. Si el parque está lleno, el hilo actual se pone en espera hasta
	 * que el número de personas disminuye por debajo del máximo permitido.
	 * Este mecanismo de control ayuda a prevenir la entrada al parque de más personas.
	 */
	protected void comprobarAntesDeEntrar(){
		while(contadorPersonasTotales >= PERSONAS_MAX){
			try{
				wait();
			}catch(InterruptedException e){
				System.out.print(e.toString());
			}
		}
	}

	/**
	 * Verifica que haya al menos una persona en el parque antes de permitir que alguien salga.
	 * Si el parque está vacío, el hilo actual se pone en espera hasta que alguien entre.
	 * Este control previene estados inválidos como tener un número negativo de personas en el parque.
	 */
	protected void comprobarAntesDeSalir(){
		while(contadorPersonasTotales <= 0){
			try{
				wait();
			}catch(InterruptedException e){
				System.out.print(e.toString());
			}
		}
	}

}
