package jukebot.utils;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public interface Command {

    void execute(final GuildMessageReceivedEvent e, final String query);

    default CommandProperties properties() {
        return this.getClass().getAnnotation(CommandProperties.class);
    }

    default String name() {
        return this.getClass().getSimpleName();
    }

}