package io.spring.util;

import io.spring.model.Page;
import io.spring.model.Post;
import io.spring.model.User;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.Select;
import org.springframework.stereotype.Component;

@Getter
@Component
public class ModelGenerator {
    private final Faker faker = new Faker();

    private Model<User> user;
    private Model<Page> page;
    private Model<Post> post;

    @PostConstruct
    private void init() {
        user = Instancio.of(User.class)
                .ignore(Select.field(User::getId))
                .ignore(Select.field(User::getPosts))
                .ignore(Select.field(User::getCreatedAt))
                .ignore(Select.field(User::getUpdatedAt))
                .supply(Select.field(User::getFirstName), () -> faker.name().firstName())
                .supply(Select.field(User::getLastName), () -> faker.name().lastName())
                .supply(Select.field(User::getEmail), () -> faker.internet().emailAddress())
                .supply(Select.field(User::getUsername), () -> faker.name().username())
                .supply(Select.field(User::getPassword), () -> faker.internet().password())
                .toModel();
        page = Instancio.of(Page.class)
                .ignore(Select.field(Page::getId))
                .ignore(Select.field(Page::getCreatedAt))
                .ignore(Select.field(Page::getUpdatedAt))
                .supply(Select.field(Page::getSlug), () -> faker.internet().slug())
                .supply(Select.field(Page::getName), () -> faker.name().title())
                .supply(Select.field(Page::getBody), () -> faker.text().text(10, 1000))
                .toModel();
        post = Instancio.of(Post.class)
                .ignore(Select.field(Post::getId))
                .ignore(Select.field(Post::getAuthor))
                .supply(Select.field(Post::getSlug), () -> faker.internet().slug())
                .supply(Select.field(Post::getName), () -> faker.lorem().word())
                .supply(Select.field(Post::getBody), () -> faker.lorem().paragraph())
                .toModel();
    }
}
