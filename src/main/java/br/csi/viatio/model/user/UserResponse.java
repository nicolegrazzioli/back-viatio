package br.csi.viatio.model.user;

import java.util.UUID;

public record UserResponse(UUID id, String name, String email) {
    public UserResponse(User user) {
        this(user.getId(), user.getName(), user.getEmail());
    }
}

