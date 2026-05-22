package com.tiktokgame.likequest_backend.exceptions;

public class PlayerCountExceedsLimitException extends Exception {
    String pin;
    int max;

    public PlayerCountExceedsLimitException(String pin, int max) {
        this.pin = pin;
        this.max = max;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }
}
