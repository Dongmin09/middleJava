package tcp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class MultiCharClient2 {

	public void clientStart() {
		Socket socket = null;
		try {
			socket = new Socket("192.168.142.25", 7777);

			System.out.println("서버에 연결되었습니다.");

			// 송신용 스레드
			ClientSender sender = new ClientSender(socket);

			// 수신용 스레드
			ClientReceiver receiver = new ClientReceiver(socket);

			sender.start();
			receiver.start();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	class ClientSender extends Thread {
		private DataOutputStream dos;
		private Scanner scan = new Scanner(System.in);

		public ClientSender(Socket socket) {
			try {
				dos = new DataOutputStream(socket.getOutputStream());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void run() {
			try {
				if (dos != null) {
					// 시작하자 마자 자신의 대화명을 서버로 전송한다.
					System.out.print("대화명 >> ");
					dos.writeUTF(scan.nextLine());
				}

				while (dos != null) {
					// 키보드로 입력받은 메시지를 서버로 전송
					dos.writeUTF(scan.nextLine());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	class ClientReceiver extends Thread {
		private DataInputStream dis;

		public ClientReceiver(Socket socket) {
			try {
				dis = new DataInputStream(socket.getInputStream());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void run() {
			while (dis != null) {
				// 서버로부터 수신한 메시지를 출력하기
				try {
					System.out.println(dis.readUTF());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void main(String[] args) {
		new MultiCharClient2().clientStart();
	}
}
