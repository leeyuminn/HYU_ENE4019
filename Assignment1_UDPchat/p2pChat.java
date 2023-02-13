import java.io.*;
import java.net.*;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.security.MessageDigest;
import java.util.Scanner;

public class p2pChat{

    //IP Address mapping(hash)
    public static String getRoomAddress(String chatRoomName){
        String ipAddress = null;
        try {
            //입력받은 채팅방 이름을 "SHA-256"해시를 이용해서
            //Multicast address인 225.x.y.z로 변환 -> 해시키 뒤쪽 세자리이용해서 (x, y, z)값 구하기
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");//대입한 메시지("SHA-256")의 digest오브젝트 작성
            messageDigest.update(chatRoomName.getBytes()); //지정된 바이트 데이터를 사용해 다이제스트를 갱신.

            byte byteMessage[] = messageDigest.digest();//byte배열로 해시를 반환한다.

            StringBuffer stringBuffer =  new StringBuffer();//문자열 추가/삭제할 때 사용하는 자료형 객체 생성

            for(int i = 0; i < byteMessage.length; i++){
                //추가할 문자(byte를 HexString으로 변환)
                String string = Integer.toString((byteMessage[i] & 0xff) + 0x100, 16).substring(1);
                //0xff: ff(16) = 1111 1111(2) = 255(10)
                //byteMessage[i]의 기존값 출력 위해 = byteMessage[i] & 0xff
                //1. 8비트의 byte형을 32비트의 int형으로 강제 형변환 : byteMessage[i] & 0xff
                //2. 강제로 3자리의 String으로 변환 : +0x100
                //3. 불필요하게 붙은 제일 앞의 1을 제거 : .substring(1)

                //문자열 추가
                stringBuffer.append(string);
            }
            ipAddress = stringBuffer.toString();//

            //다시 byte로 바꿔서 뒤 3자리 뽑아오기
            byte hashAddress[] = ipAddress.getBytes();
            //64개(0~63)니까 61, 62, 63번째 가져와서 각각 x, y, z에 넣어
            byte x = hashAddress[61];
            byte y = hashAddress[62];
            byte z = hashAddress[63];
            ipAddress = "225." + x + "." + y + "." + z;

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return ipAddress;
    }

    //peer는 server와 client의 역할을 모두 수행 -> Thread를 사용하여 동시에 수행해줄 것.


    public static void main(String[] args) throws InterruptedException {
        int portNo = 0;
        //실행예시: 'java p2pChat portNo' 여기서 포트넘은 집의 포트넘을 뜻함.
        try{
            String portN = args[0]; //multicast port number
            portNo = Integer.parseInt(portN);
        } catch (NumberFormatException e){
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }

        while (true){
            System.out.println("P2P Chatting room start!");
            System.out.println("What do you wnat? : ");

            Scanner scanner = new Scanner(System.in);
            String command = scanner.nextLine(); //ex. #JOIN cNet yumin 또는 #EXIT
            String[] commandSplit = command.split("\\s");//commandSplit={#JOIN, cNet, yumin}

            //#JOIN
            //채팅방이름이 없으면 개설, 이미 있으면 참여
            if(commandSplit[0].equals("#JOIN")){
                String chatRoomName = commandSplit[1];
                String userName = commandSplit[2];
                String roomIP = getRoomAddress(chatRoomName);

                Write write = new Write(portNo, roomIP, userName);
                Thread writer = new Thread(write);
                Read read = new Read(portNo, roomIP, userName);
                Thread reader = new Thread(read);

                writer.start();
                reader.start();
                writer.join();
                reader.join();
            }
            else{
                System.out.println("Wrong Command");
                System.out.println("ReWrite Command! ");
            }
        }
    }
}
