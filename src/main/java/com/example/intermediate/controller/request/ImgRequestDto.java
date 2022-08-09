package com.example.intermediate.controller.request;

public class ImgRequestDto {
    String image;

    public ImgRequestDto(String image) {
        this.image = image;
    }

    public String getImage() {
        return this.image;
    }

    public void setImage(final String image) {
        this.image = image;
    }
}
