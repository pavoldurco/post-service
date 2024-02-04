package sk.pelikan.post.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import sk.pelikan.post.dao.PostDao;
import sk.pelikan.post.domain.Post;
import sk.pelikan.post.domain.User;
import sk.pelikan.post.exception.ExternalApiException;
import sk.pelikan.post.exception.PostNotFoundException;
import sk.pelikan.post.exception.UserNotFoundException;
import sk.pelikan.post.service.impl.PostServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {
    @Mock
    private PostDao postDao;
    @Mock
    private RestTemplate restTemplate;
    @InjectMocks
    private PostServiceImpl postService;
    private Post post;
    private User user;

    @BeforeEach
    void setUp() {
        post = new Post(1, 1, "Title", "Body");
        user = new User(1, "User Name");
    }

    @Nested
    class createPost {
        @Test
        void shouldSavePostWhenUserExists() {
            Post expectedPost = new Post(1, 1, "Title", "Body");

            when(restTemplate.getForObject("https://jsonplaceholder.typicode.com/users/1", User.class)).thenReturn(user);
            when(postDao.save(post)).thenReturn(expectedPost);

            Post savedPost = postService.createPost(post);

            assertEquals(post.getId(), savedPost.getId());
            assertEquals(post.getUserId(), savedPost.getUserId());
            assertEquals(post.getTitle(), savedPost.getTitle());
            assertEquals(post.getBody(), savedPost.getBody());
        }

        @Test
        void whenUserNotFound_thenThrowUserNotFoundException() {
            int invalidUserId = 66;

            when(restTemplate.getForObject("https://jsonplaceholder.typicode.com/users/66", User.class)).thenReturn(null);

            assertThrows(UserNotFoundException.class, () -> postService.createPost(new Post(1, invalidUserId, "Title", "Body")));
        }

        @Test
        void whenExternalApiCommunicationFails_thenThrowExternalApiException() {
            when(restTemplate.getForObject("https://jsonplaceholder.typicode.com/users/1", User.class)).thenThrow(new RestClientException("Communication error"));
            assertThrows(ExternalApiException.class, () -> postService.createPost(new Post(1, 1, "Title", "Body")));
        }
    }

    @Nested
    class getPost {
        @Test
        void whenPostExistsInDatabase_thenItShouldBeReturned() {
            Integer postId = 1;
            Post expectedPost = new Post(postId, 1, "Title", "Body");

            when(postDao.findById(postId)).thenReturn(Optional.of(expectedPost));

            Optional<Post> result = postService.getPost(postId);

            assertTrue(result.isPresent());
            assertEquals(expectedPost, result.get());
        }

        @Test
        void whenPostNotInDatabaseButFoundViaExternalApi_thenShouldSaveAndReturnPost() {
            Integer postId = 32;
            Post expectedPost = new Post(postId, 1, "Title", "Body");

            when(postDao.findById(postId)).thenReturn(Optional.empty());
            when(restTemplate.getForObject("https://jsonplaceholder.typicode.com/posts/32", Post.class)).thenReturn(expectedPost);
            when(postDao.save(expectedPost)).thenReturn(expectedPost);

            Optional<Post> result = postService.getPost(postId);

            assertTrue(result.isPresent());
            assertEquals(expectedPost, result.get());
        }

        @Test
        void whenExternalApiCallFails_thenThrowExternalApiException() {
            Integer postId = -1;

            when(restTemplate.getForObject("https://jsonplaceholder.typicode.com/posts/32", Post.class)).thenThrow(new RestClientException("Communication error"));
            when(postDao.findById(postId)).thenReturn(Optional.empty());

            assertThrows(ExternalApiException.class, () -> postService.getPost(postId));
        }
        @Test
        void whenNotPostFound_thenThrowPostNotFoundException() {
            Integer postId = 40;

            when(restTemplate.getForObject("https://jsonplaceholder.typicode.com/posts/40", Post.class)).thenReturn(null);
            when(postDao.findById(postId)).thenReturn(Optional.empty());

            assertThrows(PostNotFoundException.class, () -> postService.getPost(postId));
        }

    }

    @Nested
    class updatePost {
        @Test
        public void shouldUpdatePostSuccessfully() {
            Post updatedPost = new Post(1, 1, "New Title", "New Body");

            when(postDao.findById(1)).thenReturn(Optional.of(post));
            when(postDao.save(post)).thenReturn(updatedPost);

            Optional<Post> result = postService.updatePost(updatedPost);

            assertEquals("New Title", result.get().getTitle());
            assertEquals("New Body", result.get().getBody());
        }

        @Test
        public void shouldThrowExceptionWhenPostNotFound() {
            when(postDao.findById(1)).thenThrow(new PostNotFoundException("Post with ID: 1 does not exist."));

            assertThrows(PostNotFoundException.class, () -> postService.updatePost(new Post(1, 1, "Title", "Body")));
        }
    }

    @Nested
    class deletePost {
        @Test
        public void shouldDeletePostSuccessfully() {
            Integer postId = 1;

            when(postDao.findById(postId)).thenReturn(Optional.of(post));

            postService.deletePost(postId);

            verify(postDao, times(1)).deleteById(postId);
        }

        @Test
        public void shouldThrowPostNotFoundExceptionWhenPostDoesNotExist() {
            Integer postId = 1;

            when(postDao.findById(postId)).thenReturn(Optional.empty());

            assertThrows(PostNotFoundException.class, () -> {
                postService.deletePost(postId);
            });

            verify(postDao, never()).deleteById(1);
        }
    }
}