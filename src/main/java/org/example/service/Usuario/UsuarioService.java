package org.example.service.Usuario;

import lombok.extern.log4j.Log4j2;
import org.example.dto.Usuario.UsuarioDTO;
import org.example.model.entity.Usuario.UsuarioEntity;
import org.example.repository.Usuario.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Log4j2
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public UsuarioDTO criarUsuario(UsuarioEntity usuarioEntity) {
        usuarioEntity.setSenha(passwordEncoder.encode(usuarioEntity.getSenha()));

        UsuarioEntity usuarioCriado = usuarioRepository.save(usuarioEntity);

        return toResponseDTO(usuarioCriado);
    }

    public List<UsuarioDTO> listarUsuarios() {

        List<UsuarioEntity> listaUsuarios = usuarioRepository.findAll();

        List<UsuarioDTO> listaDTO = listaUsuarios.stream()
                .map(this::toResponseDTO)
                .toList();
        return listaDTO;
    }

    public Optional<UsuarioDTO> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .map(this::toResponseDTO);
    }

    private UsuarioDTO toResponseDTO(UsuarioEntity usuario) {
        UsuarioDTO dto = new UsuarioDTO();

        dto.setNome(usuario.getNome());
        dto.setTelefone(usuario.getTelefone());
        dto.setEmail(usuario.getEmail());


        return dto;
    }
}
