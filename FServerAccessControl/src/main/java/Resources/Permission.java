package Resources;

public enum Permission {
    NONE, READ, WRITE, RW;

    Permission() {
    }

    public static Permission fromValue(String value) {
        return valueOf(value);
    }

    @Override
    public String toString() {
        return name();
    }
}
