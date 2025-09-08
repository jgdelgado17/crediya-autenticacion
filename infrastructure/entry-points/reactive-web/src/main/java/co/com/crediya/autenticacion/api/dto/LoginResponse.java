package co.com.crediya.autenticacion.api.dto;

import co.com.crediya.autenticacion.model.securityports.TokenInfo;
import co.com.crediya.autenticacion.model.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private String token;
    private String email;
    private String name;
    private Date creationDate;
    private Date expirationDate;
    private long expiresInSeconds;

    public LoginResponse(User user, TokenInfo tokenInfo) {
        this.token = tokenInfo.getToken();
        this.email = user.getEmail();
        this.name = user.getName();
        this.creationDate = tokenInfo.getCreationDate();
        this.expirationDate = tokenInfo.getExpirationDate();
        this.expiresInSeconds = tokenInfo.getExpiresIn();
    }
}
