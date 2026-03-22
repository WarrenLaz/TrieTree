# Trie Tree API

Simple REST API implementing a Trie (Prefix Tree) using Java and Spring Boot.

## Tech
- Java
- Spring Boot
- Gradle

## Run
```bash
./gradlew build
./gradlew bootRun
```
API runs at: http://localhost:8080

Endpoints
Insert
```
POST /api/trie/insert
```
```
{ "word": "example" }
```
Search
```
GET /api/trie/search?word=example
```
Prefix
```
GET /api/trie/prefix?prefix=ex
```
Notes
Fast prefix-based operations
No license
