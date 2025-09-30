package com.moodiary.recommendContent.repository;

import com.moodiary.recommendContent.entity.ContentType;
import com.moodiary.recommendContent.entity.RecommendContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RecommentContentRepository extends JpaRepository<RecommendContent,Long> {
    List<RecommendContent> findByUserIdAndContentTypeAndCreateAtBetween(Long userId, ContentType contentType, LocalDateTime startDateTime, LocalDateTime endDateTime);
}
