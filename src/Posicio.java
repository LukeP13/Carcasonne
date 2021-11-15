/** @file Posicio.java
    @brief Classe Posicio
*/

/** @class Posicio
    @brief Peca Seguidor, es pot colocar al tauler sobre una rajola per puntuar
    @author Lluc Pages Perez
*/
public class Posicio implements Cloneable{

    private int _x; ///< Coordenades x de la posició
    private int _y; ///< Coordenades y de la posició
    
    /** @brief Constructor per defecte
    @pre: ---
    @post: inicialitza posicio a 0 */
    public Posicio(){
        _x = 0;
        _y = 0;
    }
    
    /** @brief Constructor amb parametres
    @pre: ---
    @post: Inicialitza posicio a x,y */
    public Posicio(int x, int y){
        _x = x;
        _y = y;
    }
    
    /** @brief Crea una copia de la posicio
    @pre: ---
    @post: Retorna una Posicio copia */
    @Override
    public Posicio clone(){
    	Object obj=null;
        try{
            obj=super.clone();
        }catch(CloneNotSupportedException ex){
            System.err.println("No es pot duplicar");
        }
        return (Posicio)obj;
    }
    
    /** @brief Diu quina es la coordenada x
    @pre: ---
    @post: retorna coordenada x */
    public int getX(){
        return _x;
    }

    /** @brief Diu quina es la coordenada y
    @pre: ---
    @post: retorna coordenada y */
    public int getY(){
        return _y;
    }
    
    /** @brief Modifica la coordenada x
    @pre: ---
    @post: Valor assignat a x */
    public void setX(int x) {
    	_x = x;
    }
    
    /** @brief Modifica la coordenada y
    @pre: ---
    @post: Valor assignat a y */
    public void setY(int y) {
    	_y = y;
    }

    /** @brief Compara la posicio actual amb p
    @pre: ---
    @post: Diu si la posició es mes gran o mes petita a p */
    public int compareTo(Posicio p){
        int res = -1;
        if(_y > p._y) res = 1;
        else if (_y == p._y){
            if(_x > p._x) res = 1;
            else if(_x == p._x) res = 0;
        }
        return res;
    }    
    
    /** @brief Diu si la posicio actual es igual a o
    @pre: ---
   	@post: Diu si this es igual a o */
    @Override
    public boolean equals(Object o){
        if(o instanceof Posicio){ //Mirem si l'objecte és una posició
            Posicio p = (Posicio) o;
            return this.compareTo(p) == 0;
        }
        else{ //L'objecte no és una posició
            return false;
        }
    }
    
    /** @brief Diu com representar la posicio en format string
    @pre: ---
    @post: Retorna string que representa la posicio */
    @Override
    public String toString(){
        return "[" + _x + "," + _y + "]";
    }

}
