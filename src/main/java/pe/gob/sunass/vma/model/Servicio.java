package pe.gob.sunass.vma.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(exclude={"roles"})
@Entity
@Table(schema="vma", name="auth_servicio")
public class Servicio implements Serializable {
  private static final long serialVersionUID = 2065925241286815070L;

  @Id
  @Column(name="id", nullable=false)
  private Integer id;

  @Column(name="method", length=10, nullable=false)
  private String metodo;

//  @Column(name="orden", nullable=false)
//  private Integer orden;

  @Column(name="url", length=100, nullable=false)
  private String url;

//  @ManyToMany(fetch=FetchType.EAGER, cascade=CascadeType.PERSIST)
//  @JoinTable(schema="vma", name="auth_servicio_role",
//             joinColumns={@JoinColumn(name="servicio_id", referencedColumnName="id", nullable=false, updatable=false)},
//             inverseJoinColumns={@JoinColumn(name="role_id", referencedColumnName="id", nullable=false, updatable=false)}
//            )
//  @Fetch(FetchMode.JOIN)
//  private Set<Role> roles = new HashSet<Role>(0);

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("{" + Servicio.class.getSimpleName() + ":");
    sb.append("id").append("=").append(this.id).append(", ");
    sb.append("metodo").append("=").append(this.metodo).append(", ");
//    sb.append("orden").append("=").append(this.orden).append(", ");
    sb.append("url").append("=").append(this.url);
    sb.append("}");

    return sb.toString();
  }
}
