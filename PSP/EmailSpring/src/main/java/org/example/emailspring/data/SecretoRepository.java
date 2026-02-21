package org.example.emailspring.data;

import org.example.emailspring.data.entity.SecretoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SecretoRepository extends JpaRepository<SecretoEntity, Long> {
}

