package com.moodiary.recommendContent.controller;

import com.moodiary.recommendContent.dto.ResponseDto;
import com.moodiary.recommendContent.entity.ContentType;
import com.moodiary.recommendContent.service.RecommendContentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recommend")
@Tag(name = "추천 컨텐츠 API", description = "책, 시, 영화, 음악, 명언 추천 컨텐츠 API")
public class RecommentContentController {
    private final RecommendContentService recommendContentService;

    @GetMapping("/book/create")
    @Operation(summary = "책 추천 컨텐츠 생성", description = "새로운 책 추천 컨텐츠를 생성합니다")
    public ResponseEntity<?> createRecommendBook() {
        ResponseDto responseDto = recommendContentService.createNewRecommendBook();
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }


    @GetMapping("/poem/create")
    @Operation(summary = "시 추천 컨텐츠 생성", description = "새로운 시 추천 컨텐츠를 생성합니다")
    public ResponseEntity<?> createRecommendPoem() {
        ResponseDto responseDto = recommendContentService.createRecommendPoem();
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }


    @GetMapping("/movie/create")
    @Operation(summary = "영화 추천 컨텐츠 생성", description = "새로운 영화 추천 컨텐츠를 생성합니다")
    public ResponseEntity<?> createRecommendMovie() {
        ResponseDto responseDto = recommendContentService.createRecommendMovie();
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }



    @GetMapping("/music/create")
    @Operation(summary = "음악 추천 컨텐츠 생성", description = "새로운 음악 추천 컨텐츠를 생성합니다")
    public ResponseEntity<?> createRecommendMusic() {
        ResponseDto responseDto = recommendContentService.createRecommendMusic();
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping("/wise-saying/create")
    @Operation(summary = "명언 추천 컨텐츠 생성", description = "새로운 명언 추천 컨텐츠를 생성합니다")
    public ResponseEntity<?> createRecommendWiseSaying() {
        ResponseDto responseDto = recommendContentService.createRecommendWiseSaying();
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    // 이번달 추천 컨텐츠 (음악, 영화, 시, 책)
    @GetMapping("/read")
    @Operation(summary = "추천 컨텐츠 조회", description = "특정 년도와 월의 추천 컨텐츠를 조회합니다")
    public ResponseEntity<?> getRecommendContent(@RequestParam int year, @RequestParam int month, @RequestParam ContentType contentType) {
        List<ResponseDto> responseDtos = recommendContentService.getRecommendContent(year, month, contentType);
        return new ResponseEntity<>(responseDtos, HttpStatus.OK);
    }

    @GetMapping("/read/{id}")
    @Operation(summary = "추천 컨텐츠 상세 조회", description = "ID로 특정 추천 컨텐츠를 조회합니다")
    public ResponseEntity<?> getRecommendContentId(@PathVariable Long id) {
        ResponseDto responseDto = recommendContentService.getRecommendContentId(id);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}
