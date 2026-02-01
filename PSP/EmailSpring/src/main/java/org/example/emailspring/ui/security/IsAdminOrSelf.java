package org.example.emailspring.ui.security;

import org.example.emailspring.common.Constantes;
import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize(Constantes.SECURITY_ROLE_ADMIN_OR_SELF)
public @interface IsAdminOrSelf {
}

