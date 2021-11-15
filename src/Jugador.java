/** @file Jugador.java
    @brief Interface Jugador
*/ 

/** @class Jugador
    @brief Interface Jugador que controla els punts i seguidors propis
    @author Lluc Pages Perez
*/
public interface Jugador{
    
    /** @brief Diu quin tipus de seguidor es
    @pre: ---
    @post: Retorna string segons tipus de jugador */
    public String tipusJugador();

    /** @brief Diu quants punts te jugador
    @pre: ---
    @post: Retorna la quantitat de punts acumulats */
    public int punts();
    
    /** @brief Retorna seguidor numero i
    @pre: 0 <= i < 7
    @post: Diu quin tipus de seguidor és */
    public Seguidor takeSeguidor(int i);
    
    /** @brief Suma punts a Jugador
    @pre: ---
    @post: Puntuacio augmentada */
    public void afegPunts(int pts);
}