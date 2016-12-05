package namtran.chatfirebase;

public class ChatModel {
    public ChatModel(String user, String message) {
        this.user = user;
        this.message = message;
    }
    String user;
    String message;
    public String getUser() {
        return user;
    }
    public void setUser(String user) {
        this.user = user;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}