package ee.a1nu.application.bot;

public interface DiscordBot {
    void sendMessage(String guild, String channel, String message);
}
