import java.awt.*;
import java.awt.event.KeyListener;
import java.io.*;
import java.util.*;

public class Sintactico {
    private Lexico lexico;
    private String lex, token, alcanceGlb, funcAct;
    private String[] dimenciones;
    private Queue<Simbolo> decParam;
    private Queue<String> paramU;
    private Stack<String> pilaTip;
    private Stack<String> etqCtrlF, etqCtrlI;
    private Hashtable<String, String> tiposExp;
    private boolean cmpRegFunc, cmpRegPro, cmpReg, esIter;
    private char tipoF;

    public Sintactico() {
        lexico = new Lexico();
        dimenciones = new String[2];
        decParam = new LinkedList<Simbolo>();
        paramU = new LinkedList<String>();
        tiposExp = new Hashtable<String, String>();
        genreaTipoExp();
        pilaTip = new Stack<String>();
        etqCtrlF = new Stack<String>();
        etqCtrlI = new Stack<String>();
        alcanceGlb="";
        funcAct = "";
        cmpRegFunc = false;
        cmpRegPro = false;
        cmpReg = false;
        esIter = false;
        tipoF = 'I';
        if (lexico.analizar)
            prgm();
        generaSalida(lexico.archCod);
    }

    private void generaSalida(String archivo){
        File f = new File("archivos/"+archivo);
        try {
            FileWriter fw = new FileWriter(f);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);
            pw.write(TabSim.genCodSimbolos());
            pw.write(GenCodigo.getCodigo());
            pw.close();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void genreaTipoExp(){
        tiposExp.put("E:=E","");
        tiposExp.put("A:=A","" );
        tiposExp.put("R:=R","");
        tiposExp.put("L:=L","");
        tiposExp.put("R:=E","");
        tiposExp.put("E+E","E");
        tiposExp.put("E+R","R");
        tiposExp.put("R+E","R");
        tiposExp.put("R+R","R");
        tiposExp.put("A+A","A");
        tiposExp.put("E-E","E");
        tiposExp.put("E-R","R");
        tiposExp.put("R-E","R");
        tiposExp.put("R-R","R");
        tiposExp.put("E*E","E");
        tiposExp.put("E*R","R");
        tiposExp.put("R*E","R");
        tiposExp.put("R*R","R");
        tiposExp.put("E/E","R");
        tiposExp.put("E/R","R");
        tiposExp.put("R/E","R");
        tiposExp.put("R/R","R");
        tiposExp.put("E^E","E");
        tiposExp.put("E^R","R");
        tiposExp.put("R^E","R");
        tiposExp.put("R^R","R");
        tiposExp.put("-E","E");
        tiposExp.put("-R","R");
        tiposExp.put("LyL","L");
        tiposExp.put("LoL","L");
        tiposExp.put("noL","L");
        tiposExp.put("E>E","L");
        tiposExp.put("R>E","L");
        tiposExp.put("E>R","L");
        tiposExp.put("R>R","L");
        tiposExp.put("E<E","L");
        tiposExp.put("R<E","L");
        tiposExp.put("E<R","L");
        tiposExp.put("R<R","L");
        tiposExp.put("E>=E","L");
        tiposExp.put("R>=E","L");
        tiposExp.put("E>=R","L");
        tiposExp.put("R>=R","L");
        tiposExp.put("E<=E","L");
        tiposExp.put("R<=E","L");
        tiposExp.put("E<=R","L");
        tiposExp.put("R<=R","L");
        tiposExp.put("E<>E","L");
        tiposExp.put("R<>E","L");
        tiposExp.put("E<>R","L");
        tiposExp.put("R<>R","L");
        tiposExp.put("A<>A","L");
        tiposExp.put("E=E","L");
        tiposExp.put("R=E","L");
        tiposExp.put("E=R","L");
        tiposExp.put("R=R","L");
        tiposExp.put("A=A","L");
    }

    private void erra(String tipoErr, String descrip) {
        CtrlError.imprimError(lexico.getNumLinea(), lexico.getNumColum(), "<" + tipoErr + ">", descrip);
    }

    private void consume(String esperado, String recibido, String msg ){
        if (!esperado.equals(recibido))
            erra("Sintaxis", msg);
        sigLexema();
    }

    private void consume(String esperado, String recibido){
        consume(esperado, recibido, "Se esperaba <"+esperado+"> y llego: "+recibido);
    }

    private void sigLexema() {
        lex = lexico.lexico();
        token = lexico.tipoToken();
    }

    private void consta() {
        String tipo = "NotDef", ident="";
        char tipoAux='I';
        boolean continua = false;
        if (lex.equals("entero") || lex.equals("decimal") || lex.equals("alfabetico") || lex.equals("logico")) {
            if (lex.equals("entero")) {
                tipo = "CteEnt";
                tipoAux = 'E';
            }else if (lex.equals("decimal")) {
                tipo = "CteDec";
                tipoAux = 'R';
            }else if (lex.equals("alfabetico")) {
                tipo = "CteAlf";
                tipoAux = 'A';
            }else if (lex.equals("logico")) {
                tipo = "CteLog";
                tipoAux = 'L';
            }
            consume(lex,lex);
        }else
            consume("entero, decimal, alfabetico o logico", lex);

        do {
            ident = lex;
            consume("Identi", token);
            consume(":=", lex);
            if (token.equals("CteEnt") || token.equals("CteDec") || token.equals("CteAlf") || token.equals("CteLog")) {
                GenCodigo.insert("LIT",lex,"0");
                if (tipo != token)
                    erra("Semantico", "Se esperaba " + tipo + " y llego: " + token);
                if(!TabSim.contieneClave(ident)) {
                    TabSim.insertaSim(ident, new Simbolo(ident,'C',tipoAux,"0","0"));
                    TabSim.insertarCte(ident, lex);
                }else
                    erra("Semantico","La constante <"+ident+"> ya ha sido declarada.");
                consume(token, token);
            }else
                consume("Valor Constante", token);
            GenCodigo.insert("STO","0",ident);
            if (lex.equals(",")){
                consume(",",lex);
                continua = true;
            }else
                continua = false;
        } while (continua);
        consume(";",lex);
    }

    private void var(String tipo) {
        char tipoAux='I', claseAux='V';
        String ident="",dim1="", dim2="", alcanceTmp="";

        if (!alcanceGlb.equals("")){
            claseAux='L';
            alcanceGlb = "$"+(alcanceTmp = alcanceGlb);
        }
        if(tipo.equals("entero"))
            tipoAux='E';
        else if(tipo.equals("decimal"))
            tipoAux='R';
        else if(tipo.equals("logico"))
            tipoAux='L';
        else if (tipo.equals("alfabetico"))
            tipoAux='A';

        do {
            ident = lex;
            dim1 = dim2 = "0";
            consume("Identi", token);
            if (lex.equals("[")) {
                defDimen();
                dim1 = dimenciones[0];
                dim2 = dimenciones[1];
            }
            if (lex.equals(":=")) {
                sigLexema();
                cte();
                GenCodigo.insert("STO","0",ident+alcanceGlb);
            }
            if (!TabSim.contieneClave(ident+alcanceGlb))
                TabSim.insertaSim(ident+alcanceGlb, new Simbolo(ident,claseAux,tipoAux, dim1, dim2));
            else
                erra("Semantico", "La variable<"+ident+"> ya a sido declaranda anteriormente");
            if (!lex.equals(","))
                break;
            else
                sigLexema();
        } while (true);
        consume(";", lex);
        alcanceGlb = alcanceTmp;
    }

    private void defDimen(){
        int dim=0;
        dimenciones[0] = "0";
        dimenciones[1] = "0";
        do{
            consume("[",lex);
            if (token.equals("CteEnt") || token.equals("Identi")) {
                if (token.equals("CteEnt")){
                    //GenCodigo.insert("LIT",lex,"0");
                    if (dim < 2)
                        dimenciones[dim] = lex;
                }else {
                    //GenCodigo.insert("LOD",lex,"0");
                    if (TabSim.contieneClave(lex)){
                        if (TabSim.claseSim(lex) == 'C') {
                            if (TabSim.tipoSim(lex) == 'E')
                                dimenciones[dim] = TabSim.valorCte(lex);
                            else
                                erra("Semantico", "La dimencion del arrego no es entera");
                        }else
                            erra("Semantico", "Declaracion del arrego o array con un valor no constante.");
                    } else
                        erra("Semantico", "Identificador no definido");
                }
                consume(token, token);
            }else
                consume("CteEnt o Identi", token);
            consume("]",lex);
            dim++;
        }while (lex.equals("["));
    }

    private void dimen() {
        int dim = 0;
        String tip = "";
        dimenciones[0] = "0";
        dimenciones[1] = "0";
        do {
            consume("[", lex);
            expr();
            dimenciones[dim] = pilaTip.pop();
            if (!dimenciones[dim].equals("E"))
                erra("Semantico", "Se esperaba tipo entero en las dimenciones de la variable y llego: "+dimenciones[dim]);
            if (dim>=2)
                erra("Semantica", "No se permiten arreglos de mas de 2 dimenciones");
            dim++;
            consume("]", lex);
        }while (lex.equals("["));

    }

    private void cte() {
        if (token.equals("CteEnt") || token.equals("CteDec") || token.equals("CteAlf") || token.equals("CteLog")) {
            GenCodigo.insert("LIT",lex,"0");
            consume(token, token);
        }else
            consume("Valor constante", token);
    }

    private void funParam(){
        do {
            if (lex.equals(",")) {
                sigLexema();
            }
            expr();
            paramU.add(pilaTip.pop());
        }while (lex.equals(","));
    }

    private void callFun(){
        consume("(",lex);
        if (!lex.equals(")"))
            funParam();
        consume(")",lex);
    }

    private void term() {
        if (lex.equals("(")) {
            sigLexema();
            expr();
            consume(")",lex);
        } else if (token.equals("CteEnt") || token.equals("CteDec") || token.equals("CteAlf") || token.equals("CteLog")) {
            if (token.equals("CteEnt") || token.equals("CteDec"))
                GenCodigo.insert("LIT",lex,"0");
            if (token.equals("CteAlf"))
                GenCodigo.insert("LIT",lex,"0");
            if (token.equals("CteLog")){
                if (lex.equals("verdadero"))
                    GenCodigo.insert("LIT","V","0");
                else if (lex.equals("falso"))
                    GenCodigo.insert("LIT","F","0");
            }
            if (token.equals("CteEnt")) pilaTip.push("E");
            else if (token.equals("CteDec")) pilaTip.push("R");
            else if (token.equals("CteAlf")) pilaTip.push("A");
            else if (token.equals("CteLog")) pilaTip.push("L");
            sigLexema();
        } else if (token.equals("Identi")) {
            String id = lex;
            sigLexema();
            if (lex.equals("(")) { ///func
                String auxP = "", auxId = "", Ex = GenCodigo.sigEtiqueta();
                GenCodigo.insert("LOD", Ex, "0");
                callFun();
                while (!paramU.isEmpty())
                    auxP += "$" + paramU.poll();
                if (auxP.equals("")) auxId = id + "$";
                else auxId = id + auxP;
                id = auxId;
                if (!TabSim.contieneClave(auxId)){
                    erra("Semantico", "La funcion<" + auxId + "> no esta definida");
                    pilaTip.push("I");
                }else
                    pilaTip.push(TabSim.tipoSim(auxId) + "");
                GenCodigo.insert("CAL",auxId,"0");
                TabSim.insertaSim(Ex,new Simbolo(Ex,'I','I',GenCodigo.numLin()+"","0"));
            }
            else if (lex.equals("[")){ ///Dimen
                String auxT = "I", auxS="";
                dimen();
                if (!TabSim.contieneClave(id+"$"+alcanceGlb) && !TabSim.contieneClave(id))
                        erra("Semantico", "La variable<" + id + "> no ha sido declarada");
                else if (TabSim.contieneClave(id+"$"+alcanceGlb))
                    id=id+"$"+alcanceGlb;
                auxT = TabSim.tipoSim(id)+"";
                if (!dimenciones[0].equals("0") && !dimenciones[1].equals("0") && !TabSim.esMatriz(id))
                    erra("Semantico", "La variable<"+id+"> no es una matriz");
                else if (!dimenciones[0].equals("0") && dimenciones[1].equals("0") && !TabSim.esArray(id))
                    erra("Semantico", "La variable<"+id+"> no es un vector");
                pilaTip.push(auxT);
            }else {
                String auxT = "I";
                if (!TabSim.contieneClave(id+"$"+alcanceGlb) && !TabSim.contieneClave(id))
                        erra("Semantico", "La variable<"+id+"> no ha sido declarada");
                else if (TabSim.contieneClave(id+"$"+alcanceGlb))
                    id=id+"$"+alcanceGlb;
                auxT = TabSim.tipoSim(id)+"";
                pilaTip.push(auxT);
            }
            GenCodigo.insert("LOD",id,"0");
        } else {
            erra("Sintaxis", "Se esperaba (, valor costante, llamada a funcion, verdadero o falso y llego " + lex);
            sigLexema();
        }
    }

    private void opSig() {
        String op = "";
        if (lex.equals("-")) {
            op = lex;
            sigLexema();
        }
        term();
        if (op.equals("-")){
            try {
                String tp = pilaTip.pop();
                tp = op + tp;
                if (tiposExp.containsKey(tp)) {
                    pilaTip.push(tiposExp.get(tp));
                } else {
                    pilaTip.push("I");
                    erra("Semantico", "Conflicto en tipos de la operacion \"" + op + "\" < " + tp + " >");
                }
            }catch (EmptyStackException e){
                erra("Sintaxis","Error al evaluar los valores en la pila: " + e.getMessage());
            }
        }
        if (op.equals("-"))
            GenCodigo.insert("OPR","0","8");
    }

    private void opPot() {
        String op = "";
        do {
            if (lex.equals("^")) {
                op = lex;
                sigLexema();
            }
            opSig();
            if (op.equals("^")){
                try {
                    String tp = "",
                            tp2 = pilaTip.pop(),
                            tp1 = pilaTip.pop();
                    tp = tp1 + op + tp2;
                    if (tiposExp.containsKey(tp))
                        pilaTip.push(tiposExp.get(tp));
                    else {
                        pilaTip.push("I");
                        erra("Semantico", "Conflicto en tipos de la operacion \"" + op + "\" < " + tp + " >");
                    }
                }catch (EmptyStackException e){
                    erra("Sintaxis","Error al evaluar los valores en la pila: " + e.getMessage());
                }
            }
            if (op.equals("^"))
                GenCodigo.insert("OPR","0","7");
        } while (lex.equals("^"));
    }

    private void opMul() {
        String op = "";
        do {
            if (lex.equals("*") || lex.equals("/")) {
                op = lex;
                sigLexema();
            }
            opPot();
            if (op.equals("*") || op.equals("/")){
                try {
                    String tp = "",
                            tp2 = pilaTip.pop(),
                            tp1 = pilaTip.pop();
                    tp = tp1 + op + tp2;
                    if (tiposExp.containsKey(tp))
                        pilaTip.push(tiposExp.get(tp));
                    else {
                        pilaTip.push("I");
                        erra("Semantico", "Conflicto en tipos de la operacion \"" + op + "\" < " + tp + " >");
                    }
                }catch (EmptyStackException e){
                    erra("Sintaxis","Error al evaluar los valores en la pila: " + e.getMessage());
                }
            }
            if (op.equals("*"))
                GenCodigo.insert("OPR","0","4");
            else if (op.equals("/"))
                GenCodigo.insert("OPR","0","5");
        } while (lex.equals("*") || lex.equals("/"));
    }

    private void opSum() {
        String op= "";
        boolean bin = false;
        do {
            if ((lex.equals("+") || lex.equals("-")) && bin) {
                op = lex;
                sigLexema();
            }
            opMul();
            if (op.equals("+") || op.equals("-")){
                try {
                    String tp = "",
                            tp2 = pilaTip.pop(),
                            tp1 = pilaTip.pop();
                    tp = tp1 + op + tp2;
                    if (tiposExp.containsKey(tp))
                        pilaTip.push(tiposExp.get(tp));
                    else {
                        pilaTip.push("I");
                        erra("Semantico", "Conflicto en tipos de la operacion \"" + op + "\" < " + tp + " >");
                    }
                }catch (EmptyStackException e){
                    erra("Sintaxis","Error al evaluar los valores en la pila: " + e.getMessage());
                }
            }
            if (op.equals("+"))
                GenCodigo.insert("OPR","0","2");
            else if (op.equals("-"))
                GenCodigo.insert("OPR","0","3");
            if (lex.equals("+") || lex.equals("-"))
                bin = true;
        } while (lex.equals("+") || lex.equals("-"));
    }

    private void opRel() {
        String op = "";
        opSum();
        if (token.equals("OpeRel")) {
            op = lex;
            sigLexema();
            opSum();
            if (op.equals("<") || op.equals("<=") || op.equals(">") || op.equals(">=") ||
                    op.equals("=") || op.equals("<>") ){
                try {
                    String tp = "",
                            tp2 = pilaTip.pop(),
                            tp1 = pilaTip.pop();
                    tp = tp1 + op + tp2;
                    if (tiposExp.containsKey(tp))
                        pilaTip.push(tiposExp.get(tp));
                    else {
                        pilaTip.push("I");
                        erra("Semantico", "Conflicto en tipos de la operacion \"" + op + "\" < " + tp + " >");
                    }
                }catch (EmptyStackException e){
                    erra("Sintaxis","Error al evaluar los valores en la pila: " + e.getMessage());
                }
            }
            if (op.equals("<")) GenCodigo.insert("OPR","0","9");
            else if (op.equals(">")) GenCodigo.insert("OPR","0","10");
            else if (op.equals("<=")) GenCodigo.insert("OPR","0","11");
            else if (op.equals(">=")) GenCodigo.insert("OPR","0","12");
            else if (op.equals("<>")) GenCodigo.insert("OPR","0","13");
            else if (op.equals("=")) GenCodigo.insert("OPR","0","14");
        }
    }

    private void opNo() {
        String op = "";
        if (lex.equals("no")) {
            op = lex;
            sigLexema();
        }
        opRel();
        if (op.equals("no")){
            try {
                String tp = "",
                        tp1 = pilaTip.pop();
                tp = op + tp1;
                if (tiposExp.containsKey(tp))
                    pilaTip.push(tiposExp.get(tp));
                else {
                    pilaTip.push("I");
                    erra("Semantico", "Conflicto en tipos de la operacion \"" + op + "\" < " + tp + " >");
                }
            }catch (EmptyStackException e){
                erra("Sintaxis","Error al evaluar los valores en la pila: " + e.getMessage());
            }
        }
        if (op.equals("no"))
            GenCodigo.insert("OPR","0","17");
    }

    private void opY() {
        String op = "";
        do {
            if (lex.equals("y")) {
                op = lex;
                sigLexema();
            }
            opNo();
            if (op.equals("y")){
                try {
                    String tp = "",
                            tp2 = pilaTip.pop(),
                            tp1 = pilaTip.pop();
                    tp = tp1 + op + tp2;
                    if (tiposExp.containsKey(tp))
                        pilaTip.push(tiposExp.get(tp));
                    else {
                        pilaTip.push("I");
                        erra("Semantico", "Conflicto en tipos de la operacion \"" + op + "\" < " + tp + " >");
                    }
                }catch (EmptyStackException e){
                    erra("Sintaxis","Error al evaluar los valores en la pila: " + e.getMessage());
                }
            }
            if (op.equals("y"))
                GenCodigo.insert("OPR","0","15");
        } while (lex.equals("y"));
    }

    private void opO(){
        String op = "";
        do {
            if (lex.equals("o")) {
                op = lex;
                sigLexema();
            }
            opY();
            if (op.equals("o")){
                try {
                    String tp = "",
                            tp2 = pilaTip.pop(),
                            tp1 = pilaTip.pop();
                    tp = tp1 + op + tp2;
                    if (tiposExp.containsKey(tp))
                        pilaTip.push(tiposExp.get(tp));
                    else {
                        pilaTip.push("I");
                        erra("Semantico", "Conflicto en tipos de la operacion \"" + op + "\" < " + tp + " >");
                    }
                }catch (EmptyStackException e){
                    erra("Sintaxis","Error al evaluar los valores en la pila: " + e.getMessage());
                }
            }
            if (op.equals("o"))
                GenCodigo.insert("OPR","0","16");
        } while (lex.equals("o"));
    }

    private void expr() {
        opO();
    }

    private void param() {
        Simbolo aux = null;
        do {
            char tipoParam='I';
            if (lex.equals("entero") || lex.equals("decimal") || lex.equals("alfabetico") || lex.equals("logico")) {
                if (lex.equals("entero")) tipoParam = 'E';
                else if (lex.equals("decimal")) tipoParam = 'R';
                else if (lex.equals("alfabetico")) tipoParam = 'A';
                else if (lex.equals("logico")) tipoParam = 'L';
                consume(lex, lex);
            }else
                consume("Tipo de dato", lex);

            aux = new Simbolo(lex,'R',tipoParam,"0","0");
            if (!decParam.contains(aux))
                decParam.add(aux);
            else
                erra("Sintactico", "La variable<"+lex+"> ya ha sido declarada en el ambito:"+alcanceGlb);

            consume("Identi", token);
            if (!lex.equals(","))
                break;
            else
                sigLexema();
        } while (true);
    }

    private void proc() {
        cmpRegPro = true;
        String tipoVar="", ident = lex, inicioF="";
        Stack<Simbolo> tempParam = new Stack<>();
        alcanceGlb = lex+"$";
        inicioF = GenCodigo.numLin()+"";
        consume("Identi", token);
        consume("(", lex);
        if (!lex.equals(")")) {
            param();
            alcanceGlb = ident;
            while (!decParam.isEmpty()) {
                alcanceGlb += ("$" + decParam.peek().tipo);
                tempParam.push(decParam.poll());
            }
            String aux = "";
            while (!tempParam.isEmpty()) {
                aux = tempParam.peek().nombre+"$"+alcanceGlb;
                GenCodigo.insert("STO","0",aux);
                TabSim.insertaSim(aux, tempParam.pop());
            }
        }
        consume(")",lex);
        if (!TabSim.contieneClave(alcanceGlb))
            TabSim.insertaSim(alcanceGlb,new Simbolo(ident,'P','I',inicioF,"0"));
        else
            erra("Semantico", "La variable<"+ident+"> ya ha sido delcarada anteiormente");
        if (!lex.equals("inicio")) {
            while (lex.equals("entero") || lex.equals("decimal") || lex.equals("alfabetico") || lex.equals("logico")) {
                tipoVar = lex;
                sigLexema();
                var(tipoVar);
            }
        }
        bloque();
        consume(";",lex);
        GenCodigo.insert("OPR","0","1");
        cmpRegPro = false;
        alcanceGlb = "";
    }

    private void func(String tipo) {
        String tipoVar="", ident = lex;
        String iniF = GenCodigo.numLin()+"";
        Stack<Simbolo> tempParam = new Stack<>();
        cmpRegFunc = true;
        cmpReg = false;
        alcanceGlb = lex+"$";
        char tipoFun = 'I';
        if(tipo.equals("entero")) tipoFun = 'E';
        else if (tipo.equals("decimal")) tipoFun = 'R';
        else if (tipo.equals("alfabetico")) tipoFun = 'A';
        else if (tipo.equals("logico")) tipoFun = 'L';
        tipoF = tipoFun;
        consume("Identi", token);
        consume("(", lex);
        if (!lex.equals(")")) {
            param();
            alcanceGlb = ident;
            while (!decParam.isEmpty()) {
                alcanceGlb += ("$" + decParam.peek().tipo);
                tempParam.push(decParam.poll());
            }
            String aux = "";
            while (!tempParam.isEmpty()) {
                aux = tempParam.peek().nombre + "$" + alcanceGlb;
                GenCodigo.insert("STO", "0", aux);
                TabSim.insertaSim(aux, tempParam.pop());
            }
        }
        consume(")", lex);
        funcAct = alcanceGlb;
        if (!TabSim.contieneClave(alcanceGlb))
            TabSim.insertaSim(alcanceGlb, new Simbolo(ident,'F',tipoFun,iniF,"0"));
        else
            erra("Semantico", "La funcion<"+ident+":"+alcanceGlb+"> ya ha sido declarada anteiormente");
        if (!lex.equals("inicio")) {
            while (lex.equals("entero") || lex.equals("decimal") || lex.equals("alfabetico") || lex.equals("logico")) {
                tipoVar = lex;
                sigLexema();
                var(tipoVar);
            }
        }
        bloque();
        if (!cmpReg)
            erra("Semantico", "Las funcion<"+ident+"> deben contener almenos un estatuto regresa.");
        cmpReg = false;
        cmpRegFunc = false;
        tipoF = 'I';
        consume(";", lex);
        alcanceGlb = "";
    }

    private void asigna(String id){
        if (lex.equals("[")) {
            dimen();
            if (!TabSim.esArray(id) && !TabSim.esMatriz(id))
                erra("Semantico", "La variable<"+id+"> no debe ser dimencionada");
            if (!dimenciones[0].equals("0") && dimenciones[1].equals("0") && TabSim.esMatriz(id))
                erra("Semantico", "La variable<"+id+"> debe tener dos dimenciones");
            if (!dimenciones[0].equals("0") && !dimenciones[1].equals("0") && TabSim.esArray(id))
                erra("Semantico", "La variable<"+id+"> debe tener solo una dimencion");
        }else {
            if (TabSim.esArray(id) || TabSim.esMatriz(id))
                erra("Semantico", "La variable<"+id+">  debe ser dimencionada");
        }
        consume(":=",lex);
        expr();
        String tp="",
                tp2 = pilaTip.pop(),
                tp1 = pilaTip.pop();
        tp = tp1+":="+tp2;
        if (!tiposExp.containsKey(tp))
            erra("Semantico", "Conflicoto en tipos en la operacion := < "+tp+" >");
        GenCodigo.insert("STO","0",id);
    }

    private void regresa(){
        consume("regresa",lex);
        if (!lex.equals(";")) {
            expr();
            if (cmpRegPro)
                erra("Semantico", "Los procedimeintos solo pueden tener regresa vacios");
            if (cmpRegFunc){
                try {
                    if (!pilaTip.pop().equals(tipoF + ""))
                        erra("Semantico", "El tipo de dato en regresa debe coincidir con el de la func o proc.");
                }catch (EmptyStackException e){
                    erra("Sintaxis","Error al evaluar los valores en la pila: " + e.getMessage());
                }
            }
            GenCodigo.insert("STO","0",funcAct);
        }
        consume(";", lex);
        GenCodigo.insert("OPR","0","1");
    }

    private void sino(){
        sigLexema();
        if (lex.equals("inicio")){
            bloque();
            consume(";", lex);
        }else {
            estatuto();
        }
    }

    private void siSn(){
        String falsEt= GenCodigo.sigEtiqueta(), finS = GenCodigo.sigEtiqueta();
        sigLexema();
        consume("(", lex);
        expr();
        consume(")", lex);
        try {
            String tempT = pilaTip.pop();
            if (!tempT.equals("L"))
                erra("Semantico", "Se esperaba una expresion logica y llego: " + tempT);
        }catch (EmptyStackException e){
            erra("Sintaxis","Error al evaluar los valores en la pila: " + e.getMessage());
        }
        GenCodigo.insert("JMC","F",falsEt);
        if (lex.equals("inicio")){
            bloque();
            consume(";", lex);
            if (lex.equals("sino")) {
                GenCodigo.insert("JMP","0",finS);
                TabSim.insertaSim(falsEt, new Simbolo(falsEt,'I','I',GenCodigo.numLin()+"","0"));
                sino();
            }else
                TabSim.insertaSim(falsEt, new Simbolo(falsEt,'I','I',GenCodigo.numLin()+"","0"));
            TabSim.insertaSim(finS, new Simbolo(finS,'I','I',GenCodigo.numLin()+"","0"));
        }else{
            estatuto();
            TabSim.insertaSim(falsEt, new Simbolo(falsEt,'I','I',GenCodigo.numLin()+"","0"));
            if (lex.equals("sino")) {
                GenCodigo.insert("JMP","0",finS);
                TabSim.insertaSim(falsEt, new Simbolo(falsEt,'I','I',GenCodigo.numLin()+"","0"));
                sino();
            }else
                TabSim.insertaSim(falsEt, new Simbolo(falsEt,'I','I',GenCodigo.numLin()+"","0"));
            TabSim.insertaSim(finS, new Simbolo(finS,'I','I',GenCodigo.numLin()+"","0"));
        }
    }

    private void para(){
        String idAux = "", idTemp="", cteTemp="",oper="";
        String finP = GenCodigo.sigEtiqueta(), iniEx = GenCodigo.sigEtiqueta();
        int dirInicio=0;
        esIter = true;
        sigLexema();
        idAux = lex;
        if (!TabSim.contieneClave(idAux) && !TabSim.contieneClave(idAux + "$" + alcanceGlb))
                erra("Semantico", "La variable<" + idAux + "> no ha sido declarada");

        if (TabSim.contieneClave(idAux + "$" + alcanceGlb))
            idAux = idAux + "$" + alcanceGlb;

        if (TabSim.tipoSim(idAux) != 'E')
            erra("Semantica", "El identificador<"+idAux+"> en estatuto para debe ser entero");

        consume("Identi", token);
        consume("en", lex);
        consume("rango", lex);
        expr();
        GenCodigo.insert("STO","0",idAux);
        try {
            if (!pilaTip.pop().equals("E"))
                erra("Semantico", "Valor del rango en la sentecia para debe ser entero");
        }catch (EmptyStackException e){
            erra("Sintaxis","Error al evaluar los valores en la pila: " + e.getMessage());
        }
        consume("a", lex);
        dirInicio = GenCodigo.numLin();
        etqCtrlF.push(finP);
        etqCtrlI.push(iniEx);
        GenCodigo.insert("LOD",idAux,"0");
        expr();
        if (!pilaTip.pop().equals("E"))
            erra("Semantico", "Valor del rango en la sentecia para debe ser entero");

        if (lex.equals("decr"))
            GenCodigo.insert("OPR","0","12");
        else
            GenCodigo.insert("OPR","0","11");
        GenCodigo.insert("JMC","F",finP);
        if (lex.equals("incr") || lex.equals("decr")){
            oper = lex;
            sigLexema();
            if (token.equals("Identi") || token.equals("CteEnt")) {
                if (token.equals("Identi")) {
                    idTemp = lex;
                    if (!TabSim.contieneClave(idTemp) && !TabSim.contieneClave(idTemp + "$" + alcanceGlb))
                        erra("Semantico", "La variable<" + idTemp + "> no ha sido declarada");
                    if (!TabSim.contieneClave(idTemp + "$" + alcanceGlb))
                        idTemp = idTemp + "$" + alcanceGlb;
                    if (TabSim.tipoSim(idTemp) != 'E')
                        erra("Semantica", "El tipo en incr o decr debe ser entero.");
                }else
                    cteTemp = lex;
                sigLexema();
            }else
                consume("CteEnt o Identi", token);
        }
        if (lex.equals("inicio")){
            bloque();
            consume(";", lex);
        } else
            estatuto();
        TabSim.insertaSim(iniEx, new Simbolo(iniEx,'I','I',GenCodigo.numLin()+"","0"));
        if (!oper.equals("")){
            GenCodigo.insert("LOD",idAux,"0");
            if (!cteTemp.equals(""))
                GenCodigo.insert("LIT",cteTemp,"0");
            else
                GenCodigo.insert("LOD",idTemp,"0");
            if (oper.equals("incr"))
                GenCodigo.insert("OPR","0","2");
            else if (oper.equals("decr"))
                GenCodigo.insert("OPR","0","3");
            GenCodigo.insert("STO","0",idAux);
        }else {
            GenCodigo.insert("LOD",idAux,"0");
            GenCodigo.insert("LIT","1","0");
            GenCodigo.insert("OPR","0","2");
            GenCodigo.insert("STO","0",idAux);
        }
        GenCodigo.insert("JMP","0",dirInicio+"");
        TabSim.insertaSim(finP,new Simbolo(finP,'I','I',GenCodigo.numLin()+"","0"));
        etqCtrlF.pop();
        etqCtrlI.pop();
        esIter = false;
    }

    private void itera(){
        int iniI=0;
        String finI = GenCodigo.sigEtiqueta();
        esIter = true;
        sigLexema();
        consume("mientras", lex);
        consume("(", lex);
        iniI = GenCodigo.numLin();
        etqCtrlI.push(iniI+"");
        etqCtrlF.push(finI);
        expr();
        try {
            if (!pilaTip.pop().equals("L"))
                erra("Semantico", "Se esperaba exresion logica en el estatuto itera");
        }catch (EmptyStackException e){
            erra("Sintaxis","Error al evaluar los valores en la pila: " + e.getMessage());
        }
        consume(")", lex);
        GenCodigo.insert("JMC","F",finI);
        if (lex.equals("inicio")) {
            bloque();
            consume(";", lex);
        }else
            estatuto();
        GenCodigo.insert("JMP","0",""+iniI);
        TabSim.insertaSim(finI,new Simbolo(finI,'I','I',GenCodigo.numLin()+"","0"));
        etqCtrlF.pop();
        etqCtrlI.pop();
        esIter = false;
    }

    private void opcion(){
        String tipoE = "I", tipoC = "I";
        String opcTemp = GenCodigo.sigVarTemp(), eFinCaso = "", eFinOpc = GenCodigo.sigEtiqueta();
        esIter = true;
        sigLexema();
        consume("opcion", lex);
        consume("(", lex);
        expr();
        try {
            tipoE = pilaTip.pop();
            if (tipoE.equals("I"))
                erra("Semantico", "No se puede evaluar un tipo de dato indefinido");
        }catch (EmptyStackException e){
            erra("Sintaxis","Error al evaluar los valores en la pila: " + e.getMessage());
        }
        TabSim.insertaSim(opcTemp, new Simbolo(opcTemp,'V',tipoE.charAt(0),"0","0"));
        GenCodigo.insert("STO","0",opcTemp);
        etqCtrlF.push(eFinOpc);
        consume(")", lex);
        consume("inicio",lex);
        do {
            consume("caso",lex);
            if (token.equals("CteEnt") || token.equals("CteDec") || token.equals("CteAlf")) {
                if (token.equals("CteEnt")) tipoC = "E";
                else if (token.equals("CteDec")) tipoC = "R";
                else if (token.equals("CteAlf")) tipoC = "A";
                if (!tipoE.equals(tipoC))
                    erra("Semantico", "El tipo de valor en caso<"+tipoC+"> no coincide con el de la exprecion<"+tipoE+">");
                GenCodigo.insert("LIT",lex,"0");
                consume(token, token);
            }else
                consume("Valor constante entero, decimal o alfabetico", token);

            eFinCaso = GenCodigo.sigEtiqueta();
            GenCodigo.insert("LOD",opcTemp,"0");
            GenCodigo.insert("OPR","0","14");
            GenCodigo.insert("JMC","F",eFinCaso);

            consume(":",lex);
            while (!lex.equals("caso") && !lex.equals("otro") && lexico.continuarLeec()){
                if (lex.equals("interrumpe")){
                    interrumpe();
                    break;
                }
                estatuto();
            }
            TabSim.insertaSim(eFinCaso,new Simbolo(eFinCaso,'I','I',GenCodigo.numLin()+"","0"));
        }while (!lex.equals("fin") && !lex.equals("otro") && lexico.continuarLeec());
        if (lex.equals("otro")){
            consume("otro",lex);
            consume("caso",lex);
            consume(":",lex);
            while (!lex.equals("fin") && lexico.continuarLeec()){
                if (lex.equals("interrumpe")) {
                    interrumpe();
                    break;
                }
                estatuto();
            }
        }
        consume("fin",lex);
        consume(";",lex);
        TabSim.insertaSim(eFinOpc, new Simbolo(eFinOpc,'I','I',GenCodigo.numLin()+"","0"));
        etqCtrlF.pop();
        esIter = false;
    }

    private void continua(){
        consume("continua",lex);
        consume(";", lex);
        if (!etqCtrlI.empty())
            GenCodigo.insert("JMP","0",etqCtrlI.peek());
    }

    private void interrumpe(){
        consume("interrumpe", lex);
        consume(";", lex);
        if (!esIter)
            erra("Semantico", "Sentencia interrupe solo es valida en ciclos iterativos o haz opcion");
        if (!etqCtrlF.isEmpty())
            GenCodigo.insert("JMP","0",etqCtrlF.peek());
    }

    private void imprime(){
        consume("(", lex);
        do{
            if(lex.equals(",")) {
                GenCodigo.insert("OPR","0","20");
                consume(",", lex);
            }
            expr();
            try {
                if (pilaTip.pop().equals("I"))
                    erra("Semantica", "No se puede imprimir un tipo de dato indeterminado.");
            }catch (EmptyStackException e){
                erra("Sintaxis","Error al evaluar los valores en la pila: " + e.getMessage());
            }
        }while (lex.equals(","));
        consume(")",lex);
    }

    private void lee(){
        String idAux = "";
        consume("(",lex);
        idAux = lex;
        if (!TabSim.contieneClave(idAux) && !TabSim.contieneClave(idAux+"$"+alcanceGlb))
            erra("Semantico", "La variable<"+idAux+"> no a sido declarada.");
        else if (TabSim.contieneClave(idAux+"$"+alcanceGlb))
            idAux = idAux+"$"+alcanceGlb;
        consume("Identi",token);
        if (lex.equals("[")) {
            dimen();
        }
        consume(")", lex);
        GenCodigo.insert("OPR",idAux,"19");
    }

    private void estatuto() {
        String id = "";
        if (token.equals("Identi") || lex.equals("imprime") || lex.equals("imprimenl") || lex.equals("lee")) {
            id = lex;
            sigLexema();
            if (lex.equals("(")) {
                if(id.equals("imprime") || id.equals("imprimenl")) {
                    imprime();
                    if (id.equals("imprime"))
                        GenCodigo.insert("OPR","0","20");
                    else
                        GenCodigo.insert("OPR","0","21");
                }else if(id.equals("lee"))
                    lee();
                else {
                    String param = "", Ex = GenCodigo.sigEtiqueta();
                    GenCodigo.insert("LOD",Ex,"0");
                    callFun();
                    while (!paramU.isEmpty())
                        param += "$"+paramU.poll();
                    if (param.equals(""))
                        param = "$";
                    if (!TabSim.contieneClave(id+param))
                        erra("Semantico", "La funcion<"+id+param+"> no a sido definida");
                    GenCodigo.insert("CAL",id+param,"0");
                    TabSim.insertaSim(Ex, new Simbolo(Ex,'I','I',GenCodigo.numLin()+"","0"));
                }
                consume(";", lex);
            } else if (lex.equals("[") || lex.equals(":=")) {
                String idaux = id;
                if (!TabSim.contieneClave(id)){
                    if (!TabSim.contieneClave(id+"$"+alcanceGlb)){
                        erra("Semantico", "La variable<"+id+"> no ha sido declarada.");
                    }else {
                        idaux = id+"$"+alcanceGlb;
                        pilaTip.push(TabSim.tipoSim(id + "$" + alcanceGlb) + "");
                        if (TabSim.claseSim(id + "$" + alcanceGlb) == 'C')
                            erra("Semantico", "No se puede sobreescribir el valor de la constante:"+id);
                    }
                }else{
                    pilaTip.push(TabSim.tipoSim(id)+"");
                    if (TabSim.claseSim(id) == 'C')
                        erra("Semantico", "No se puede sobreescribir el valor de la constante:"+id);
                }
                asigna(idaux);
                consume(";", lex);
            }else{
                erra("Sintaxis", "Se esperaba [,:=,) y llego: "+lex);
                sigLexema();
            }
        } else if (lex.equals("si")) {
            siSn();
        } else if (lex.equals("iterar")) {
            itera();
        } else if (lex.equals("para")) {
            para();
        } else if (lex.equals("haz")) {
            opcion();
        } else if (lex.equals("regresa")) {
            cmpReg = true;
            if (!cmpRegFunc && !cmpRegPro)
                erra("Semantico", "El estatuto regresa solo valido en funciones o procedimientos");
            regresa();
        }else if (lex.equals("interrumpe")){
            interrumpe();
        }else if (lex.equals("continua")){
            continua();
        }else {
            erra("Sintaxis", "Se esperaba algun estatuto valido y llego: "+lex);
            sigLexema();
        }
    }

    private void bloque(){
        consume("inicio", lex);
        if(!lex.equals("fin")) {
            do {
                if (!lex.equals("fin"))
                    estatuto();
            } while (!lex.equals("fin") && lexico.continuarLeec());
        }
        consume("fin", lex);
    }

    public void prgm() {
        String tipoVar="",tipoFun="", inicioE = GenCodigo.sigEtiqueta();
        TabSim.insertaSim("_P",new Simbolo("_P",'I','I',"1","0"));
        sigLexema();
        if (!lex.equals("programa")) {
            while (lex.equals("constante") || lex.equals("entero") || lex.equals("decimal") || lex.equals("logico") || lex.equals("alfabetico")) {
                if (lex.equals("constante")) {
                    sigLexema();
                    consta();
                } else {
                    tipoVar = lex;
                    tipoFun = lex;
                    sigLexema();
                    if (!lex.equals("funcion"))
                        var(tipoVar);
                    else
                        break;

                }
            }
            GenCodigo.insert("JMP","0",inicioE);
            while (lex.equals("procedimiento") || lex.equals("funcion") || lex.equals("entero") || lex.equals("decimal") || lex.equals("logico") || lex.equals("alfabetico")) {
                if (lex.equals("procedimiento")) {
                    sigLexema();
                    proc();
                } else if (lex.equals("funcion")) {
                    sigLexema();
                    func(tipoFun);
                } else {
                    tipoFun = lex;
                    sigLexema();
                    if (!lex.equals("funcion"))
                        erra("Sintaxis", "Declaracion de variables fuera de lugar: " + lex);
                    else {
                        sigLexema();
                        func(tipoFun);
                    }
                }
            }
        }
        consume("programa", lex);
        TabSim.insertaSim(inicioE,new Simbolo(inicioE,'I','I',""+GenCodigo.numLin(),"0"));
        bloque();
        consume("programa", lex);
        consume(".", lex);

        if (!lex.equals(""))
            erra("Sintaxis", "Codigo inalcanzable. " + lex);

        CtrlError.imprimTotalError();
    }

}