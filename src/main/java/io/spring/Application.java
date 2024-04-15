package io.spring;

import io.spring.model.Page;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootApplication
@RestController
public class Application {
    private List<Page> pages = new ArrayList<>();
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @GetMapping("/")
    String home() {
        return "Hello World!!!!";
    }

    @GetMapping("/pages")
    List<Page> index(@RequestParam(defaultValue = "10") Integer limit) {
        return pages.stream().limit(limit).toList();
    }

    @PostMapping("/pages")
    Page create(@RequestBody Page page) {
        pages.add(page);
        return page;
    }

    @GetMapping("/pages/{id}")
    Optional<Page> show(@PathVariable String id) {
        return pages.stream()
                .filter(page -> page.getSlug().equals(id))
                .findFirst();
    }

    @PutMapping("/pages/{id}")
    Page update(@PathVariable String id, @RequestBody Page data) {
        var optionalPage = pages.stream()
                .filter(p -> p.getSlug().equals(id))
                .findFirst();
        if (optionalPage.isPresent()) {
            var page = optionalPage.get();
            page.setName(data.getName());
            page.setSlug(data.getSlug());
            page.setBody(data.getBody());
        }
        return data;
    }

    @DeleteMapping("/pages/{id}")
    void delete(@PathVariable String id) {
        pages.removeIf(page -> page.getSlug().equals(id));
    }
}