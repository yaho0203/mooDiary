package com.moodiary.recommendContent.controller;

import com.moodiary.recommendContent.dto.ResponseDto;
import com.moodiary.recommendContent.entity.ContentType;
import com.moodiary.recommendContent.service.RecommendContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recommend")
public class RecommentContentController {
    private final RecommendContentService recommendContentService;

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
        ResponseDto responseDto = recommendContentService.createRecommendMovie();
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }



    @GetMapping("/music/create")
    public ResponseEntity<?> createRecommendMusic() {
        ResponseDto responseDto = recommendContentService.createRecommendMusic();
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping("/wise-saying/create")
    public ResponseEntity<?> createRecommendWiseSaying() {
        ResponseDto responseDto = recommendContentService.createRecommendWiseSaying();
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    // 이번달 추천 컨텐츠 (음악, 영화, 시, 책)
    @GetMapping("/read")
    public ResponseEntity<?> getRecommendContent(@RequestParam int year, @RequestParam int month, @RequestParam ContentType contentType) {
        List<ResponseDto> responseDtos = recommendContentService.getRecommendContent(year, month, contentType);
        return new ResponseEntity<>(responseDtos, HttpStatus.OK);
    }

    @GetMapping("/read/{id}")
    public ResponseEntity<?> getRecommendContentId(@PathVariable Long id) {
        ResponseDto responseDto = recommendContentService.getRecommendContentId(id);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}
