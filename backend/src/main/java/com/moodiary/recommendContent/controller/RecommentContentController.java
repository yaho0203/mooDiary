package com.moodiary.recommendContent.controller;

import com.moodiary.recommendContent.dto.ResponseDto;
import com.moodiary.recommendContent.service.RecommendContentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/recommend")
public class RecommentContentController {
    private RecommendContentService recommendContentService;

    @GetMapping("/book/create")
    public ResponseEntity<?> createRecommendBook() {
        ResponseDto responseDto = recommendContentService.createNewRecommendBook();
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }


    @GetMapping("/poem/create")
    public ResponseEntity<?> createRecommendPoem() {
        ResponseDto responseDto = recommendContentService.createRecommendPoem();
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }


    @GetMapping("/movie/create")
    public ResponseEntity<?> createRecommendMovie() {
        ResponseDto responseDto = recommendContentService.getRecommendMovie();
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }



    @GetMapping("/book/create")
    public ResponseEntity<?> createRecommendMusic() {
        ResponseDto responseDto = recommendContentService.getRecommendMusic();
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    // 이번달 추천 컨텐츠 (음악, 영화, 시, 책)
}
