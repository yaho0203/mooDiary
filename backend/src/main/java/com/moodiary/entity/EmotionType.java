package com.moodiary.entity;

public enum EmotionType {
    HAPPY("행복"),
    SAD("슬픔"),
    ANGRY("분노"),
    DEPRESSED("우울"),
    CALM("평온"),
    EXCITED("흥분"),
    ANXIOUS("불안"),
    DISAPPOINTED("실망"),
    FRUSTRATED("좌절"),
    NEUTRAL("중립");
    
    private final String description;
    
    EmotionType(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static EmotionType fromString(String emotionString) {
        if (emotionString == null || emotionString.trim().isEmpty()) {
            return NEUTRAL;
        }
        
        String lowerCase = emotionString.toLowerCase().trim();
        switch (lowerCase) {
            case "happy":
            case "joy":
            case "행복":
                return HAPPY;
            case "sad":
            case "슬픔":
                return SAD;
            case "angry":
            case "분노":
                return ANGRY;
            case "depressed":
            case "우울":
                return DEPRESSED;
            case "calm":
            case "평온":
                return CALM;
            case "excited":
            case "흥분":
                return EXCITED;
            case "anxious":
            case "불안":
                return ANXIOUS;
            case "disappointed":
            case "실망":
                return DISAPPOINTED;
            case "frustrated":
            case "좌절":
                return FRUSTRATED;
            case "neutral":
            case "중립":
            default:
                return NEUTRAL;
        }
    }
}
