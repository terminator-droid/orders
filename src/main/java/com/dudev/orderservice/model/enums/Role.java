package com.dudev.orderservice.model.enums;

import org.jspecify.annotations.NonNull;
import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ADMIN, USER;

    @Override
    public @NonNull String getAuthority() {
        return name();
    }
}
