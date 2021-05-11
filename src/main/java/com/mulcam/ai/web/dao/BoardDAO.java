package com.mulcam.ai.web.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.mulcam.ai.web.vo.BoardVO;

@Repository
public class BoardDAO {
	
	@Autowired
	SqlSession sqlSession;

	public List<BoardVO> listArticles() {
		// TODO Auto-generated method stub
		return sqlSession.selectList("mapper.board.selectAllBoard");
	}

	public void boardWrite(Map<String, Object> articleMap) {
		sqlSession.insert("mapper.board.insert", articleMap);
		
	}


	public BoardVO viewArticle(int articleNO){
		return sqlSession.selectOne("mapper.board.selectArticle", articleNO);
	}

}

/*


CREATE SEQUENCE  board_seq  
START WITH 1
INCREMENT BY 1;

CREATE TABLE BOARD 
(
  ARTICLENO NUMBER(10) NOT NULL 
, PARENTNO NUMBER(10) DEFAULT 0 NOT NULL 
, TITLE VARCHAR2(500) NOT NULL 
, CONTENT VARCHAR2(4000) 
, IMAGEFILENAME VARCHAR2(100) 
, WRITEDATE DATE DEFAULT sysdate NOT NULL 
, ID VARCHAR2(20) 
, CONSTRAINT BOARD_PK PRIMARY KEY 
  (
    ARTICLENO 
  )
  ENABLE 
);



CREATE OR REPLACE TRIGGER BOARD_TRIGGER 
BEFORE INSERT ON BOARD 
REFERENCING NEW AS NEW
FOR EACH ROW
BEGIN
SELECT BOARD_SEQ.NEXTVAL INTO :NEW.ARTICLENO FROM dual;
END;

ALTER TRIGGER board_trigger ENABLE;


insert into board(parentNO,title,content,id)
values(1,'[답변]test1','테스트 성공했나요?','a');

SELECT * FROM BOARD;

select level,
articleNO,
parentNO,
LPAD(' ', 4*(LEVEL-1)) || title title,
content,
writeDate,
imageFileName,
id
from board
start with parentNO=0
connect by prior articleNO=parentNO
order siblings by articleNO desc;





drop table board cascade CONSTRAINTS;




 */
