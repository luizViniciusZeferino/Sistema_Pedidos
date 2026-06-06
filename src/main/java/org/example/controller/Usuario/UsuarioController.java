package org.example.controller.Usuario;

import org.example.dto.Usuario.UsuarioDTO;
import org.example.model.entity.Usuario.UsuarioEntity;
import org.example.service.Usuario.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping
    public UsuarioDTO criarUsuario(@RequestBody UsuarioEntity usuarioEntity) {
        return usuarioService.criarUsuario(usuarioEntity);
    }

    @GetMapping
    public List<UsuarioDTO> listarUsuarios() {
        return usuarioService.listarUsuarios();
    }

    @GetMapping("/email/{email}")
    public Optional<UsuarioDTO> buscarPorEmail(@PathVariable String email) {
        return usuarioService.buscarPorEmail(email);
    }
}
