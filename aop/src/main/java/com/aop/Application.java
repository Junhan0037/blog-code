package com.aop;

import com.aop.board.Board;
import com.aop.board.BoardRepository;
import com.aop.board.BoardService;
import com.aop.user.User;
import com.aop.user.UserRepository;
import com.aop.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@SpringBootApplication
@RestController @RequiredArgsConstructor
public class Application implements CommandLineRunner {

    private final BoardService boardService;
    private final BoardRepository boardRepository;
    private final UserService userService;
    private final UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        for (int i = 0; i < 100; i++) {
            boardRepository.save(new Board(i+"번째 게시글의 제목", i+"번째 게시글의 내용"));
            userRepository.save(new User(i+"@email.com", i+"번째 사용자"));
        }
    }

    @GetMapping("/boards")
    public List<Board> getBoards() {
        return boardService.getBoards();
    }

    @GetMapping("/users")
    public List<User> getUsers() {
        return userService.getUsers();
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
