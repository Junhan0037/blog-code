package com.aop;

import com.aop.board.BoardService;
import com.aop.history.History;
import com.aop.history.HistoryRepository;
import com.aop.user.User;
import com.aop.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class AopApplicationTests {

    @Autowired BoardService boardService;
    @Autowired UserService userService;
    @Autowired HistoryRepository historyRepository;

    @Test
    public void findBoards() throws Exception {
        assertThat(boardService.getBoards().size()).isEqualTo(100);
    }

    @Test
    public void findUsers() throws Exception {
        assertThat(userService.getUsers().size()).isEqualTo(100);
    }

    @Test
    public void updateUsers() throws Exception {
        List<User> users = userService.getUsers();
        for(int i = 0; i < 5; i++){
            User user = users.get(i);
            user.setEmail("junhan@email.com");
            userService.update(user);
        }

        List<History> histories = historyRepository.findAll();
        assertThat(histories.size()).isEqualTo(5);
        assertThat(histories.get(0).getUserId()).isEqualTo(2L);
        assertThat(histories.get(1).getUserId()).isEqualTo(4L);
    }

}
