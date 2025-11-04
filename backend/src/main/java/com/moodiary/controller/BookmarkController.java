package com.moodiary.controller;

import com.moodiary.dto.BookmarkDto;
import com.moodiary.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/bookmarks")
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @PostMapping("/{diaryId}")
    public void addBookmark(@PathVariable Long diaryId, @RequestParam Long userId) {
        bookmarkService.addBookmark(userId, diaryId);
    }

    @DeleteMapping("/{diaryId}")
    public void removeBookmark(@PathVariable Long diaryId, @RequestParam Long userId) {
        bookmarkService.removeBookmark(userId, diaryId);
    }

    @GetMapping
    public List<BookmarkDto> getBookmarks(@RequestParam Long userId) {
        return bookmarkService.getBookmarksByUser(userId);
    }
}
