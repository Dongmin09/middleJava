package kr.or.ddit.util;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

/*
 * SqlSession 객체를 제공하기 위한 유틸 클래스
 */
public class MyBatisUtil {
	
	private static SqlSessionFactory sessionFactory;
	
	static {
			try {
		
				// 1-1. xml설정문서 읽어오기
				// 설정파일의 인코딩 정보 설정(한글처리를 위해서..)
				Charset charset = Charset.forName("UTF-8");
				Resources.setCharset(charset);
				Reader rd = 
						Resources.getResourceAsReader("config/mybatis-config.xml");
				
				// 1-2. 위에서 읽어온 Reader객체를 이용하여 실제 작업을 진행할 객체 생성하기
				sessionFactory = 
						new SqlSessionFactoryBuilder().build(rd);
				rd.close(); // 자원 반납
							
				} catch (IOException e) {
					e.printStackTrace();
			}
		}
	/*
	 * SqlSession객체를 제공하는 팩토리 메서드
	 * SqlSession객체
	 */
	public static SqlSession getInstance() {
		return sessionFactory.openSession();
	}
	
	/*
	 * SqlSession객체를 제공하는 팩토리 메서드
	 * autoCommit 오토커밋 여부
	 * SqlSession객체
	 */
	public static SqlSession getInstance(boolean autoCommit) {
		return sessionFactory.openSession(autoCommit);
	}
	
	
}
