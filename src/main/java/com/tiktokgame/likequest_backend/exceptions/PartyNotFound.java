package com.tiktokgame.likequest_backend.exceptions;

public class PartyNotFound extends Exception{
    private String pin;

    public PartyNotFound(String pin) {
        this.pin = pin;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }
}
