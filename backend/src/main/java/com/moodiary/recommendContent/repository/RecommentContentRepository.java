package com.moodiary.recommendContent.repository;

import com.moodiary.recommendContent.entity.RecommendContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecommentContentRepository extends JpaRepository<RecommendContent,Long> {

}
