package sk.pelikan.post.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sk.pelikan.post.domain.Post;
import sk.pelikan.post.exception.ExternalApiException;
import sk.pelikan.post.exception.PostNotFoundException;
import sk.pelikan.post.exception.UserNotFoundException;
import sk.pelikan.post.service.PostService;

import java.util.Optional;

@RestController
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody Post post) {
        try {
            Post result = postService.createPost(post);
            return new ResponseEntity<>(result, HttpStatus.CREATED);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (ExternalApiException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getPost(@PathVariable Integer id) {
        try {
            Optional<Post> result = postService.getPost(id);
            return result.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
        } catch (PostNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (ExternalApiException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping
    public ResponseEntity<Post> updatePost(@RequestBody Post post) {
        try {
            Optional<Post> result = postService.updatePost(post);
            return result.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
        } catch (PostNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (ExternalApiException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Integer id) {
        try {
            postService.deletePost(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (PostNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}