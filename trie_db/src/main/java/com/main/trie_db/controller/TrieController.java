package com.main.trie_db.controller;

import com.main.trie_db.model.Trie;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.io.File;
import java.util.*;

@RestController
@RequestMapping("/trie")
public class TrieController {

    private static final String MASTER_DIR = "data/master/";
    private static final String CACHE_DIR = "data/cache/";

    // Add a username + ID to a user's cache trie
    @PostMapping("/cache/{userId}")
    public ResponseEntity<String> insertToUserCache(
            @PathVariable String userId,
            @RequestParam String id,
            @RequestParam String username) {

        try {
            String path = CACHE_DIR + userId + ".trie";
            Trie trie;

            File file = new File(path);
            if (file.exists()) {
                trie = Trie.loadObject(path);
            } else {
                trie = new Trie(userId);
            }

            trie.add(id, username);
            Trie.saveObject(path, trie);
            return ResponseEntity.ok("Inserted into cache trie for user " + userId);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

    // Search in user's cache trie by username
    @GetMapping("/cache/{userId}")
    public ResponseEntity<List<String>> searchUserCache(
            @PathVariable String userId,
            @RequestParam String query) {

        try {
            String path = CACHE_DIR + userId + ".trie";
            File file = new File(path);
            if (!file.exists()) {
                return ResponseEntity.ok(new ArrayList<>()); // return empty if no cache trie
            }

            Trie trie = Trie.loadObject(path);
            List<String> matches = trie.getMatchingIds(query);
            return ResponseEntity.ok(matches);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }

    // Insert into master trie (based on starting character)
    @PostMapping("/master")
    public ResponseEntity<String> insertToMaster(
            @RequestParam String id,
            @RequestParam String username) {

        try {
            char startChar = Character.toLowerCase(username.charAt(0));
            String path = MASTER_DIR + startChar + ".trie";
            Trie trie;

            File file = new File(path);
            if (file.exists()) {
                trie = Trie.loadObject(path);
            } else {
                trie = new Trie(String.valueOf(startChar));
            }

            trie.add(id, username);
            Trie.saveObject(path, trie);
            return ResponseEntity.ok("Inserted into master trie for character " + startChar);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

    // Search master trie for username
    @GetMapping("/master")
    public ResponseEntity<List<String>> searchMaster(
            @RequestParam String query) {

        try {
            if (query.isEmpty()) return ResponseEntity.ok(new ArrayList<>());

            char startChar = Character.toLowerCase(query.charAt(0));
            String path = MASTER_DIR + startChar + ".trie";
            File file = new File(path);
            if (!file.exists()) {
                return ResponseEntity.ok(new ArrayList<>());
            }

            Trie trie = Trie.loadObject(path);
            List<String> matches = trie.get(query);
            return ResponseEntity.ok(matches);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }
}
