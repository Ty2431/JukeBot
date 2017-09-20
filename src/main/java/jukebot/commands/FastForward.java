package jukebot.commands;

import jukebot.utils.Bot;
import jukebot.utils.Command;
import jukebot.JukeBot;
import jukebot.utils.Permissions;
import jukebot.audioutilities.GuildMusicManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.Color;

public class FastForward implements Command {

    private final Permissions permissions = new Permissions();

    public void execute(MessageReceivedEvent e, String query) {

        final GuildMusicManager musicManager = JukeBot.getGuildMusicManager(e.getGuild());
        final AudioTrack currentTrack = musicManager.player.getPlayingTrack();

        if (currentTrack == null) {
            e.getTextChannel().sendMessage(new EmbedBuilder()
                    .setColor(Bot.EmbedColour)
                    .setTitle("No playback activity")
                    .setDescription("There's nothing playing.")
                    .build()
            ).queue();
            return;
        }

        if (!permissions.isElevatedUser(e.getMember(), true) || !permissions.isBaller(e.getAuthor().getId(), 2)) {
            e.getTextChannel().sendMessage(new EmbedBuilder()
                    .setColor(Bot.EmbedColour)
                    .setTitle("Permission Error")
                    .setDescription("You need to have the DJ role and also be [a Donator!](https://www.patreon.com/Devoxin)")
                    .build()
            ).queue();
            return;
        }

        if (!currentTrack.isSeekable()) {
            e.getTextChannel().sendMessage(new EmbedBuilder()
                    .setColor(Bot.EmbedColour)
                    .setTitle("Unable to Seek")
                    .setDescription("The currently playing track doesn't support seeking.")
                    .build()
            ).queue();
            return;
        }

        int fastf = 10000;

        if (query.length() > 0) {
            try {
                fastf = Integer.parseInt(query) * 1000;
            } catch (Exception err) {
                fastf = 10000;
            }
        }

        if (currentTrack.getPosition() + fastf >= currentTrack.getDuration())
            musicManager.handler.playNext(musicManager.player.getPlayingTrack());
        else {
            currentTrack.setPosition(currentTrack.getPosition() + fastf);
            e.getTextChannel().sendMessage(new EmbedBuilder()
                    .setColor(Bot.EmbedColour)
                    .setTitle("Fast-Forward")
                    .setDescription("The current track has been fastforwarded.")
                    .build()
            ).queue();
        }

    }
}
