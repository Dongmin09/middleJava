package kr.or.ddit.tcp.test;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Iterator;
import java.util.Scanner;

public class TcpMultiChatClient {

    public void clientStart() {
        try {
            String serverIp = "192.168.142.22";

            Socket socket = new Socket(serverIp, 7777);
            System.out.println("서버에 연결되었습니다.");
            System.out.println();

            // 메시지 전송용 쓰레드 생성
            ClientSender sender = new ClientSender(socket);

            // 메시지 수신용 쓰레드 생성
            ClientReceiver receiver = new ClientReceiver(socket);

            sender.start();
            receiver.start();

        } catch (Exception e) {
            // TODO: handle exception
        }

    } // clientStart 메서드 끝...

    public static void main(String[] args) {
        new TcpMultiChatClient().clientStart();
    }

    // ---------------------------------------
    // 메시지 전송용 쓰레드
    class ClientSender extends Thread {
        private Socket socket;
        private DataInputStream dis;
        private DataOutputStream dos;

        private String name;
        private Scanner scan;

        // 생성자
        public ClientSender(Socket socket) {
            this.socket = socket;
            scan = new Scanner(System.in);

            try {
                dis = new DataInputStream(socket.getInputStream());
                dos = new DataOutputStream(socket.getOutputStream());

                if (dos != null) {
//					클라이언트가 처음 실행되면 자신의대화명(이름)을 입력받아
//					서버로 전송하고 대화명의 중복여부를 feedBack으로 받아서 확인한다.
                    System.out.println("대화명 : ");
                    String name = scan.nextLine();

                    while (true) {
                        dos.writeUTF(name); // 대화명 전송

                        String feedBack = dis.readUTF(); // 대화명 중복여부를 받는다.

                        if ("이름중복".equals(feedBack)) { // 대화명이 중복되면...
                            System.out.println(name + "은 대화명이 중복됩니다...");
                            System.out.println("다른 대화명을 입력하세요...");

                            System.out.print("대화명 : ");
                            name = scan.nextLine();

                        } else { // 중복되지 않을 때
                            this.name = name;
                            System.out
                                    .println("[" + name + "]이 대화방에 입장했습니다...");
                            break; // 반복문 탈출...
                        }
                    } //while문 끝...
                }
            } catch (Exception e) {
                // TODO: handle exception
            }
        } // 생성자

        @Override
        public void run() {
            try {
                while (dos != null) {
                    // 키보드로 입력한 메시지를 서버로 전송한다.
                    dos.writeUTF("[" + name + "] : " + scan.nextLine());
                    if (scan.nextLine().equals("/w")) {

                       privateMessage();
//                        여기에 귓속말 메서드 넣는거 같은데
//                        어떻게 만들지???
                    }
                }
            } catch (Exception e) {
                // TODO: handle exception
            }
        }

    }


    //---------------------------------------------------------

    // 메시지 수신용 쓰레드
    class ClientReceiver extends Thread {
        private Socket socket;
        private DataInputStream dis;

        // 생성자
        public ClientReceiver(Socket socket) {
            this.socket = socket;
            try {
                dis = new DataInputStream(socket.getInputStream());
            } catch (Exception e) {
                // TODO: handle exception
            }
        } // 생성자 끝...

        @Override
        public void run() {
            try {
                while (dis != null) {
                    // 서버가 보내온 메시지를 받아서 화면에 출력한다.
                    System.out.println(dis.readUTF());
                }
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }
}
