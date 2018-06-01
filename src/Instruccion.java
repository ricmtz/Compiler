
public class Instruccion {
    public String nemon;
    public String dir1;
    public String dir2;

    public Instruccion(String nemon, String dir1, String dir2) {
        this.nemon = nemon;
        this.dir1 = dir1;
        this.dir2 = dir2;
    }

    @Override
    public String toString() {
        return nemon + " " + dir1 + "," + dir2 + '\n';
    }
}
