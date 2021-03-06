package com.darky.commands.user;

import com.darky.core.Database;
import com.github.johnnyjayjay.discord.commandapi.CommandEvent;
import com.github.johnnyjayjay.discord.commandapi.ICommand;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.*;

public class ProfileCommand implements ICommand {
    @Override
    public void onCommand(CommandEvent event, Member member, TextChannel channel, String[] args) throws Exception {
        if (event.getMessage().getMentionedMembers().size()==1) member=event.getMessage().getMentionedMembers().get(0);
        BufferedImage image = ImageIO.read(new File("./profile/default.png"));
        Graphics2D g = image.createGraphics();
        Map<RenderingHints.Key, Object> hints = new HashMap<>();
        // hints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        // hints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        hints.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        // hints.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        // hints.put(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g.setRenderingHints(hints);

        Font font = new Font("Open Sans Bold", 0, 55);
        g.setFont(font);

        // add Avatar and Name
        g.drawString(member.getEffectiveName(), 320, 180);
        g.drawImage(ImageIO.read(new URL(member.getUser().getAvatarUrl()).openConnection().getInputStream()), 84, 87, 200, 200, null);

        ArrayList<String> badges = new ArrayList<>();
        if (event.getConfig().getOwnersAsList().contains(member.getUser().getIdLong())) {
            badges.add("developer.png");
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(event.getDatabase().getCreateTime(member.getUser()));
        Calendar max = Calendar.getInstance();
        max.set(2018, 12, 31);
        if (calendar.before(max)) {
            badges.add("early-user.png");
        }

        int i = 0;
        for (String filename:badges) {
            int plus = i*70;
            g.drawImage(ImageIO.read(new File("./profile/"+filename)), 320+plus, 190, 70, 70, null);
            i++;
        }

        // add Details
        g.drawString("Coins", 100, 370);
        g.drawString(String.valueOf(event.getDatabase().getCoins(member.getUser())), 320, 370);

        g.drawString("Miner", 100, 440);
        g.drawString(String.valueOf(event.getDatabase().getMinerfromUser(member.getUser()).size()), 320, 440);

        g.dispose();

        File file = new File("profile.png");
        file.createNewFile();
        ImageIO.write(image, "png", file);
        event.getChannel().sendFile(file, file.getName()).queue();
    }

    @Override
    public Message info(Member member, String prefix, Set<String> labels, Database database) {
        return null;
    }

}
