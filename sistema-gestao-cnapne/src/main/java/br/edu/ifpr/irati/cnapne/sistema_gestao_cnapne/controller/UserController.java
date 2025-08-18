package br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.controller;

import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.DTO.user.CreateUserDTO;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.data.entity.User;
import br.edu.ifpr.irati.cnapne.sistema_gestao_cnapne.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody @Valid CreateUserDTO dto) {
        User newUser = userService.createUser(dto.email(), dto.password(), dto.role());
        return ResponseEntity.ok(newUser);
    }
}