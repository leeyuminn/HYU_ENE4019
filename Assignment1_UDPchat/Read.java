import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

//채팅 읽어오기
public class Read implements Runnable{
    int portNum;
    String IpAddress;
    String UserName;

    Read(int portNumber, String ipAddress, String userName){
        portNum = portNumber;
        IpAddress = ipAddress;
        UserName = userName;
    }

    @Override
    public void run() {
        try {
            //receive할 소켓 생성
            MulticastSocket multicastSocket = new MulticastSocket(portNum);

            //채팅방에 join
            multicastSocket.joinGroup(InetAddress.getByName(IpAddress));

            //massage 계속해서 receive
            while(true){
                //512 chunk
                byte readcontent[] = new byte[512];

                DatagramPacket datagramPacket = new DatagramPacket(readcontent, 512);

                multicastSocket.receive(datagramPacket);

                String stringMsg = new String(readcontent);

                System.out.println(stringMsg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}