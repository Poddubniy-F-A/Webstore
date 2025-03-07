package com.webstore.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "users")
public class User {
    public enum Status {
        cust,
        mod,
        ww
    }

    @Id
    private String login;

    @Column
    private String password;

    @Column
    private String nick;

    @Column
    @Enumerated(EnumType.STRING)
    private Status status;
}