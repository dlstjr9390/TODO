package com.example.noticeboard.service;

import com.example.noticeboard.Repository.BoardRepository;
import com.example.noticeboard.dto.BoardRequestDto;
import com.example.noticeboard.dto.BoardResponseDto;
import com.example.noticeboard.entity.Board;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class BoardService {
    private final BoardRepository boardRepository;


    public BoardService(BoardRepository boardRepository) {
        this.boardRepository= boardRepository;
    }

//    public BoardService(ApplicationContext context) {
//      1.'Bean' 이름으로 가져오기
//      BoardRepository boardRepository = (BoardRepository) context.getBean("boardRepository");

//      2.'Bean' 클래스 형식으로 가져오기
//      BoardRepository boardRepository = context.getBean(BoardRepository.class);
//      this.boardRepository = boardRepository;
//    }

    public BoardResponseDto createBoard(BoardRequestDto requestDto){
        Board board = new Board(requestDto);

        Board insertBoard = boardRepository.save(board);

        BoardResponseDto boardResponseDto = new BoardResponseDto(board);

        return boardResponseDto;
    }

    public List<BoardResponseDto> getBoards(){
        return boardRepository.findAllByOrderByModifiedAtDesc().stream().map(BoardResponseDto::new).toList();
    }

//    public BoardResponseDto detailBoard(Long id){
//        Board board = boardRepository.find(id);
//        BoardResponseDto boardResponseDto = new BoardResponseDto(board);
//        return boardResponseDto;
//    }

    @Transactional
    public Long updateBoard(Long id,BoardRequestDto boardRequestDto){

        Board board = findBoard(id);
        if(board.getPassword().equals(boardRequestDto.getPassword())) {
            board.update(boardRequestDto);
            return id;
        } else{
            throw new IllegalArgumentException("비밀번호가 틀렸습니다.");
        }

    }

    public Long deleteBoard(Long id, BoardRequestDto boardRequestDto) {
        Board board = findBoard(id);

        if(board.getPassword().equals(boardRequestDto.getPassword())) {
            boardRepository.delete(board);
            return id;
        } else{
            throw new IllegalArgumentException("비밀번호가 틀렸습니다.");
        }

    }

    private Board findBoard(Long id){
        return boardRepository.findById(id).orElseThrow(()->
                new IllegalArgumentException("선택한 글은 존재하지 않습니다.")
        );
    }
}
