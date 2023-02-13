import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Receive implements Runnable {

    Socket socket;

    String chroomName;
    String userName;

    BufferedReader bufferedReader;
    BufferedWriter bufferedWriter;
    PrintWriter printWriter;

    HashMap<String, HashMap<String, PrintWriter>> chroomTable;

    public Receive(Socket socket, String roomName, String userName, HashMap<String, HashMap<String, PrintWriter>> hashMap, BufferedReader br) {
        this.socket = socket;
        this.chroomName = roomName;
        this.userName = userName;
        this.chroomTable = hashMap;
        this.bufferedReader = br;
    }

    @Override
    public void run() {

        try {
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            printWriter = new PrintWriter(bufferedWriter, true); //출력 스트림 얻어오기.

            for(;;){
                String clientMsg = bufferedReader.readLine(); //msg읽어오기

                if (!clientMsg.startsWith("#")){
                    String msgForm = userName + " : " + clientMsg;
                    //송신자 제외하고 보내주기
                    for(String user : chroomTable.get(chroomName).keySet()){ //테이블 유저돌면서
                        if(!user.equals(userName)) { //이름 같으면 송신자니까 걔만 제외하고 보내라.
                            printWriter = new PrintWriter(chroomTable.get(chroomName).get(user), true);
                            printWriter.println(msgForm);
                        }
                    }
                }
                //#으로 시작한다면 다른 명령어 또는 지원되지 않는 메시지 형태임.
                if(clientMsg.equals("#STATUS")){
                    //현재 상태 (현재 채팅룸 이름, 구성원 이름 출력->클라이언트화면에서
                    List<String> member = new ArrayList<>();
                    for(String user : chroomTable.get(chroomName).keySet()){
                        member.add(user);
                    }
                    String msgForm = "";
                    for(int i = 0; i < member.size(); i++){
                        msgForm = msgForm + " " + member.get(i);
                    }
                    String msgFormPlus ="[" + msgForm + " ] in chatting room [ " + chroomName + " ].";
                    printWriter = new PrintWriter(bufferedWriter, true);
                    printWriter.println(msgFormPlus);
                }
                else if(clientMsg.equals("#EXIT")){
                    //현재 채팅방에서 해당 멤버 제거
                    printWriter.println(clientMsg);

                    synchronized (chroomTable){
                        HashMap<String, PrintWriter> Room = chroomTable.get(chroomName);
                        Room.remove(userName); // 구성원에서 해당 멤버 삭제!
                        System.out.println(userName + " is OUT this chatting room [ " + chroomName + " ].");
                    }
                    bufferedReader.close();
                    bufferedWriter.close();
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bufferedReader.close();
                bufferedWriter.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

