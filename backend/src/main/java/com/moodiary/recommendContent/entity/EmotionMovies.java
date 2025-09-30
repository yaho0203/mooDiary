package com.moodiary.recommendContent.entity;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
@Getter
public class EmotionMovies {
    // 긍정적 감정: 행복한 기분을 더 즐길 수 있는 밝고 따뜻한 영화들
    public static final List<Map<String, String>> HAPPY_MOVIES = Arrays.asList(
            Map.of("title", "러브 액츄얼리", "director", "리처드 커티스", "genre", "로맨틱 코미디"),
            Map.of("title", "인사이드 아웃", "director", "피트 닥터", "genre", "애니메이션"),
            Map.of("title", "사운드 오브 뮤직", "director", "로버트 와이즈", "genre", "뮤지컬"),
            Map.of("title", "마마미아!", "director", "필리다 로이드", "genre", "뮤지컬"),
            Map.of("title", "토이 스토리", "director", "존 래스터", "genre", "애니메이션"),
            Map.of("title", "해리 포터와 마법사의 돌", "director", "크리스 콜럼버스", "genre", "판타지"),
            Map.of("title", "이웃집 토토로", "director", "미야자키 하야오", "genre", "애니메이션"),
            Map.of("title", "노팅힐", "director", "로저 미첼", "genre", "로맨틱 코미디"),
            Map.of("title", "포레스트 검프", "director", "로버트 저메키스", "genre", "드라마"),
            Map.of("title", "라라랜드", "director", "데이미언 셔젤", "genre", "뮤지컬"),
            Map.of("title", "어바웃 타임", "director", "리처드 커티스", "genre", "로맨스"),
            Map.of("title", "미니언즈", "director", "카일 발다", "genre", "애니메이션"),
            Map.of("title", "센과 치히로의 행방불명", "director", "미야자키 하야오", "genre", "애니메이션"),
            Map.of("title", "태극기 휘날리며", "director", "강제규", "genre", "전쟁 드라마"),
            Map.of("title", "엽기적인 그녀", "director", "곽재용", "genre", "로맨틱 코미디")
    );

    // 부정적 감정: 슬픔을 극복하고 희망을 주는 따뜻한 영화들
    public static final List<Map<String, String>> SAD_MOVIES = Arrays.asList(
            Map.of("title", "굿 윌 헌팅", "director", "구스 반 산트", "genre", "드라마"),
            Map.of("title", "인턴", "director", "낸시 마이어스", "genre", "코미디 드라마"),
            Map.of("title", "리틀 미스 선샤인", "director", "조나단 데이튼", "genre", "코미디 드라마"),
            Map.of("title", "위대한 쇼맨", "director", "마이클 그레이시", "genre", "뮤지컬"),
            Map.of("title", "빌리 엘리어트", "director", "스티븐 달드리", "genre", "드라마"),
            Map.of("title", "원더", "director", "스티븐 초보스키", "genre", "가족 드라마"),
            Map.of("title", "쇼생크 탈출", "director", "프랭크 다라본트", "genre", "드라마"),
            Map.of("title", "인생은 아름다워", "director", "로베르토 베니니", "genre", "코미디 드라마"),
            Map.of("title", "업", "director", "피트 닥터", "genre", "애니메이션"),
            Map.of("title", "슬럼독 밀리어네어", "director", "대니 보일", "genre", "드라마"),
            Map.of("title", "킹스 스피치", "director", "톰 후퍼", "genre", "드라마"),
            Map.of("title", "맨 온 와이어", "director", "제임스 마시", "genre", "다큐멘터리"),
            Map.of("title", "국제시장", "director", "윤제균", "genre", "드라마"),
            Map.of("title", "7번방의 선물", "director", "이환경", "genre", "코미디 드라마"),
            Map.of("title", "코코", "director", "리 언크리치", "genre", "애니메이션")
    );

    // 부정적 감정: 분노를 잠재우고 마음을 진정시키는 평화로운 영화들
    public static final List<Map<String, String>> ANGRY_MOVIES = Arrays.asList(
            Map.of("title", "캐스트 어웨이", "director", "로버트 저메키스", "genre", "드라마"),
            Map.of("title", "월든", "director", "다큐멘터리", "genre", "다큐멘터리"),
            Map.of("title", "바람이 분다", "director", "미야자키 하야오", "genre", "애니메이션"),
            Map.of("title", "잃어버린 도시 Z", "director", "제임스 그레이", "genre", "모험 드라마"),
            Map.of("title", "조제, 호랑이 그리고 물고기들", "director", "이누도 잇신", "genre", "로맨스"),
            Map.of("title", "하울의 움직이는 성", "director", "미야자키 하야오", "genre", "애니메이션"),
            Map.of("title", "환상의 빛", "director", "고레에다 히로카즈", "genre", "드라마"),
            Map.of("title", "라이프 오브 파이", "director", "이안", "genre", "모험 드라마"),
            Map.of("title", "미드나잇 인 파리", "director", "우디 앨런", "genre", "코미디"),
            Map.of("title", "잉글리시 페이션트", "director", "안소니 밍겔라", "genre", "로맨스"),
            Map.of("title", "아무도 모른다", "director", "고레에다 히로카즈", "genre", "드라마"),
            Map.of("title", "그린 북", "director", "피터 패럴리", "genre", "드라마"),
            Map.of("title", "밀양", "director", "이창동", "genre", "드라마"),
            Map.of("title", "봄 여름 가을 겨울 그리고 봄", "director", "김기덕", "genre", "드라마"),
            Map.of("title", "세상의 중심에서 사랑을 외치다", "director", "유키사다 이사오", "genre", "로맨스")
    );

    // 부정적 감정: 우울함을 극복하고 삶의 의미를 되찾게 해주는 영감을 주는 영화들
    public static final List<Map<String, String>> DEPRESSED_MOVIES = Arrays.asList(
            Map.of("title", "데드 포에츠 소사이어티", "director", "피터 위어", "genre", "드라마"),
            Map.of("title", "인디아나 존스", "director", "스티븐 스필버그", "genre", "모험"),
            Map.of("title", "백 투 더 퓨처", "director", "로버트 저메키스", "genre", "SF 코미디"),
            Map.of("title", "매트릭스", "director", "워쇼스키 자매", "genre", "SF 액션"),
            Map.of("title", "라이온 킹", "director", "로저 앨러스", "genre", "애니메이션"),
            Map.of("title", "록키", "director", "존 G. 아빌드센", "genre", "스포츠 드라마"),
            Map.of("title", "루디", "director", "데이비드 안스포", "genre", "스포츠 드라마"),
            Map.of("title", "더 헬프", "director", "테이트 테일러", "genre", "드라마"),
            Map.of("title", "히든 피겨스", "director", "테오도어 멜피", "genre", "드라마"),
            Map.of("title", "주토피아", "director", "바이런 하워드", "genre", "애니메이션"),
            Map.of("title", "찰리와 초콜릿 공장", "director", "팀 버튼", "genre", "판타지"),
            Map.of("title", "미스터 홀랜드의 오퍼스", "director", "스티븐 헤렉", "genre", "드라마"),
            Map.of("title", "완득이", "director", "이한", "genre", "코미디 드라마"),
            Map.of("title", "도가니", "director", "황동혁", "genre", "사회 드라마"),
            Map.of("title", "숨바꼭질", "director", "허정", "genre", "드라마")
    );

    // 긍정적 감정: 평온한 마음을 더 깊게 만족시킬 수 있는 잔잔하고 아름다운 영화들
    public static final List<Map<String, String>> CALM_MOVIES = Arrays.asList(
            Map.of("title", "비포 선라이즈", "director", "리처드 링클레이터", "genre", "로맨스"),
            Map.of("title", "마이 블루베리 나이츠", "director", "왕가위", "genre", "로맨스"),
            Map.of("title", "로스트 인 트랜슬레이션", "director", "소피아 코폴라", "genre", "드라마"),
            Map.of("title", "허 Her", "director", "스파이크 존즈", "genre", "SF 로맨스"),
            Map.of("title", "어느 멋진 날", "director", "이윤기", "genre", "로맨스"),
            Map.of("title", "연인", "director", "김의석", "genre", "로맨스"),
            Map.of("title", "인 더 무드 포 러브", "director", "왕가위", "genre", "로맨스"),
            Map.of("title", "너의 이름은", "director", "신카이 마코토", "genre", "애니메이션"),
            Map.of("title", "500일의 썸머", "director", "마크 웹", "genre", "로맨틱 코미디"),
            Map.of("title", "클로저", "director", "마이크 니콜스", "genre", "로맨스 드라마"),
            Map.of("title", "8월의 크리스마스", "director", "허진호", "genre", "로맨스"),
            Map.of("title", "접촉", "director", "로버트 저메키스", "genre", "SF 드라마"),
            Map.of("title", "시간을 달리는 소녀", "director", "호소다 마모루", "genre", "애니메이션"),
            Map.of("title", "일 포스티노", "director", "미하엘 래드포드", "genre", "드라마"),
            Map.of("title", "모노노케 히메", "director", "미야자키 하야오", "genre", "애니메이션")
    );

    // 긍정적 감정: 흥분과 에너지를 만족시킬 수 있는 스릴 넘치는 영화들
    public static final List<Map<String, String>> EXCITED_MOVIES = Arrays.asList(
            Map.of("title", "어벤져스: 엔드게임", "director", "루소 형제", "genre", "슈퍼히어로"),
            Map.of("title", "탑건: 매버릭", "director", "조셉 코신스키", "genre", "액션"),
            Map.of("title", "미션 임파서블", "director", "브라이언 드 팔마", "genre", "액션"),
            Map.of("title", "매드 맥스: 분노의 도로", "director", "조지 밀러", "genre", "액션"),
            Map.of("title", "다크 나이트", "director", "크리스토퍼 놀란", "genre", "액션"),
            Map.of("title", "인셉션", "director", "크리스토퍼 놀란", "genre", "SF 액션"),
            Map.of("title", "존 윅", "director", "채드 스타헬스키", "genre", "액션"),
            Map.of("title", "스피드", "director", "얀 드 봉", "genre", "액션"),
            Map.of("title", "아이언맨", "director", "존 패브로", "genre", "슈퍼히어로"),
            Map.of("title", "레디 플레이어 원", "director", "스티븐 스필버그", "genre", "SF 액션"),
            Map.of("title", "가디언즈 오브 갤럭시", "director", "제임스 건", "genre", "슈퍼히어로"),
            Map.of("title", "베이비 드라이버", "director", "에드가 라이트", "genre", "액션"),
            Map.of("title", "추격자", "director", "나홍진", "genre", "스릴러"),
            Map.of("title", "신세계", "director", "박훈정", "genre", "액션"),
            Map.of("title", "아저씨", "director", "이정범", "genre", "액션")
    );

    // 부정적 감정: 불안함을 달래주고 안정감을 주는 따뜻하고 편안한 영화들
    public static final List<Map<String, String>> ANXIOUS_MOVIES = Arrays.asList(
            Map.of("title", "패딩턴", "director", "폴 킹", "genre", "가족 코미디"),
            Map.of("title", "월-E", "director", "앤드루 스탠턴", "genre", "애니메이션"),
            Map.of("title", "모아나", "director", "론 클레멘츠", "genre", "애니메이션"),
            Map.of("title", "니모를 찾아서", "director", "앤드루 스탠턴", "genre", "애니메이션"),
            Map.of("title", "빅 히어로", "director", "돈 홀", "genre", "애니메이션"),
            Map.of("title", "겨울왕국", "director", "크리스 벅", "genre", "애니메이션"),
            Map.of("title", "쿵푸팬더", "director", "마크 오스본", "genre", "애니메이션"),
            Map.of("title", "주먹왕 랄프", "director", "리치 무어", "genre", "애니메이션"),
            Map.of("title", "몬스터 주식회사", "director", "피트 닥터", "genre", "애니메이션"),
            Map.of("title", "라따뚜이", "director", "브래드 버드", "genre", "애니메이션"),
            Map.of("title", "인크레더블", "director", "브래드 버드", "genre", "애니메이션"),
            Map.of("title", "소울", "director", "피트 닥터", "genre", "애니메이션"),
            Map.of("title", "스튜어트 리틀", "director", "롭 민코프", "genre", "가족 코미디"),
            Map.of("title", "신비한 동물사전", "director", "데이비드 예이츠", "genre", "판타지"),
            Map.of("title", "해리 포터 시리즈", "director", "크리스 콜럼버스", "genre", "판타지")
    );

    // 부정적 감정: 실망을 극복하고 새로운 희망을 찾게 해주는 영감을 주는 영화들
    public static final List<Map<String, String>> DISAPPOINTED_MOVIES = Arrays.asList(
            Map.of("title", "퍼슛 오브 해피니스", "director", "가브리엘레 무치노", "genre", "드라마"),
            Map.of("title", "몬티 파이튼의 인생의 의미", "director", "테리 존스", "genre", "코미디"),
            Map.of("title", "프리덤 라이터스", "director", "리처드 라그라벤즈", "genre", "드라마"),
            Map.of("title", "언터처블: 1%의 우정", "director", "올리비에 나카슈", "genre", "코미디 드라마"),
            Map.of("title", "이미테이션 게임", "director", "모르텐 틸둠", "genre", "드라마"),
            Map.of("title", "아름다운 마음", "director", "론 하워드", "genre", "드라마"),
            Map.of("title", "스탠 바이 미", "director", "롭 라이너", "genre", "성장 드라마"),
            Map.of("title", "굿바이 레닌", "director", "볼프강 베커", "genre", "코미디 드라마"),
            Map.of("title", "몰리스 게임", "director", "애런 소킨", "genre", "드라마"),
            Map.of("title", "엘리펀트 맨", "director", "데이비드 린치", "genre", "드라마"),
            Map.of("title", "트루먼 쇼", "director", "피터 위어", "genre", "코미디 드라마"),
            Map.of("title", "터미널", "director", "스티븐 스필버그", "genre", "코미디 드라마"),
            Map.of("title", "건축학개론", "director", "이용주", "genre", "로맨스"),
            Map.of("title", "박하사탕", "director", "이창동", "genre", "드라마"),
            Map.of("title", "올드보이", "director", "박찬욱", "genre", "스릴러")
    );

    // 부정적 감정: 좌절감을 극복하고 동기부여를 얻을 수 있는 힘이 되는 영화들
    public static final List<Map<String, String>> FRUSTRATED_MOVIES = Arrays.asList(
            Map.of("title", "브레이브하트", "director", "멜 깁슨", "genre", "역사 드라마"),
            Map.of("title", "글래디에이터", "director", "리들리 스콧", "genre", "역사 액션"),
            Map.of("title", "알라딘", "director", "론 클레멘츠", "genre", "애니메이션"),
            Map.of("title", "덤보", "director", "팀 버튼", "genre", "판타지"),
            Map.of("title", "에린 브로코비치", "director", "스티븐 소더버그", "genre", "드라마"),
            Map.of("title", "감격시대", "director", "임권택", "genre", "드라마"),
            Map.of("title", "더 바이러스", "director", "토니 스콧", "genre", "스릴러"),
            Map.of("title", "매트릭스 레볼루션", "director", "워쇼스키 자매", "genre", "SF 액션"),
            Map.of("title", "스파르타쿠스", "director", "스탠리 큐브릭", "genre", "역사 드라마"),
            Map.of("title", "아바타", "director", "제임스 카메론", "genre", "SF 액션"),
            Map.of("title", "라스트 사무라이", "director", "에드워드 즈윅", "genre", "액션 드라마"),
            Map.of("title", "레버넌트: 되살아난 자", "director", "알레한드로 이냐리투", "genre", "액션 드라마"),
            Map.of("title", "킹 아서", "director", "가이 리치", "genre", "액션 어드벤처"),
            Map.of("title", "태극기 휘날리며", "director", "강제규", "genre", "전쟁 드라마"),
            Map.of("title", "명량", "director", "김한민", "genre", "액션 드라마")
    );

    // 중립적 감정: 다양한 장르의 균형잡힌 명작 영화들
    public static final List<Map<String, String>> NEUTRAL_MOVIES = Arrays.asList(
            Map.of("title", "시민 케인", "director", "오슨 웰스", "genre", "드라마"),
            Map.of("title", "카사블랑카", "director", "마이클 커티즈", "genre", "로맨스"),
            Map.of("title", "대부", "director", "프란시스 포드 코폴라", "genre", "범죄 드라마"),
            Map.of("title", "쇼생크 탈출", "director", "프랭크 다라본트", "genre", "드라마"),
            Map.of("title", "12명의 성난 사람들", "director", "시드니 루멧", "genre", "드라마"),
            Map.of("title", "버티고", "director", "알프레드 히치콕", "genre", "스릴러"),
            Map.of("title", "2001: 스페이스 오디세이", "director", "스탠리 큐브릭", "genre", "SF"),
            Map.of("title", "도쿄 이야기", "director", "오즈 야스지로", "genre", "드라마"),
            Map.of("title", "8½", "director", "페데리코 펠리니", "genre", "드라마"),
            Map.of("title", "안토니오니의 욕망", "director", "미켈란젤로 안토니오니", "genre", "드라마"),
            Map.of("title", "황야의 7인", "director", "존 스터지스", "genre", "서부"),
            Map.of("title", "로렌스 오브 아라비아", "director", "데이비드 린", "genre", "서사 드라마"),
            Map.of("title", "올드보이", "director", "박찬욱", "genre", "스릴러"),
            Map.of("title", "기생충", "director", "봉준호", "genre", "스릴러"),
            Map.of("title", "아가씨", "director", "박찬욱", "genre", "스릴러")
    );


    public List<Map<String, String>> getHappyMovies() {
        return HAPPY_MOVIES;
    }

    public List<Map<String, String>> getSadMovies() {
        return SAD_MOVIES;
    }

    public List<Map<String, String>> getAngryMovies() {
        return ANGRY_MOVIES;
    }

    public List<Map<String, String>> getDepressedMovies() {
        return DEPRESSED_MOVIES;
    }

    public List<Map<String, String>> getCalmMovies() {
        return CALM_MOVIES;
    }

    public List<Map<String, String>> getExcitedMovies() {
        return EXCITED_MOVIES;
    }

    public List<Map<String, String>> getAnxiousMovies() {
        return ANXIOUS_MOVIES;
    }

    public List<Map<String, String>> getDisappointedMovies() {
        return DISAPPOINTED_MOVIES;
    }

    public List<Map<String, String>> getFrustratedMovies() {
        return FRUSTRATED_MOVIES;
    }

    public List<Map<String, String>> getNeutralMovies() {
        return NEUTRAL_MOVIES;
    }
}
