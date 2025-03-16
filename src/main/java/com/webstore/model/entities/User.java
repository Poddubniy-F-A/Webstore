package com.webstore.model.entities;

import com.webstore.model.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "users")
public class User {

    @Id
    private String login;

    @Column
    private String password;

    @Column
    private String nick;

    @Column
    @Enumerated(EnumType.STRING)
    private Role role;
}
