package br.csi.pilago.model.user;

public record RegisterRequest(String name, String email, String password, String phone) {
}
