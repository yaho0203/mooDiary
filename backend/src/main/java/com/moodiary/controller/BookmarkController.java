package com.moodiary.controller;

import com.moodiary.dto.BookmarkDto;
import com.moodiary.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/bookmarks")
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @PostMapping("/{diaryId}")
    public ResponseEntity<?> addBookmark(@PathVariable Long diaryId) {
        bookmarkService.addBookmark(diaryId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{diaryId}")
    public ResponseEntity<?> removeBookmark(@PathVariable Long diaryId) {
        bookmarkService.removeBookmark(diaryId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/registered")
    public ResponseEntity<BookmarkDto> getBookmarks() {
        BookmarkDto bookmarkDto =  bookmarkService.getBookmarksByUser();
        return new ResponseEntity<>(bookmarkDto, HttpStatus.OK);
    }
}
