package com.moodiary.repository;

import com.moodiary.entity.Bookmark;
import com.moodiary.entity.User;
import com.moodiary.entity.DiaryEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    List<Bookmark> findByUser(User user);
    Optional<Bookmark> findByUserAndDiaryEntry(User user, DiaryEntry diaryEntry);
    void deleteByUserAndDiaryEntry(User user, DiaryEntry diaryEntry);
}
