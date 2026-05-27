package br.csi.viatio.dto.user;

import br.csi.viatio.model.User;

import java.util.UUID;

public record UserResponse(UUID id, String name, String email) {
    public UserResponse(User user) {
        this(user.getId(), user.getName(), user.getEmail());
    }
}

