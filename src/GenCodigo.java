import java.util.Vector;

public class GenCodigo {
    private static Vector<Instruccion> codigo = new Vector<Instruccion>();
    private static int numEtiq = 0;
    private static int numVar = 0;

    public static String sigEtiqueta(){
        String etiqeta =  "_E"+numEtiq;
        numEtiq++;
        return etiqeta;
    }

    public static String sigVarTemp(){
        String var = "var$Temp$"+numVar;
        numVar++;
        return var;
    }

    public static void insert(String nemo, String dir1, String dir2){
        codigo.add(new Instruccion(nemo, dir1,dir2));
    }

    public static int numLin(){
        return codigo.size()+1;
    }

    public static String getCodigo(){
        String cod = "";
        int lin = 1;
        for (Instruccion i : codigo){
            cod += lin + " " + i.toString();
            lin++;
        }
        cod += lin + " " + "OPR 0,0";
        return cod;
    }
}
