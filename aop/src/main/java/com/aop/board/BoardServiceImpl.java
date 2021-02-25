package com.aop.board;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BoardServiceImpl implements BoardService {

    @Autowired BoardRepository boardRepository;

    @Override
    public List<Board> getBoards() {
        return boardRepository.findAll();
    }

}
