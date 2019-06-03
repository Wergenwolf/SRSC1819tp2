package Utils;

import Resources.AccessOperation;
import Resources.Account;
import Resources.Token;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

public class TokenMaker {

    static protected Token makeKey(String Resource, AccessOperation Operation, long ExpireTime) {
        return new Token(Resource, Operation, ExpireTime);
    }

    static protected Token makeKey(String Resource, AccessOperation Operation) {
        return new Token(Resource, Operation);
    }

    static protected Token makeKey(String Resource, AccessOperation Operation, String Account) {
        return new Token(Resource, Operation, Account);
    }

    static protected Token makeKey(String Resource, AccessOperation Operation, long ExpireTime, String Account) {
        return new Token(Resource, Operation, ExpireTime, Account);
    }

    static public String genToken(Token capability) throws Exception {
        String token = capability.getIdentifier();
        token += capability.getOperation().name() + ".";

        if (capability.getExpirationDate() != 0)
            token += String.valueOf(capability.getExpirationDate());
        if (capability.getLinkedAccount() != null)
            token += capability.getLinkedAccount();

        token = CryptoUtil.encryptAES(token);

        if (capability.getLinkedAccount() != null)
            token = token + "." + capability.getLinkedAccount();
        return token + "." + capability.getExpirationDate();
    }

    //Use this one when u dont include the Account in the capability
    static public boolean checkPermission(List<String> capabilities, String Resource, AccessOperation Operation) throws Exception {
        Date now = new Date();
        Date expirationCapa;
        boolean auth = false;

        Iterator<String> it = capabilities.iterator();
        while (it.hasNext()) {
            String tokenKey = it.next();
            String[] fields = tokenKey.split(Pattern.quote("."));
            long exTime = 0;
            String acc = null;
            //1 so expiration date

            exTime = Long.parseLong(fields[1]);

            expirationCapa = new Date(exTime);
            if (now.after(expirationCapa) && exTime != 0) {
                it.remove();
                continue;
            }

            if (genToken(makeKey(Resource, Operation, exTime, acc)).equals(tokenKey))
                auth = true;
        }
        return auth;
    }

    //Use this when u include the Account in the capability (wont be used o this job but its here)
    static public boolean checkPermission(Account account, String Resource, AccessOperation Operation) throws Exception {
        List<String> capabilities = account.getCapabilities();
        Date now = new Date();
        Date expirationCapa;
        boolean auth = false;

        Iterator<String> it = capabilities.iterator();
        while (it.hasNext()) {
            String tokenKey = it.next();
            int countDots = countDots(tokenKey);
            String[] fields = tokenKey.split(Pattern.quote("."));
            long exTime = 0;
            String acc = null;
            switch (countDots) {
                case 1:
                    exTime = Long.parseLong(fields[1]);
                    break;
                case 2:
                    exTime = Long.parseLong(fields[2]);
                    acc = fields[1];
                    break;
            }
            expirationCapa = new Date(exTime);
            if (acc != null && !account.getUsername().equalsIgnoreCase(acc)) {
                it.remove();
                continue;
            }
            if (now.after(expirationCapa) && exTime != 0) {
                it.remove();
                continue;
            }

            if (genToken(makeKey(Resource, Operation, exTime, acc)).equals(tokenKey))
                auth = true;
        }
        return auth;
    }

    static private int countDots(String text) {
        int count = 0;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '.')
                count++;
        }
        return count;
    }
}
