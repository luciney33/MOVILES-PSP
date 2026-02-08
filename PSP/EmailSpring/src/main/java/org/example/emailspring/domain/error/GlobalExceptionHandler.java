package org.example.emailspring.domain.error;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.example.emailspring.common.Constantes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.crypto.AEADBadTagException;
import javax.crypto.BadPaddingException;
import java.security.GeneralSecurityException;
import java.security.SignatureException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleNotFound(EntityNotFoundException ex) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<String> handleUnauthorized(UnauthorizedException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<String> handleForbidden(ForbiddenException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }

    @ExceptionHandler(BadRequestException.class)
    public Object handleBadRequest(BadRequestException ex, HttpServletRequest request) {
        if (request.getRequestURI().contains(Constantes.ACTIVAR)) {
            ModelAndView modelAndView = new ModelAndView(Constantes.TEMPLATE_ERROR);
            modelAndView.addObject(Constantes.MENSAJE_ERROR, ex.getMessage());
            return modelAndView;
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
    @ExceptionHandler({AEADBadTagException.class, BadPaddingException.class})
    public ResponseEntity<String> handleCryptographyError(Exception ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Constantes.CONTRASEÑA_DE_CIFRADO_INCORRECTA_O_CLAVE_CORRUPTA);
    }

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<String> handleSignatureError(SignatureException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Constantes.LA_FIRMA_DIGITAL_NO_ES_VALIDA_EL_MENSAJE_HA_SIDO_MANIPULADO);
    }

    @ExceptionHandler(GeneralSecurityException.class)
    public ResponseEntity<String> handleGeneralSecurity(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Constantes.ERROR_PROCESANDO_LAS_CLAVES_DE_SEGURIDAD);
    }
}
