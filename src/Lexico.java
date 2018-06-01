import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Scanner;

public class Lexico{
    
    private final int ACP=99, ERR=-1;
    private String token="", entrada;
    public  String archCod;
    private int posLec=0, numLin=1, numCol=0;
    public boolean analizar = true;

    private final int[][] MATRIZ_TRAN = {
                 //let   dig  _     <    >    =    /    .  +,-,%,^  "   :   ;,()[]  *  sp,tab  nl !¿?@
            /*00*/{  1,   2,   1,   7,  10,  12,  17,  15,  16,    5,  13,   15,   16,    0,   0, ERR},
            /*01*/{  1,   1,   1, ACP, ACP, ACP, ACP, ACP, ACP,  ACP, ACP,  ACP,  ACP,  ACP, ACP, ERR},
            /*02*/{ACP,   2, ACP, ACP, ACP, ACP, ACP,   3, ACP,  ACP, ACP,  ACP,  ACP,  ACP, ACP, ERR},
            /*03*/{ERR,   4, ERR, ERR, ERR, ERR, ERR, ERR, ERR,  ERR, ERR,  ERR,  ERR,  ERR, ERR, ERR},
            /*04*/{ACP,   4, ACP, ACP, ACP, ACP, ACP, ACP, ACP,  ACP, ACP,  ACP,  ACP,  ACP, ACP, ERR},
            /*05*/{  5,   5,   5,   5,   5,   5,   5,   5,   5,    6,   5,   5,     5,    5, ERR,   5},
            /*06*/{ACP, ACP, ACP, ACP, ACP, ACP, ACP, ACP, ACP,  ACP, ACP,  ACP,   ACP, ACP, ACP, ERR},
            /*07*/{ACP, ACP, ACP, ACP,   9,   8, ACP, ACP, ACP,  ACP, ACP,  ACP,   ACP, ACP, ACP, ERR},
            /*08*/{ACP, ACP, ACP, ACP, ACP, ACP, ACP, ACP, ACP,  ACP, ACP,  ACP,   ACP, ACP, ACP, ERR},
            /*09*/{ACP, ACP, ACP, ACP, ACP, ACP, ACP, ACP, ACP,  ACP, ACP,  ACP,   ACP, ACP, ACP, ERR},
            /*10*/{ACP, ACP, ACP, ACP, ACP,  11, ACP, ACP, ACP,  ACP, ACP,  ACP,   ACP, ACP, ACP, ERR},
            /*11*/{ACP, ACP, ACP, ACP, ACP, ACP, ACP, ACP, ACP,  ACP, ACP,  ACP,   ACP, ACP, ACP, ERR},
            /*12*/{ACP, ACP, ACP, ACP, ACP, ACP, ACP, ACP, ACP,  ACP, ACP,  ACP,   ACP, ACP, ACP, ERR},
            /*13*/{ACP, ACP, ACP, ACP, ACP,  14, ACP, ACP, ACP,  ACP, ACP,  ACP,   ACP, ACP, ACP, ERR},
            /*14*/{ACP, ACP, ACP, ACP, ACP, ACP, ACP, ACP, ACP,  ACP, ACP,  ACP,   ACP, ACP, ACP, ERR},
            /*15*/{ACP, ACP, ACP, ACP, ACP, ACP, ACP, ACP, ACP,  ACP, ACP,  ACP,   ACP, ACP, ACP, ERR},
            /*16*/{ACP, ACP, ACP, ACP, ACP, ACP, ACP, ACP, ACP,  ACP, ACP,  ACP,   ACP, ACP, ACP, ERR},
            /*17*/{ACP, ACP, ACP, ACP, ACP, ACP,  18, ACP, ACP,  ACP, ACP,  ACP,    19, ACP, ACP, ERR},
            /*18*/{ 18,  18,  18,  18,  18,  18,  18,  18,  18,   18,  18,   18,    18,  18,  21, ERR},
            /*19*/{ 19,  19,  19,  19,  19,  19,  19,  19,  19,   19,  19,   19,    20,  19,  19, ERR},
            /*20*/{ 19,  19,  19,  19,  19,  19,  21,  19,  19,   19,  19,   19,    20,  19,  19, ERR},
            /*21*/{ACP, ACP, ACP, ACP, ACP, ACP, ACP, ACP, ACP,  ACP, ACP,  ACP,   ACP, ACP, ACP, ERR}
    };

    private final String[] PAL_RES = {
            "constante", "decimal", "entero", "alfabetico", "logico", "funcion", "si", "regresa",
            "sino", "fin", "inicio", "para", "en", "rango", "a", "incr", "decr", "iterar", "mientras",
            "haz", "opcion", "caso", "procedimiento", "imprime", "imprimenl", "lee", "programa",
            "interrumpe", "continua", "otro"
    };

    private final String[] OPE_LOG = {
        "no", "o", "y"
    };

    private final String[] CTE_LOG = {
        "falso", "verdadero"
    };
    public Lexico(){
        archCod = carArchivo();
        posLec=0;
    }

    private boolean esPalRes( String p ){
        for( int i=0; i<PAL_RES.length; i++ )
            if( PAL_RES[i].equals(p) ) 
                return true; 
        return false;
    }

    private boolean esOpeLog( String p ){
        for( int i=0; i<OPE_LOG.length; i++ )
            if( OPE_LOG[i].equals(p) )
                return true;
        return false;
    }

    private boolean esCteLog( String p ){
        for( int i=0; i<CTE_LOG.length; i++)
            if( CTE_LOG[i].equals(p) )
                return true;
        return false;
    }

    private String carArchivo(){
        String archE="", archS="";
        int reng=1, col=1;
        File ficheroE;
        Scanner sc = new Scanner( System.in );
        entrada="";
        System.out.print("Archivo a compilar: ");
        archE = sc.nextLine();
        sc.close();
        System.out.println();

        archS = archE.substring(0,archE.length()-3);
        archS+="eje";
        ficheroE = new File("archivos/"+archE);         
        if (ficheroE.exists()) {
            analizar = true;
            try (Scanner aFuente = new Scanner(ficheroE)) {
                String linea = "";
                while (aFuente.hasNext()) {
                    linea = (aFuente.nextLine() + "\n");
                    System.out.print("[" + reng + "] " + linea);
                    entrada += linea;
                    reng++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            analizar = false;
            System.out.print("El archivo<"+archE+"> no exixte en la carpeta de \"archivos\"");
        }
        return archS;
    }

    private int colCar( char c){
        if( Character.isAlphabetic(c) ) return 0;
        if( Character.isDigit(c) ) return 1;
        if( c == '_' ) return 2;
        if( c == '<' ) return 3;
        if( c == '>' ) return 4;
        if( c == '=' ) return 5;
        if( c == '/' ) return 6;
        if( c == '.' ) return 7;
        if( c == '+' || c == '-' || c == '%' || c == '^' ) return 8;
        if( c == '"' ) return 9;
        if( c == ':' ) return 10;
        if( c==';' || c == ',' || c == '(' ||
            c == ')' || c == '[' || c == ']' ) return 11;
        if( c == '*' ) return 12;
        if( c == ' ' || c == '\t' ) return 13;
        if( c == '\n' ) return 14;
        if( c == '!' || c == '?' || c == '@' ) return 15;
        erra("Lexico", "Caracter no valido: " + c);
        return ERR;
    }

    public String lexico(){
        int estado=0, estAnt=0;
        String lexema="";
        token = "";
        while( estado != ACP && estado != ERR && posLec < entrada.length() ){
            char c = entrada.charAt(posLec++);
            int col = colCar(c);
            if( c == '\n')
                numLin++;
            if( col>=0 && col<=15){
                estAnt = estado;
                estado = MATRIZ_TRAN[estado][col];
                if( estado!=ACP && estado!=ERR && estado!=0 )
                    lexema += c;
            }
            if( estado != ERR && estado != ACP )
                estAnt = estado;
            if( estAnt==21 ){
                if(estAnt ==ACP )
                    posLec--;
                lexema="";
                estado=0;
            }
            if( estado==ACP && c=='\n' )
                numLin--;
        }
        if(estado==ACP || estado==ERR)
            posLec--;
        else estAnt = estado;
        token = "NoTokn";
        if( estado == 21 || estAnt == 18 || estado == 0 )
            lexema = token = "";
        switch(estAnt){
            case 1: token = "Identi";
                    if( esCteLog(lexema) ) token = "CteLog";
                    else if( esOpeLog(lexema) ) token = "OpeLog";
                    else if( esPalRes(lexema) ) token = "PalRes";
                    break;
            case 2:token="CteEnt";break;
            case 4:token="CteDec";break;
            case 6:token="CteAlf";break;
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:token="OpeRel";break;
            case 13:
            case 15:token="Delimi";break;
            case 14:token="OpeAsi";break;
            case 16:
            case 17:token="OpeAri";break;
        }
        numCol = numColum(lexema);
        return lexema;
    }

    public String tipoToken(){
        return token;
    }

    private int numColum(String lex){
        int pos, lastNL;
        pos = entrada.lastIndexOf(lex, posLec);
        lastNL = entrada.lastIndexOf('\n', pos);
        return pos-lastNL;
    }

    public int getNumLinea(){
        return numLin;
    }
    public int getNumColum(){
        return numCol;
    }
    public boolean continuarLeec(){ return posLec<entrada.length();}
    private void erra(String tipoErr, String descrip){
        CtrlError.imprimError(numLin, numCol, "<"+tipoErr+">", descrip);
    }   
}