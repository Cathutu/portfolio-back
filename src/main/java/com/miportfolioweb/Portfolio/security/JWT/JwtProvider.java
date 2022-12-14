package com.miportfolioweb.Portfolio.security.JWT;

import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.miportfolioweb.Portfolio.security.UsuarioPrincipal;

import java.util.Date;

// Crea el token, se valida y se extrae el nombre del usuario
@Component
public class JwtProvider {

    private static  final Logger logger = LoggerFactory.getLogger(JwtEntryPoint.class);

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private int expiration;

    public String generateToken(Authentication authentication) {
        UsuarioPrincipal usuarioPrincipal = (UsuarioPrincipal) authentication.getPrincipal();
        return Jwts.builder().setSubject(usuarioPrincipal.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + expiration * 1000))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public String getNombreUsuarioFromToken(String token){
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Token mal formado " +e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("Token no soportado " +e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("Token expirado " +e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("Token vacío " +e.getMessage());
        } catch (SignatureException e) {
            logger.error("Error en la firma " +e.getMessage());
        }
        return false;
    }
}
