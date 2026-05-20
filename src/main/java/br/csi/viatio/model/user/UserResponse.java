package br.csi.viatio.model.user;

public record UserResponse(Long id, String name, String email) {
    public UserResponse(User user) {
        this(user.getId(), user.getName(), user.getEmail());
    }
}

