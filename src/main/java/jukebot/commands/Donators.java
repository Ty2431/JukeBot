package jukebot.commands;

import jukebot.Database;
import jukebot.JukeBot;
import jukebot.utils.Command;
import jukebot.utils.CommandProperties;
import jukebot.utils.Permissions;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@CommandProperties(developerOnly = true, description = "Manage and view donators and their tiers", category = CommandProperties.category.MISC)
public class Donators implements Command {

    private final Permissions permissions = new Permissions();

    public void execute(GuildMessageReceivedEvent e, String query) {

        if (!permissions.isBotOwner(e.getAuthor().getIdLong())) {
            e.getChannel().sendMessage(new EmbedBuilder()
                    .setColor(JukeBot.embedColour)
                    .setTitle("Donators")
                    .setDescription("Command reserved for bot developer.")
                    .build()
            ).queue();
            return;
        }

        String[] args = query.split(" ");

        if (args.length == 0) {
            e.getChannel().sendMessage(new EmbedBuilder()
                    .setColor(JukeBot.embedColour)
                    .setTitle("Donators")
                    .setDescription("<getall|get|set> [id] [tier]")
                    .build()
            ).queue();
            return;
        }

        if (args.length != 3) {
            if ("getall".equalsIgnoreCase(args[0])) {
                final HashMap<Long, Integer> donatorsMap = Database.getAllDonators();
                if (donatorsMap.isEmpty()) {
                    e.getChannel().sendMessage(new EmbedBuilder()
                            .setColor(JukeBot.embedColour)
                            .setTitle("Donators")
                            .setDescription("No donators returned.")
                            .build()
                    ).queue();
                    return;
                }

                Message m = e.getChannel().sendMessage("Please wait...").complete();

                StringBuilder t1 = new StringBuilder().append("\u200B"); // Fail-safe in case no donators exist in this tier
                StringBuilder t2 = new StringBuilder().append("\u200B"); // Fail-safe in case no donators exist in this tier
                StringBuilder t3 = new StringBuilder().append("\u200B"); // Fail-safe in case no donators exist in this tier

                final int numOfDonators = donatorsMap.size();

                CompletableFuture<List<Donator>> usersFuture = new CompletableFuture<>();

                List<Donator> donatorsList = new ArrayList<>();

                for (HashMap.Entry<Long, Integer> entry : donatorsMap.entrySet()) {
                    long id = entry.getKey();
                    int level = entry.getValue();
                    e.getJDA().retrieveUserById(entry.getKey()).queue(u -> {
                        donatorsList.add(new Donator(u.getName(), level, id));
                        if (donatorsList.size() == numOfDonators) {
                            usersFuture.complete(donatorsList);
                        }
                    }, t -> {
                        donatorsList.add(new Donator("Unknown User", level, id));
                        if (donatorsList.size() == numOfDonators) {
                            usersFuture.complete(donatorsList);
                        }
                    });
                }
                usersFuture.thenAcceptAsync(list -> {
                    for (Donator d : list) {
                        if (1 == d.level) {
                            t1.append("`")
                                    .append(d.id).append("` ")
                                    .append(d.name)
                                    .append("\n");
                        } else if (2 == d.level) {
                            t2.append("`")
                                    .append(d.id).append("` ")
                                    .append(d.name)
                                    .append("\n");
                        } else if (3 == d.level) {
                            t3.append("`")
                                    .append(d.id).append("` ")
                                    .append(d.name)
                                    .append("\n");
                        }
                    }
                    m.editMessage(new EmbedBuilder()
                            .setColor(JukeBot.embedColour)
                            .addField("Tier 1", t1.toString(), true)
                            .addField("Tier 2", t2.toString(), true)
                            .addField("Tier 3", t3.toString(), true)
                            .build()
                    ).queue();
                });
            } else if ("get".equalsIgnoreCase(args[0])){
                final int userTier = Database.getTier(Long.parseLong(args[1]));
                e.getChannel().sendMessage(new EmbedBuilder()
                        .setColor(JukeBot.embedColour)
                        .setTitle("Donator Status")
                        .setDescription("User **" + args[1] + "** has Tier **" + userTier + "**")
                        .build()
                ).queue();
            } else {
                e.getChannel().sendMessage(new EmbedBuilder()
                        .setColor(JukeBot.embedColour)
                        .setTitle("Donators")
                        .setDescription("<getall|get|set> [id] [tier]")
                        .build()
                ).queue();
            }
        } else {
            if ("set".equalsIgnoreCase(args[0])) {
                final boolean result = Database.setTier(Long.parseLong(args[1]), Integer.parseInt(args[2]));
                e.getChannel().sendMessage(new EmbedBuilder()
                        .setColor(JukeBot.embedColour)
                        .setTitle("Donator Tier")
                        .setDescription("The user's tier was " + (result ? "updated" : "unchanged"))
                        .build()
                ).queue();
            }
        }

    }

    private static class Donator {
        String name;
        int level;
        long id;

        Donator(String name, int level, long id) {
            this.name = name;
            this.level = level;
            this.id = id;
        }
    }
}
