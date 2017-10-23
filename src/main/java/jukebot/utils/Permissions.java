package jukebot.utils;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import jukebot.DatabaseHandler;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.managers.AudioManager;

public class Permissions {

    private final DatabaseHandler db = new DatabaseHandler();

    public boolean isBotOwner(long userID) {
        return userID == Bot.BotOwnerID;
    }

    private boolean isDJ(Member m) {
        return m.getRoles().stream().anyMatch(r -> "dj".equalsIgnoreCase(r.getName()));
    }

    public boolean isElevatedUser(Member m, boolean AllowLone) {
        if (AllowLone)
            return isALoner(m) || m.isOwner() || isBotOwner(m.getUser().getIdLong()) || isDJ(m);
        else
            return m.isOwner() || isBotOwner(m.getUser().getIdLong()) || isDJ(m);
    }

    private boolean isALoner(Member m) {
        return (m.getVoiceState().inVoiceChannel() && m.getVoiceState().getChannel().getMembers().stream().filter(u -> !u.getUser().isBot()).count() == 1);
    }

    public boolean isTrackRequester(AudioTrack track, long requester) {
        return (long) track.getUserData() == requester;
    }

    public int getTierLevel(long userID) {
        return isBotOwner(userID) ? 3 : db.getTier(userID);
    }

    public boolean canPost(TextChannel channel) {
        return channel.getGuild().getSelfMember().hasPermission(channel, Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS);
    }

    public ConnectionError canConnect(VoiceChannel channel) {
        if (!channel.getGuild().getSelfMember().hasPermission(channel, Permission.VOICE_CONNECT, Permission.VOICE_SPEAK))
            return new ConnectionError("Invalid Channel Permissions", "Your VoiceChannel doesn't allow me to Connect/Speak\n\nPlease grant me the 'Connect' and 'Speak' permissions or move to another channel.");

        if (channel.getUserLimit() != 0 && channel.getMembers().size() >= channel.getUserLimit() && !channel.getGuild().getSelfMember().hasPermission(channel, Permission.VOICE_MOVE_OTHERS))
            return new ConnectionError("VoiceChannel Full", "Your VoiceChannel is full. Raise the user limit or grant me the 'Move Members' permission.");

        return null;
    }

    public boolean checkVoiceChannel(Member m) {
        final AudioManager manager = m.getGuild().getAudioManager();

        Bot.LOG.debug("M-VC OK: " + (m.getVoiceState().inVoiceChannel()) + " | CONNECTED: " + (manager.isConnected() || manager.isAttemptingToConnect()));

        return m.getVoiceState().inVoiceChannel() && (!manager.isAttemptingToConnect() && !manager.isConnected() || manager.getConnectedChannel().getIdLong() == m.getVoiceState().getChannel().getIdLong());
    }

    public enum CONNECT_STATUS {
        NO_CONNECT_SPEAK,
        USER_LIMIT,
        CONNECT
    }

}
