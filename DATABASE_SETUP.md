# 데이터베이스 설정 가이드

## 감정 분석 컬럼 크기 확장

새로운 감정 타입(`FRUSTRATED`, `DISAPPOINTED` 등)을 지원하기 위해 데이터베이스 컬럼 크기를 확장해야 합니다.

### 수동 실행 (개발 환경)

```sql
USE moodiary;
ALTER TABLE diary_entries MODIFY COLUMN text_emotion VARCHAR(20);
ALTER TABLE diary_entries MODIFY COLUMN facial_emotion VARCHAR(20);
ALTER TABLE diary_entries MODIFY COLUMN integrated_emotion VARCHAR(20);
```

### 자동 실행 (Flyway 사용)

1. Flyway 의존성 추가 후
2. `V1_1__update_emotion_columns.sql` 스크립트가 자동 실행됩니다.

## 감정 타입 목록

- `HAPPY` (행복)
- `SAD` (슬픔)
- `ANGRY` (분노)
- `DEPRESSED` (우울)
- `CALM` (평온)
- `EXCITED` (흥분)
- `ANXIOUS` (불안)
- `DISAPPOINTED` (실망) - **새로 추가**
- `FRUSTRATED` (좌절) - **새로 추가**
- `NEUTRAL` (중립)
