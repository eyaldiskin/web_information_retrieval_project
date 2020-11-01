
public class LetterCount {
    private ConfusionMatrix matrix = new ConfusionMatrix();
    private int[] count = new int[27];

    public void add(char a){
        int aa = a - 97;
        if (a == '*') {
            aa = 26;
        }
        count[aa]++;
    }
    public void add(char a, char b) {
        if ((!Character.isLetter(a)&& a!='*') || (!Character.isLetter(b)&&b!='*')){
            return;
        }
        matrix.add(a, b);
    }
    public int get(char a){
        int aa = a - 97;
        if (a == '*') {
            aa = 26;
        }
        return count[aa];
    }
    public int get(char a, char b){
        return matrix.get(a, b);
    }
    

}