package Resources;

public class Account {
    private final String username;
    private boolean read;
    private boolean write;

    public Account(String username, boolean read, boolean write) {
        this.username = username;
        this.read = read;
        this.write = write;
    }


    public String getUsername() {
        return username;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public boolean isWrite() {
        return write;
    }

    public void setWrite(boolean write) {
        this.write = write;
    }
}
