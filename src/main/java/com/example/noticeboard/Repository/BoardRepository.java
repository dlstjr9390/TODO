package com.example.noticeboard.Repository;

import com.example.noticeboard.dto.BoardRequestDto;
import com.example.noticeboard.dto.BoardResponseDto;
import com.example.noticeboard.entity.Board;
import jakarta.persistence.EntityManager;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.List;


@Repository
public class BoardRepository {
    private final JdbcTemplate jdbcTemplate;

    public BoardRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Board insert(Board board){
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "INSERT INTO board (title, writer, contents, password, date) VALUES(?, ?, ?, ?, NOW())";
        jdbcTemplate.update( con -> {
            PreparedStatement preparedStatement = con.prepareStatement(sql,
                    Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, board.getTitle());
            preparedStatement.setString(2, board.getWriter());
            preparedStatement.setString(3, board.getContents());
            preparedStatement.setString(4, board.getPassword());
            return preparedStatement;
        },
        keyHolder);

        Long id = keyHolder.getKey().longValue();
        board.setId(id);
        return  board;
    }

    public List<BoardResponseDto> findAll(){
        String sql = "SELECT id, title, writer, contents, date FROM board";

        return jdbcTemplate.query(sql, new RowMapper<BoardResponseDto>() {
            @Override
            public BoardResponseDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                Long id = rs.getLong("id");
                String title = rs.getString("title");
                String writer = rs.getString("writer");
                String contents = rs.getString("contents");
                Date date = rs.getDate("date");
                return new BoardResponseDto(id,title,writer,contents,date);
            }
        });
    }

    public Board view(Long id){
        String sql = "SELECT id,title, writer, contents, date FROM board WHERE id = ? ";
        return jdbcTemplate.query(sql,resultSet ->{
            if(resultSet.next()){
                Board board = new Board();
                board.setId(resultSet.getLong("id"));
                board.setTitle(resultSet.getString("title"));
                board.setWriter(resultSet.getString("writer"));
                board.setContents(resultSet.getString("contents"));
                board.setDate(resultSet.getDate("date"));
                return board;
            } else{
                return null;
            }
        },id);
    }

    public void update(Long id,BoardRequestDto requestDto){
        String sql = "UPDATE board SET title = ?, writer = ?, contents = ? WHERE id = ?";
        jdbcTemplate.update(sql,requestDto.getTitle(),requestDto.getWriter(), requestDto.getTitle(),id);
    }


    public void delete(Long id) {
        String sql = "DELETE FROM board WHERE id = ?";
        jdbcTemplate.update(sql,id);
    }

    public Board findById(Long id){

        String sql = "SELECT * FROM board WHERE id = ?";
        return jdbcTemplate.query(sql,resultSet ->{
            if(resultSet.next()){
                Board board = new Board();
                board.setTitle(resultSet.getString("title"));
                board.setWriter(resultSet.getString("writer"));
                board.setPassword(resultSet.getString("password"));
                board.setContents(resultSet.getString("contents"));
                board.setDate(resultSet.getDate("date"));
                return board;
            } else{
                return null;
            }
        },id);
    }

    @Transactional
    public Board createBoard(EntityManager em) {
        Board board = em.find(Board.class, 1);
        board.setTitle("Hello2");
        board.setWriter("Robbert2");
        board.setPassword("159772");
        board.setContents("@Transactional 테스트 중!2");

        System.out.println("createBoard 메서드 종료");
        return board;
    }

}