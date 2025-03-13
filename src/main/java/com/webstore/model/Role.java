package com.webstore.model;

import lombok.Getter;

@Getter
public enum Role {
    CUST("CUSTOMER"),
    MOD("MODERATOR"),
    WW("WH_WORKER");

    private final String roleName;

    Role(String roleName) {
        this.roleName = roleName;
    }

    @Override
    public String toString() {
        return roleName;
    }
}
