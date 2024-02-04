package sk.pelikan.post.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import sk.pelikan.post.domain.Post;

import java.util.List;

public interface PostDao extends CrudRepository<Post, Integer> {
    /**
     * Retrieves a distinct list of user IDs from all posts.
     * This method is useful for identifying all unique users who have made posts.
     *
     * @return a List of unique user IDs as Integer
     */
    @Query("SELECT DISTINCT p.userId FROM Post p")
    List<Integer> findByUserId();
}
