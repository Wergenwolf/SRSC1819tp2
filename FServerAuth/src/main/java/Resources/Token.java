package Resources;

public class Token {
    final String identifier;
    final AccessOperation operation;
    final long expirationDate;
    final String linkedAccount;

    public Token(String identifier, AccessOperation operation) {
        this.identifier = identifier;
        this.operation = operation;
        this.expirationDate = 0;
        this.linkedAccount = null;
    }

    public Token(String identifier, AccessOperation operation, long expirationDate) {
        this.identifier = identifier;
        this.operation = operation;
        this.expirationDate = expirationDate;
        this.linkedAccount = null;
    }

    public Token(String identifier, AccessOperation operation, String linkedAccount) {
        this.identifier = identifier;
        this.operation = operation;
        this.linkedAccount = linkedAccount;
        this.expirationDate = 0;
    }

    public Token(String identifier, AccessOperation operation, long expirationDate, String linkedAccount) {
        this.identifier = identifier;
        this.operation = operation;
        this.expirationDate = expirationDate;
        this.linkedAccount = linkedAccount;
    }

    public String getIdentifier() {
        return identifier;
    }

    public AccessOperation getOperation() {
        return operation;
    }

    public long getExpirationDate() {
        return expirationDate;
    }

    public String getLinkedAccount() {
        return linkedAccount;
    }
}
