package io.spring.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "pages")
@Getter
@Setter
public class Page {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    @Column(unique = true)
    private String slug;
    private String name;
    private String body;
}
