package com.moodiary.controller;

import com.moodiary.dto.BookmarkDto;
import com.moodiary.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/bookmarks")
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

    @GetMapping("/{diaryId}")
    public ResponseEntity<List<BookmarkDto>> getBookmarks(@RequestParam Long userId) {
        List<BookmarkDto> bookmarkDtoList =  bookmarkService.getBookmarksByUser(userId);
        return new ResponseEntity<>(bookmarkDtoList, HttpStatus.OK);
    }
}
