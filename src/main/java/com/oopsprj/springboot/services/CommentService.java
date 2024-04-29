package com.oopsprj.springboot.services;

import com.oopsprj.springboot.entity.Comment;
import com.oopsprj.springboot.entity.Post;
import com.oopsprj.springboot.entity.User;
import com.oopsprj.springboot.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    public Comment createComment(Long postId, Long userId, String body) {
        Post post = postService.getPostById(postId);
        if (post == null) {
            throw new RuntimeException("Post does not exist");
        }

        User user = userService.getUserById(userId);
        if (user == null) {
            throw new RuntimeException("User does not exist");
        }

        Comment comment = new Comment();
        comment.setBody(body);
        comment.setPost(post);
        comment.setUser(user);
        return commentRepository.save(comment);
    }

    public Comment getCommentById(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment does not exist"));
    }

    public Comment updateComment(Long id, String body) {
        Comment comment = getCommentById(id);
        comment.setBody(body);
        return commentRepository.save(comment);
    }

    public void deleteComment(Long id) {
        Comment comment = getCommentById(id);
        commentRepository.delete(comment);
    }
}
