package com.thriveq.crm.auth.auth_service.util;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.*;
import com.thriveq.crm.auth.auth_service.configuration.JwtProperties;
import org.springframework.stereotype.Component;

import java.nio.file.*;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.util.*;

@Component
public class JwtIssuer {

    private final RSASSASigner signer;
    private final JwtProperties props;

    public JwtIssuer(JwtProperties props) throws Exception {
        this.props = props;
        String pem = Files.readString(Path.of(props.getPrivateKeyPath()))
                .replaceAll("-----(BEGIN|END) PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
        byte[] der = Base64.getDecoder().decode(pem);
        /** certs/jwt-private.pem is text — Base64 wrapped in -----BEGIN PRIVATE KEY-----. That's convenient for storage,
         but Java's signing code can't sign with a string. It needs a live RSAPrivateKey object. This line is the conversion: **/
        RSAPrivateKey key = (RSAPrivateKey) KeyFactory.getInstance("RSA")
                .generatePrivate(new PKCS8EncodedKeySpec(der));
        this.signer = new RSASSASigner(key);
    }

    public String issue(UUID userId, String email, List<String> roles) {
        Instant now = Instant.now();
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject(userId.toString())
                .issuer(props.getIssuer())
                .audience(props.getAudience())
                .issueTime(Date.from(now))
                .expirationTime(Date.from(now.plusSeconds(props.getTtlSeconds())))
                .jwtID(UUID.randomUUID().toString())
                .claim("email", email)
                .claim("roles", roles)          // ROLES. Not permissions.
                .build();

        try {
            SignedJWT jwt = new SignedJWT(
                    new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(props.getKid()).build(),
                    claims);
            jwt.sign(signer);
            return jwt.serialize();
        } catch (JOSEException e) {
            throw new IllegalStateException("sign failed", e);
        }
    }
}