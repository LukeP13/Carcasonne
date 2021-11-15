/** @file Baralla.java
    @brief Classe Baralla
*/

/** @class Baralla
    @brief Implementacio d'una pila de Rajoles utilitzant una LinkedList
    @author Joel Carrasco Mora
*/

import java.util.LinkedList;
import java.util.Collections;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Baralla extends ImageView{
	
	private LinkedList<Rajola> _b; ///< Guardarem les rajoles a una LinkedList i la utilitzarem a forma de pila
	
	/** @brief Crea una baralla buida amb imatge
	@pre Imatge ima existent
	@post Crea una llista de Rajoles _b buida*/
	public Baralla(Image ima) {
		_b = new LinkedList<Rajola>();
                setImage(ima);
                setFitWidth(150);
                setPreserveRatio(true);
                setSmooth(true);
                setCache(true); 
	}
	
	/** @brief Afegeix Rajola a la pila
	@pre ---
	@post Afegeix la Rajola a la pila _b*/
	public void afegirRajola(Rajola r) {
		_b.push(r);
	}
	
	/** @brief Barreja les rajoles aleatoriament
	@pre _b no buida
	@post Barreja aleatoriament les Rajoles dins de _b*/
	public void barrejar() {
		Collections.shuffle(_b);
	}
	
	/** @brief Retorna primera Rajola
	@pre ---
	@post Retorna la primera rajola de _b i la treu de la pila*/
	public Rajola obtenirRajola() {
        if(!esBuida()) return _b.pop();
        else return null;
	}

	/** @brief Consulta si la baralla es buida
	@pre ---
	@post Retorna cert si _b es buida o fals altrament*/
	public boolean esBuida() {
		return _b.isEmpty();
	}
	
	
	/** @brief Consulta les rajoles restants
	@pre ---
	@post Retorna cert si _b es buida o fals altrament*/
	public int restant() {
		return _b.size();
	}
	
	/** @invariant ---*/
}
