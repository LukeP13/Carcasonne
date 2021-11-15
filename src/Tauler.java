/** @file Tauler.java
    @brief Classe tauler
*/

/** @class Tauler
    @brief Implementacio d'un Tauler de caselles dinamic per posar rajoles
    @author Joel Carrasco Mora, ajudat per Lluc Pages Perez
*/

import java.util.LinkedList;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

public class Tauler {
	
    private Casella [][] _t; ///< Matriu de caselles per formar el Tauler
    private static final int MIDA_CASELLA = 70; //< Mida de les caselles
    private static final int MIDA_INICIAL = 5; //< Mida inicial del tauler MIDA_INICIAL^2
	
    public static final int X_INI = 5; //< Posicions x,y per la Rajola inicial del joc
    public static final int Y_INI = 1;
        
    /** @brief Crea un Tauler amb la mida inicial
    @pre --
    @post Crea un Tauler MIDA_INICIAL^2*/
    public Tauler() {
            _t = new Casella[MIDA_INICIAL][MIDA_INICIAL];
    }
	
    /** @brief Crea grup caselles
    @pre Imatge ima existent
    @post Crea un grup de caselles amb la imatge ima corresponent i els guarda la posicio*/
    public Group grupCaselles(Image img) {
        Group grupCaselles = new Group();
        Image imatgeCasella = null; 

        imatgeCasella = img;

        for (int i=0; i < MIDA_INICIAL; i++) {
            for (int j=0; j < MIDA_INICIAL; j++) {
                Casella casella = new Casella(imatgeCasella, MIDA_CASELLA);
                casella.setX((j+X_INI)*MIDA_CASELLA);
                casella.setY((i+Y_INI)*MIDA_CASELLA);
                _t[j][i] = casella;
                grupCaselles.getChildren().add(casella);
            }
        }
        return grupCaselles;
    }

    /** @brief Afegeix Rajola al Tauler a la Posicio desitjada
    @pre Rajola r != null, grupCaselles creat, imatgeCasella existent i root creat 
    @post Afegeix la Rajola r a la Posicio desitjada i retorna cert si s'ha posat exitosament o fals altrament, a mes si fa falta expandeix el Tauler i el Pane root*/
    public boolean afegirRajola(Rajola r, Posicio p, Group grupCaselles, Image imatgeCasella, Pane root) {
            boolean afegida = false;
            if(posicioValida(p.getX(), p.getY()) && !_t[p.getX()][p.getY()].ocupada()) {
                    if(teAdjacent(p) && coincideix(r, p)) {
                            if(esLimit(p)) {
                                    expandir(grupCaselles, imatgeCasella, root);
                                    p.setX(p.getX()+1);
                                    p.setY(p.getY()+1);
                            }
                            _t[p.getX()][p.getY()].afegir(r);
                            afegida = true;
                    }
            }
            return afegida;
    }
     
    /** @brief Afegeix Rajola inicial
    @pre Rajola r != null i Posicio p valida
    @post Afegeix la Rajola inicial r a la Posicio p*/
    public void afegirRajolaIni(Rajola r, Posicio p){
            _t[p.getX()][p.getY()].afegir(r);
            r.move(p.getX()+X_INI, p.getY()+Y_INI);
    }
	
    /** @brief Modifica Posicio segons la direccio que es vol anar
    @pre Posicio p inicialitzada
    @post Modifica la Posicio p segons la direccio direcc*/
    private void modificarPosicio(char direcc, Posicio p) {
            switch (direcc) {
            case 'N':
                    p.setY(p.getY() - 1);
            break;
            case 'S':
                    p.setY(p.getY() + 1);
            break;
            case 'W':
                    p.setX(p.getX() - 1);
            break;
            case 'E':
                    p.setX(p.getX() + 1);
            break;
            }
    }
	
    /** @brief Modifica la direccio del cami
    @pre Posicio p i direcc valides
    @post Retorna la direccio on va el cami que no sigui igual a direcc*/
    private char modificarDireccioCami(char direcc, Posicio p) {
            if(_t[p.getX()][p.getY()].ocupada()) direcc = _t[p.getX()][p.getY()].rajola().direccioCami(direcc);
            return direcc;
    }
	
    /** @brief Retorna la direccio contraria a la direccio entrada
    @pre --
    @post Retorna la direccio contraria a direcc*/
    private char direccioContraria(char direcc) {
            switch (direcc) {
            case 'N':
                    direcc = 'S';
            break;
            case 'S':
                    direcc = 'N';
            break;
            case 'E':
                    direcc = 'W';
            break;
            case 'W':
                    direcc = 'E';
            break;
            }
            return direcc;
    }
	
    /** @brief Consulta si el mig de la Rajola a la Posicio es especial (fi o inici de cami)
    @pre Posicio p valida
    @post Retorna cert si el mig es encreuament ('X'), monestir ('M') o vila ('V') */
    private boolean migEspecial(Posicio p) {
    char c = _t[p.getX()][p.getY()].rajola().mig();
    return c == 'X' || c == 'M' || c == 'V';
    }
	
    /** @brief Busca si hi ha seguidor en un cami que comenci per la direccio desitjada
    @pre Posicio p i direcc valides
    @post Retorna cert si ha trobat Seguidor seguint el cami comencat en Posicio p o fals altrament*/
    private boolean seguirSeguidor(char direcc, Posicio p) {
            boolean seguidor = false;
            Casella inicial = _t[p.getX()][p.getY()];
            seguidor = inicial.rajola().seguidorTipus(direcc, 'C');
            if(!seguidor) {
                    modificarPosicio(direcc, p);
                    if(_t[p.getX()][p.getY()].ocupada() && (!migEspecial(p) || _t[p.getX()][p.getY()].rajola().rajolaEspecial())) {
                            direcc = modificarDireccioCami(direcc, p);
                    }
            }
            while (!seguidor && _t[p.getX()][p.getY()].ocupada() && (!migEspecial(p) ||
                            _t[p.getX()][p.getY()].rajola().rajolaEspecial()) && _t[p.getX()][p.getY()] != inicial) {

                    seguidor = _t[p.getX()][p.getY()].rajola().seguidorTipus('Q', 'C');
                    if(!seguidor)modificarPosicio(direcc, p);
                    if(_t[p.getX()][p.getY()].ocupada() && (!migEspecial(p) || _t[p.getX()][p.getY()].rajola().rajolaEspecial())) {
                            direcc = modificarDireccioCami(direcc, p);
                    }

            }
            if (!seguidor && _t[p.getX()][p.getY()].ocupada() && migEspecial(p) && !_t[p.getX()][p.getY()].rajola().rajolaEspecial()){
                    direcc = direccioContraria(direcc);
                    seguidor = _t[p.getX()][p.getY()].rajola().seguidorTipus(direcc, 'C');
            }
            return seguidor;
    }
	
    /** @brief Comprova si els camins que surten d'una Rajola estan ocupats
    @pre Posicio p i direcc valides
    @post Retorna cert si ha trobat Seguidor o fals altrament*/
    private boolean camiOcupat(char direcc, Posicio p) {
            boolean seguidor = false;
            if(((!migEspecial(p) || _t[p.getX()][p.getY()].rajola().rajolaEspecial()) || _t[p.getX()][p.getY()].rajola().rajolaEspecial())) {
                    seguidor = seguirSeguidor('N', p.clone()) || seguirSeguidor('S', p.clone()) || 
                                    seguirSeguidor('E', p.clone()) || seguirSeguidor('W', p.clone()) || _t[p.clone().getX()][p.clone().getY()].rajola().seguidorTipus('M', 'C');
            }
            else seguidor = seguirSeguidor(direcc, p);
            return seguidor;
    }
	
    /** @brief Comprova si una ciutat esta ocupada per un Seguidor
    @pre Posicio p i direcc valides
    @post Retorna cert si a la ciutat hi ha Seguidor o fals altrament*/
    private boolean ciutatOcupada(char direcc, Posicio p) {
        boolean seguidor = false;
        int x = p.getX(), y = p.getY();
        Rajola r = _t[x][y].rajola();
        if(r.mig() != 'V'){
            if(direcc == 'N' && r.nord() == 'V') seguidor = iCiutatOcupada('N', p, p);     
            else if(direcc == 'E' && r.est() == 'V') seguidor = iCiutatOcupada('E', p, p);
            else if(direcc == 'S' && r.sud() == 'V') seguidor = iCiutatOcupada('S', p, p);
            else if(direcc == 'W' && r.oest() == 'V') seguidor = iCiutatOcupada('W', p, p);
        } else {
        boolean s1, s2, s3, s4;
        s1 = s2 = s3 = s4 = false;
            if(r.nord() == 'V') s1 = iCiutatOcupada('N', p, p);
            if(s1 == false && r.est() == 'V') s2 = iCiutatOcupada('E', p, p);
            if(s1 == false && s2 == false && r.sud() == 'V') s3 = iCiutatOcupada('S', p, p);   
            if(s1 == false && s2 == false && s3 == false && r.oest() == 'V') s4 = iCiutatOcupada('W', p, p);
            seguidor = s1 || s2 || s3 || s4;
        }
        alliberaCaselles();
        return seguidor;
    }

    /** @brief Comprova si una ciutat esta ocupada per un Seguidor (inmersio)
    @pre Posicio p i direcc valides
    @post Retorna cert si a la ciutat hi ha Seguidor o fals altrament*/
    private boolean iCiutatOcupada(char direcc, Posicio p, Posicio pInicial) {
            boolean seguidor = false;
        Posicio pAct = p.clone();
            modificarPosicio(direcc, pAct);
        int x = pAct.getX(), y = pAct.getY(); 
        if(!pAct.equals(pInicial)){
            if(_t[x][y].ocupada() && !_t[x][y].visitada()) {
                    char direcCont = direccioContraria(direcc); //Direccio contraria a la arribada
                    Rajola r = _t[x][y].rajola();
                    if(r.mig() == 'V') {
                            _t[x][y].visita();
                            seguidor = r.seguidorTipus('Q', 'V');
                            if(direcCont != 'N' && r.nord() == 'V' && !seguidor) seguidor = iCiutatOcupada('N', pAct, pInicial);
                            if(direcCont != 'E' && r.est() == 'V' && !seguidor) seguidor = iCiutatOcupada('E', pAct, pInicial);
                            if(direcCont != 'S' && r.sud() == 'V' && !seguidor) seguidor = iCiutatOcupada('S', pAct, pInicial);
                            if(direcCont != 'W' && r.oest() == 'V' && !seguidor) seguidor = iCiutatOcupada('W', pAct, pInicial);
                    }
                    else seguidor = r.seguidorTipus(direcCont, 'V');
            }
        }		
            return seguidor;
    }
		
    /** @brief Comprovar si hi ha seguidor a un cami, vila o monestir
    @pre Posico p i direcc valides
    @post Retorna cert si ha trobat seguidor partint de p i direcc o fals altrament*/
    private boolean hiHaSeguidor(char direcc, Posicio p){
            boolean seguidor = false;
            switch (_t[p.getX()][p.getY()].rajola().toType(direcc)) {
                case 'C':
                    seguidor = camiOcupat(direcc, p);
                    break;
                case 'V':
                        seguidor = ciutatOcupada(direcc, p);
                    break;
                case 'M':
                    seguidor = _t[p.getX()][p.getY()].rajola().seguidorMonestir();
                    break;
                default:
                    seguidor = true; //Territori no valid
                    break;
            }
            return seguidor;
    }
	
    /** @brief Puntuar cami als jugadors
    @pre Posicio p i direcc valides
    @post Assigna als seguidors (si hi ha) els punts del cami si aquest esta tancat*/
    private void puntuarCami(char direcc, Posicio p) {
        int punts = 0;
        LinkedList<Posicio> seguid = new LinkedList<>();
        Casella inicial = _t[p.getX()][p.getY()];
        if(_t[p.getX()][p.getY()].rajola().seguidorTipus(direcc, 'C')) 
                seguid.push(p.clone());
        punts++;
        modificarPosicio(direcc, p); 
        if(_t[p.getX()][p.getY()].ocupada() && (!migEspecial(p) || _t[p.getX()][p.getY()].rajola().rajolaEspecial())) 
            direcc = modificarDireccioCami(direcc, p);
        
        while (_t[p.getX()][p.getY()].ocupada() && (!migEspecial(p) || _t[p.getX()][p.getY()].rajola().rajolaEspecial()) && _t[p.getX()][p.getY()] != inicial) {
                if(_t[p.getX()][p.getY()].rajola().teSeguidor() && _t[p.getX()][p.getY()].rajola().seguidorTipus('Q', 'C')) //Per mirar si el seguidor es a un cami
                        seguid.push(p.clone());
                punts++;
                modificarPosicio(direcc, p);
                if(_t[p.getX()][p.getY()].ocupada() && (!migEspecial(p) || _t[p.getX()][p.getY()].rajola().rajolaEspecial())) 
                        direcc = modificarDireccioCami(direcc, p);
        }
        if(_t[p.getX()][p.getY()].ocupada() && migEspecial(p) && !_t[p.getX()][p.getY()].rajola().rajolaEspecial() && _t[p.getX()][p.getY()] != inicial) {
                punts++;
                direcc = direccioContraria(direcc);
                if (_t[p.getX()][p.getY()].rajola().seguidorTipus(direcc, 'C')) {
                        seguid.push(p.clone());
                }
        }
        while(!seguid.isEmpty() && _t[p.getX()][p.getY()].ocupada()){
                Posicio pAux = seguid.pop();
                _t[pAux.getX()][pAux.getY()].rajola().seguidor().puntua(punts);
                retornarSeguidor(pAux);
        }
    }

    /** @brief Puntuar ciutat al Jugador amb mes seguidors
	@pre LinkedList llista buida
	@post Puntua ciutat al Jugador amb mes seguidors a la ciutat*/
    private void puntuaSeguidorsCiutat(LinkedList<Posicio> list, int pts){
        if(!list.isEmpty()){
            Posicio p = list.pop();

            Seguidor s = _t[p.getX()][p.getY()].rajola().seguidor();
            retornarSeguidor(p);

            int n = 0;
            Seguidor [] ls =  new Seguidor [Joc.nJugadors()];
            int [] li = new int [Joc.nJugadors()];
            ls[n] = s; li[n] = 1; n++;

            while(!list.isEmpty()){
                p = list.pop();
                s = _t[p.getX()][p.getY()].rajola().seguidor();
                retornarSeguidor(p);

                boolean trobat = false;
                int i = 0;
                while(!trobat && i < n){
                    trobat = ls[i].propietari() == s.propietari();
                    if(!trobat) i++;
                }
                if(trobat) li[i]++;
                else{
                    ls[i] = s;
                    li[i] = 1;
                    n++;
                }
            }
            int max = 0;
            for(int i = 1; i < n; i++)
                if(li[max] < li[i]) max = i;

            ls[max].puntua(pts);
        }
    }
    
    /** @brief Consulta punts vila (inmersio)
	@pre Posicions i direcc valides
	@post Retorna els punts que suma una ciutat en cas de estar tancada o -1 altrament*/
    private int iPuntuaVila(Posicio pAnt, char direcc, Posicio pInicial, LinkedList<Posicio> seguidors){
    	int pts = 0;
    	Posicio pAct = pAnt.clone();
    	modificarPosicio(direcc, pAct);
    	int x = pAct.getX(), y = pAct.getY();
        if(!pAct.equals(pInicial)){
        	if(_t[x][y].ocupada() && !_t[x][y].visitada()){
        		Rajola r = _t[x][y].rajola();
        		int pts1 = 0, pts2 = 0, pts3 = 0, pts4 = 0;
                if(r.mig() == 'V'){
                	_t[x][y].visita();
                	if(r.nord() == 'V') pts1 = iPuntuaVila(pAct, 'N', pInicial, seguidors);
                	if(r.est() == 'V') pts2 = iPuntuaVila(pAct, 'E', pInicial, seguidors);
                        if(r.sud() == 'V') pts3 = iPuntuaVila(pAct, 'S', pInicial, seguidors);
                        if(r.oest() == 'V') pts4 = iPuntuaVila(pAct, 'W', pInicial, seguidors);
                        
                        if(pts1 == -1 || pts2 == -1 || pts3 == -1 || pts4 == -1) pts = -1;
                        else pts = pts1 + pts2 + pts3 + pts4 + 2; //Augmentem en 1 els punts
                        if(pts > -1 && r.seguidorTipus('Q', 'V')) seguidors.push(pAct);
                } else {
                	char c = direccioContraria(direcc);
                	if(!_t[x][y].visitadaDoble()){
                		pts += 2;
                		_t[x][y].visitaDoble();
                	}
                    if(r.seguidorTipus(c, 'V')) seguidors.push(pAct);
                }
        	} else if(!_t[x][y].ocupada()) pts = -1;
        }
        return pts;
    }
    
    /** @brief Puntua vila des d'una direccio
	@pre Posicio p i direcc valides
	@post Puntua al Seguidor que toca*/
    private void puntuaVila(Posicio p, char direcc){
        int pts = 0;
        Posicio pAux = p.clone();
        modificarPosicio(direcc, pAux);
        int xAux = pAux.getX(), yAux = pAux.getY();
        int x = p.getX(), y = p.getY();

        if(_t[xAux][yAux].ocupada() && !_t[xAux][yAux].visitada()){
            LinkedList<Posicio> seguidors = new LinkedList<>();
            pts = iPuntuaVila(p, direcc, p, seguidors);
            if(pts > -1 && _t[x][y].rajola().seguidorTipus(direcc, 'V')) 
                seguidors.push(p);
            if(pts > 0 && !seguidors.isEmpty())
                puntuaSeguidorsCiutat(seguidors, pts+2);
        }
    }
    
    /** @brief Puntua vila al Jugador
	@pre Posicio p valida
	@post Puntua vila al Jugador amb mes seguidors a aquesta en cas d'estar tancada*/
    private void puntuarViles(Posicio p){
        int x = p.getX(), y = p.getY();
        Rajola r = _t[x][y].rajola();
        if(r.mig() != 'V'){
            if(r.nord() == 'V') puntuaVila(p, 'N');
            if(r.est() == 'V') puntuaVila(p, 'E');
            if(r.sud() == 'V') puntuaVila(p, 'S');
            if(r.oest() == 'V') puntuaVila(p, 'W');
        } else {
            LinkedList<Posicio> seguidors = new LinkedList<>();
            int pts = 0, pts1 = 0, pts2 = 0, pts3 = 0, pts4 = 0;
            if(r.nord() == 'V') pts1 = iPuntuaVila(p, 'N', p, seguidors);
            if(pts1 > -1 && r.est() == 'V') pts2 = iPuntuaVila(p, 'E', p, seguidors);
            if(pts1 > -1 && pts2 > -1 && r.sud() == 'V') pts3 = iPuntuaVila(p, 'S', p, seguidors);
            if(pts1 > -1 && pts2 > -1 && pts3 > -1 && r.oest() == 'V') pts4 = iPuntuaVila(p, 'W', p, seguidors);
            
            if(pts1 == -1 || pts2 == -1 || pts3 == -1 || pts4 == -1) pts = -1;
            else if(!seguidors.isEmpty()){
                pts = pts1 + pts2 + pts3 + pts4 + 2; //Augmentem en 1 els punts
                puntuaSeguidorsCiutat(seguidors, pts);
            } 
                
        }
        alliberaCaselles();
    }
    
    /** @brief Buscar el fi d'un cami
	@pre Posicio p valida
	@post Troba el final d'un cami (mig especial o rajola no ocupada) modificant Posicio p*/
    private void buscarFi(Posicio p) { //Comprovar referencies
            Casella inicial = _t[p.getX()][p.getY()];
            char direcc = _t[p.getX()][p.getY()].rajola().direccioCami('Q'); //Primera direccio a mirar
            modificarPosicio(direcc, p); 
            direcc = modificarDireccioCami(direcc, p);	
            while (_t[p.getX()][p.getY()].ocupada() && (!migEspecial(p) || _t[p.getX()][p.getY()].rajola().rajolaEspecial()) && _t[p.getX()][p.getY()] != inicial) {
                    modificarPosicio(direcc, p);
                    direcc = modificarDireccioCami(direcc, p);
            }
    }
		
    /** @brief Puntuar camins als jugadors 
	@pre Posicio p valida
	@post Puntua als jugador corresponents els camins en cas de que estiguin tancats*/
    private void puntuarCamins(Posicio p){
        Posicio p2 = p.clone(); 
        if((!migEspecial(p) || _t[p.getX()][p.getY()].rajola().rajolaEspecial())) buscarFi(p2);
        if(_t[p2.getX()][p2.getY()].ocupada() && (migEspecial(p2) || _t[p2.getX()][p2.getY()] == _t[p.getX()][p.getY()])) {
            int x = p2.getX(), y = p2.getY();
            if(_t[x][y].rajola().nord() == 'C') puntuarCami('N', p2.clone());
            if(_t[x][y].rajola().sud() == 'C') puntuarCami('S', p2.clone());
            if(_t[x][y].rajola().est() == 'C') puntuarCami('E', p2.clone());
            if(_t[x][y].rajola().oest() == 'C') puntuarCami('W', p2.clone());
        }
    }
	
    /** @brief Puntuar monestir al Jugador
    @pre Posicio p valida
    @post Puntua 9 punts al Jugador amb seguidor al monestir de la Posicio p*/
    private void puntuarMonestirs(Posicio p){
        int x = p.getX()-1, y = p.getY()-1;
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3; j++){
                if(_t[x+i][y+j].ocupada()) 
                    puntuarMonestir(new Posicio(x+i,y+j));
            }
        }
    }
	
    /** @brief Puntuar als jugadors mirant des-de la posicio desitjada
    @pre Posicio p valida
    @post Puntua als jugadors corresponents*/
    public void puntuar(Posicio p){
        puntuarMonestirs(p);
        puntuarCamins(p);
        puntuarViles(p);
    }
	
    /** @brief Comprova si la Rajola esta envoltada per altres rajoles 
    @pre Posicio p valida
    @post Retorna cert si la Rajola de p esta envoltada per 8 rajoles al voltant o fals altrament*/
    private boolean rajolaEnvoltada(Posicio p) {
            return _t[p.getX()][p.getY()].ocupada() && _t[p.getX() - 1][p.getY() - 1].ocupada() && 
                            _t[p.getX() - 1][p.getY()].ocupada() &&_t[p.getX() - 1][p.getY() + 1].ocupada() &&
                            _t[p.getX()][p.getY() - 1].ocupada() &&_t[p.getX()][p.getY() + 1].ocupada() &&
                            _t[p.getX() + 1][p.getY() - 1].ocupada() &&_t[p.getX() + 1][p.getY()].ocupada() &&
                            _t[p.getX() + 1][p.getY() + 1].ocupada();
    }

    /** @brief Puntuar monestir al Jugador
    @pre Posicio p valida
    @post Puntua 9 punts al Jugador en cas de tenir Seguidor al monestir i estar aquest envoltat per 8 rajoles*/
    private void puntuarMonestir(Posicio p) {
            if(_t[p.getX()][p.getY()].rajola().teSeguidor() && rajolaEnvoltada(p) && _t[p.getX()][p.getY()].rajola().mig() == 'M' && _t[p.getX()][p.getY()].rajola().seguidorMonestir()){
                    _t[p.getX()][p.getY()].rajola().seguidor().puntua(9);
                    retornarSeguidor(p);
            }
    }

    /** @brief Retorna el Seguidor de la posicio desitjada al Jugador propietari
    @pre Posicio p valida i amb Rajola != null
    @post Retorna el Seguidor al Jugador propietari en cas d'haver-hi en la Posicio p*/
    public void retornarSeguidor(Posicio p){
            _t[p.getX()][p.getY()].rajola().retornarSeguidor();
    }
	
    /** @brief Afegeix seguidor a la Rajola
    @pre Posicio p i posRajola valides i amb Rajola != null
    @post Retorna cert si el Seguidor ha sigut posat exitosament o fals altrament*/
    public boolean afegirSeguidor(Seguidor s, char posRajola, Posicio p) {
            boolean afegit = false;
            if(posicioValida(p.getX(),p.getY()) && _t[p.getX()][p.getY()].ocupada() && !hiHaSeguidor(posRajola, p.clone())) {
                    _t[p.getX()][p.getY()].rajola().posarSeguidor(s, posRajola);
                    afegit = true;
            }
            return afegit;
    }
	
    /** @brief Comprova si una Rajola es pot posar en almenys una Posicio del Tauler
    @pre Rajola r != null
    @post Cert si almenys hi ha una Posicio valida o fals altrament*/
    public boolean rajolaPossible(Rajola r) {
            boolean possible = false;
            int rotacio;
            for(rotacio = 0; rotacio < 4 && !possible; rotacio++) {
                    for(int i = 0; i < _t.length && !possible; i++) {
                            for(int j = 0; j < _t.length && !possible; j++) {
                                    if(!_t[i][j].ocupada() && teAdjacent(new Posicio(i, j)) && coincideix(r, new Posicio(i, j))) possible = true;
                            }
                    }
                    r.rotarDreta();
            }
            for(int i = 0; i < 4 - rotacio; i++) r.rotarDreta();
            return possible;
    }
	
    /** @brief Comprova si una posicio es dins del rang del Tauler
    @pre ---
    @post Cert si la Posicio es troba dins del rang del Tauler o fals altrament*/
    public boolean posicioValida(int x, int y) {
            return x >= 0 && x < _t.length && y >= 0 && y < _t[0].length;
    }
	
    /** @brief Allibera les caselles visitades
    @pre ---
    @post Allibera totes les caselles visitades*/
    private void alliberaCaselles(){
        for (Casella[] _t1 : _t) {
            for (int j = 0; j < _t[0].length; j++) {
                _t1[j].allibera();
                _t1[j].alliberaDoble();
            }
        }
    }
	
    /** @brief Comprova si hi ha rajoles adjacents a la Posicio desitjada
    @pre Posicio p valida
    @post Retorna cert si hi ha almenys una Rajola adjacent o fals altrament*/
    private boolean teAdjacent(Posicio p) {
            return posicioValida(p.getX() - 1, p.getY()) && _t[p.getX() - 1][p.getY()].ocupada() || posicioValida(p.getX() + 1, p.getY()) && _t[p.getX() + 1][p.getY()].ocupada() ||
                            posicioValida(p.getX(), p.getY() - 1) && _t[p.getX()][p.getY() - 1].ocupada() || posicioValida(p.getX(), p.getY() + 1) && _t[p.getX()][p.getY() + 1].ocupada();
    }

    /** @brief Comprova si la Rajola coincideix amb les adjacents
    @pre Posicio p valida i amb Rajoles adjacents i r != null 
    @post Cert si les direccions de la Rajola coincideixen amb les adjacents o fals si almenys una no coincideix*/
    private boolean coincideix(Rajola r, Posicio p) {
            return (!posicioValida(p.getX() - 1, p.getY()) || !_t[p.getX() - 1][p.getY()].ocupada() || _t[p.getX() - 1][p.getY()].rajola().est() == r.oest()) && 
                            (!posicioValida(p.getX() + 1, p.getY()) || !_t[p.getX() + 1][p.getY()].ocupada() || _t[p.getX() + 1][p.getY()].rajola().oest() == r.est()) &&
                            (!posicioValida(p.getX(), p.getY() - 1) || !_t[p.getX()][p.getY() - 1].ocupada() || _t[p.getX()][p.getY() - 1].rajola().sud() == r.nord()) &&
                            (!posicioValida(p.getX(), p.getY() + 1) || !_t[p.getX()][p.getY() + 1].ocupada() || _t[p.getX()][p.getY() + 1].rajola().nord() == r.sud());
    }

    /** @brief Comprova si la posicio desitjada es al limit del Tauler
    @pre Posicio p valida
    @post Retorna cert si la Posicio p es al limit del Tauler o fals altrament*/
    private boolean esLimit(Posicio p) {
            return p.getX() == _t.length - 1 || p.getX() == 0 || p.getY() == _t[0].length - 1 || p.getY() == 0;
    }
	
    /** @brief Assignar i mostrar caselles inicials
    @pre grupCaselles creat i imatgeCasella existent
    @post Assigna i mostra en pantalla les caselles que falten a aux per assignar per dalt i per l'esquerra*/
    private void afegirCasellesInicials(Casella [][] aux, Group grupCaselles, Image imatgeCasella) {
            for(int i = 0; i < _t.length; i++) {
                    Casella casella = new Casella();
        aux[0][i] = casella;
            }
            for(int j = 1; j < _t.length; j++) {
                    Casella casella = new Casella();
        aux[j][0] = casella;
            }
    }
	
    /** @brief Assignar i mostrar caselles finals
    @pre grupCaselles creat i imatgeCasella existent
    @post Assigna i mostra en pantalla les caselles que falten a aux per assignar per sota i per la dreta*/
    private void afegirCasellesFinals(Casella [][] aux, Group grupCaselles, Image imatgeCasella) {
        for (int i = 0; i < aux.length; i++) {
            for (int j = _t.length; j < aux.length; j++) {
                Casella casella = new Casella(imatgeCasella, MIDA_CASELLA);
                casella.setX((j+X_INI)*MIDA_CASELLA);
                casella.setY((i+Y_INI)*MIDA_CASELLA);
                aux[j][i] = casella;
                grupCaselles.getChildren().add(casella);
            }
        }
        for (int i = _t.length; i < aux.length; i++) {
            for (int j = 0; j < _t.length; j++) {
                Casella casella = new Casella(imatgeCasella, MIDA_CASELLA);
                casella.setX((j+X_INI)*MIDA_CASELLA);
                casella.setY((i+Y_INI)*MIDA_CASELLA);
                aux[j][i] = casella;
                grupCaselles.getChildren().add(casella);
            }
        }
    }
	
    /** @brief Expandir Tauler
    @pre imatgeCasella existent i root creat
    @post Expandeix el tauler sumant 2 a l'altura i a l'amplada, centra les rajoles posades i expandeix el root per poder fer scroll*/
    private void expandir(Group grupCaselles, Image imatgeCasella, Pane root) {
            Casella [][] aux = new Casella[_t.length + 2][_t.length + 2];
            for(int i = 0; i < _t.length; i++) {
                    for(int j = 0; j < _t[i].length; j++) {
                            aux[i + 1][j + 1] = _t[i][j];
                            if(_t[i][j].ocupada()){
                                    _t[i][j].rajola().move(i+1+X_INI, j+1+Y_INI);
                if(_t[i][j].rajola().teSeguidor()) _t[i][j].rajola().seguidor().addMove(MIDA_CASELLA,MIDA_CASELLA);
            }                        
                    }
            }
            root.setPrefSize(root.getWidth() + 150, root.getHeight() + 150);
            afegirCasellesInicials(aux, grupCaselles, imatgeCasella);
            afegirCasellesFinals(aux, grupCaselles, imatgeCasella);
            _t = aux;
    }
	
    /** @invariant Tauler sempre de mesura quadrada*/
}