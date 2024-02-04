package sk.pelikan.post.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import sk.pelikan.post.domain.Post;
import sk.pelikan.post.domain.User;
import sk.pelikan.post.exception.ExternalApiException;
import sk.pelikan.post.exception.PostNotFoundException;
import sk.pelikan.post.exception.UserNotFoundException;
import sk.pelikan.post.service.PostService;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PostControllerIT {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper jsonMapper;
    @MockBean
    private PostService postService;
    private Post post;

    @BeforeEach
    void setUp() {
        post = new Post(1, 1, "Title", "Body");
    }

    @Nested
    class createPost {
        @Test
        public void shouldCreatePostSuccessfully() throws Exception {
            Post createdPost = post;

            when(postService.createPost(post)).thenReturn(createdPost);

            mockMvc.perform(post("/posts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonMapper.writeValueAsString(post)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(createdPost.getId()))
                    .andExpect(jsonPath("$.userId").value(createdPost.getUserId()))
                    .andExpect(jsonPath("$.title").value(createdPost.getTitle()))
                    .andExpect(jsonPath("$.body").value(createdPost.getBody()));
        }

        @Test
        public void shouldReturnNotFoundForNonExistingUser() throws Exception {
            when(postService.createPost(post)).thenThrow(new UserNotFoundException("User with ID: " + post.getUserId() + " does not exist."));

            mockMvc.perform(post("/posts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonMapper.writeValueAsString(post)))
                    .andExpect(status().isNotFound());
        }

        @Test
        public void shouldHandleExternalApiException() throws Exception {
            when(postService.createPost(post)).thenThrow(new ExternalApiException("Failed to verify user with ID: " + post.getUserId()));

            mockMvc.perform(post("/posts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonMapper.writeValueAsString(post)))
                    .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    class getPost {
        @Test
        public void getPost_ReturnsPost_IfExists() throws Exception {
            Post createdPost = post;
            when(postService.getPost(1)).thenReturn(Optional.of(post));

            mockMvc.perform(get("/posts/{id}", 1)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(createdPost.getId()))
                    .andExpect(jsonPath("$.userId").value(createdPost.getUserId()))
                    .andExpect(jsonPath("$.title").value(createdPost.getTitle()))
                    .andExpect(jsonPath("$.body").value(createdPost.getBody()));
        }

        @Test
        public void getPost_ReturnsNotFound_IfNotExists() throws Exception {
            when(postService.getPost(999)).thenReturn(Optional.empty());

            mockMvc.perform(get("/posts/{id}", 999)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }

        @Test
        public void getPost_ReturnsServerError_WhenExternalApiFails() throws Exception {
            when(postService.getPost(1)).thenThrow(new ExternalApiException("External API error"));

            mockMvc.perform(get("/posts/{id}", 1)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    class updatePost {
        @Test
        public void updatePost_ReturnsUpdatedPost_IfExists() throws Exception {
            Post updatedPost = new Post(1, 1, "Updated Title", "Updated Body");
            when(postService.updatePost(post)).thenReturn(Optional.of(updatedPost));

            mockMvc.perform(put("/posts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonMapper.writeValueAsString(post)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(updatedPost.getId()))
                    .andExpect(jsonPath("$.userId").value(updatedPost.getUserId()))
                    .andExpect(jsonPath("$.title").value(updatedPost.getTitle()))
                    .andExpect(jsonPath("$.body").value(updatedPost.getBody()));
        }

        @Test
        public void updatePost_ReturnsNotFound_IfNotExists() throws Exception {
            when(postService.updatePost(post)).thenReturn(Optional.empty());

            mockMvc.perform(put("/posts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonMapper.writeValueAsString(post)))
                    .andExpect(status().isNotFound());
        }

        @Test
        public void updatePost_ReturnsServerError_WhenExternalApiFails() throws Exception {
            when(postService.updatePost(post)).thenThrow(new ExternalApiException("External API error"));

            mockMvc.perform(put("/posts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonMapper.writeValueAsString(post)))
                    .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    class deletePost {
        @Test
        public void deletePost_ReturnsNoContent_IfExists() throws Exception {
            doNothing().when(postService).deletePost(1);

            mockMvc.perform(delete("/posts/{id}", 1))
                    .andExpect(status().isNoContent());
        }

        @Test
        public void deletePost_ReturnsNotFound_IfNotExists() throws Exception {
            doThrow(new PostNotFoundException("Post not found")).when(postService).deletePost(999);

            mockMvc.perform(delete("/posts/{id}", 999))
                    .andExpect(status().isNotFound());
        }
    }
}