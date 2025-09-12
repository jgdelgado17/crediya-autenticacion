package co.com.crediya.autenticacion.r2dbc.modules.user.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class UserEntity {
    @Id
    @Column("id_user")
    private Integer idUser;
    private String names;
    @Column("last_name")
    private String lastName;
    private String email;
    @Column("document_number")
    private String documentNumber;
    @Column("phone_number")
    private String phoneNumber;
    @Column("base_salary")
    private Float baseSalary;
    @Column("id_role")
    private Integer idRole;
    private String passcode;
}
