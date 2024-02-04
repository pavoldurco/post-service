package sk.pelikan.post.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import sk.pelikan.post.dao.PostDao;
import sk.pelikan.post.domain.Post;
import sk.pelikan.post.domain.User;
import sk.pelikan.post.exception.ExternalApiException;
import sk.pelikan.post.exception.PostNotFoundException;
import sk.pelikan.post.exception.UserNotFoundException;
import sk.pelikan.post.service.PostService;

import java.util.Optional;

@Service
public class PostServiceImpl implements PostService {
    private final RestTemplate restTemplate;
    private final PostDao postDao;

    public PostServiceImpl(RestTemplate restTemplate, PostDao postDao) {
        this.restTemplate = restTemplate;
        this.postDao = postDao;
    }

    @Override
    public Post createPost(Post post) {
        String uri = "https://jsonplaceholder.typicode.com/users/" + post.getUserId();
        User user;

        try {
            user = restTemplate.getForObject(uri, User.class);
        } catch (Exception e) {
            throw new ExternalApiException("Failed to verify user with ID: " + post.getUserId(), e);
        }

        if (user != null) {
            return postDao.save(post);
        } else {
            throw new UserNotFoundException("User with ID: " + post.getUserId() + " does not exist.");
        }
    }

    @Override
    public Optional<Post> getPost(Integer id) {
        Optional<Post> optionalPost = postDao.findById(id);
        if (optionalPost.isPresent()) {
            return optionalPost;
        } else {
            String uri = "https://jsonplaceholder.typicode.com/posts/" + id;
            Post post;
            try {
                post = restTemplate.getForObject(uri, Post.class);
            } catch (Exception e) {
                throw new ExternalApiException("Failed to fetch post from external API with ID: " + id, e);
            }
            if (post != null) {
                Post savedPost = postDao.save(post);
                return Optional.of(savedPost);
            } else {
                throw new PostNotFoundException("Post with ID: " + id + " does not exist.");
            }
        }
    }

    @Override
    public Optional<Post> updatePost(Post updatedPost) {
        Integer id = updatedPost.getId();

        Optional<Post> existingPostOpt = getPost(id);

        if (existingPostOpt.isPresent()) {
            Post existingPost = existingPostOpt.get();
            existingPost.setTitle(updatedPost.getTitle());
            existingPost.setBody(updatedPost.getBody());
            Post savedPost = postDao.save(existingPost);
            return Optional.of(savedPost);
        } else {
            throw new PostNotFoundException("Post with ID: " + id + " does not exist.");
        }
    }

    @Override
    public void deletePost(Integer id) {
        Optional<Post> postOptional = postDao.findById(id);
        if (postOptional.isPresent()) {
            Post post = postOptional.get();
            postDao.deleteById(post.getId());
        } else {
            throw new PostNotFoundException("Post with ID: " + id + " does not exist.");
        }
    }
}