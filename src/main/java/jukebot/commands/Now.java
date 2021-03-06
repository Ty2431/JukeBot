package jukebot.commands;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import jukebot.JukeBot;
import jukebot.audioutilities.AudioHandler;
import jukebot.utils.Command;
import jukebot.utils.CommandProperties;
import jukebot.utils.Helpers;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

@CommandProperties(description = "Displays the currently playing track", aliases = {"n", "np"}, category = CommandProperties.category.MEDIA)
public class Now implements Command {

    public void execute(GuildMessageReceivedEvent e, String query) {

        final AudioHandler player = JukeBot.getPlayer(e.getGuild().getAudioManager());
        final AudioTrack current = player.player.getPlayingTrack();


        if (!player.isPlaying()) {
            e.getChannel().sendMessage(new EmbedBuilder()
                    .setColor(JukeBot.embedColour)
                    .setTitle("No playback activity")
                    .setDescription("There's nothing playing.")
                    .build()
            ).queue();
            return;
        }

        e.getChannel().sendMessage(new EmbedBuilder()
                .setColor(JukeBot.embedColour)
                .setTitle("Now Playing")
                .setDescription("**[" + current.getInfo().title + "](" + current.getInfo().uri + ")**\n" +
                        "(" + Helpers.fTime(current.getPosition()) + "/" + (current.getInfo().isStream
                        ? "LIVE)"
                        : Helpers.fTime(current.getDuration()) + ") - <@" + current.getUserData() + ">"))
                .addField("Playback Settings",
                        "**Shuffle**: " + (player.isShuffleEnabled() ? "On" : "Off") + "\n**Repeat**: Off", true)
                .setFooter("Packets Dropped: " + player.trackPacketLoss + " | Sent: " + player.trackPackets, null)
                .build()
        ).queue();

    }
}
