package com.darky.util;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * https://github.com/Stupremee
 *
 * @author Stu
 */
public class DescriptionBuilder {
    private List<String> usages;
    private Set<String> perms;
    private EmbedBuilder builder;
    private String description;
    private Color color;

    public DescriptionBuilder() {
        this.usages = new ArrayList<>();
        this.perms = new HashSet<>();
    }

    public DescriptionBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public DescriptionBuilder setColor(Color color) {
        this.color = color;
        return this;
    }

    public DescriptionBuilder addUsage(String prefix, Set<String> labels, String args, String job) {
        usages.add(prefix + "[" + String.join("|", labels) +
                "]" + " " + args + " --- "
                + "**" + job + "**");
        return this;
    }

    public Message build() {
        return new MessageBuilder()
                .setEmbed(new EmbedBuilder()
                        .setTitle("Command Info")
                        .setColor(color)
                        .addField("Description", this.description == null ? "No description" : this.description, false)
                        .addField("Usage", String.join("\n", this.usages), false)
                        .addField("Required Permissions", this.perms.size() == 0 ? "No Permissions required" : String.join("\n", this.perms), false)
                        .setDescription("\n<...> = Required argument\n" +
                                "<..|..|..> = One of the arguments is required\n" +
                                "*italic* = optional argument\n" +
                                "@Member = Member Mention\n" +
                                "#Channel = Channel Mention\n" +
                                "@Role = Role Mention")
                        .setThumbnail("http://stupreme.de/assets/bot/info.png")
                        .build())
                .build();
    }
}
