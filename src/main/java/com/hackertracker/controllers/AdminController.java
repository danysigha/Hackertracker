//package com.hackertracker.security.controllers;
//
//import com.hackertracker.security.indexer.SearchIndexer;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/api/admin")
//public class AdminController {
//
//    private final SearchIndexer searchIndexer;
//
//    public AdminController(SearchIndexer searchIndexer) {
//        this.searchIndexer = searchIndexer;
//    }
//
//    @GetMapping("/rebuild-index")
//    public ResponseEntity<String> rebuildIndex() {
//        try {
//            searchIndexer.rebuildIndex();
//            return ResponseEntity.ok("Index rebuild process completed successfully");
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Index rebuild was interrupted: " + e.getMessage());
//        }
//    }
//}