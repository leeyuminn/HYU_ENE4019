import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Server {

    public static void main(String[] args) throws IOException {
        int portNumber1 = Integer.parseInt(args[0]);
        int portNumber2 = Integer.parseInt(args[1]);

        System.out.println("Server Open!");

        chatServer chatserver = new chatServer(portNumber1);
        Thread chatServerThread = new Thread(chatserver);
        chatServerThread.start();

    }

    public static class chatServer implements Runnable {
        int portNumber = 0;
        String command = null;
        String act = null;
        String chroomName = null;
        String userName = null;

        ServerSocket serverSocket;
        Socket socket; //서버에 접속을 요청할 클라이언트의 소켓.

        BufferedReader bufferedReader;
        BufferedWriter bufferedWriter;
        PrintWriter printWriter;

        public chatServer(int portNumber) {
            this.portNumber = portNumber;
        }

        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(portNumber);
                HashMap<String, HashMap<String, PrintWriter>> chroomTable;
                chroomTable = new HashMap<String, HashMap<String, PrintWriter>>();

                for (; ; ) {
                    socket = serverSocket.accept();

                    bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream())); //입력 스트림 얻어오기.
                    bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

                    printWriter = new PrintWriter(bufferedWriter, true); //출력 스트림 얻어오기.

                    command = bufferedReader.readLine();
                    String[] splitCommand = command.split(" ");
                    act = splitCommand[0];
                    chroomName = splitCommand[1];
                    userName = splitCommand[2];

                    if (act.equals("#JOIN")) {
                        synchronized (chroomTable) {
                            //data가져와서
                            HashMap<String, PrintWriter> tempTable;
                            tempTable = chroomTable.get(chroomName); //채팅방이름에 해당하는 해시맵value

                            if (!check(tempTable)) {
                                String errorMsg = "Fail: [" + chroomName + "] room is not exist.";
                                printWriter.println(errorMsg);
                            }

                            HashMap<String, PrintWriter> Room;
                            Room = chroomTable.get(chroomName);
                            Room.put(userName, printWriter);

                            System.out.println(userName + " JOIN in [" + chroomName + "].");
                        }
                    } else if (act.equals("#CREATE")) {
                        synchronized (chroomTable) {
                            //data가져와서
                            HashMap<String, PrintWriter> tempTable;
                            tempTable = chroomTable.get(chroomName); //채팅방이름에 해당하는 해시맵value

                            if (check(tempTable)) {
                                String errorMsg = "Fail: [" + chroomName + "] room already existed.";
                                printWriter.println(errorMsg);
                            }
                            //create
                            HashMap<String, PrintWriter> create = new HashMap<>();
                            chroomTable.put(chroomName, create);

                            create.put(userName, printWriter);

                            System.out.println(userName + " CREATES [" + chroomName + "].");
                        }
                    }
                    //채팅룸 입성!
                    Receive receiver = new Receive(socket, chroomName, userName, chroomTable, bufferedReader);
                    Thread receiveThread = new Thread(receiver);
                    receiveThread.start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public static boolean check(HashMap<String, PrintWriter> table) {
        if (table == null)
            return false;
        else
            return true;
    }
}
