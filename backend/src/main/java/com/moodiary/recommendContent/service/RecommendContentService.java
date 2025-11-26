package com.moodiary.recommendContent.service;

import com.moodiary.entity.DiaryEntry;
import com.moodiary.entity.EmotionType;
import com.moodiary.entity.UserUserDetails;
import com.moodiary.jwt.JwtTokenProvider;
import com.moodiary.recommendContent.component.GeminiApiResponse;
import com.moodiary.recommendContent.component.NaverBookClient;
import com.moodiary.recommendContent.dto.ResponseDto;
import com.moodiary.recommendContent.entity.*;
import com.moodiary.recommendContent.repository.RecommentContentRepository;
import com.moodiary.repository.DiaryRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service

public class RecommendContentService {
    private final RecommentContentRepository recommentContentRepository;
    private final EmotionBooks emotionBooks;
    private final DiaryRepository diaryRepository;
    private final NaverBookClient naverBookClient;
    private final GeminiApiResponse geminiApiResponse;
    private final EmotionPoems emotionPoems;
    private final EmotionMovies emotionMovies;
    private final EmotionSong emotionSong;


    public RecommendContentService(RecommentContentRepository recommentContentRepository, EmotionBooks emotionBooks, DiaryRepository diaryRepository, JwtTokenProvider jwtTokenProvider, NaverBookClient naverBookClient, GeminiApiResponse geminiApiResponse, EmotionPoems emotionPoems, EmotionMovies emotionMovies, EmotionSong emotionSong) {
        this.recommentContentRepository = recommentContentRepository;
        this.emotionBooks = emotionBooks;
        this.diaryRepository = diaryRepository;

        this.naverBookClient = naverBookClient;
        this.geminiApiResponse = geminiApiResponse;
        this.emotionPoems = emotionPoems;
        this.emotionMovies = emotionMovies;
        this.emotionSong = emotionSong;
    }

    public ResponseDto createNewRecommendBook() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserUserDetails userDetails = (UserUserDetails) auth.getPrincipal();
        Long userId = userDetails.getUser().getId();

        ResponseDto responseDto = new ResponseDto();
        DiaryEntry diary = diaryRepository.findTopByUserIdOrderByCreatedAtDesc(userId).orElse(null);
        Random random = new Random();
        int randomNumber = random.nextInt(40);

        String bookTitle = switch (diary.getIntegratedEmotion()) {
            case HAPPY -> emotionBooks.getHappyBooks(randomNumber);
            case SAD -> emotionBooks.getSadBooks(randomNumber);
            case ANGRY -> emotionBooks.getAngryBooks(randomNumber);
            case DEPRESSED -> emotionBooks.getDepressedBooks(randomNumber);
            case CALM -> emotionBooks.getCalmBooks(randomNumber);
            case EXCITED -> emotionBooks.getExcitedBooks(randomNumber);
            case ANXIOUS -> emotionBooks.getAnxiousBooks(randomNumber);
            default -> emotionBooks.getNeutralBooks(randomNumber);
        };

        String content = geminiApiResponse.getBookGeminiResponse(bookTitle, diary.getIntegratedEmotion().getDescription());
        String bookImageUrl = naverBookClient.getBookImageUrl(bookTitle);

        LocalDateTime now = LocalDateTime.now();

        RecommendContent recommendContent = new RecommendContent();
        recommendContent.setTitle(bookTitle);
        recommendContent.setUser(userDetails.getUser());
        recommendContent.setContent(content);
        recommendContent.setImageUri(bookImageUrl);
        recommendContent.setCreateAt(now);
        recommendContent.setContentType(ContentType.BOOK);

        recommentContentRepository.save(recommendContent);

        responseDto.setTitle(bookTitle);
        responseDto.setContent(content);
        responseDto.setImageUrl(bookImageUrl);

        return responseDto;
    }

    public ResponseDto createRecommendPoem() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserUserDetails userDetails = (UserUserDetails) auth.getPrincipal();
        Long userId = userDetails.getUser().getId();

        ResponseDto responseDto = new ResponseDto();
        DiaryEntry diary = diaryRepository.findTopByUserIdOrderByCreatedAtDesc(userId).orElse(null);
        Random random = new Random();
        int randomNumber = random.nextInt(15);

        List<Map<String, String>> poems;
        switch (diary.getIntegratedEmotion()) {
            case HAPPY -> poems = emotionPoems.getHappyPoems();
            case SAD -> poems = emotionPoems.getSadPoems();
            case ANGRY -> poems = emotionPoems.getAngryPoems();
            case DEPRESSED -> poems = emotionPoems.getDepressedPoems();
            case CALM -> poems = emotionPoems.getCalmPoems();
            case EXCITED -> poems = emotionPoems.getExcitedPoems();
            case ANXIOUS -> poems = emotionPoems.getAnxiousPoems();
            case DISAPPOINTED -> poems = emotionPoems.getDisappointedPoems();
            case FRUSTRATED -> poems = emotionPoems.getFrustratedPoems();
            default -> poems = emotionPoems.getNeutralPoems();
        }

        Map<String, String> selectedPoem = poems.get(randomNumber);
        String poemTitle = selectedPoem.get("title");
        String poemAuthor = selectedPoem.get("author");

        String poemResponse = geminiApiResponse.getPomeGeminiResponse(poemTitle, diary.getIntegratedEmotion().getDescription(), poemAuthor);

        responseDto.setTitle(poemTitle);
        responseDto.setContent(poemResponse);
        LocalDateTime now = LocalDateTime.now();

        RecommendContent recommendContent = new RecommendContent();
        recommendContent.setTitle(poemTitle);
        recommendContent.setUser(userDetails.getUser());
        recommendContent.setContent(poemResponse);
        recommendContent.setImageUri(null);
        recommendContent.setCreateAt(now);
        recommendContent.setContentType(ContentType.POEM);
        recommentContentRepository.save(recommendContent);

        return responseDto;
    }

    public ResponseDto createRecommendMovie() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserUserDetails userDetails = (UserUserDetails) auth.getPrincipal();
        Long userId = userDetails.getUser().getId();

        ResponseDto responseDto = new ResponseDto();
        DiaryEntry diary = diaryRepository.findTopByUserIdOrderByCreatedAtDesc(userId).orElse(null);
        Random random = new Random();
        int randomNumber = random.nextInt(15);


        List<Map<String, String>> movie = null;

        switch (diary.getIntegratedEmotion()) {
            case HAPPY -> movie = emotionMovies.getHappyMovies();
            case SAD -> movie = emotionMovies.getSadMovies();
            case ANGRY -> movie = emotionMovies.getAngryMovies();
            case DEPRESSED -> movie = emotionMovies.getDepressedMovies();
            case CALM -> movie = emotionMovies.getCalmMovies();
            case EXCITED -> movie = emotionMovies.getExcitedMovies();
            case ANXIOUS -> movie = emotionMovies.getAnxiousMovies();
            case DISAPPOINTED -> movie = emotionMovies.getDisappointedMovies();
            case FRUSTRATED -> movie = emotionMovies.getFrustratedMovies();
            case NEUTRAL -> movie = emotionMovies.getNeutralMovies();
        }

// 바로 값 가져오기
        Map<String, String> selectedMovie = movie.get(randomNumber);
        String movieTitle = selectedMovie.get("title");
        String movieDirector = selectedMovie.get("director");

        String movieResponse = geminiApiResponse.getMovieGeminiResponse(movieTitle, diary.getIntegratedEmotion().getDescription(), movieDirector);


        LocalDateTime now = LocalDateTime.now();
        RecommendContent recommendContent = new RecommendContent();
        recommendContent.setTitle(movieTitle);
        recommendContent.setUser(userDetails.getUser());
        recommendContent.setContent(movieResponse);
        recommendContent.setImageUri(null);
        recommendContent.setCreateAt(now);
        recommendContent.setContentType(ContentType.MOVIE);
        recommentContentRepository.save(recommendContent);

        responseDto.setTitle(movieTitle);
        responseDto.setContent(movieResponse);

        return responseDto;

    }

    public ResponseDto createRecommendMusic() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserUserDetails userDetails = (UserUserDetails) auth.getPrincipal();
        Long userId = userDetails.getUser().getId();

        ResponseDto responseDto = new ResponseDto();
        DiaryEntry diary = diaryRepository.findTopByUserIdOrderByCreatedAtDesc(userId).orElse(null);
        Random random = new Random();
        int randomNumber = random.nextInt(15);


        List<Map<String, String>> music = null;

        switch (diary.getIntegratedEmotion()) {
            case HAPPY -> music = emotionSong.getHappySongs();
            case SAD -> music = emotionSong.getSadSongs();
            case ANGRY -> music = emotionSong.getAngrySongs();
            case DEPRESSED -> music = emotionSong.getDepressedSongs();
            case CALM -> music = emotionSong.getCalmSongs();
            case EXCITED -> music = emotionSong.getExcitedSongs();
            case ANXIOUS -> music = emotionSong.getAnxiousSongs();
            case DISAPPOINTED -> music = emotionSong.getDisappointedSongs();
            case FRUSTRATED -> music = emotionSong.getFrustratedSongs();
            case NEUTRAL -> music = emotionSong.getNeutralSongs();
        }


// 바로 값 가져오기
        Map<String, String> selectedMovie = music.get(randomNumber);
        String musicTitle = selectedMovie.get("title");
        String musicArtist = selectedMovie.get("artist");

        String musicResponse = geminiApiResponse.getMusicGeminiResponse(musicTitle, diary.getIntegratedEmotion().getDescription(), musicArtist);


        LocalDateTime now = LocalDateTime.now();
        RecommendContent recommendContent = new RecommendContent();
        recommendContent.setTitle(musicTitle);
        recommendContent.setUser(userDetails.getUser());
        recommendContent.setContent(musicResponse);
        recommendContent.setImageUri(null);
        recommendContent.setCreateAt(now);
        recommendContent.setContentType(ContentType.MUSIC);
        recommentContentRepository.save(recommendContent);

        responseDto.setTitle(musicTitle);
        responseDto.setContent(musicResponse);

        return responseDto;

    }

    public List<ResponseDto> getRecommendContent(int year, int month, ContentType contentType) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserUserDetails userDetails = (UserUserDetails) auth.getPrincipal();
        Long userId = userDetails.getUser().getId();

        LocalDateTime startDateTime = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime endDateTime = LocalDateTime.of(year, month, startDateTime.toLocalDate().lengthOfMonth(), 23, 59, 59);


        List<RecommendContent> contents = recommentContentRepository.findByUserIdAndContentTypeAndCreateAtBetween(
                userId, contentType, startDateTime, endDateTime
        );

        List<ResponseDto> responseDtos = new ArrayList<>();

        for (RecommendContent recommendContent : contents) {
            ResponseDto responseDto = new ResponseDto();
            responseDto.setTitle(recommendContent.getTitle());
            responseDto.setContent(recommendContent.getContent());
            responseDto.setImageUrl(recommendContent.getImageUri());
            responseDto.setContentId(recommendContent.getId());
            responseDtos.add(responseDto);
        }

        return responseDtos;
    }

    public ResponseDto getRecommendContentId(Long id) {
        RecommendContent recommendContent = recommentContentRepository.findById(id).orElse(null);
        ResponseDto responseDto = new ResponseDto();

        responseDto.setTitle(recommendContent.getTitle());
        responseDto.setContent(recommendContent.getContent());
        responseDto.setImageUrl(recommendContent.getImageUri());
        responseDto.setContentId(recommendContent.getId());
        return responseDto;
    }

    public ResponseDto createRecommendWiseSaying() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserUserDetails userDetails = (UserUserDetails) auth.getPrincipal();
        Long userId = userDetails.getUser().getId();

        ResponseDto responseDto = new ResponseDto();
        DiaryEntry diary = diaryRepository.findTopByUserIdOrderByCreatedAtDesc(userId).orElse(null);
        Random random = new Random();
        int randomNumber = random.nextInt(40);

        List<String> wiseSayingList = null;

        switch (diary.getIntegratedEmotion()) {
            case HAPPY -> wiseSayingList = EmotionWiseSaying.HAPPY_QUOTES;
            case SAD -> wiseSayingList = EmotionWiseSaying.SAD_QUOTES;
            case ANGRY -> wiseSayingList = EmotionWiseSaying.ANGRY_QUOTES;
            case DEPRESSED -> wiseSayingList = EmotionWiseSaying.DEPRESSED_QUOTES;
            case CALM -> wiseSayingList = EmotionWiseSaying.CALM_QUOTES;
            case EXCITED -> wiseSayingList = EmotionWiseSaying.EXCITED_QUOTES;
            case ANXIOUS -> wiseSayingList = EmotionWiseSaying.ANXIOUS_QUOTES;
            case DISAPPOINTED -> wiseSayingList = EmotionWiseSaying.DISAPPOINTED_QUOTES;
            case FRUSTRATED -> wiseSayingList = EmotionWiseSaying.FRUSTRATED_QUOTES;
            case NEUTRAL -> wiseSayingList = EmotionWiseSaying.NEUTRAL_QUOTES;
        }

        String wiseSaying = wiseSayingList.get(randomNumber);

        String wiseSayingResponse = geminiApiResponse.getWiseSayingGeminiResponse(wiseSaying, diary.getIntegratedEmotion().getDescription());


        LocalDateTime now = LocalDateTime.now();
        RecommendContent recommendContent = new RecommendContent();
        recommendContent.setTitle(wiseSaying);
        recommendContent.setUser(userDetails.getUser());
        recommendContent.setContent(wiseSayingResponse);
        recommendContent.setImageUri(null);
        recommendContent.setCreateAt(now);
        recommendContent.setContentType(ContentType.WISESAYING);
        recommentContentRepository.save(recommendContent);

        responseDto.setTitle(wiseSaying);
        responseDto.setContent(wiseSayingResponse);

        return responseDto;


    }
}