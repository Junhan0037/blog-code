package com.aop.aspect;

import com.aop.history.History;
import com.aop.history.HistoryRepository;
import com.aop.user.User;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class UserHistory {

    @Autowired HistoryRepository historyRepository;

    @Pointcut("execution(* com.aop.user.UserService.update(*)) && args(user)")
    public void updateUser(User user){}

    @AfterReturning("updateUser(user)")
    public void saveHistory(User user){
        historyRepository.save(new History(user.getId()));
    }

}
