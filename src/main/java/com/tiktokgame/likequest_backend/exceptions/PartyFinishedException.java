package com.tiktokgame.likequest_backend.exceptions;

public class PartyFinishedException extends Exception {
    private String pin;

    public PartyFinishedException(String pin) {
        this.pin = pin;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }
}
