package com.oopsprj.springboot.services;

import com.oopsprj.springboot.entity.Post;
import com.oopsprj.springboot.entity.User;
import com.oopsprj.springboot.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserService userService;

    public void createPost(String postBody, Long userId) {
        User user = userService.getUserById(userId);
        Post post = new Post(postBody, LocalDateTime.now(), user); // Use constructor to set body, createdAt, and user
        postRepository.save(post);
    }

    public List<Post> getAllPostsInReverseChronologicalOrder() {
        // Fetch all posts from the database sorted by creation date in descending order
        return postRepository.findAllByOrderByCreatedAtDesc();
    }

    public Post getPostById(Long id) {
        return postRepository.findById(id)
                .orElse(null); // Return null if post not found
    }

    public Post updatePost(Long id, String body) {
        Post post = getPostById(id);
        if (post != null) {
            post.setBody(body);
            return postRepository.save(post);
        }
        return null; // Return null if post not found
    }

    public boolean deletePost(Long id) {
        Post post = getPostById(id);
        if (post != null) {
            postRepository.delete(post);
            return true; // Return true if the post was successfully deleted
        }
        return false; // Return false if post not found or deletion failed
    }

}
