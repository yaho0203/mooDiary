package com.moodiary.controller;

import com.moodiary.dto.BookmarkDto;
import com.moodiary.service.BookmarkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/bookmarks")
@RequiredArgsConstructor
@Tag(name = "북마크 API", description = "북마크 조회, 추가, 삭제 API")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @GetMapping("/all")
    @Operation(summary = "전체 북마크 조회", description = "사용자의 모든 북마크를 조회합니다")
    public ResponseEntity<List<BookmarkDto.DiaryContent>> getAllBookmarks() {
        List<BookmarkDto.DiaryContent> bookmarks = bookmarkService.getAllBookmarksByUser();
        return new ResponseEntity<>(bookmarks, HttpStatus.OK);
    }

    @GetMapping("/registered")
    @Operation(summary = "등록된 북마크 조회", description = "등록된 북마크를 조회합니다")
    public ResponseEntity<BookmarkDto> getBookmarks() {
        BookmarkDto bookmarkDto = bookmarkService.getBookmarksByUser();
        return new ResponseEntity<>(bookmarkDto, HttpStatus.OK);
    }
    @GetMapping("/{diaryId}")
    @Operation(summary = "특정 북마크 조회", description = "특정 일기의 북마크를 조회합니다")
    public ResponseEntity<BookmarkDto.DiaryContent> getBookmark(@PathVariable Long diaryId) {
        BookmarkDto.DiaryContent bookmark = bookmarkService.getBookmarkByDiaryId(diaryId);
        return ResponseEntity.ok(bookmark);
    }

    @PostMapping("/{diaryId}")
    @Operation(summary = "북마크 추가", description = "특정 일기를 북마크에 추가합니다")
    public ResponseEntity<?> addBookmark(@PathVariable Long diaryId) {
        bookmarkService.addBookmark(diaryId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{diaryId}")
    @Operation(summary = "북마크 삭제", description = "특정 일기의 북마크를 삭제합니다")
    public ResponseEntity<?> removeBookmark(@PathVariable Long diaryId) {
        bookmarkService.removeBookmark(diaryId);
        return ResponseEntity.ok().build();
    }
}
