package com.example.cookshare2;

public class UsersF {

    public String user;
    public String passwword;

    public UsersF(String user, String password){
        this.user = user;
        this.passwword = password;
    }
    public void setUser(String user) {
        this.user = user;
    }
    public void setPassword(String password) {
        this.passwword = password;
    }
    public String getUser() {
        return user;
    }
    public String getPasswword() {
        return passwword;
    }


    public String toString() {
        return "users: " +
                "user='" + user + '\''+
                ", password='" + passwword + '\''
                ;
    }
}
