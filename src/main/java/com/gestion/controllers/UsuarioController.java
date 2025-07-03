package com.gestion.controllers;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gestion.dto.CrearUsuarioRequest;
import com.gestion.dto.UsuarioDTO;
import com.gestion.models.Usuario;
import com.gestion.services.UsuarioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService service;

    @GetMapping
    public CollectionModel<EntityModel<UsuarioDTO>> getAll() {
        List<EntityModel<UsuarioDTO>> usuarios = service.listarUsuarios().stream()
            .map(usuario -> EntityModel.of(usuario,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UsuarioController.class).getById(usuario.getIdUsuario())).withSelfRel(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UsuarioController.class).getAll()).withRel("usuarios")
            ))
            .collect(Collectors.toList());

        return CollectionModel.of(usuarios,
            WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UsuarioController.class).getAll()).withSelfRel()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        try {
            UsuarioDTO usuario = service.buscarUsuarioPorId(id);
            EntityModel<UsuarioDTO> usuarioModel = EntityModel.of(usuario,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UsuarioController.class).getById(id)).withSelfRel(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UsuarioController.class).getAll()).withRel("usuarios")
            );
            return ResponseEntity.ok(usuarioModel);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(Map.of("mensaje", ex.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<EntityModel<Usuario>> crear(@RequestBody CrearUsuarioRequest request) {
        Usuario creado = service.crearUsuario(request);
        EntityModel<Usuario> usuarioModel = EntityModel.of(creado,
            WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UsuarioController.class).getById(creado.getIdUsuario())).withSelfRel(),
            WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UsuarioController.class).getAll()).withRel("usuarios")
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioModel);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<UsuarioDTO>> editarUsuario(@PathVariable Integer id, @RequestBody UsuarioDTO usuarioDTO) {
        UsuarioDTO actualizado = service.actualizarUsuario(id, usuarioDTO);
        EntityModel<UsuarioDTO> usuarioModel = EntityModel.of(actualizado,
            WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UsuarioController.class).getById(id)).withSelfRel(),
            WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UsuarioController.class).getAll()).withRel("usuarios")
        );
        return ResponseEntity.ok(usuarioModel);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        service.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }
}
