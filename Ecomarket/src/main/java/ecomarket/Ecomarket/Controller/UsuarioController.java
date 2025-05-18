package ecomarket.Ecomarket.Controller;

import ecomarket.Ecomarket.Model.Usuario;
import ecomarket.Ecomarket.Repositorio.UsuarioRepository;
import ecomarket.Ecomarket.DTO.LoginRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
    @Autowired
    private UsuarioRepository usuarioRepository;
    @PostMapping
    public ResponseEntity<Usuario> crearUsuario(@Valid @RequestBody Usuario request) {
        
        if (usuarioRepository.existsByCorreoElectronico(request.getCorreoElectronico())) {
            return ResponseEntity.badRequest().build(); 
        }

        
        Usuario usuarioGuardado = usuarioRepository.save(request);
        return ResponseEntity.ok(usuarioGuardado); 
    }
    @GetMapping
    public ResponseEntity<Iterable<Usuario>> listarUsuarios() {
        return ResponseEntity.ok(usuarioRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarUsuario(@PathVariable Integer id) {
        return usuarioRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUsuario(@RequestBody LoginRequest loginRequest) {
        Usuario usuario = usuarioRepository.findByCorreoElectronico(loginRequest.getCorreoElectronico());
        if (usuario != null && usuario.getContraseña().equals(loginRequest.getContrasena())) {
            return ResponseEntity.ok("Inicio de sesión exitoso");
        } else {
            System.out.println(usuario.getContraseña());
            System.out.println(loginRequest.getContrasena());
            System.out.println(loginRequest.getCorreoElectronico());
            return ResponseEntity.status(401).body("Credenciales inválidas");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> actualizarUsuario(@PathVariable Integer id, @Valid @RequestBody Usuario request) {
        return usuarioRepository.findById(id)
                .map(usuario -> {
                    usuario.setNombre(request.getNombre());
                    usuario.setCorreoElectronico(request.getCorreoElectronico());
                    usuario.setContraseña(request.getContraseña());
                    Usuario usuarioActualizado = usuarioRepository.save(usuario);
                    return ResponseEntity.ok(usuarioActualizado);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Integer id) {
        return usuarioRepository.findById(id)
                .map(usuario -> {
                    usuarioRepository.deleteById(id);
                    return ResponseEntity.noContent().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
