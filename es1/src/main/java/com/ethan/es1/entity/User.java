package com.ethan.es1.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class User {

    private Integer id;
    private String name;
    private String age;

    public User(String name, String age){
        this.name = name;
        this.age = age;
    }
}
