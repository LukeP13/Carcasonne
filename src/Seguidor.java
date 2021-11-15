/** @file Seguidor.java
    @brief Classe Seguidor
*/

import javafx.scene.input.MouseEvent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/** @class Seguidor
    @brief Peça Seguidor, es pot colocar al tauler sobre una rajola per puntuar
    @author Lluc Pages Perez
*/
public class Seguidor extends ImageView{
    
    public static final int PIXELS = 30; ///< Mida en pixels de la peça
    
    private int xIni; ///< Posició x en la pantalla on es coloca a l'inici
    private int yIni; ///< Posició y en la pantalla on es coloca a l'inici
    
    private boolean colocat; ///< Ens diu si el seguidor està colocat o està lliure
    private Jugador prop; ///< Jugador "propietari" de la peça

    private double _mouseX, _mouseY; ///< coordenades del clic del mouse (coord. pantalla)
    private double _oldX, _oldY;     ///< posició actual de la peça (coord. pantalla)
    
    /** @brief Constructor amb parametres
    @pre: ---
    @post: Inicialitza Seguidor amb imatge i propietari 'j'. Crea eventHandlers pressed i drag*/
    public Seguidor(Image ima, Jugador j){
        colocat = false;
        prop = j;
        setImage(ima);
        setFitWidth(PIXELS);    // redimensionament, establim amplada
        setPreserveRatio(true);  // no volem distorsions
        setSmooth(true);         // volem qualitat a la representació de la imatge
        setCache(true); 
        
        //EventHandler Click i Drag
        setOnMousePressed((MouseEvent e) -> {  // Hi afegim un EventHandler anòmim amb funció lambda 
           _mouseX = e.getSceneX();  // obtenim coordenades pantalla del lloc del clic, i les guardem
           _mouseY = e.getSceneY();
        } );
        setOnMouseDragged((MouseEvent e) -> {   // EventHandler per moure la peça quan l'arrosseguem 
           relocate(e.getSceneX()-_mouseX+_oldX, e.getSceneY()-_mouseY+_oldY);
        } );
    }
    
    /** @brief Ens diu si el seguidor està disponible per utilitzar
    @pre: ---
    @post: diu si seguidor està colocat */
    public boolean disponible(){
        return !colocat;
    }
    
    /** @brief Ens diu quin es el jugador propietari
    @pre: ---
    @post: Retorna el jugador propietari */
    public Jugador propietari(){
        return prop;
    }
    
    /** @brief Coloca seguidor
    @pre: ---
    @post: Seguidor colocat */
    public void coloca(){
        colocat = true;
    }
    
    /** @brief Recull seguidor i el torna a la posicio inicial
    @pre: xIni && yIni no null
    @post: seguidor no colocat */
    public void recull(){
        colocat = false;
        move(xIni,yIni);
    }
    
    /** @brief Inicialitza la posició inicial
    @pre: ---
    @post: Posicio inicial inicialitzada */
    public void setPosIni(int x, int y){
        xIni = x;
        yIni = y;
    }
    
    /** @brief Afegeix puntuacio al propietari
    @pre: ---
    @post: Punts afegits */
    public void puntua(int pts){
        prop.afegPunts(pts);
    }
    
    // ----------- Operacions de moviment de la peça ------------ //
    
    /** @brief Recupera la posicio x anterior
    @pre: ---
    @post: Diu quina era la posicio x anterior*/
    public double getOldX() {
        return _oldX;
    }
    
    /** @brief Recupera la posicio y anterior
    @pre: ---
    @post: Diu quina era la posicio y anterior*/
    public double getOldY() {
        return _oldY;
    }
    
    /** @brief Mou el seguidor a la posició x,y en pixels
    @pre: ---
    @post: peça moguda a posició x,y */
    public void move(int x, int y) {
        _oldX = x; 
        _oldY = y;
        relocate(_oldX, _oldY);
    }
    
    /** @brief Recupera la posicio anterior
    @pre: ---
    @post: Torna a la posicio anterior*/
    public void abortMove() {
        relocate(_oldX, _oldY);
    }
    
    /** @brief Mou el seguidor respecte la posicio anterior
    @pre: ---
    @post: recoloca el seguidor segons posicio anterior i coordenades x i y*/
    public void addMove(int x, int y){
        _oldX += x;
        _oldY += y;
        relocate(_oldX, _oldY);
    }
    
    /** @brief Diu si el seguidor actual és igual a l'objecte
    @pre: ---
    @post: Diu si this es igual a o */
    @Override
    public boolean equals(Object o){
        if(o instanceof Seguidor){ //Mirem si l'objecte és una posició
            Seguidor s = (Seguidor) o;
            boolean res = (prop == s.prop) && (xIni == s.xIni) && (yIni == s.yIni);
            return res;
        }
        else{ //L'objecte no és una posició
            return false;
        }
    }
}