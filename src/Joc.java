/** @file Joc.java
    @brief Classe Joc - Main
*/


import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Scanner;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.control.ScrollPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javax.swing.ImageIcon;

/** @class Joc
    @brief Classe principal amb informacio necessaria per jugar. Conte el main, dibuixa l'interfac grafica i 
    *      controla envents de les diferents peces que el composen, aixi com les puntuacions i el fitxer de sortida a mode de log.
    *      També s'encarrega d'inicialitzar tota la informacio a traves d'un fitxer demanat per teclat.
    @author Lluc Pages Perez, ajudat per Joel Carrasco Mora
*/
public class Joc extends Application implements EventHandler<ActionEvent>{

    ///Constants
    public static final int MIDA_CASELLA = 70; ///< Mida de la casella en pixels
    public static final int WIDTH = 5; ///< Amplada del tauler en caselles
    public static final int HEIGHT = 5; ///< Altura del tauler en caselles
    public static final int X_BARALLA = 10; ///< Coordenades X de la baralla en pixels
    public static final int Y_BARALLA = 630; ///< Coordenades Y de la baralla en pixels
    public static final int TEXT_SIZE = 30; ///< Mida del text de les puntuacions

    ///Atributs
    private Tauler taul = new Tauler(); ///< Tauler on es colocaran les rajoles
    private Baralla barall = new Baralla(retImg("Baralla")); ///< Baralla de rajoles
    private static Jugador [] jug; ///< Conte els jugadors ordenats per torn de participacio
    
    ///Atributs funcionals
    private Rajola pickRajola; ///< Conté la rajola a colocar en cada torn
    private Posicio pRaj; ///< Posició on s'ha colocat la ultima rajola
    private int jugAct = -1; ///< Numero de jugador que te el torn
    private int step = 0; ///< Passos per a cada torn -> Step 0: Colocar rajola al tauler; Step 1: colocar seguidor; Step 2: puntuar
    
    ///Atributs dibuix
    private Text cartesRestants; ///< Mostra el nombre de cartes restants a la baralla
    private Text textJugAct; ///< Mostra el Jugador que te el torn actualment
    private static Text [] score; ///< Ens mostra les puntuacions de cada jugador
    
    private Button button; ///< Boto que ens permet passar torn en cas que seguidor no es pugui colocar
    private ScrollPane scrollPane; ///< Panell amb barres laterals per poder visualitzar tot el tauler en cas d'augmentar la mida
    private Pane root; ///< Panell que conte tots els elements a mostrar
    private Group grupCaselles = taul.grupCaselles(retImg("Casella")); ///< Grup per mostrar les caselles del tauler
    private Group grupBaralla = new Group(); ///< Grup per mostrar la baralla
    private Group grupRajoles = new Group(); ///< Grup per mostrar les Rajoles
    private Group grupSeguidors = new Group(); ///< Grup per mostrar els Seguidors
    private Group grupPuntuacions = new Group(); ///< Grup per mostrar el text: puntuacions, cartes restants i jugador actual
    
    ///Fitxer
    private static PrintWriter pw; ///< Fitxer de sortida
    private static Scanner f; ///< Fitxer d'entrada
    
    /* ###### Funcions ###### */
    
    /** @brief Diu quants jugadors hi han
    @pre ---
    @post Retorna el nombre de jugadors de la partida */
    public static int nJugadors(){ return jug.length; }
    
    /** @brief Finalitza la partida, bloqueja els moviments i mostra guanyador
    @pre ---
    @post Partida finalitzada */
    private void acabarPartida(){
        step = -1;
        int [] win = new int[jug.length];
        win[0] = 0;
        int n = 1;
        for(int i = 1; i < jug.length; i++){
            if(jug[win[0]].punts() < jug[i].punts()){
                win[0] = i;
                n = 1;
            }
            else if(jug[win[0]].punts() == jug[i].punts()){
                win[n] = i;
                n++;
            }
        }
        String s = "Partida Finalitzada\n        Winner ";
        for(int i = 0; i < n; i++) s += "\n     " + (i+1) + ". Jugador " + (win[i]+1);
        pw.println(s);
        Label textFinal = new Label(s);
        textFinal.setFont(Font.font(TEXT_SIZE+5));
        textFinal.relocate(500,200);
        textFinal.setBackground(new Background(new BackgroundFill(Color.WHITESMOKE, CornerRadii.EMPTY, Insets.EMPTY)));
        grupPuntuacions.getChildren().add(textFinal);
    }
    
    /** @brief Mostra la puntuacio del jugador 
    @pre ---
    @post Retorna string segons tipus de jugador */
    public static void setScore(Jugador j, int pts){
        int i = 0;
        while(j != jug[i] && i < jug.length) i++;
        score[i].setText(""+jug[i].punts());
        pw.println("Jugador " + i + " suma +" + pts + " punts");
    }
    
    /** @brief Juga torn de jugador cpu
    @pre ---
    @post torn de jugador CPU realitzat */
    private void jugarCPU(){
        if(jug[jugAct] instanceof JugadorCPU){ //Mirem si l'objecte és un CPU
            pw.println("*CPU*");
            Rajola r = makeRajola(barall.obtenirRajola(), X_BARALLA+200, Y_BARALLA);
            grupRajoles.getChildren().add(r);
            JugadorCPU jCpu = (JugadorCPU) jug[jugAct];
            
            boolean exit = jCpu.torn(taul,r,grupCaselles,retImg("Casella"),root, pw);
            while(!exit){
                grupRajoles.getChildren().remove(r);
                r = barall.obtenirRajola();
                if(r != null){
                    r = makeRajola(r, X_BARALLA+200, Y_BARALLA);
                    grupRajoles.getChildren().add(r);
                    exit = jCpu.torn(taul,r,grupCaselles,retImg("Casella"),root, pw);
                } else acabarPartida();
            }
            avancaJugador();
        } else System.err.println("Jugador no CPU");
    }
    
    /** @brief Avança el torn del jugador
    @pre ---
    @post Avança el torn del jugador i fa jugar a la CPU en cas necessari */
    private void avancaJugador(){
    	if(barall.restant() > 0) {
            step = -1; ///Bloquejem moviments
            
            ///Avancem torn jugador
            jugAct++; 
            if(jugAct >= jug.length) jugAct = 0; 
            
            ///Ho representem al fitxer
            pw.println("\n----------- Torn de Jugador " + (jugAct+1) + " -----------");
            textJugAct.setText("Torn de jugador numero "+(jugAct+1));
            
            if("CPU".equals(jug[jugAct].tipusJugador())) jugarCPU(); ///Fem jugar a la CPU
            else{
                step = 0; //Desbloquejem moviments
                ///Treiem nova carta a jugar
                Rajola r = barall.obtenirRajola();
                pickRajola = makeRajola(r, X_BARALLA+200, Y_BARALLA);
                grupRajoles.getChildren().add(pickRajola);

                cartesRestants.setText("Cartes restants: " + barall.restant());
                pw.println("Rajola obtinguda: " + r);
            }
    	}
    	else acabarPartida(); ///Finalitza la partida
    }
    
    /** @brief Realitza accio de boto -> passar torn
    @pre ---
    @post Torn passat en cas necessari */
    @Override
    public void handle(ActionEvent event){
        if(event.getSource()==button && step == 1){
            pw.println("No coloca seguidor, passa torn");
            step++;
            taul.puntuar(pRaj);
            step=0;
            //Cambiem de jugador
            avancaJugador();
        }
    }
    
    /** @brief Diu en quina direccio s'ha colocat el seguidor respecte la rajola segons coordenades
    @pre ---
    @post Retorna direccio en la que s'ha colocat el seguidor */
    private char toCharPosicio(double x, double y){
        char res;
        if(x > -20 && x < 20 && y > -20 && y < 20){ res = 'M'; }
        else if(x > 20 && y < 20 && y > -20){ res = 'W'; }
        else if(x < -20 && y < 20 && y > -20){ res = 'E'; }
        else if(y > 20 && x < 20 && x > -20){ res = 'N'; }
        else if(y < -15 && x < 20 && x > -20){ res = 'S'; } 
        else{ res = 'X'; }
        return res;
    }

    /** @brief Crea el panell i tota la informacio que conte: Seguidors, Baralla, Tauler, Puntuacions, etc.
    @pre ---
    @post Panell i informacio creada */
    private Parent createContent(){
        //Panell
        root = new Pane();
        root.setPrefSize(WIDTH*MIDA_CASELLA + 550, HEIGHT*MIDA_CASELLA+450);
        root.getChildren().addAll(grupCaselles, grupBaralla, grupRajoles, grupSeguidors, grupPuntuacions);
        
        //Rajola Inicial
        pickRajola.coloca();
        taul.afegirRajolaIni(pickRajola, new Posicio(2,2));
        grupRajoles.getChildren().add(pickRajola);
        
        //Baralla
        Baralla b = makeBaralla(barall);
        b.setX(X_BARALLA); b.setY(Y_BARALLA); //Coloca posició baralla
        grupBaralla.getChildren().add(b);
        cartesRestants = new Text();
        cartesRestants.setText("Cartes restants: " + b.restant());
        cartesRestants.setFont(Font.font(TEXT_SIZE - 10));
        cartesRestants.setX(X_BARALLA + 10); cartesRestants.setY(Y_BARALLA - 20);
        grupBaralla.getChildren().add(cartesRestants);
        
        ///Puntuació Jugadors
        score = new Text [jug.length];
        for(int i = 0; i < jug.length; i++){
            Text titolPunt = new Text("Jugador " + (i+1) + ":");
            titolPunt.setFont(Font.font(TEXT_SIZE));
            titolPunt.setX(20); titolPunt.setY(30*i+50);
            grupPuntuacions.getChildren().add(titolPunt);
            score[i] = new Text("0");
            score[i].setFont(Font.font(TEXT_SIZE));
            score[i].setX(170); score[i].setY(30*i+50);
            grupPuntuacions.getChildren().add(score[i]);
        }
        
        ///Torn Actual
        textJugAct = new Text("Torn de jugador numero " + (jugAct+1));
        textJugAct.setFont(Font.font(TEXT_SIZE+5));
        textJugAct.setX(300); textJugAct.setY(40);
        grupPuntuacions.getChildren().add(textJugAct);
        
        //Botons
        button = new Button();
        button.setText("Passar Seguidor");
        button.setOnAction(this);
        button.relocate(50,550);
        root.getChildren().add(button);
        
        //Barres scroll
        scrollPane = new ScrollPane();
        scrollPane.setContent(root);

        //Seguidors
        for(int i = 0; i < jug.length; i++)
            for(int j = 0; j < 7; j++){
                Seguidor seg = makeSeguidor(jug[i].takeSeguidor(j), j, i);
                if(seg != null) grupSeguidors.getChildren().add(seg);
            }

        return scrollPane;
    }

    /** @brief Crea la baralla amb eventHandler per click
    @pre ---
    @post EventHandlers creats */
    private Baralla makeBaralla(Baralla b){
        b.setOnMouseClicked((MouseEvent e) -> {
        	if(step == 0 && !pickRajola.colocada() && !taul.rajolaPossible(pickRajola)) {
	                if(!pickRajola.colocada()){
	                    grupRajoles.getChildren().remove(pickRajola);
	                    pw.println("*Carta descartada*");
	                }
	                Rajola r = b.obtenirRajola(); 
                        cartesRestants.setText("Cartes restants: " + b.restant());
	                if(r != null){
	                    pickRajola = makeRajola(r, X_BARALLA+200, Y_BARALLA);
	                    grupRajoles.getChildren().add(pickRajola);
	                    pw.println("Rajola obtinguda: " + r);
	                } else acabarPartida();
        	}
        });
        return b;
    }

    /** @brief Crea Seguidor amb eventHandler per released i el coloca segons x,y
    @pre ---
    @post Seguidor Creat */
    private Seguidor makeSeguidor(Seguidor s, int x, int y){
        s.setPosIni(x*20+20, y*40+300);
        s.move(x*20+20, y*40+300);
        s.setOnMouseReleased((MouseEvent e) -> {
            // li afegim event handler per a "release"
            int newX = toBoard(s.getLayoutX()-20, MIDA_CASELLA) - Tauler.X_INI;    // getLayout(X|Y) retorna la traslació corresponent al layout del node
            int newY = toBoard(s.getLayoutY()-15, MIDA_CASELLA) - Tauler.Y_INI;    // toBoard transforma la coordenada pantalla a coord. tauler
            if(step == 1 && s.propietari() == jug[jugAct] && pRaj.equals(new Posicio(newX,newY))){
                double provX = invToBoard(newX + Tauler.X_INI, MIDA_CASELLA);
                double provY = invToBoard(newY + Tauler.Y_INI, MIDA_CASELLA);
                double i = provX - s.getLayoutX();
                double j = provY - s.getLayoutY();
                char c = toCharPosicio(i-20,j-15);
                boolean correcte = false;
                if(c != 'X') correcte = taul.afegirSeguidor(s, c, new Posicio(newX, newY));
                if(!correcte) s.abortMove();
                else{
                    s.move((int)(provX-i),(int)(provY-j));
                    pw.println("Seguidor colocat a posicio " + c + " de la rajola");
                    step++;
                    taul.puntuar(pRaj);
                    step = 0;
                    avancaJugador();
                } 
            } else s.abortMove(); //---------------------Afegir
        });
        return s;
    }
    
    /** @brief Crea Rajola amb eventHandler per released && clicked i la coloca al lloc
    @pre ---
    @post Seguidor Creat */
    private Rajola makeRajola(Rajola r, int x, int y) {
        r.move(toBoard(x, MIDA_CASELLA), toBoard(y, MIDA_CASELLA));
        r.setOnMouseClicked((MouseEvent e) -> {
        	if (e.getClickCount() == 2 && !r.colocada()) {
        		r.rotarDreta();
        	}
        });
        r.setOnMouseReleased((MouseEvent e) -> {
            // li afegim event handler per a "release"
            if(step == 0 && !r.colocada()){
                int newX = toBoard(r.getLayoutX(), MIDA_CASELLA);    // getLayout(X|Y) retorna la traslació corresponent al layout del node
                int newY = toBoard(r.getLayoutY(), MIDA_CASELLA);   // toBoard transforma la coordenada pantalla a coord. tauler
                Posicio p = new Posicio(newX-Tauler.X_INI, newY-Tauler.Y_INI);
                boolean correcte = taul.afegirRajola(r, p, grupCaselles, retImg("Casella"), root); // procurem fer moviment proposat amb el mouse
                if(correcte){
                    step++;
                    r.coloca();
                    r.move(p.getX()+Tauler.X_INI, p.getY()+Tauler.Y_INI);
                    pw.println("Rajola " + r + " colocada a la posició -> " + p);
                    pRaj = p;
                } else r.abortMove();
            }
        });
        return r;
    }
    
    /** @brief Diu la posicio del tauler que correspon als pixels segons la mida
    @pre ---
    @post Retorna la posicio segons els pixels i la mida */
    private static int toBoard(double pixel, double size) { 
        int ret = (int)((pixel+size/2) / size);
        return ret;
    }
    
    /** @brief Diu la posicio en pixels que correspon a la posicio del tauler
    @pre ---
    @post Retorna posicio en pixels */
    private static double invToBoard(int ret, int size) { 
        double pix = ((double)size / 2) + (size*ret);
        return pix;
    }
    
    /** @brief Metode on arranca la aplicacio de JavaFX
    @pre ---
    @post Aplicacio iniciada */
    @Override
    public void start(Stage primaryStage) {
        //Emplenem informació llegint el fitxer
        llegirFitxer(f);
        barall.barrejar();
        
        Scene scene = new Scene(createContent());
        primaryStage.setTitle("Pagerrasco's Carcassonne"); // títol
        primaryStage.setScene(scene);        // escena
        primaryStage.show();                 // fem visible
        
        pw.println("-------------- PARTIDA INICIADA ---------------");
        avancaJugador(); /// Torn 1r jugador
    }

    /** @brief Busca la imatge amb nom str
    @pre Imatge existent
    @post Retorna imatge corresponent */
    private Image retImg(String str){
        ImageIcon imageIcon = null;
        imageIcon = new ImageIcon(getClass().getResource("imatges/" + str + ".png"));
        Image fxImage = new javafx.scene.image.Image(imageIcon.getDescription());
        return fxImage;
    }
    
    /** @brief Llegeix la informacio i inicialitza jugadors i Baralla
    @pre: ---
    @post: llegeix la informacio de la baralla i jugadors desde fitxer */
    private void llegirFitxer(Scanner fitxer){
        String opt = fitxer.next();
        int nJugadors = 0;

        while(!opt.equals("#")){ //Bucle per llegir tota la informació
            switch (opt) {
                case "nombre_jugadors": //Definim el numero de jugadors
                    if(fitxer.hasNextInt()) nJugadors = fitxer.nextInt();
                    jug = new Jugador [nJugadors];
                    for(int i = 0; i < nJugadors; i++) jug[i] = new JugadorH(retImg("Seguidor"+i));
                    break;
                case "jugadors_cpu": //Definim quins jugadors seran CPU
                    while (fitxer.hasNextInt()) {
                        int aux = fitxer.nextInt();
                        jug[aux-1] = new JugadorCPU(retImg("Seguidor"+(aux-1)));
                    }
                    break;
                case "rajoles": //Definim la baralla
                    String str = fitxer.next();
                    while(!str.equals("#")){
                        int nRaj = fitxer.nextInt();
                        for(int i = 0; i < nRaj; i++) barall.afegirRajola(new Rajola(str, retImg(str), MIDA_CASELLA));
                        str = fitxer.next();
                    }
                    break;
                case "rajola_inicial":
                    String s = fitxer.next();
                    pickRajola = new Rajola(s,retImg(s),MIDA_CASELLA);
                    break;
            }
            opt = fitxer.next();
        }
    }

    /** @brief Funcio inicial Main
    @pre: ---
    @post: Programa finalitzat */
    public static void main(String argv[]) throws IOException {
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);

        //Llegir nom Fitxer
        System.out.println("Nom del fitxer:");
        String nomFitxer = br.readLine();
        f = new Scanner(new File(nomFitxer));
        
        //Generem fitxer d'escritura
        FileWriter textLog = new FileWriter("./out.txt");
        pw = new PrintWriter(textLog);
        
        //Comencem el joc
        launch(argv);
        pw.println("\n----------- PARTIDA FINALITZADA -----------");
        textLog.close();
    }
    
}

