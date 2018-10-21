package com.darky.commands.user;

import com.darky.core.Database;
import com.darky.core.GithubStuff;
import com.darky.util.DescriptionBuilder;
import com.github.johnnyjayjay.discord.commandapi.CommandEvent;
import com.github.johnnyjayjay.discord.commandapi.ICommand;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHUser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import static com.darky.core.Messages.editMessage;
import static com.darky.core.Messages.sendMessage;

public class RepoCommand implements ICommand {

    private GHRepository repo;

    public RepoCommand(GHRepository repo) {
        this.repo = repo;
    }

    @Override
    public void onCommand(CommandEvent event, Member member, TextChannel channel, String[] args) throws Exception {
        sendMessage(event.getDatabase(), channel, "Loading...", "Please wait!", member.getUser(), true).queue(
                msg -> {
                    try {
                        GHUser hax = null;
                        GHUser stu = null;
                        for (GHUser user : repo.getCollaborators()) {
                            if (user.getLogin().equals("Schlauer-Hax")) hax = user;
                            else if (user.getLogin().equals("Stupremee")) stu = user;
                        }

                        ArrayList<GithubStuff.CommitData> commitsdatas = (ArrayList<GithubStuff.CommitData>) new GithubStuff().getData(repo, hax, stu);


                        EmbedBuilder builder = new EmbedBuilder().setDescription("[Click here!](https://github.com/TheDiscordBot/Darky)\nBranches: " + repo.getBranches().size() +
                                "\nCollaborators: " + repo.getCollaborators().size() +
                                "\nStars: " + repo.getStargazersCount() +
                                "\nCommits: " + repo.listCommits().asList().size());

                        for (GithubStuff.CommitData commitData : commitsdatas) {
                            builder.addField(commitData.getUser().getLogin(), "Commits: " + commitData.getCommits() + " - Lines added: "
                                    + commitData.getAdded() + " - Lines changed: " + commitData.getChanged() + " - Lines deleted: " + commitData.getRemoved(), true);
                        }
                        editMessage(msg, event.getDatabase(), "Github Stats", null, member.getUser(), false, null, builder).queue();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        );
    }

    @Override
    public Message info(Member member, String prefix, Set<String> labels, Database database) {
        return new DescriptionBuilder()
                .setColor(database.getColor(member.getUser()))
                .addUsage(prefix, labels, "@Member *Reason*", "Shows useful Informations about the Darky Repository on Github")
                .build();
    }
}
