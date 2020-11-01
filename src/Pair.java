

public class Pair{
    public  int value;
    public  Direction key;

    public Pair(int value, Direction key) {
        this.key = key;
        this.value = value;
    }

    public Direction getKey() { return key; }
    public int getValue() { return value; }
}

