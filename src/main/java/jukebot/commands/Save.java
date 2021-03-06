package jukebot.commands;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import jukebot.JukeBot;
import jukebot.audioutilities.AudioHandler;
import jukebot.utils.Command;
import jukebot.utils.CommandProperties;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

@CommandProperties(description = "DMs you the currently playing track", category = CommandProperties.category.MEDIA)
public class Save implements Command {

    public void execute(GuildMessageReceivedEvent e, String query) {

        final AudioHandler player = JukeBot.getPlayer(e.getGuild().getAudioManager());
        final AudioTrack currentTrack = player.player.getPlayingTrack();

        if (!player.isPlaying()) {
            e.getChannel().sendMessage(new EmbedBuilder()
                    .setColor(JukeBot.embedColour)
                    .setTitle("No playback activity")
                    .setDescription("There's nothing playing.")
                    .build()
            ).queue();
            return;
        }

        if (query.length() > 0 && "all".equalsIgnoreCase(query)) {

            if (player.getQueue().isEmpty()) {
                e.getChannel().sendMessage(new EmbedBuilder()
                        .setColor(JukeBot.embedColour)
                        .setTitle("No songs queued")
                        .setDescription("There are no songs in the queue.")
                        .build()
                ).queue();
                return;
            }

            StringBuilder sb = new StringBuilder();

            for (AudioTrack track : player.getQueue())
                sb.append(track.getInfo().title)
                        .append(" - ")
                        .append(track.getInfo().uri)
                        .append("\r\n");

            e.getAuthor().openPrivateChannel().queue(dm ->
                    dm.sendFile(
                            sb.toString().getBytes(), "queue.txt", null
                    ).queue(null, error ->
                            e.getChannel().sendMessage(new EmbedBuilder()
                                    .setColor(JukeBot.embedColour)
                                    .setTitle("Unable to DM")
                                    .setDescription("I was unable to DM you.\nEnsure I'm not blocked and your DMs are enabled.")
                                    .build()
                            ).queue()
                    )
            );
        } else {
            e.getAuthor().openPrivateChannel().queue(dm ->
                    dm.sendMessage(
                            new EmbedBuilder()
                            .setColor(JukeBot.embedColour)
                            .setTitle(currentTrack.getInfo().title, currentTrack.getInfo().uri)
                            .build()
                    ).queue(null, error ->
                            e.getChannel().sendMessage(new EmbedBuilder()
                                    .setColor(JukeBot.embedColour)
                                    .setTitle("Unable to DM")
                                    .setDescription("I was unable to DM you.\nEnsure I'm not blocked and your DMs are enabled.")
                                    .build()
                            ).queue()
                    )
            );
        }

    }
}
