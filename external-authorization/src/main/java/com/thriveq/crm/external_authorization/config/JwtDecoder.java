package com.thriveq.crm.external_authorization.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;

import java.nio.file.Files;
import java.nio.file.Path;

import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Configuration
public class JwtDecoder {

    @Bean
    public ReactiveJwtDecoder reactiveJwtDecoder(
            @Value("${jwt.public-key-path}") String path,
            @Value("${jwt.issuer}") String issuer,
            @Value("${jwt.audience}") String audience
    ) throws Exception {
        String pem = Files.readString(Path.of(path))
                .replaceAll("-----(BEGIN|END) PUBLIC KEY-----", "")
                .replaceAll("\\s", "");
        byte[] der = Base64.getDecoder().decode(pem);
        /** certs/jwt-public.pem is text — Base64 wrapped in -----BEGIN PUBLIC KEY-----. That's convenient for storage,
         but Java's decoding code can't decode with a string. It needs a live RSAPublicKey object. This line is the conversion: **/
        RSAPublicKey key = (RSAPublicKey) KeyFactory.getInstance("RSA")
                .generatePublic(new X509EncodedKeySpec(der));

        NimbusReactiveJwtDecoder decoder = NimbusReactiveJwtDecoder.withPublicKey(key).build();

        OAuth2TokenValidator<Jwt> withIssue = JwtValidators.createDefaultWithIssuer(issuer);
        /**
         * Identical — the lambda is just the compact form. The jwt in jwt -> ... is the parameter name for that method. You're saying "when someone calls this with a JWT, call it jwt, and here's what to do with it.
         * Who calls it, and when. Your @Bean method runs once, at startup — it builds the decoder and hands it the validator. Nothing is validated yet. Then on every request:
         *
         * decoder.decode(token) is called from your controller,
         * The decoder parses the JWT and verifies the signature with the public key
         * It produces a Jwt object
         * It passes that object into your validator: audience.validate(thatJwt); this is called internally, we never define that.
         * Your lambda body finally runs, with jwt bound to the real token
         */
        OAuth2TokenValidator<Jwt> withAudience = jwt ->
                jwt.getAudience() != null
                        && jwt.getAudience().contains(audience) ?
                        OAuth2TokenValidatorResult.success() : OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token", "required audience missing", null));

        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(withIssue, withAudience));
        return decoder;
    }
}
