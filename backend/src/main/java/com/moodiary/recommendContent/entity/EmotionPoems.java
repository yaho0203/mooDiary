package com.moodiary.recommendContent.entity;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
@Getter
public class EmotionPoems {
    public static final List<Map<String, String>> HAPPY_POEMS = Arrays.asList(
            Map.of("title", "즐거운 우리 집", "author", "윤석중"),
            Map.of("title", "고향의 봄", "author", "이원수"),
            Map.of("title", "퐁당퐁당", "author", "이원수"),
            Map.of("title", "어린이날 노래", "author", "윤석중"),
            Map.of("title", "새", "author", "윤석중"),
            Map.of("title", "참새", "author", "윤석중"),
            Map.of("title", "강아지똥", "author", "권정생"),
            Map.of("title", "몽실언니", "author", "권정생"),
            Map.of("title", "햇살", "author", "나태주"),
            Map.of("title", "풀꽃", "author", "나태주"),
            Map.of("title", "자전거", "author", "나태주"),
            Map.of("title", "꽃을 보듯 너를 본다", "author", "나태주"),
            Map.of("title", "더불어 숲", "author", "나태주"),
            Map.of("title", "첫사랑", "author", "이해인"),
            Map.of("title", "민들레", "author", "이해인")
    );

    public static final List<Map<String, String>> SAD_POEMS = Arrays.asList(
            Map.of("title", "서시", "author", "윤동주"),
            Map.of("title", "별 헤는 밤", "author", "윤동주"),
            Map.of("title", "자화상", "author", "윤동주"),
            Map.of("title", "십자가", "author", "윤동주"),
            Map.of("title", "또 다른 고향", "author", "윤동주"),
            Map.of("title", "참회록", "author", "윤동주"),
            Map.of("title", "쉽게 씌어진 시", "author", "윤동주"),
            Map.of("title", "하늘과 바람과 별과 시", "author", "윤동주"),
            Map.of("title", "간", "author", "윤동주"),
            Map.of("title", "길", "author", "윤동주"),
            Map.of("title", "새로운 길", "author", "윤동주"),
            Map.of("title", "흰 그림자", "author", "윤동주"),
            Map.of("title", "슬픈 족속", "author", "윤동주"),
            Map.of("title", "눈 오는 지도", "author", "윤동주"),
            Map.of("title", "소년", "author", "윤동주")
    );

    public static final List<Map<String, String>> ANGRY_POEMS = Arrays.asList(
            Map.of("title", "광야", "author", "이육사"),
            Map.of("title", "절정", "author", "이육사"),
            Map.of("title", "꽃", "author", "이육사"),
            Map.of("title", "청포도", "author", "이육사"),
            Map.of("title", "강 건너간 노래", "author", "이육사"),
            Map.of("title", "교목", "author", "이육사"),
            Map.of("title", "눈", "author", "이육사"),
            Map.of("title", "소년 행", "author", "이육사"),
            Map.of("title", "풀잎 단장", "author", "이육사"),
            Map.of("title", "빼앗긴 들에도 봄은 오는가", "author", "이상화"),
            Map.of("title", "나의 침실로", "author", "이상화"),
            Map.of("title", "그날이 오면", "author", "심훈"),
            Map.of("title", "상록수", "author", "심훈"),
            Map.of("title", "나는 왕이로소이다", "author", "김동환"),
            Map.of("title", "조국찬가", "author", "홍사용")
    );

    public static final List<Map<String, String>> DEPRESSED_POEMS = Arrays.asList(
            Map.of("title", "님의 침묵", "author", "한용운"),
            Map.of("title", "알 수 없어요", "author", "한용운"),
            Map.of("title", "나룻배와 행인", "author", "한용운"),
            Map.of("title", "단식", "author", "한용운"),
            Map.of("title", "독자", "author", "한용운"),
            Map.of("title", "오감도", "author", "이상"),
            Map.of("title", "거울", "author", "이상"),
            Map.of("title", "날개", "author", "이상"),
            Map.of("title", "권태", "author", "이상"),
            Map.of("title", "향수", "author", "정지용"),
            Map.of("title", "유리창", "author", "정지용"),
            Map.of("title", "백록담", "author", "정지용"),
            Map.of("title", "대장간의 유혹", "author", "정지용"),
            Map.of("title", "카페 프란스", "author", "정지용"),
            Map.of("title", "진달래꽃", "author", "김소월")
    );

    public static final List<Map<String, String>> CALM_POEMS = Arrays.asList(
            Map.of("title", "승무", "author", "조지훈"),
            Map.of("title", "봉황수", "author", "조지훈"),
            Map.of("title", "고풍의상", "author", "조지훈"),
            Map.of("title", "완화삼", "author", "조지훈"),
            Map.of("title", "지조론", "author", "조지훈"),
            Map.of("title", "나그네", "author", "박목월"),
            Map.of("title", "청노루", "author", "박목월"),
            Map.of("title", "산도화", "author", "박목월"),
            Map.of("title", "윤사월", "author", "박목월"),
            Map.of("title", "기차는 8시에 떠나네", "author", "박목월"),
            Map.of("title", "국화 옆에서", "author", "서정주"),
            Map.of("title", "추천사", "author", "서정주"),
            Map.of("title", "자화상", "author", "서정주"),
            Map.of("title", "화사", "author", "서정주"),
            Map.of("title", "동천", "author", "서정주")
    );

    public static final List<Map<String, String>> EXCITED_POEMS = Arrays.asList(
            Map.of("title", "거센 바람", "author", "유치환"),
            Map.of("title", "생명의 서", "author", "유치환"),
            Map.of("title", "바위", "author", "유치환"),
            Map.of("title", "깃발", "author", "유치환"),
            Map.of("title", "정열", "author", "유치환"),
            Map.of("title", "돛", "author", "유치환"),
            Map.of("title", "행복", "author", "유치환"),
            Map.of("title", "청춘", "author", "김동명"),
            Map.of("title", "파도", "author", "김동명"),
            Map.of("title", "화개", "author", "고은"),
            Map.of("title", "춘설", "author", "고은"),
            Map.of("title", "농무", "author", "신경림"),
            Map.of("title", "갈대", "author", "신경림"),
            Map.of("title", "목계장터", "author", "신경림"),
            Map.of("title", "산", "author", "신경림")
    );

    public static final List<Map<String, String>> ANXIOUS_POEMS = Arrays.asList(
            Map.of("title", "무제", "author", "정지용"),
            Map.of("title", "병원", "author", "이상"),
            Map.of("title", "문학", "author", "김수영"),
            Map.of("title", "풀", "author", "김수영"),
            Map.of("title", "폭포", "author", "김수영"),
            Map.of("title", "어느 날 고궁을 나서며", "author", "김수영"),
            Map.of("title", "새들도 세상을 뜨는구나", "author", "황지우"),
            Map.of("title", "게 눈 속의 연꽃", "author", "황지우"),
            Map.of("title", "연인", "author", "황지우"),
            Map.of("title", "살아있는 것은 흔들리면서", "author", "도종환"),
            Map.of("title", "담쟁이", "author", "도종환"),
            Map.of("title", "흔들리며 피는 꽃", "author", "도종환"),
            Map.of("title", "수선화에게", "author", "정호승"),
            Map.of("title", "슬픔이 기쁨에게", "author", "정호승"),
            Map.of("title", "내가 사랑하는 사람", "author", "정호승")
    );

    public static final List<Map<String, String>> DISAPPOINTED_POEMS = Arrays.asList(
            Map.of("title", "엄마야 누나야", "author", "김소월"),
            Map.of("title", "산유화", "author", "김소월"),
            Map.of("title", "초혼", "author", "김소월"),
            Map.of("title", "접동새", "author", "김소월"),
            Map.of("title", "금잔디", "author", "김소월"),
            Map.of("title", "못잊어", "author", "김소월"),
            Map.of("title", "왕십리", "author", "김소월"),
            Map.of("title", "예전엔 미처 몰랐어", "author", "김소월"),
            Map.of("title", "가는 길", "author", "김소월"),
            Map.of("title", "오직 한 사람의 차지기 쁘도록", "author", "한용운"),
            Map.of("title", "내 마음을 아실 이", "author", "한용운"),
            Map.of("title", "애욕", "author", "한용운"),
            Map.of("title", "그대를 사랑합니다", "author", "김광섭"),
            Map.of("title", "성북동 비둘기", "author", "김광섭"),
            Map.of("title", "추일서정", "author", "김광섭")
    );

    public static final List<Map<String, String>> FRUSTRATED_POEMS = Arrays.asList(
            Map.of("title", "모든 경계에는", "author", "황지우"),
            Map.of("title", "십이월", "author", "황지우"),
            Map.of("title", "풍장", "author", "황지우"),
            Map.of("title", "즉흥", "author", "황지우"),
            Map.of("title", "겨울-나무로부터 봄-나무에로", "author", "황지우"),
            Map.of("title", "사랑의 변주곡", "author", "김수영"),
            Map.of("title", "절망", "author", "김수영"),
            Map.of("title", "거대한 뿌리", "author", "김수영"),
            Map.of("title", "변방에 우짖는 새", "author", "신경림"),
            Map.of("title", "길", "author", "신경림"),
            Map.of("title", "민요", "author", "신경림"),
            Map.of("title", "옥수수밭 옆에 당신을 매장하겠소", "author", "도종환"),
            Map.of("title", "접시꽃 당신", "author", "도종환"),
            Map.of("title", "어느 무덤가에서", "author", "도종환"),
            Map.of("title", "내 마음의 옥토에서", "author", "이상화")
    );

    public static final List<Map<String, String>> NEUTRAL_POEMS = Arrays.asList(
            Map.of("title", "꽃", "author", "김춘수"),
            Map.of("title", "처용단장", "author", "김춘수"),
            Map.of("title", "눈", "author", "김춘수"),
            Map.of("title", "빈집", "author", "김춘수"),
            Map.of("title", "소", "author", "김춘수"),
            Map.of("title", "바다와 나비", "author", "김기림"),
            Map.of("title", "기상도", "author", "김기림"),
            Map.of("title", "연가", "author", "김기림"),
            Map.of("title", "태양의 맨살", "author", "김기림"),
            Map.of("title", "해설", "author", "박두진"),
            Map.of("title", "향현", "author", "박두진"),
            Map.of("title", "도봉", "author", "박두진"),
            Map.of("title", "묵상", "author", "박두진"),
            Map.of("title", "사슴", "author", "노천명"),
            Map.of("title", "그런 날이 있다", "author", "나태주")
    );

    public List<Map<String, String>> getHappyPoems() {
        return HAPPY_POEMS;
    }

    public List<Map<String, String>> getSadPoems() {
        return SAD_POEMS;
    }

    public List<Map<String, String>> getAngryPoems() {
        return ANGRY_POEMS;
    }

    public List<Map<String, String>> getDepressedPoems() {
        return DEPRESSED_POEMS;
    }

    public List<Map<String, String>> getCalmPoems() {
        return CALM_POEMS;
    }

    public List<Map<String, String>> getExcitedPoems() {
        return EXCITED_POEMS;
    }

    public List<Map<String, String>> getAnxiousPoems() {
        return ANXIOUS_POEMS;
    }

    public List<Map<String, String>> getDisappointedPoems() {
        return DISAPPOINTED_POEMS;
    }

    public List<Map<String, String>> getFrustratedPoems() {
        return FRUSTRATED_POEMS;
    }

    public List<Map<String, String>> getNeutralPoems() {
        return NEUTRAL_POEMS;
    }
}