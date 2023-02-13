import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Read implements Runnable {
    Socket socket;
    String uName;
    String chroomName;

    BufferedReader bufferedReader;

    public Read(Socket socket, String uName, String chroomName) throws IOException {
        this.socket = socket;
        this.uName = uName;
        this.chroomName = chroomName;
        this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream())); //입력 스트림 읽어오기.
    }

    @Override
    public void run() {
        try {

            while(true){
                String serverMessage = bufferedReader.readLine();

                if (serverMessage.equals("#EXIT")) {
                    bufferedReader.close();
                    break;
                }
                else if(serverMessage.startsWith("Fail:")){
                    System.out.println(serverMessage);
                    System.exit(0);
                }
                else
                    System.out.println(serverMessage);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bufferedReader.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
