
public class Simbolo {

    public String nombre;
    public char clase;
    public char tipo;
    public String dimen1;
    public String dimen2;

    public Simbolo(String nom, char cla, char tip, String dim1, String dim2){
        nombre = nom;
        clase = cla;
        tipo = tip;
        dimen1 = dim1;
        dimen2 = dim2;
    }

    @Override
    public String toString() {
        return "Simbolo{" +
                "nombre='" + nombre + '\'' +
                ", clase='" + clase + '\'' +
                ", tipo='" + tipo + '\'' +
                ", dimen1=" + dimen1 +
                ", dimen2=" + dimen2 +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Simbolo simbolo = (Simbolo) o;

        if (clase != simbolo.clase) return false;
        if (nombre != null ? !nombre.equals(simbolo.nombre) : simbolo.nombre != null) return false;
        return dimen2 != null ? dimen2.equals(simbolo.dimen2) : simbolo.dimen2 == null;
    }

}
