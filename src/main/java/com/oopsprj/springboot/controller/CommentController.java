package com.oopsprj.springboot.controller;

import com.oopsprj.springboot.entity.Comment;
import com.oopsprj.springboot.services.CommentService;
import com.oopsprj.springboot.ErrorResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comment")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @PostMapping
    public ResponseEntity<Object> createComment(@RequestBody CommentRequest commentRequest) {
        try {
            commentService.createComment(commentRequest.getPostID(), commentRequest.getUserID(), commentRequest.getCommentBody());
            return ResponseEntity.ok("Comment created successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<Object> getCommentById(@RequestParam("commentID") Long id) {
        try {
            Comment comment = commentService.getCommentById(id);
            if (comment != null) {
                CommentResponse commentResponse = new CommentResponse(comment.getId(), comment.getBody(), new UserResponse(comment.getUser().getId(), comment.getUser().getName()));
                return ResponseEntity.ok(commentResponse);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Comment does not exist"));
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        }
    }

    @PatchMapping
    public ResponseEntity<Object> updateComment(@RequestBody CommentUpdateRequest commentUpdateRequest) {
        try {
            Long commentID = commentUpdateRequest.getCommentID(); // Assuming you have added this method in CommentUpdateRequest class
            if (commentID == null) {
                throw new RuntimeException("CommentID is required for update");
            }

            commentService.updateComment(commentID, commentUpdateRequest.getCommentBody());
            return ResponseEntity.ok("Comment edited successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        }
    }
    @DeleteMapping
    public ResponseEntity<Object> deleteComment(@RequestParam("commentID") Long id) {
        try {
            commentService.deleteComment(id);
            return ResponseEntity.ok("Comment deleted");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        }
    }
    // Inner class to handle request body for creating a comment
    public static class CommentRequest {
        private String commentBody;
        private Long postID;
        private Long userID;

        public String getCommentBody() {
            return commentBody;
        }

        public void setCommentBody(String commentBody) {
            this.commentBody = commentBody;
        }

        public Long getPostID() {
            return postID;
        }

        public void setPostID(Long postID) {
            this.postID = postID;
        }

        public Long getUserID() {
            return userID;
        }

        public void setUserID(Long userID) {
            this.userID = userID;
        }
    }

    // Inner class to handle request body for updating a comment
    public static class CommentUpdateRequest {
        private Long commentID;
        private String commentBody;

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
