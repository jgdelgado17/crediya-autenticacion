package co.com.crediya.autenticacion.model.securityports;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class TokenInfo {
    private String token;
    private Date creationDate;
    private Date expirationDate;
    private long expiresIn;
}
