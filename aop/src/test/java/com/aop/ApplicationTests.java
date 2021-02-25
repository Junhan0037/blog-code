package com.aop;

import com.aop.board.BoardService;
import com.aop.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ApplicationTests {

    @Autowired BoardService boardService;
    @Autowired UserService userService;

    @Test
    public void findBoards() throws Exception {
        assertThat(boardService.getBoards().size()).isEqualTo(100);
    }

    @Test
    public void findUsers() throws Exception {
        assertThat(userService.getUsers().size()).isEqualTo(100);
    }

}
