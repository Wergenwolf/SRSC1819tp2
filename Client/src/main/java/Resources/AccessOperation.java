package Resources;

public enum AccessOperation {
    READ, WRITE, DELETE, MODIFY, CREATE;

    AccessOperation() {
    }

    public static AccessOperation fromValue(String value) {
        return valueOf(value);
    }

    @Override
    public String toString() {
        return name();
    }
}
