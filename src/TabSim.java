import java.util.Enumeration;
import java.util.Hashtable;

public class TabSim {
    private static Hashtable<String, Simbolo> tabSim = new Hashtable<String, Simbolo>();
    private static Hashtable<String, String> constantes = new Hashtable<String, String>();

    private TabSim(){
    }

    public static void insertaSim(String key, Simbolo sim){
        tabSim.put(key, sim);
    }

    public static void insertarCte(String key, String val){
        constantes.put(key,val);
    }

    public static boolean contieneClave(String key){
        return tabSim.containsKey(key);
    }

    public static char claseSim(String key){
        return tabSim.get(key).clase;
    }

    public static char tipoSim(String key){
        if (tabSim.containsKey(key))
            return tabSim.get(key).tipo;
        return 'I';
    }

    public static boolean esArray(String key){
        Simbolo temp = tabSim.get(key);
        if (!temp.dimen1.equals("0") && temp.dimen2.equals("0"))
            return true;
        return false;
    }

    public static boolean esMatriz(String key){
        Simbolo temp = tabSim.get(key);
        if (!temp.dimen1.equals("0") && !temp.dimen2.equals("0"))
            return true;
        return false;
    }

    public static String valorCte(String key){
        if(constantes.containsKey(key))
            return constantes.get(key);
        return null;
    }

    public static void imprimeTabSim(){
        Enumeration<Simbolo> simbolos = tabSim.elements();
        Enumeration<String> claves = tabSim.keys();
        while (simbolos.hasMoreElements()){
            System.out.println(simbolos.nextElement()+ "\t\t" + claves.nextElement());
        }
    }
    public static String genCodSimbolos(){
        String codigo = "";
        Simbolo aux = null;
        Enumeration<Simbolo> simbolos = tabSim.elements();
        Enumeration<String> claves = tabSim.keys();
        while (simbolos.hasMoreElements()){
            aux  = simbolos.nextElement();
            codigo += claves.nextElement() + "," + aux.clase + "," + aux.tipo + "," + aux.dimen1 + "," + aux.dimen2 + ",#,\n";
        }
        codigo += "@\n";
        return codigo;
    }
}
