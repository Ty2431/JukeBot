package jukebot.commands;

import jukebot.JukeBot;
import jukebot.audioutilities.AudioHandler;
import jukebot.utils.Command;
import jukebot.utils.CommandProperties;
import jukebot.utils.Permissions;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

@CommandProperties(description = "Ends the queue and current track", category = CommandProperties.category.CONTROLS)
public class Stop implements Command {

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

        if (!permissions.isElevatedUser(e.getMember(), true)) {
            e.getChannel().sendMessage(new EmbedBuilder()
                    .setColor(JukeBot.embedColour)
                    .setTitle("Permission Error")
                    .setDescription("You need to have the DJ role!")
                    .build()
            ).queue();
            return;
        }

        player.getQueue().clear();
        player.playNext();

    }
}
