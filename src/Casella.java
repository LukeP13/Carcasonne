/** @file Casella.java
    @brief Classe Casella
*/

/** @class Casella
    @brief Implementacio d'una Casella del Tauler per posar rajoles
    @author Joel Carrasco Mora
*/

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Casella extends ImageView {

    private Rajola _r; ///< Rajola a posar a la Casella
    private boolean visitada; ///< Boolean per saber si hem visitat la Casella al comprovar-les
    private boolean doble; ///< Boolean per saber si hem vistitat la Casella amb el principi de dues ciutats al comprovar-les
        
    /** @brief Crea una Casella sense imatge
    @pre ---
    @post Crea una Casella amb _r = null i visitada = false*/
    public Casella() {
        _r = null;
        visitada = false;
    }

    /** @brief Crea una Casella amb imatge
    @pre Imatge ima existent
    @post Crea una Casella amb _r = null i visitada = false amb imatge*/
    public Casella(Image ima, int amplada) {
        _r = null;
        visitada = false;
        setImage(ima);
        setFitWidth(amplada);
        setPreserveRatio(true);  
        setSmooth(true);         
        setCache(true);  
    }

    /** @brief Consulta si la Casella esta ocupada per una Rajola
    @pre ---
    @post Retorna cert si la Casella esta ocupada o fals altrament*/
    public boolean ocupada() {
            return _r != null;
    }

    /** @brief Afegeix una Rajola a la Casella
    @pre ---
    @post Atribut de Rajola _r = r*/
    public void afegir(Rajola r) {
            if(!ocupada())_r = r;
    }

    /** @brief Retorna la Rajola que ocupa la Casella
    @pre ---
    @post Retorna _r*/
    public Rajola rajola() { return _r; }
        
    /** @brief Asigna la Casella com a visitada
	@pre ---
	@post Atribut visitada = true*/
    public void visita(){ visitada = true; }
    
    /** @brief Asigna la Casella com a no visitada
	@pre ---
	@post Atribut visitada = false*/
    public void allibera(){ visitada = false; }
    
    /** @brief Consulta si la Casella ha estat visitada
	@pre ---
	@post Retorna cert si Casella ha estat visitada o fals altrament*/
    public boolean visitada(){ return visitada; }
    
    /** @brief Asigna la Casella de ciutat doble com a visitada
	@pre ---
	@post Atribut visitada = true*/
    public void visitaDoble(){ doble = true; }
    
    /** @brief Asigna la Casella de ciutat doble com a no visitada
	@pre ---
	@post Atribut visitada = false*/
    public void alliberaDoble(){ doble = false; }
    
    /** @brief Consulta si la Casella de ciutat doble ha estat visitada
	@pre ---
	@post Retorna cert si Casella de ciutat doble ha estat visitada o fals altrament*/
    public boolean visitadaDoble(){ return doble; }
    
    /** @invariant ---*/
}
