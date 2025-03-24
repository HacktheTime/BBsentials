package de.hype.bingonet.server.objects;

public class BBUser {
    public String username;
    public int userId;
    public BBUser(int userId) {
        this.userId = userId;
    }

    public String getMcUsername() {
        return username;
    }
}
