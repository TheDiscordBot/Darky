package com.darky.commands.user;

import com.darky.core.Database;
import com.github.johnnyjayjay.discord.commandapi.CommandEvent;
import com.github.johnnyjayjay.discord.commandapi.ICommand;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class ProfileCommand implements ICommand {
    @Override
    public void onCommand(CommandEvent event, Member member, TextChannel channel, String[] args) throws Exception {
        BufferedImage image = ImageIO.read(new File("./profile/default.jpg"));
        Graphics2D g = image.createGraphics();
        Map<RenderingHints.Key, Object> hints = new HashMap<>();
        hints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        hints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        hints.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        hints.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g.setRenderingHints(hints);

        Font font = new Font("Arial", 0, 55);
        g.setFont(font);

        System.out.println(event.getAuthor().getAvatarUrl());

        // add Avatar and Name
        g.drawString(event.getMember().getEffectiveName(), 332, 213);
        g.drawImage(ImageIO.read(new URL(event.getAuthor().getAvatarUrl()).openConnection().getInputStream()), 84, 87, 200, 200, null);

        // add Details
        g.drawString("Coins", 115, 400);
        g.drawString(String.valueOf(event.getDatabase().getCoins(event.getAuthor())), 320, 400);

        g.dispose();

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "jpg", os);
        } catch (IOException e) {
            e.printStackTrace();
        }
        event.getChannel().sendFile(new ByteArrayInputStream(os.toByteArray()), "join.jpg").queue();
    }

    @Override
    public Message info(Member member, String prefix, Set<String> labels, Database database) {
        return null;
    }

}
