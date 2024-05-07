package io.spring.specification;

import io.spring.dto.page.PageParamsDTO;
import io.spring.model.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class PageSpecification {
    public Specification<Page> build(PageParamsDTO params) {
        return withNameCont(params.getNameCont())
                .and(withCreatedAtGt(params.getCreatedAtGt()))
                .and(withCreatedAtLt(params.getCreatedAtLt()));
    }

    public Specification<Page> withNameCont(String name) {
        return (root, query, cb) ->
                name == null ? cb.conjunction() : cb.like(cb.lower(root.get("name")), name.toLowerCase());
    }

    private Specification<Page> withCreatedAtGt(LocalDate date) {
        return (root, query, cb) ->
                date == null ? cb.conjunction() : cb.greaterThan(root.get("createdAt"), date);
    }

    private Specification<Page> withCreatedAtLt(LocalDate date) {
        return (root, query, cb) ->
                date == null ? cb.conjunction() : cb.lessThan(root.get("createdAt"), date);
    }
}
