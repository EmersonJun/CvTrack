package br.com.cvmatch.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.proxy.HibernateProxy;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Campos comuns a todas as entidades: id (UUID) e data de criação.
 *
 * equals/hashCode seguem o padrão recomendado para JPA:
 * - duas entidades são iguais se têm o MESMO id não nulo;
 * - hashCode é constante por classe, para não mudar quando o id
 *   é gerado no persist (o que quebraria HashSet/HashMap);
 * - proxies do Hibernate (lazy loading) são comparados pela classe real.
 */
@MappedSuperclass
@Getter
public abstract class EntidadeBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false, columnDefinition = "timestamptz")
    private Instant criadoEm;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || classeReal(this) != classeReal(o)) {
            return false;
        }
        EntidadeBase outra = (EntidadeBase) o;
        return getId() != null && Objects.equals(getId(), outra.getId());
    }

    @Override
    public int hashCode() {
        return classeReal(this).hashCode();
    }

    private static Class<?> classeReal(Object objeto) {
        return objeto instanceof HibernateProxy proxy
                ? proxy.getHibernateLazyInitializer().getPersistentClass()
                : objeto.getClass();
    }
}
