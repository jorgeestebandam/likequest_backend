package com.tiktokgame.likequest_backend.exceptions;

public class MaxPlayerReachedException extends Exception {
    private String pin;

    public MaxPlayerReachedException(String pin) {
        super("Ya se ha alcanzado el limite de jugadores en la partida: "+pin);
        this.pin = pin;
    }
    public String getPin() {
        return pin;
    }
    public void setPin(String pin) {
        this.pin = pin;
    }
}
