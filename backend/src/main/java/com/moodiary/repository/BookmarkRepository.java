package com.moodiary.repository;

import com.moodiary.entity.Bookmark;
import com.moodiary.entity.User;
import com.moodiary.entity.DiaryEntry;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    List<Bookmark> findByUser(User user);
    Optional<Bookmark> findByUserAndDiaryEntry(User user, DiaryEntry diaryEntry);
    void deleteByUserAndDiaryEntry(User user, DiaryEntry diaryEntry);
    Long countByUser(User user);


    // 사용자가 북마크한 일기들의 integratedEmotionScore 평균 구하기
    @Query("SELECT AVG(b.diaryEntry.integratedEmotionScore) " +
            "FROM Bookmark b " +
            "WHERE b.user.id = :userId")
    Double findAverageIntegratedEmotionScoreByUserId(@Param("userId") Long userId);
}
