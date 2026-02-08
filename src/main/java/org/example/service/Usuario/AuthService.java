package org.example.service.Usuario;

import org.example.dto.Usuario.LoginRequestDTO;
import org.example.dto.Usuario.LoginResponseDTO;
import org.example.model.entity.Usuario.Usuario;
import org.example.repository.Usuario.UsuarioRepository;
import org.example.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AuthenticationManager authenticationManager, UsuarioRepository usuarioRepository, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.usuarioRepository = usuarioRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponseDTO autenticar(LoginRequestDTO loginRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getSenha()));

        Usuario usuario = usuarioRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        String token = jwtUtil.generateToken(usuario.getEmail());

        return new LoginResponseDTO(token);
    }
}
