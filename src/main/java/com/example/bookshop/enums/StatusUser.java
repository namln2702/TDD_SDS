package com.example.bookshop.enums;

public enum StatusUser {

    active(1),
    inActive(2);

    private final int code;
    StatusUser(int code){
        this.code = code;
    }
}
