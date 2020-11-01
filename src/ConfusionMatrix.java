
public class ConfusionMatrix {
    private int[][] matrix = new int[27][27];

    public void add(char a, char b) {
        int aa = a - 97;
        int bb = b - 97;
        if (a == '*') {
            aa = 26;
        }
        if (b == '*') {
            bb = 26;
        }
        //to put * instead of every sign like (-, ,..)
        if (aa<0 || aa>26){
            aa = 26;
        }
        if (bb<0 || bb>26){
            bb = 26;
        }

        this.matrix[aa][bb] += 1;
    }

    public int get(char a, char b) {
        int aa = a - 97;
        int bb = b - 97;
        if (a == '*') {
            aa = 26;
        }
        if (b == '*') {
            bb = 26;
        }
        if (aa<0 || aa>26){
            aa = 26;
        }
        if (bb<0 || bb>26){
            bb = 26;
        }
        return this.matrix[aa][bb];
    }
    public void print_matrix(){
        for(int i = 0; i < 27; i++)
        {
            for(int j = 0; j < 27; j++)
            {
                System.out.printf("%5d ", this.matrix[i][j]);
            }
            System.out.println();
        }
    }

}