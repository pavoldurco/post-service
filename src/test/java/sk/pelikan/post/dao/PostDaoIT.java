package sk.pelikan.post.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import sk.pelikan.post.domain.Post;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class PostDaoIT {
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private PostDao postDao;
    private Post post1, post2, post3;

    @BeforeEach
    void setUp() {
        post1 = new Post();
        post1.setUserId(1);
        post1.setTitle("Title1");
        post1.setBody("Body1");

        post2 = new Post();
        post2.setUserId(2);
        post2.setTitle("Title2");
        post2.setBody("Body2");

        post3 = new Post();
        post3.setUserId(3);
        post3.setTitle("Title3");
        post3.setBody("Body3");
    }

    @Test
    public void findByUserId_ShouldReturnDistinctUserIds() {
        entityManager.persist(post1);
        entityManager.persist(post2);
        entityManager.persist(post3);
        entityManager.flush();

        List<Integer> userIds = postDao.findByUserId();
        assertThat(userIds).hasSize(3);
        assertThat(userIds).containsExactlyInAnyOrder(1, 2, 3);
    }
}