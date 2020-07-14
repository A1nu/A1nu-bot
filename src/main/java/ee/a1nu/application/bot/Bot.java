package ee.a1nu.application.bot;

import ee.a1nu.application.bot.listeners.TestListener;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.security.auth.login.LoginException;
@Service
public class Bot implements DiscordBot {
    private JDA jda;

    public Bot(Environment env) {
        try {
            jda = new JDABuilder(AccountType.BOT)
                    .setToken(env.getProperty("spring.bot.secret"))
                    .setActivity(Activity.playing("Development"))
                    .setStatus(OnlineStatus.DO_NOT_DISTURB)
                    .addEventListeners(new TestListener())
                    .build();
        } catch (LoginException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String guild, String channel, String message) {
        try {
            jda
                    .getGuildById(guild)
                    .getTextChannelById(channel)
                    .sendMessage(message)
                    .queue();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}