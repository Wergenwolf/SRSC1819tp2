import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Security;

public class StorageThread extends Thread {
    private static DataInputStream reader;
    private static DataOutputStream writer;
    private Socket socket;

    public StorageThread(Socket clientSocket) {
        this.socket = clientSocket;
    }

    /*
     * Converts a byte to hex digit and writes to the supplied buffer
     */
    private static void byte2hex(byte b, StringBuffer buf) {
        char[] hexChars = {'0', '1', '2', '3', '4', '5', '6', '7', '8',
                '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        int high = ((b & 0xf0) >> 4);
        int low = (b & 0x0f);
        buf.append(hexChars[high]);
        buf.append(hexChars[low]);
    }

    /*
     * Converts a byte array to hex string
     */
    private static String toHexString(byte[] block) {
        StringBuffer buf = new StringBuffer();
        int len = block.length;
        for (int i = 0; i < len; i++) {
            byte2hex(block[i], buf);
            if (i < len - 1) {
                buf.append(":");
            }
        }
        return buf.toString();
    }

    private static void list(String path) {

        File[] files = new File(path).listFiles();

        String list = "";
        for (File file : files) {
            if (file.isFile()) {
                list += file.getName() + '\n';
            }
        }

    }

    private static void makeDir(String path) throws IOException {
        File file = new File(path);
        file.createNewFile();
    }

    private static void copyFile(String path1, String path2) {
        Path copied = Paths.get("src/test/resources/copiedWithNio.txt");
        Path originalPath = original.toPath();
        Files.copy(originalPath, copied, StandardCopyOption.REPLACE_EXISTING);

        assertThat(copied).exists();
        assertThat(Files.readAllLines(originalPath)
                .equals(Files.readAllLines(copied)));

    }

    public void run() {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        int tmpBytesToRead;
        BufferedReader r = null;

        try {
            reader = new DataInputStream(this.socket.getInputStream());
            writer = new DataOutputStream(this.socket.getOutputStream());

            System.out.println(reader.readUTF());

            String username = reader.readUTF();
            String password = reader.readUTF();

            boolean isPWDValid = Authenticator.checkPassword(username, password);

            if (isPWDValid) {
                String token = Authenticator.generateToken(username);
                byte[] tokenBytes = token.getBytes();
                writer.writeInt(tokenBytes.length);
                writer.write(tokenBytes);

                System.out.println("Authenticated");
            }

            System.out.println("Socket closed");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
