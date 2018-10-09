package com.darky.core;

import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHUser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GithubStuff {

    public List<CommitData> getData(GHRepository repo, GHUser... users) {
        ArrayList<CommitData> data = new ArrayList<>();
        for (GHUser user: users) {
            data.add(countDataFromCommitsWithUser(repo, user));
        }
        return data;
    }

    public CommitData countDataFromCommitsWithUser(GHRepository repo, GHUser user) {
        try {
            int commits = 0, add = 0, change = 0, remove = 0;
            for (GHCommit commit : repo.listCommits().asList()) {
                if (commit.getAuthor().equals(user)) {
                    commits++;
                    add += commit.getLinesAdded();
                    change+= commit.getLinesChanged();
                    remove+= commit.getLinesDeleted();
                }
            }
            return new CommitData(add, change, remove, commits, user);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public class CommitData {
        private int added;
        private int changed;
        private int removed;
        private int commits;
        private GHUser user;

        public CommitData(int added, int changed, int removed, int commits, GHUser user) {
            this.added = added;
            this.changed = changed;
            this.removed = removed;
            this.commits = commits;
            this.user = user;
        }

        public int getAdded() {
            return added;
        }

        public int getChanged() {
            return changed;
        }

        public int getRemoved() {
            return removed;
        }

        public int getCommits() {
            return commits;
        }

        public GHUser getUser() {
            return user;
        }
    }

}
