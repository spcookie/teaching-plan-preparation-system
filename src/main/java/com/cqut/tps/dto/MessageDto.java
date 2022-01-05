package com.cqut.tps.dto;

/**
 * @author Augenstern
 * @date 2021/12/30
 */
public class MessageDto {

    private final String message;
    Boolean statue;

    public MessageDto(String message, boolean statue) {
        this.message = message;
        this.statue = statue;
    }

    public Boolean getStatue() {
        return statue;
    }

    public String getMessage() {
        return message;
    }

}
