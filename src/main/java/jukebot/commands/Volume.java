package jukebot.commands;

import jukebot.JukeBot;
import jukebot.audioutilities.AudioHandler;
import jukebot.utils.Command;
import jukebot.utils.CommandProperties;
import jukebot.utils.Helpers;
import jukebot.utils.Permissions;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

@CommandProperties(aliases = {"vol"}, description = "Adjust the player volume", category = CommandProperties.category.CONTROLS)
public class Volume implements Command {

    private final Permissions permissions = new Permissions();

    public void execute(GuildMessageReceivedEvent e, String query) {

        final AudioHandler player = JukeBot.getPlayer(e.getGuild().getAudioManager());

        if (!player.isPlaying()) {
            e.getChannel().sendMessage(new EmbedBuilder()
                    .setColor(JukeBot.embedColour)
                    .setTitle("No playback activity")
                    .setDescription("There's nothing playing.")
                    .build()
            ).queue();
            return;
        }

        if (query.length() == 0) {
            e.getChannel().sendMessage(new EmbedBuilder()
                    .setColor(JukeBot.embedColour)
                    .setTitle("Volume")
                    .setDescription("\uD83D\uDD08 " + player.player.getVolume() + "%")
                    .build()
            ).queue();
        } else {
            if (!permissions.isElevatedUser(e.getMember(), false)) {
                e.getChannel().sendMessage(new EmbedBuilder()
                        .setColor(JukeBot.embedColour)
                        .setTitle("Permission Error")
                        .setDescription("You need to have the DJ role.")
                        .build()
                ).queue();
                return;
            }

            final int newVolume = Helpers.parseNumber(query, 100);

            player.player.setVolume(newVolume);

            e.getChannel().sendMessage(new EmbedBuilder()
                    .setColor(JukeBot.embedColour)
                    .setTitle("Volume")
                    .setDescription("\uD83D\uDD08 " + player.player.getVolume() + "%")
                    .build()
            ).queue();
        }

    }
}
