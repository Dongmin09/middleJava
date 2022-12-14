package kr.or.ddit.example;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Sender extends Thread {
	
	private Scanner scan;
	private String name;
	private DataOutputStream dos;
	
	public Sender(Socket socket) {
		
		name = "[" + socket.getInetAddress() + " : " 
				+ socket.getLocalPort() + "]";
		scan = new Scanner(System.in);
		
		try {
			dos = new DataOutputStream(socket.getOutputStream());
		}catch(IOException ex) {
			ex.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		while(dos != null) {
			
			try {
				dos.writeUTF(name + " >>> " + scan.nextLine());
			}catch(IOException ex) {
				ex.printStackTrace();
			}
			
		}
	}
	
}
