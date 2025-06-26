package com.main.trie_db.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.main.trie_db.model.Trie;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/trie")
public class TrieController {
    private static final String BASE_PATH = System.getProperty("user.dir") + "/src/main/java/com/main/trie_db";
    private static final String MASTER_DIR = BASE_PATH + "/data/master/";
    private static final String CACHE_DIR = BASE_PATH + "/data/cache/";

    @Operation(
        summary = "Insert a username and ID into a user's cache trie",
        description = "Creates or updates a serialized trie file under the user's cache. Trie is stored at /data/cache/{userId}.ser.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Successfully inserted"),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
        }
    )
    @PostMapping("/cache/{userId}")
    public ResponseEntity<String> insertToUserCache(
            @PathVariable String userId,
            @RequestParam String id,
            @RequestParam String username) {

        try {
            String path = CACHE_DIR + userId + ".ser";
            Trie trie;

            File file = new File(path);
            if (file.exists()) {
                trie = Trie.loadObject(path);
            } else {
                trie = new Trie(CACHE_DIR + userId);
            }

            trie.add(username, id);
            trie.saveObject();
            return ResponseEntity.ok("Inserted into cache trie for user " + userId);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }


    @Operation(
        summary = "Search for usernames in a user's cache trie",
        description = "Looks up matching usernames from the user's cache trie by prefix.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Matches returned successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
        }
    )
    @GetMapping("/cache/{userId}")
    public ResponseEntity<List<String>> searchUserCache(
            @PathVariable String userId,
            @RequestParam String query) {

        try {
            String path = CACHE_DIR + userId + ".ser";
            File file = new File(path);
            if (!file.exists()) {
                return ResponseEntity.ok(new ArrayList<>()); // return empty if no cache trie
            }

            Trie trie = Trie.loadObject(path);
            List<String> matches = trie.get(query);
            return ResponseEntity.ok(matches);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }


    @Operation(
        summary = "Insert a username and ID into the master trie",
        description = "Each master trie is grouped by starting character of the username and saved as a serialized file.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Successfully inserted"),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
        }
    )
    @PostMapping("/master")
    public ResponseEntity<String> insertToMaster(
            @RequestParam String id,
            @RequestParam String username) {

        try {
            char startChar = Character.toLowerCase(username.charAt(0));
            String path = MASTER_DIR + startChar;
            Trie trie;
            File file = new File(path);
            if (file.exists()) {
                trie = Trie.loadObject(path);
            } else {
                trie = new Trie(path);
            }

            trie.add(username, id);
            trie.saveObject();
            return ResponseEntity.ok("Inserted into master trie for character " + startChar);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

    @Operation(
        summary = "Search for usernames in the master trie",
        description = "Searches the master trie by the first character of the query, returning matching usernames.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Matches returned successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
        }
    )
    @GetMapping("/master")
    public ResponseEntity<List<String>> searchMaster(
            @RequestParam String query) {

        try {
            if (query.isEmpty()) return ResponseEntity.ok(new ArrayList<>());
            char startChar = Character.toLowerCase(query.charAt(0));
            String path = MASTER_DIR + startChar + ".ser";
            File file = new File(path);
            if (!file.exists()) {
                return ResponseEntity.ok(new ArrayList<>());
            }
            System.out.print("OK");
            Trie trie = Trie.loadObject(path);
            List<String> matches = trie.get(query);
            System.out.println(matches.toString());
            return ResponseEntity.ok(matches);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }
}
