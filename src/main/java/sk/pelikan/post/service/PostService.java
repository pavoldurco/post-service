package sk.pelikan.post.service;

import sk.pelikan.post.domain.Post;

import java.util.Optional;

public interface PostService {
    /**
     * Creates a new post after verifying the existence of the user.
     * The user's existence is verified via an external API call.
     * Throws UserNotFoundException if the user does not exist.
     * Throws ExternalApiException if the external API call fails.
     *
     * @param post the post to be created
     * @return the saved post with an assigned ID
     */
    Post createPost(Post post);

    /**
     * Retrieves a post by its ID. Initially tries to find the post in the local database;
     * if not found, attempts to fetch it from an external API.
     * Throws PostNotFoundException if the post cannot be found locally and in the external API.
     * Throws ExternalApiException if the external API call fails.
     *
     * @param id the ID of the post to retrieve
     * @return an Optional containing the post if found, or an empty Optional if not found
     */
    Optional<Post> getPost(Integer id);

    /**
     * Updates an existing post's title and body with the provided values.
     * Throws PostNotFoundException if the post with the given ID does not exist.
     *
     * @param post the post to update, containing the new title and body
     * @return an Optional containing the updated post
     */
    Optional<Post> updatePost(Post post);

    /**
     * Deletes a post by its ID. Throws PostNotFoundException if the post with the given ID does not exist.
     *
     * @param id the ID of the post to delete
     */
    void deletePost(Integer id);
}