package com.moodiary.recommendContent.entity;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class EmotionSong {

    public static final List<Map<String, String>> HAPPY_SONGS = Arrays.asList(
            Map.of("title", "좋은 날", "artist", "아이유"),
            Map.of("title", "Gee", "artist", "소녀시대"),
            Map.of("title", "행복", "artist", "H.O.T"),
            Map.of("title", "캔디", "artist", "H.O.T"),
            Map.of("title", "삐삐", "artist", "아이유"),
            Map.of("title", "러브 다이브", "artist", "아이브"),
            Map.of("title", "하트시그널", "artist", "트와이스"),
            Map.of("title", "Feel My Rhythm", "artist", "레드벨벳"),
            Map.of("title", "Next Level", "artist", "에스파"),
            Map.of("title", "Weekend", "artist", "태연"),
            Map.of("title", "무지개", "artist", "임재범"),
            Map.of("title", "기쁜 우리 젊은 날", "artist", "윤도현"),
            Map.of("title", "날개", "artist", "god"),
            Map.of("title", "축복", "artist", "이선희"),
            Map.of("title", "Happy", "artist", "태연")
    );

    public static final List<Map<String, String>> SAD_SONGS = Arrays.asList(
            Map.of("title", "8282", "artist", "다비치"),
            Map.of("title", "거짓말", "artist", "빅뱅"),
            Map.of("title", "혼자", "artist", "씨스타"),
            Map.of("title", "미안해", "artist", "양파"),
            Map.of("title", "가시", "artist", "버즈"),
            Map.of("title", "이별택시", "artist", "김연우"),
            Map.of("title", "눈물", "artist", "이소은"),
            Map.of("title", "사랑했지만", "artist", "김범수"),
            Map.of("title", "아픔", "artist", "조성모"),
            Map.of("title", "애인", "artist", "이문세"),
            Map.of("title", "그녀에게 전해주오", "artist", "양희은"),
            Map.of("title", "슬픈 사랑의 노래", "artist", "임재범"),
            Map.of("title", "눈의 꽃", "artist", "박효신"),
            Map.of("title", "쓸쓸함", "artist", "윤종신"),
            Map.of("title", "이별", "artist", "김건모")
    );

    public static final List<Map<String, String>> ANGRY_SONGS = Arrays.asList(
            Map.of("title", "거리에서", "artist", "성시경"),
            Map.of("title", "변해가네", "artist", "서태지와 아이들"),
            Map.of("title", "Come Back Home", "artist", "서태지와 아이들"),
            Map.of("title", "발해를 꿈꾸며", "artist", "서태지와 아이들"),
            Map.of("title", "시간아 멈춰라", "artist", "MC 스나이퍼"),
            Map.of("title", "항상", "artist", "조용필"),
            Map.of("title", "사랑 하나면 돼", "artist", "김수철"),
            Map.of("title", "운명", "artist", "god"),
            Map.of("title", "분노", "artist", "넥스트"),
            Map.of("title", "The Abyss", "artist", "넥스트"),
            Map.of("title", "Cry", "artist", "넥스트"),
            Map.of("title", "폭풍", "artist", "넥스트"),
            Map.of("title", "분노의 날", "artist", "신해철"),
            Map.of("title", "재", "artist", "신해철"),
            Map.of("title", "민물장어의 꿈", "artist", "신해철")
    );

    public static final List<Map<String, String>> DEPRESSED_SONGS = Arrays.asList(
            Map.of("title", "우울한 편지", "artist", "조용필"),
            Map.of("title", "혼자 남은 밤", "artist", "김광석"),
            Map.of("title", "서른 즈음에", "artist", "김광석"),
            Map.of("title", "이등병의 편지", "artist", "김광석"),
            Map.of("title", "먼지가 되어", "artist", "김광석"),
            Map.of("title", "바람이 불어오는 곳", "artist", "김광석"),
            Map.of("title", "그대 안의 블루", "artist", "김광석"),
            Map.of("title", "사랑했지만", "artist", "김광석"),
            Map.of("title", "너무 아픈 사랑은 사랑이 아니었음을", "artist", "김광석"),
            Map.of("title", "나무", "artist", "안치환"),
            Map.of("title", "사람들은 말하네", "artist", "안치환"),
            Map.of("title", "봄날은 간다", "artist", "김윤아"),
            Map.of("title", "슬픈 축제", "artist", "조동진"),
            Map.of("title", "자장가", "artist", "조동진"),
            Map.of("title", "행진", "artist", "조동진")
    );

    public static final List<Map<String, String>> CALM_SONGS = Arrays.asList(
            Map.of("title", "그대 곁에 있을게", "artist", "유재하"),
            Map.of("title", "사랑하기 때문에", "artist", "유재하"),
            Map.of("title", "어제 오늘 그리고", "artist", "유재하"),
            Map.of("title", "가리워진 길", "artist", "유재하"),
            Map.of("title", "우울한 편지", "artist", "조용필"),
            Map.of("title", "고추잠자리", "artist", "조용필"),
            Map.of("title", "허공", "artist", "이승환"),
            Map.of("title", "천일동안", "artist", "이승환"),
            Map.of("title", "라라라", "artist", "이승환"),
            Map.of("title", "바람꽃", "artist", "이문세"),
            Map.of("title", "옛사랑", "artist", "이문세"),
            Map.of("title", "광화문 연가", "artist", "이문세"),
            Map.of("title", "소녀", "artist", "이문세"),
            Map.of("title", "붉은 노을", "artist", "이문세"),
            Map.of("title", "가을 우체국 앞에서", "artist", "윤도현")
    );

    public static final List<Map<String, String>> EXCITED_SONGS = Arrays.asList(
            Map.of("title", "Fantastic Baby", "artist", "빅뱅"),
            Map.of("title", "Bang Bang Bang", "artist", "빅뱅"),
            Map.of("title", "I Am The Best", "artist", "2NE1"),
            Map.of("title", "불타오르네", "artist", "방탄소년단"),
            Map.of("title", "Idol", "artist", "방탄소년단"),
            Map.of("title", "DNA", "artist", "방탄소년단"),
            Map.of("title", "Very Good", "artist", "블락비"),
            Map.of("title", "한 번 더 말해줘", "artist", "빅뱅"),
            Map.of("title", "좋아", "artist", "윤종신"),
            Map.of("title", "축제", "artist", "H.O.T"),
            Map.of("title", "전사의 후예", "artist", "H.O.T"),
            Map.of("title", "We Are The Future", "artist", "H.O.T"),
            Map.of("title", "빛", "artist", "god"),
            Map.of("title", "거짓말", "artist", "H.O.T"),
            Map.of("title", "열정", "artist", "서태지와 아이들")
    );

    public static final List<Map<String, String>> ANXIOUS_SONGS = Arrays.asList(
            Map.of("title", "애상", "artist", "서태지와 아이들"),
            Map.of("title", "필승", "artist", "서태지와 아이들"),
            Map.of("title", "교실 이데아", "artist", "서태지와 아이들"),
            Map.of("title", "시간의 밖", "artist", "넥스트"),
            Map.of("title", "Shell Shock", "artist", "넥스트"),
            Map.of("title", "Halo", "artist", "넥스트"),
            Map.of("title", "잠깐만", "artist", "신해철"),
            Map.of("title", "고독", "artist", "신해철"),
            Map.of("title", "인형의 기사", "artist", "신해철"),
            Map.of("title", "슬픈 아픔", "artist", "조용필"),
            Map.of("title", "단발머리", "artist", "조용필"),
            Map.of("title", "걱정", "artist", "김광석"),
            Map.of("title", "너에게", "artist", "성시경"),
            Map.of("title", "두려움", "artist", "이승환"),
            Map.of("title", "혼란", "artist", "유재하")
    );

    public static final List<Map<String, String>> DISAPPOINTED_SONGS = Arrays.asList(
            Map.of("title", "편지", "artist", "김광석"),
            Map.of("title", "일어나", "artist", "김광석"),
            Map.of("title", "사랑이었다", "artist", "김광석"),
            Map.of("title", "두 바퀴로 가는 자동차", "artist", "김광석"),
            Map.of("title", "동행", "artist", "김광석"),
            Map.of("title", "그대만 있다면", "artist", "김광석"),
            Map.of("title", "만약에", "artist", "태진아"),
            Map.of("title", "옥경이", "artist", "태진아"),
            Map.of("title", "사모곡", "artist", "태진아"),
            Map.of("title", "마지막 그 사람", "artist", "나훈아"),
            Map.of("title", "고향역", "artist", "나훈아"),
            Map.of("title", "울고넘는 박달재", "artist", "나훈아"),
            Map.of("title", "홍시", "artist", "이문세"),
            Map.of("title", "꿈에", "artist", "조용필"),
            Map.of("title", "미안", "artist", "양파")
    );

    public static final List<Map<String, String>> FRUSTRATED_SONGS = Arrays.asList(
            Map.of("title", "울고 싶다", "artist", "서태지와 아이들"),
            Map.of("title", "Internet War", "artist", "서태지"),
            Map.of("title", "크리스마스니까", "artist", "성시경"),
            Map.of("title", "벽", "artist", "넥스트"),
            Map.of("title", "Regret", "artist", "넥스트"),
            Map.of("title", "Hero", "artist", "넥스트"),
            Map.of("title", "그대에게", "artist", "무한궤도"),
            Map.of("title", "그런 사람 또 없습니다", "artist", "이승철"),
            Map.of("title", "상처", "artist", "이승철"),
            Map.of("title", "Never Ending Story", "artist", "이승철"),
            Map.of("title", "비상", "artist", "이승환"),
            Map.of("title", "소리쳐", "artist", "이승환"),
            Map.of("title", "제발", "artist", "이승환"),
            Map.of("title", "안녕하세요", "artist", "김광석"),
            Map.of("title", "압구정 날라리", "artist", "클론")
    );

    public static final List<Map<String, String>> NEUTRAL_SONGS = Arrays.asList(
            Map.of("title", "그날들", "artist", "김광석"),
            Map.of("title", "거리에서", "artist", "김광석"),
            Map.of("title", "바위섬", "artist", "김광석"),
            Map.of("title", "일상", "artist", "조용필"),
            Map.of("title", "단발머리", "artist", "조용필"),
            Map.of("title", "친구여", "artist", "조용필"),
            Map.of("title", "모습", "artist", "이문세"),
            Map.of("title", "시간의 흐름에 몸을 맡겨", "artist", "이문세"),
            Map.of("title", "그대가 곁에 있어도 나는 그대가 그립다", "artist", "이승환"),
            Map.of("title", "결혼", "artist", "유재하"),
            Map.of("title", "하루", "artist", "유재하"),
            Map.of("title", "날개 잃은 천사", "artist", "신승훈"),
            Map.of("title", "I Believe", "artist", "신승훈"),
            Map.of("title", "미소", "artist", "신승훈"),
            Map.of("title", "인연", "artist", "이선희")
    );
}
