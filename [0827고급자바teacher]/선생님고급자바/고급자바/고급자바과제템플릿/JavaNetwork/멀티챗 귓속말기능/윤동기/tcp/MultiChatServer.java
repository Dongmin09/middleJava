package kr.or.ddit.tcp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MultiChatServer {
	// 대화명, 클라이언트의 Socket을 저장하기 위한 Map변수 선언
	Map<String, Socket> clients;

	public MultiChatServer() {
		// 동기화 처리 가능하도록 Map객체 생성
		clients = Collections.synchronizedMap(new HashMap<String, Socket>());
	}

	// 시작 메서드
	public void serverStart() {
		Socket socket = null;

		try (ServerSocket serverSocket = new ServerSocket(7777)) {
			System.out.println("서버가 시작되었습니다.");

			while (true) {
				// 클라이언트의 접속을 대기한다.
				socket = serverSocket.accept();

				System.out.println("[" + socket.getInetAddress() + " : " + socket.getPort() + "] 에서 접속하였습니다.");

				// 메시지 전송 처리를 위한 메서드 생성 및 실행
				ServerReceiver receiver = new ServerReceiver(socket);
				receiver.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * 대화방 즉, Map에 저장된 전체 유저에게 안내메시지를 전송하는 메서드
	 * 
	 * @param msg 전송할 메시지
	 */
	public void sendMessage(String msg) {
		Iterator<String> it = clients.keySet().iterator();
		while (it.hasNext()) {
			try {
				String name = it.next(); // 대화명(key값) 가져오기

				// 대화명에 해당하는 소켓객체 가져와 DataOutputStream객체 생성하기
				DataOutputStream dos = new DataOutputStream(clients.get(name).getOutputStream());

				dos.writeUTF(msg); // 메시지 보내기
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/*
	 * 대화방 즉, Map에 장된 전체 유저에게 대화메시지를 전송하는 메서드
	 * 
	 * @param msg 보낼 대화 메시지
	 * 
	 * @param from 보내는 사람 대화명
	 */
	public void sendMessage(String msg, String from) {
		sendMessage("[" + from + "] " + msg);
	}

	/**
	 * 귓속말 하는 메서드
	 * 
	 * @param msg     보낼메세지
	 * @param from    보낸사람 대화명
	 * @param whisper 받는 사람 대화명
	 */
	public void sendMessage(String msg, String from, String whisper) {
		// Map에 저장된 유저의 대화명 리스트를 추출(key값 구하기)
		Iterator<String> it = clients.keySet().iterator();
		while (it.hasNext()) {
			try {
				String name = it.next();// 대화명 (key값 구하기)
				boolean chk = false;
				// 대화명에 해당하는 Socket의 OutputStream객체 구하기
				if (name.equals(whisper)) {
					chk = true;
				}
				if (chk == true) {
					DataOutputStream out = new DataOutputStream(clients.get(whisper).getOutputStream());
					out.writeUTF("[" + from + "->" + whisper + "] " + msg);// 메시지 보내기
					System.out.println(from +"귓속말 성공" + whisper);
				} else if(chk == false) {
					System.out.println(from + "귓속말 실패" + whisper);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/*
	 * 서버에서 클라이언트로 메시지를 전송할 Thread를 Inner클래스로 정의 Inner클래스에서는 부모 클래스의 멤버들을 직접 사용할 수
	 * 있다.
	 */
	class ServerReceiver extends Thread {
		private Socket socket;
		private DataInputStream dis;
		private String name;

		public ServerReceiver(Socket socket) {
			this.socket = socket;

			try {
				// 수신용
				dis = new DataInputStream(socket.getInputStream());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void run() {
			try {
				// 서버에서는 클라이언트가 보내준 최초의 메세지 즉, 대화명을 수신한다.
				name = dis.readUTF();

				// 대화명을 받아서 다른 모든 클라이언트에게 대화방 참여 메시지를 보낸다.
				sendMessage("#" + name + "님이 입장했습니다.");

				// 대화명과 소켓정보를 Map에 저장한다.
				clients.put(name, socket);

				System.out.println("현재 서버 접속자 수는 " + clients.size() + "명 입니다.");

				// 이 후의 메시지 처리는 반복문으로 처리한다.
				// 한 클라이언트가 보낸 메시지를 다른 모든 클라이언트에게 보내준다.
				while (dis != null) {
//					sendMessage(dis.readUTF(), name);
					String msg = dis.readUTF();
					String msg2 = "";
					String[] line = msg.split(" ");

					if (line.length > 2) {
						if (line[0].equals("/w") || line[0].equals("/ㅈ")) {

							for (int i = 2; i < line.length; i++) {
								msg2 += (line[i]+" ");
							}

							sendMessage(msg2, name, line[1]); // 오버로딩
						}
					} else {
						sendMessage(msg, name); // 오버로딩
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				// 이 finally 영역이 실행된다는 것은 클라이언트의 접속이 종료되었다는 의미.
				sendMessage(name + "님이 나가셨습니다.");

				// Map에서 해당 대화명을 삭제한다.
				clients.remove(name);

				System.out.println("[" + socket.getInetAddress() + " : " + socket.getPort() + "] 에서 접속을 종료했습니다.");
				System.out.println("현재 접속자 수는 " + clients.size() + "명 입니다.");
			}
		}
	}

	public static void main(String[] args) {
		new MultiChatServer().serverStart();
	}
}
