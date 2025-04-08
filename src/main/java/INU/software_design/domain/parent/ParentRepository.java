package INU.software_design.domain.parent;

import INU.software_design.domain.parent.entity.Parent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParentRepository extends JpaRepository<Parent, Long> {
    Optional<Parent> findBySocialId(String socialId);

}
