package co.com.crediya.autenticacion.r2dbc.modules.role.data;

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
@Table(name = "roles")
public class RoleEntity {
    @Id
    @Column("unique_id")
    private Integer id;
    private String name;
    private String description;
}
