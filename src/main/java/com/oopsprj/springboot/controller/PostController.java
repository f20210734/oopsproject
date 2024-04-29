package com.oopsprj.springboot.controller;

import com.oopsprj.springboot.entity.Post;
import com.oopsprj.springboot.ErrorResponse;
import com.oopsprj.springboot.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/post")
public class PostController {
    @Autowired
    private PostService postService;

    @PostMapping
    public ResponseEntity<Object> createPost(@RequestBody PostRequest postRequest) {
        try {
            postService.createPost(postRequest.getPostBody(), postRequest.getUserID());
            return ResponseEntity.ok("Post created successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("User does not exist"));
        }
    }

    @GetMapping
    public ResponseEntity<Object> getPostById(@RequestParam("postID") Long postID) {
        try {
            Post post = postService.getPostById(postID);
            if (post != null) {
                List<CommentResponse> commentResponses = post.getComments().stream()
                        .map(comment -> new CommentResponse(comment.getId(), comment.getBody(), new UserResponse(comment.getUser().getId(), comment.getUser().getName())))
                        .collect(Collectors.toList());
                PostResponse postResponse = new PostResponse(post.getId(), post.getBody(), post.getCreatedAt(), commentResponses);
                return ResponseEntity.ok(postResponse);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Post does not exist"));
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Post does not exist"));
        }
    }

    @PatchMapping
    public ResponseEntity<Object> updatePost(@RequestBody PostUpdateRequest postUpdateRequest) {
        try {
            Post updatedPost = postService.updatePost(postUpdateRequest.getPostID(), postUpdateRequest.getPostBody());
            if (updatedPost != null) {
                return ResponseEntity.ok("Post edited successfully");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Post does not exist"));
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Post does not exist"));
        }
    }


    @DeleteMapping
    public ResponseEntity<Object> deletePost(@RequestParam("postID") Long postID) {
        try {
            boolean deleted = postService.deletePost(postID);
            if (deleted) {
                return ResponseEntity.ok("Post deleted");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Post does not exist"));
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Error deleting post"));
        }
    }
    // Inner class to handle request body for creating a post
    public static class PostRequest {
        private String postBody;
        private Long userID;

        public String getPostBody() {
            return postBody;
        }

        public void setPostBody(String postBody) {
            this.postBody = postBody;
        }

        public Long getUserID() {
            return userID;
        }

        public void setUserID(Long userID) {
            this.userID = userID;
        }
    }

    // Inner class to handle request body for updating a post
    public static class PostUpdateRequest {
        private String postBody;
        private Long postID;

        public String getPostBody() {
            return postBody;
        }

        public void setPostBody(String postBody) {
            this.postBody = postBody;
        }

        public Long getPostID() {
            return postID;
        }

        public void setPostID(Long postID) {
            this.postID = postID;
        }
    }

    // Inner class for simplified post response
    public static class PostResponse {
        private Long postID;
        private String postBody;
        private LocalDate date; // Change type to LocalDate
        private List<CommentResponse> comments;

        public PostResponse(Long postID, String postBody, LocalDateTime date, List<CommentResponse> comments) {
            this.postID = postID;
            this.postBody = postBody;
            this.date = date.toLocalDate(); // Convert LocalDateTime to LocalDate
            this.comments = comments;
        }
        public Long getPostID() {
            return postID;
        }

        public void setPostID(Long postID) {
            this.postID = postID;
        }

        public String getPostBody() {
            return postBody;
        }

        public void setPostBody(String postBody) {
            this.postBody = postBody;
        }

        public LocalDate getDate() {
            return date;
        }

        public void setDate(LocalDate date) {
            this.date = date;
        }

        public List<CommentResponse> getComments() {
            return comments;
        }

        public void setComments(List<CommentResponse> comments) {
            this.comments = comments;
        }
    }

    // Inner class for simplified comment response
    public static class CommentResponse {
        private Long commentID;
        private String commentBody;
        private UserResponse commentCreator;

        public CommentResponse(Long commentID, String commentBody, UserResponse commentCreator) {
            this.commentID = commentID;
            this.commentBody = commentBody;
            this.commentCreator = commentCreator;
        }

        public Long getCommentID() {
            return commentID;
        }

        public void setCommentID(Long commentID) {
            this.commentID = commentID;
        }

        public String getCommentBody() {
            return commentBody;
        }

        public void setCommentBody(String commentBody) {
            this.commentBody = commentBody;
        }

        public UserResponse getCommentCreator() {
            return commentCreator;
        }

        public void setCommentCreator(UserResponse commentCreator) {
            this.commentCreator = commentCreator;
        }
    }

    // Inner class for simplified user response
    public static class UserResponse {
        private Long userID;
        private String name;

        public UserResponse(Long userID, String name) {
            this.userID = userID;
            this.name = name;
        }

        public Long getUserID() {
            return userID;
        }

        public void setUserID(Long userID) {
            this.userID = userID;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
