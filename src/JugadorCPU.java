/** @file JugadorCPU.java
    @brief Classe JugadorCPU
*/

import java.io.PrintWriter;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.Group;

/** @class JugadorCPU
    @brief Jugador controlat per la maquina, implementa interface Jugador
    @author Lluc Pages Perez
*/
public class JugadorCPU implements Jugador{
    
    private int punts; ///< Punts que porta acumulats
    private Seguidor [] seguidors; ///< Seguidors que pertanyen a jugador
    
    /** @brief Constructor amb parametres
    @pre: ---
    @post: Inicialitza JugadorCPU */
    public JugadorCPU(Image img){
        punts = 0;
        seguidors = new Seguidor[7];
        for(int i = 0; i < 7; i++) seguidors[i] = new Seguidor(img, this);
    }
    
    @Override
    public int punts(){
        return punts;
    }

    @Override
    public String tipusJugador() {
        return "CPU";
    }

    @Override
    public Seguidor takeSeguidor(int i) {
        return seguidors[i];
    }

    @Override
    public void afegPunts(int pts) {
        punts += pts;
        Joc.setScore(this, pts);
    }
    
    /** @brief Realitza torn
    @pre: pw i t not null
    @post: Coloca rajola i retorna si s'ha pogut colocar */
    public boolean torn(Tauler t, Rajola r, Group grupCaselles, Image imatgeCasella, Pane root, PrintWriter pw){
        Posicio p = colocarRajola(t,r,grupCaselles,imatgeCasella,root);
        if(p != null){
            pw.println("Rajola " + r + " colocada a la posicio " + p);
            char c = colocarSeguidor(t,p);
            if(c != 'X') pw.println("Seguidor colocat a posicio " + c + " de la rajola");
            t.puntuar(p);
        }
        return p!=null;
    }
    
    /** @brief Escull Seguidor a colocar
    @pre: ---
    @post: Retorna seguidor a colocar o null en cas que cap estigui disponible */
    private Seguidor escullSeguidor(){
        int i = 0;
        Seguidor res;
        while(!seguidors[i].disponible() && i < 7) i++;
        if(!seguidors[i].disponible()) res = null;
        else res = seguidors[i];
        return res;
    }
    
    /** @brief Coloca seguidor
    @pre: posicio p del tauler ocupada
    @post: Coloca seguidor a la rajola i diu en quina part ho ha fet */
    private char colocarSeguidor(Tauler t, Posicio p){
        Seguidor s = escullSeguidor();
        char c = 'X';
        if(s != null){
            boolean colocat = false;
            int i = 0, px = 0, py = 0;
            Posicio pAux = p.clone();
            while(!colocat && i < 5){
                if(i == 1){ c = 'M'; px = 20; py = 20;}
                else if(i == 0){ c = 'N'; px = 20; py = -10; }
                else if(i == 2){ c = 'E'; px = 40; py = 20; }
                else if(i == 3){ c = 'S'; px = 20; py = 40; }
                else{ c = 'W'; px = -10; py = 20; }
                colocat = t.afegirSeguidor(s, c, pAux);
                i++;
            }
            if(colocat){
                s.move((p.getX()+Tauler.X_INI)*70+px, (p.getY()+Tauler.Y_INI)*70+py);
                s.coloca();
            }
        }
        return c;
    }
    
    /** @brief Coloca rajola
    @pre: ---
    @post: Coloca rajola si es possible i retorna en quina posicio ho ha fet, sino retorna null */
    private Posicio colocarRajola(Tauler t, Rajola r, Group grupCaselles, Image imatgeCasella, Pane root){
        boolean colocat = false;
        int x = 0, y = 0, rot = 0;
        Posicio p = null;
        while(!colocat && rot < 4){
            colocat = t.afegirRajola(r, p = new Posicio(x, y), grupCaselles, imatgeCasella, root);
            if(!colocat){
                x++;
                if(!t.posicioValida(x, y)){
                    x=0; y++;
                }
                if(!t.posicioValida(x, y)){
                    x = y = 0;
                    r.rotarDreta(); rot++;
                }
            }
        }
        if(colocat){
            r.move(p.getX()+Tauler.X_INI, p.getY()+Tauler.Y_INI);
            r.coloca();
            return p;
        }
        else return null;
    }   
    
}