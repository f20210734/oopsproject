package com.oopsprj.springboot.controller;

import com.oopsprj.springboot.entity.Comment;
import com.oopsprj.springboot.ErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.oopsprj.springboot.entity.Post;
import com.oopsprj.springboot.entity.User;
import com.oopsprj.springboot.services.PostService;
import com.oopsprj.springboot.services.UserService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

@RestController
@RequestMapping("/")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @PostMapping("/signup")
    public ResponseEntity<Object> signup(@RequestBody User user) {
        try {
            userService.createUser(user);
            return ResponseEntity.ok("Account Creation Successful");
        } catch (RuntimeException e) {
            ErrorResponse errorResponse = new ErrorResponse("Forbidden, Account already exists");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody User user) {
        try {
            User authenticatedUser = userService.authenticate(user.getEmail(), user.getPassword());
            if (authenticatedUser != null) {
                return ResponseEntity.ok("Login Successful");
            } else {
                ErrorResponse errorResponse = new ErrorResponse("Username/Password Incorrect");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            }
        } catch (RuntimeException e) {
            if (e.getMessage().equals("User not found")) {
                ErrorResponse errorResponse = new ErrorResponse("User does not exist");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            } else if (e.getMessage().equals("Incorrect password")) {
                ErrorResponse errorResponse = new ErrorResponse("Username/Password Incorrect");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            } else {
                ErrorResponse errorResponse = new ErrorResponse("Unexpected error");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
            }
        }
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserDetails(@RequestParam Long userID) {
        try {
            User user = userService.getUserById(userID);
            if (user != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("name", user.getName());
                response.put("userID", user.getId());
                response.put("email", user.getEmail());
                return ResponseEntity.ok(response);
            } else {
                ErrorResponse errorResponse = new ErrorResponse("User does not exist");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
        } catch (RuntimeException e) {
            ErrorResponse errorResponse = new ErrorResponse("User does not exist");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/")
    public ResponseEntity<?> getAllPosts() {
        try {
            List<Post> posts = postService.getAllPostsInReverseChronologicalOrder();
            List<Map<String, Object>> postsDTOs = new ArrayList<>();
            for (Post post : posts) {
                Map<String, Object> postDTO = new LinkedHashMap<>(); // Use LinkedHashMap to maintain order

                postDTO.put("postID", post.getId());
                postDTO.put("postBody", post.getBody());
                postDTO.put("date", post.getCreatedAt().toLocalDate());

                List<Map<String, Object>> commentsDTOs = new ArrayList<>();
                for (Comment comment : post.getComments()) {
                    Map<String, Object> commentDTO = new LinkedHashMap<>(); // Use LinkedHashMap to maintain order

                    commentDTO.put("commentID", comment.getId());
                    commentDTO.put("commentBody", comment.getBody());

                    Map<String, Object> commentCreator = new LinkedHashMap<>(); // Use LinkedHashMap to maintain order
                    commentCreator.put("userID", comment.getUser().getId());
                    commentCreator.put("name", comment.getUser().getName());

                    commentDTO.put("commentCreator", commentCreator);
                    commentsDTOs.add(commentDTO);
                }

                postDTO.put("comments", commentsDTOs);
                postsDTOs.add(postDTO);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("posts", postsDTOs);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse("Error retrieving posts");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsersDetails() {
        try {
            List<User> users = userService.getAllUsers();
            List<Map<String, Object>> userDTOs = new ArrayList<>();
            for (User user : users) {
                Map<String, Object> userDTO = new HashMap<>();
                userDTO.put("name", user.getName());
                userDTO.put("userID", user.getId());
                userDTO.put("email", user.getEmail());
                userDTOs.add(userDTO);
            }
            return ResponseEntity.ok(userDTOs);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse("Error retrieving user details");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }}