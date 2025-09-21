-- 감정 컬럼 크기 확장
-- FRUSTRATED, DISAPPOINTED 등 긴 감정명을 저장하기 위해 VARCHAR(20)으로 확장

ALTER TABLE diary_entries MODIFY COLUMN text_emotion VARCHAR(20);
ALTER TABLE diary_entries MODIFY COLUMN facial_emotion VARCHAR(20);
ALTER TABLE diary_entries MODIFY COLUMN integrated_emotion VARCHAR(20);
