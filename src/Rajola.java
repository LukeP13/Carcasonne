/** @file Rajola.java
    @brief Classe Rajola
*/

/** @class Rajola
    @brief Implementacio d'una Rajola del joc que guada la informacio de cada orientacio i el Seguidor
    @author Joel Carrasco Mora
*/

import java.lang.String;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class Rajola extends ImageView {
	
    private char _mig, _nord, _est, _sud, _oest; ///< Chars que indiquen de quin tipus es cada orientacio (vila 'V', cami 'C', encreuament 'X', monestir 'M' o camp 'F')
    private char _posSeguidor; ///< Char que indica en quina orientacio es troba el Seguidor si hi ha
    private Seguidor _seguidor; ///< Seguidor de la Casella
    private boolean _colocada; ///< Boolean per saber si la Rajola es troba colocada al Tauler o no
	
	private double _mouseX, _mouseY; ///< Coordenades del clic del mouse (coord. pantalla)
    private static final int INI_X = 210, INI_Y = 630;  ///< Posicio predeterminada en pantalla
    private static final int PIXELS = 70; ///< Mida de la Rajola
	
    /** @brief Crea Rajola amb imatge i informacio
	@pre Imatge ima existent
	@post Crea la rajola, guarda la informacio de cada orientacio i l'afegeix la imatge i els event handler per moure-la */
    public Rajola(String str, Image ima, int amplada){
        _mig = str.charAt(0);
        _nord = str.charAt(1);
        _est = str.charAt(2);
        _sud = str.charAt(3);
        _oest = str.charAt(4);
        _posSeguidor = ' ';
        _colocada = false;
        
        setImage(ima);
        setFitWidth(amplada);
        setPreserveRatio(true);  
        setSmooth(true);         
        setCache(true);  
		
        setOnMousePressed((MouseEvent e) -> {  
        	_mouseX = e.getSceneX();  
        	_mouseY = e.getSceneY();
        } );
        setOnMouseDragged((MouseEvent e) -> {   
        	if(!colocada()) relocate(e.getSceneX()-_mouseX+INI_X, e.getSceneY()-_mouseY+INI_Y);
        } );
    }
	
    /** @brief Moure en pantalla la Rajola
	@pre ---
	@post Coloca en pantalla la Rajola en les coordenades (x*PIXELS, y*PIXELS)*/
    public void move(int x, int y) {
        relocate(x*PIXELS, y*PIXELS);
    }
	
    /** @brief Abortar moviment
    @pre ---
    @post Retorna la Rajola a la seva posicio inicial (INI_X, INI_Y)*/
    public void abortMove() {
        relocate(INI_X, INI_Y);
    }
    
    /** @brief Consulta si la Rajola esta colocada al Tauler
    @pre ---
    @post Retorna cert si la Rajola esta colocada o fals altrament*/
    public boolean colocada(){return _colocada;}
    
    /** @brief Assigna la Rajola com a colocada
	@pre ---
	@post Atribut _colocada = true*/
    public void coloca(){ _colocada = true;}

    /** @brief Posar seguidor a la Rajola
	@pre Orientacio valida i seguidor != null
	@post Posa el seguidor seg a l'orientacio pos desitjada de la Rajola actual*/
    public void posarSeguidor(Seguidor seg, char pos){
    	_seguidor = seg;
        _posSeguidor = pos;
    }
    
    /** @brief Retorna la seguent direccio del cami demanada
	@pre Orientacio valida o 'Q' per qualsevol direccio
	@post Retorna la seguent direccio del cami que no sigui direcc o la seguent direccio qualsevol en cas de que direcc == 'Q'*/
    public char direccioCami(char direcc) {
    	char direccSeg = ' ';
    	switch (direcc) {
    	case 'N':
    		if (_nord == 'C') direccSeg = 'N';
    		else if (_est == 'C') direccSeg = 'E';
    		else if (_oest == 'C') direccSeg = 'W';
    	break;
    	case 'S':
    		if (_sud == 'C') direccSeg = 'S';
    		else if (_est == 'C') direccSeg = 'E';
    		else if (_oest == 'C') direccSeg = 'W';
    	break;
    	case 'E':
    		if (_nord == 'C') direccSeg = 'N';
    		else if (_sud == 'C') direccSeg = 'S';
    		else if (_est == 'C') direccSeg = 'E';
    	break;
    	case 'W':
    		if (_nord == 'C') direccSeg = 'N';
    		else if (_sud == 'C') direccSeg = 'S';
    		else if (_oest == 'C') direccSeg = 'W';
    	break;
    	case 'Q': //Qualsevol sortida
    		if (_nord == 'C') direccSeg = 'N';
    		else if (_sud == 'C') direccSeg = 'S';
    		else if (_est == 'C') direccSeg = 'E';
    		else if (_oest == 'C') direccSeg = 'W';
    	break;
    	}
    	return direccSeg;
    }
    
    /** @brief Consulta si el seguidor es troba en la direccio demanada en el tipus demanat
	@pre ---
	@post Retorna cert si troba un seguidor a la direccio demanada (o a qualsevol en cas de direcc == 'Q') i que el tipus de la zona sigui == tipus o fals altrament*/
    public boolean seguidorTipus(char direcc, char tipus) {
    	boolean seguidor = false;
    	switch(direcc) {
    	case 'N':
    		seguidor = _nord == tipus && _posSeguidor == 'N';
    	break;
    	case 'S':
    		seguidor = _sud == tipus && _posSeguidor == 'S';
    	break;	
    	case 'E':
    		seguidor = _est == tipus && _posSeguidor == 'E';
    	break;	
    	case 'W':
    		seguidor = _oest == tipus && _posSeguidor == 'W';
    	break;
    	case 'M':
    		seguidor = _mig == tipus && _posSeguidor == 'M';
    	break;
    	case 'Q':
    		seguidor = ( _nord == tipus && _posSeguidor == 'N' ) || ( _sud == tipus && _posSeguidor == 'S' ) || ( _est == tipus && _posSeguidor == 'E' )
        			|| ( _oest == tipus && _posSeguidor == 'W' ) || ( _mig == tipus && _posSeguidor == 'M' );
    	break;	
    	}
    	return seguidor;
    }
    
    /** @brief Retorna de quin tipus es l'orientacio demanada de la Rajola
	@pre ---
	@post Retorna el tipus que te la Rajola a la direccio direcc demanada*/
    public char toType(char direcc) {
    	char tipus = ' ';
    	switch(direcc) {
    	case 'N':
    		tipus = _nord;
    	break;
    	case 'S':
    		tipus = _sud;
    	break;	
    	case 'E':
    		tipus = _est;
    	break;	
    	case 'W':
    		tipus = _oest;
    	break;
    	case 'M':
    		tipus = _mig;
    	break;	
    	}
    	return tipus;
    }
    
    /** @brief Consulta si es tracta de la Rajola especial amb ciutat al mig i dues sortides de cami
	@pre ---
	@post Retorna cert si es tracta de la Rajola especial (per defecte sense rotar: VCCVV)*/
    public boolean rajolaEspecial() {
    	boolean especial = false;
    	if(_mig == 'V') {
    		int count = 0;
    		if(_nord == 'C') count++;
    		if(_est == 'C') count++;
    		if(_sud == 'C') count++;
    		if(_oest == 'C') count++;
    		if(count == 2) especial = true;
    	}
    	return especial;
    }
    
    /** @brief Consulta si hi ha un monestir i si aquest te Seguidor
    @pre ---
    @post Cert si hi ha monestir i Seguidor o fals altrament*/
    public boolean seguidorMonestir() {
    	return seguidorTipus('M', 'M');
    }

    /** @brief Consulta si la Rajola te Seguidor
    @pre ---
    @post Cert si aquesta Rajola te Seguidor o fals altrament*/
    public boolean teSeguidor(){ return _seguidor != null; }
    
    /** @brief Retornar Seguidor
    @pre Rajola amb Seguidor
    @post Teu el Seguidor de la Rajola i el retorna al Jugador corresponent*/
    public void retornarSeguidor(){
            _posSeguidor = ' ';
            _seguidor.recull();///Allibera seguidor i el guarda al jugador altre cop
            _seguidor = null;
    }
	
    /** @brief Rotar Rajola a la dreta
    @pre ---
    @post Gira a la dreta les coordenades i rota la imatge a la dreta 90 graus*/
    public void rotarDreta() {
            char nord = _nord;
            char sud= _sud;
            char est = _est;
            char oest = _oest;
            _nord = oest;
            _sud = est;
            _est = nord;
            _oest = sud;
            setRotate(getRotate() + 90);
    }

    /** @brief Retornar Seguidor de la Rajola 
    @pre ---
    @post Retorna atribut_segudior*/
    public Seguidor seguidor(){ return _seguidor; }
	
    /** @brief Consultar tipus al mig de la Rajola
    @pre --
    @post Retorna de quin tipus es el mig de la Rajola*/
    public char mig(){ return _mig; }
    
    /** @brief Consultar tipus al nord de la Rajola
	@pre --
	@post Retorna de quin tipus es el nord de la Rajola*/
    public char nord(){ return _nord; }
    
    /** @brief Consultar tipus a l'est de la Rajola
	@pre --
	@post Retorna de quin tipus es l'est de la Rajola*/
    public char est(){ return _est; }
    
    /** @brief Consultar tipus al sud de la Rajola
	@pre --
	@post Retorna de quin tipus es el sud de la Rajola*/
    public char sud(){ return _sud; }
    
    /** @brief Consultar tipus l'oest de la Rajola
	@pre --
	@post Retorna de quin tipus es l'oest de la Rajola*/
    public char oest(){ return _oest; }
    
    /** @brief Passar a string la Rajola en format (MNESW)
   	@pre --
   	@post Passa a string els atributs d'orientacio*/
    @Override
    public String toString(){
        return "" + _mig + _nord + _est + _sud + _oest;
    }
    
    /** @invariant ---*/
}