package com.aop.board;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class Board {

    public Board(String title, String content) {
        this.title = title;
        this.content = content;
    }

    @Id @GeneratedValue
    private Long id;

    private String title;

    private String content;

}
