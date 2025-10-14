package com.gustavo.finansync.service;

import com.gustavo.finansync.dto.AuthDTOs;
import com.gustavo.finansync.entity.User;
import com.gustavo.finansync.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registra um novo usuário no sistema.
     * @param request DTO com os dados do usuário.
     */
    public User register(AuthDTOs.UserRegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalStateException("O e-mail informado já está em uso.");
        }

        User newUser = new User(
                request.name(),
                request.email(),
                passwordEncoder.encode(request.password())
        );
        return userRepository.save(newUser);
    }

    /**
     * Autentica um usuário com base no e-mail e senha.
     * @param email O e-mail do usuário.
     * @param password A senha (não criptografada).
     * @return A entidade User se a autenticação for bem-sucedida.
     */
    public User authenticate(String email, String password) {
        User usuario = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado para o e-mail: " + email));

        if (!passwordEncoder.matches(password, usuario.getPasswordHash())) {
            throw new IllegalArgumentException("Senha inválida.");
        }
        return usuario;
    }

    /**
     * NOVO MÉTODO: Busca um usuário pelo e-mail.
     * Será usado pelos controllers para obter o usuário logado.
     */
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Usuário autenticado não encontrado no banco de dados."));
    }
}
