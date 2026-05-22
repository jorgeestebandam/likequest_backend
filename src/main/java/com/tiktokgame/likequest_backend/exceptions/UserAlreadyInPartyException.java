package com.tiktokgame.likequest_backend.exceptions;

public class UserAlreadyInPartyException extends Exception{
   private String user;
    private String pin;

    public UserAlreadyInPartyException(String user,String pin){
        super("Usuario: " + user + ", ya existe en la partida: "+pin);
        this.user=user;
        this.pin=pin;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }
}
