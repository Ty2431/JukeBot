package jukebot.utils;

import net.dv8tion.jda.core.managers.AudioManager;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Helpers {

    public static void ScheduleClose(AudioManager manager) {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.schedule(() -> {
            manager.closeAudioConnection();
            executor.shutdown();
        }, 1, TimeUnit.SECONDS);
    }

}
