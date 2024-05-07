package io.spring.repository;

import io.spring.model.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface PageRepository extends JpaRepository<Page, Long>, JpaSpecificationExecutor<Page> {
    Optional<Page> findBySlug(String slug);
}
