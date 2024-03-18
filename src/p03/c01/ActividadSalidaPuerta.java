package src.p03.c01;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ActividadSalidaPuerta implements Runnable{

	private static final int NUMSALIDAS = 20;
	private String puerta;
	private IParque parque;
	
	public ActividadSalidaPuerta(String puerta, IParque parque) {
		this.puerta = puerta;
		this.parque = parque;
	}
	
	@Override
	public void run() {
		for (int i = 0; i < NUMSALIDAS; i ++) {
			try {
				parque.salirDelParque(puerta);
				TimeUnit.MILLISECONDS.sleep(new Random().nextInt(5)*1000);
			} catch (InterruptedException e) {
				Logger.getGlobal().log(Level.INFO, "Entrada interrumpida / Finalizada " + puerta );
				Logger.getGlobal().log(Level.INFO, e.toString());
				return;
			}
		}
		Logger.getGlobal().log(Level.INFO,"Finalizada salida por la puerta " + puerta);
		
	}
}
