package kr.or.ddit.board;
import java.util.List;
import java.util.Scanner;

import kr.or.ddit.board.service.BoardServiceImpl;
import kr.or.ddit.board.service.IBoardService;
import kr.or.ddit.board.vo.BoardVO;

public class BoardMain {
		
	private Scanner scan = new Scanner(System.in); 
	private IBoardService boardService;
	
	public BoardMain() {
		boardService = BoardServiceImpl.getInstance();
	}

	/**
	 * 메뉴를 출력하는 메서드
	 */
	public void displayMenu(){
		System.out.println();
		System.out.println("----------------------");
		System.out.println("  === 작 업 선 택 ===");
		System.out.println("  1. 게시판 입력");
		System.out.println("  2. 게시판 삭제");
		System.out.println("  3. 게시판 수정");
		System.out.println("  4. 전체 게시판 출력");
		System.out.println("  5. 게시판 검색");
		System.out.println("  6. 프로그램 종료.");
		System.out.println("----------------------");
		System.out.print("원하는 작업 선택 >> ");
	}
	
	/**
	 * 프로그램 시작메서드
	 */
	public void start(){
		int choice;
		do{
			displayMenu(); //메뉴 출력
			choice = Integer.parseInt(scan.nextLine()); // 메뉴번호 입력받기
			switch(choice){
				case 1 :  // 자료 입력
					insertBoard();
					break;
				case 2 :  // 자료 삭제
					deleteBoard();
					break;
				case 3 :  // 자료 수정
					updateBoard();
					break;
				case 4 :  // 전체 자료 출력
					displayBoardAll();
					break;
				case 5 :  // 자료 검색
					searchBoard();
					break;
				case 6 :  // 작업 끝
					System.out.println("작업을 마칩니다.");
					break;
				default :
					System.out.println("번호를 잘못 입력했습니다. 다시입력하세요");
			}
		}while(choice!=6);
	}
	
	/**
	 * 회원정보 검색 메서드
	 */
	private void searchBoard() {
		
		System.out.println();
		System.out.println("검색할 게시판정보를 입력하세요");
		System.out.print("번호 >> ");
		String boardNo = scan.nextLine().trim();
		
		System.out.print("제목 >> ");
		String boardTitle = scan.nextLine().trim();
		
		System.out.print("작성자 >> ");
		String boardWriter = scan.nextLine().trim();
		
		System.out.print("내용 >> ");
		String boardContents = scan.nextLine().trim();
		
		
		BoardVO bv = new BoardVO();
		bv.setBoardNo(boardNo);
		bv.setBoardTitle(boardTitle);
		bv.setBoardWriter(boardWriter);
		bv.setBoardContent(boardContents);
		
		List<BoardVO> boardList = boardService.searchBoardList(bv);
		
		System.out.println();
		System.out.println("=======================================");
		System.out.println("번호\t제목\t작성자\t작성날짜");
		System.out.println("=======================================");
		
		if(boardList.size() == 0) {
			System.out.println("출력할 게시판정보가 없습니다.");
		}else {
			for(BoardVO bv2 : boardList) {
				System.out.println(bv2.getBoardNo() + "\t" + bv2.getBoardTitle()+"\t"
						+bv2.getBoardWriter()+"\t"+bv2.getBoardDate());				
				System.out.println("내용: " + bv2.getBoardContent());
				System.out.println();
			}
		}
		System.out.println("=======================================");
		System.out.println("검색 끝.");
	}

	/**
	 * 전체 회원 정보를 출력하는 메서드
	 */
	private void displayBoardAll() {
		System.out.println();
		System.out.println("=======================================");
		System.out.println("번호\t제목\t작성자\t작성날짜");
		System.out.println("=======================================");
		
		List<BoardVO> boardList = boardService.getAllBoardList();
		if(boardList.size() == 0) {
			System.out.println("출력할 게시판정보가 없습니다.");
		}else {
			for(BoardVO bv : boardList) {
				System.out.println(bv.getBoardNo() + "\t" + bv.getBoardTitle()+"\t"
						+bv.getBoardWriter()+"\t"+bv.getBoardDate());				
				System.out.println("내용: " + bv.getBoardContent());
				System.out.println();
			}
		}
		System.out.println("=======================================");
		System.out.println("출력 끝.");
	}

	private void deleteBoard() {
		System.out.println();
		System.out.println("삭제할 게시판정보를 입력하세요");
		System.out.print("번호 >> ");
		String boardNo = scan.nextLine();
		
		int cnt = boardService.removeBoard(boardNo);
		
		if(cnt > 0) {
			System.out.println(boardNo + "번 게시판 삭제 성공!");
		}else {
			System.out.println(boardNo + "번 게시판 삭제 실패!!");
		}
		
	}

	/**
	 * 회원정보를 수정하는 메서드
	 */
	private void updateBoard() {

		boolean chk = false;
		
		String boardNo = "";
		do {
			System.out.println();
			System.out.println("수정할 게시판 정보를 입력하세요.");
			System.out.print("번호 >> ");
			
			boardNo = scan.nextLine();
			
			chk = checkBoard(boardNo);
			
			if(chk == false) {
				System.out.println("게시판 번호가 " + boardNo + "인 게시판은 존재하지 않습니다.");
				System.out.println("다시입력해주세요.");
			}
			
		} while(chk == false);
		
		 System.out.print("제목 >> ");
		 String boardTitle = scan.nextLine();
		 
		 System.out.print("작성자 >> ");
		 String boardWriter = scan.nextLine();
		 
		 System.out.print("내용 >> ");
		 String boardContents = scan.nextLine();
		 
		 BoardVO bv = new BoardVO();
		 bv.setBoardNo(boardNo);
		 bv.setBoardTitle(boardTitle);
		 bv.setBoardWriter(boardWriter);
		 bv.setBoardContent(boardContents);
		 
		 int cnt = boardService.modifyBoard(bv);
		 
		 if(cnt > 0) {
				System.out.println(boardNo + "번 게시판 수정 성공!");
			}else {
				System.out.println(boardNo + "번 게시판 수정 실패!!");
			}
	}
	
	private boolean checkBoard(String boardNo) {
		
		return boardService.checkBoard(boardNo);
		
	}

	/**
	 * 회원정보 추가하는 메서드
	 */
	private void insertBoard() {
		
		System.out.println();
		System.out.println("추가할 게시판정보를 입력하세요");
		System.out.print("제목 >> ");
		
		String boardTitle = scan.nextLine();
		
		 System.out.print("작성자 >> ");
		 String boardWriter = scan.nextLine();
		 
		 System.out.print("내용 >> ");
		 String boardContents = scan.nextLine();
		 
		 BoardVO bv = new BoardVO();
		 bv.setBoardTitle(boardTitle);
		 bv.setBoardWriter(boardWriter);
		 bv.setBoardContent(boardContents);
		 
		 int cnt = boardService.registerBoard(bv);
		
		 if(cnt > 0) {
				System.out.println("게시판 등록 성공!");
		}else {
			System.out.println("게시판 등록 실패!!");
		}
	}

	public static void main(String[] args) {
		BoardMain boardObj = new BoardMain();
		boardObj.start();
	}

}






