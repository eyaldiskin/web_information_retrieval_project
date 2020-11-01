import java.util.Objects;

public class StringPair {
    public String value;
    public String key;

    public StringPair(char value, char key) {
        this.key = String.valueOf(key);
        this.value = String.valueOf(value);
    }

    public StringPair(String value, String key) {
        this.key = key;
        this.value = value;
    }

    public StringPair(char value, String key) {
        this.key = key;
        this.value = String.valueOf(value);
    }

    public StringPair(String value, char key) {
        this.key = String.valueOf(key);
        this.value = value;
    }

    public boolean same_value() {
        return this.key.equals(this.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof StringPair)) {
            return false;
        }
        StringPair other = (StringPair) obj;
        return (this.key.equals(other.key)) && (this.value.equals(other.value));
    }
}