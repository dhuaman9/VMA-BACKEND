package pe.gob.sunass.vma.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pe.gob.sunass.vma.dto.RecuperarPasswordDTO;
import pe.gob.sunass.vma.dto.TokenPasswordDTO;
import pe.gob.sunass.vma.model.TokenPassword;
import pe.gob.sunass.vma.service.TokenPasswordService;
import pe.gob.sunass.vma.service.UsuarioService;

@RestController
@CrossOrigin
@RequestMapping("/tokens")
public class TokenController {
    private final TokenPasswordService tokenService;
    private final UsuarioService usuarioService;

    @Autowired
    public TokenController(TokenPasswordService tokenService, UsuarioService usuarioService) {
        this.tokenService = tokenService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/{token}")
    public TokenPasswordDTO getToken(@PathVariable String token) {
        TokenPassword tokenDB = tokenService.findTokenByToken(token);
        return new TokenPasswordDTO(tokenDB.getToken(), tokenDB.isCompletado());
    }

    @PostMapping("/recuperar-password/{token}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void recuperarPassword(@PathVariable String token, @RequestBody RecuperarPasswordDTO dto) {
        usuarioService.recuperarPassword(token, dto);
    }
}
