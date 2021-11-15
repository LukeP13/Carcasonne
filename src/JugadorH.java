/** @file JugadorH.java
    @brief Classe JugadorH
*/

import javafx.scene.image.Image;

/** @class JugadorH
    @brief Jugador controlat per huma, implementa interface Jugador
    @author Lluc Pages Perez
*/
public class JugadorH implements Jugador{
    
    private int punts; ///< Punts que porta acumulats
    private Seguidor [] seguidors; ///< Seguidors que pertanyen a jugador
    
    /** @brief Constructor amb parametres
    @pre: ---
    @post: Inicialitza Jugador Huma */
    public JugadorH(Image img){
        punts = 0;
        seguidors = new Seguidor[7];
        for(int i = 0; i < 7; i++) seguidors[i] = new Seguidor(img, this);
    }
    
    @Override
    public String tipusJugador() {
        return "Huma";
    }
    
    @Override
    public int punts(){
        return punts;
    }
    
    @Override
    public Seguidor takeSeguidor(int i){
        return seguidors[i];
    }
    
    @Override
    public void afegPunts(int pts) {
        punts += pts;
        Joc.setScore(this, pts);
    }
    
}

    
