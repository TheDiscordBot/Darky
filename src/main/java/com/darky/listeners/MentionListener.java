package com.darky.listeners;

import com.darky.core.Messages;
import com.darky.core.caching.Cache;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.FileReader;
import java.io.IOException;

public class MentionListener extends ListenerAdapter {

    private Cache cache;

    public MentionListener(Cache cache) {
        this.cache = cache;
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (event.getMessage().getContentRaw().equals(event.getGuild().getSelfMember().getAsMention())) {
            MavenXpp3Reader reader = new MavenXpp3Reader();
            Model model = null;
            try {
                model = reader.read(new FileReader("pom.xml"));
            } catch (IOException | XmlPullParserException e) {
                e.printStackTrace();
            }
            EmbedBuilder builder = new EmbedBuilder().setTitle("Hi!").setDescription("I'm better than bulby :P")
                    .addField("Prefix", "d!", true);
            StringBuilder stringBuilder = new StringBuilder();
            model.getDependencies().forEach(
                    dependency -> stringBuilder.append(dependency.getArtifactId()+" - "+dependency.getVersion()+"\n")
            );
            stringBuilder.append("\nOther used Dependencies: \nEdited Command Api - Johnny");
            builder.addField("Dependencies", stringBuilder.toString(), false);
            StringBuilder devs = new StringBuilder();
            model.getDevelopers().forEach(
                    developer -> devs.append(developer.getId()+" - [Website]("+developer.getUrl()+"), [E-Mail](https://hax.bigbotnetwork.de/redirect.html?url=mailto:"+developer.getEmail()+")\n")
            );
            builder.addField("Developer", devs.toString(), false);
            builder.addField("Join our Dev Server!", "[Click here!](https://discord.gg/hxt7SvG)", true);
            builder.addField("Github", "[Click here!](https://github.com/TheDiscordBot/Darky)",false);
            builder.addField("Twitch", "[Click here!](https://www.twitch.tv/darky_devs)", false);
            builder.addField("We are sponsored by LudusHosting", "[Click here!](https://ludus-hosting.de/aff.php?aff=45)", false);
            Messages.sendMessage(cache, event.getChannel(), null, null, event.getAuthor(), false, null, builder).queue();

        }
    }
}
