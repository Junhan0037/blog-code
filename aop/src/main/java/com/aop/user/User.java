package com.aop.user;

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
public class User {

    public User(String email, String name) {
        this.email = email;
        this.name = name;
    }

    @Id @GeneratedValue
    private Long id;

    private String email;

    private String name;

}
