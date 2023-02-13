import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.Scanner;

//채팅 입력
public class Write implements Runnable{
    int portNum;
    String IpAddress;
    String PeerName;

    Write(int portNumber, String ipAddress, String userName){
        portNum = portNumber;
        IpAddress = ipAddress;
        PeerName = userName;
    }

    @Override
    public void run() {
        //계속해서 메시지 write하기
        while(true){
            Scanner scanner = new Scanner(System.in);
            String writeMsg = scanner.nextLine();
            boolean containShap = writeMsg.contains("#");

            if(writeMsg.equals("#EXIT")){
                writeMsg = PeerName + " is out.";

                try {
                    //send할 packet 만들어서
                    DatagramPacket datagramPacket = new DatagramPacket(writeMsg.getBytes(), writeMsg.getBytes().length, InetAddress.getByName(IpAddress), portNum);
                    //전송하기 socket이용
                    MulticastSocket multicastSocket = new MulticastSocket();
                    multicastSocket.joinGroup(InetAddress.getByName(IpAddress));
                    multicastSocket.send(datagramPacket);

                    multicastSocket.close();
                    System.exit(0);
                    scanner.close();
                    break;

                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //#EXIT아닌데 #으로 시작하면 그건 불허
            else if(containShap){
                System.out.println("# is only command");
            }
            //#EXIT아니면 메시지일테니까 만들어서 전송해줘야 해
            else{
                writeMsg = PeerName + ": " + writeMsg;

                try {
                    DatagramPacket datagramPacket = new DatagramPacket(writeMsg.getBytes(), writeMsg.getBytes().length, InetAddress.getByName(IpAddress), portNum);
                    MulticastSocket multicastSocket = new MulticastSocket();
                    multicastSocket.joinGroup(InetAddress.getByName(IpAddress));
                    multicastSocket.send(datagramPacket);

                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}