package com.moodiary.entity;

public enum EmotionType {
    HAPPY("행복"),
    SAD("슬픔"),
    ANGRY("분노"),
    DEPRESSED("우울"),
    CALM("평온"),
    EXCITED("흥분"),
    ANXIOUS("불안"),
    NEUTRAL("중립");
    
    private final String description;
    
    EmotionType(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
