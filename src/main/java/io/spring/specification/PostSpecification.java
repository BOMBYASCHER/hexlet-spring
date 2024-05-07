package io.spring.specification;

import java.time.LocalDate;
import io.spring.dto.post.PostParamsDTO;
import io.spring.model.Post;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class PostSpecification {
    public Specification<Post> build(PostParamsDTO params) {
        return withAuthorId(params.getAuthorId())
                .and(withCreatedAtGt(params.getCreatedAtGt()))
                .and(withCreatedAtLt(params.getCreatedAtLt()))
                .and(withNameCont(params.getNameCont()));
    }

    private Specification<Post> withAuthorId(Long id) {
        return (root, query, cb) ->
                id == null ? cb.conjunction() : cb.equal(root.get("author").get("id"), id);
    }

    private Specification<Post> withCreatedAtGt(LocalDate date) {
        return (root, query, cb) ->
                date == null ? cb.conjunction() : cb.greaterThan(root.get("createdAt"), date);
    }

    private Specification<Post> withCreatedAtLt(LocalDate date) {
        return (root, query, cb) ->
                date == null ? cb.conjunction() : cb.lessThan(root.get("createdAt"), date);
    }

    private Specification<Post> withNameCont(String name) {
        return (root, query, cb) ->
                name == null ? cb.conjunction() : cb.like(cb.lower(root.get("name")), name.toLowerCase());
    }
}
