import java.text.DecimalFormat;

public class CtrlError{
    private static int numErrorres;
    
    private CtrlError(){
        numErrorres=0;
    }
    public static void imprimError(int linea, int colum, String tipo, String descrip){
        DecimalFormat form = new DecimalFormat("0000");
        System.out.println("Linea["+form.format(linea)+"]Colum["+form.format(colum)+"]\tError"+tipo+" "+descrip);
        numErrorres++;
    }

    public static void imprimTotalError(){
        System.out.println("Total de errores: " + numErrorres);
    }
}