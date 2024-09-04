package me.hcfalerts.practice.utilities.string;

import me.hcfalerts.practice.HCFPractice;
import me.hcfalerts.practice.utilities.TaskUtil;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Animation {

    public static String title, footer;

    public static void init() {
        List<String> titles = HCFPractice.get().getScoreboardConfig().getStringList("TITLE");
        AtomicInteger p = new AtomicInteger();
        TaskUtil.runTimerAsync(() -> {
            if (p.get() == titles.size()) p.set(0);
            title = titles.get(p.getAndIncrement());
        }, 0L, (long) (HCFPractice.get().getScoreboardConfig().getDouble("TITLE_TASK") * 20L));

        if (HCFPractice.get().getScoreboardConfig().getBoolean("FOOTER_ENABLED")) {
            List<String> footers = HCFPractice.get().getScoreboardConfig().getStringList("FOOTER");
            AtomicInteger b = new AtomicInteger();
            TaskUtil.runTimerAsync(() -> {
                if (b.get() == footers.size()) b.set(0);
                footer = footers.get(b.getAndIncrement());
            }, 0L, (long) (HCFPractice.get().getScoreboardConfig().getDouble("FOOTER_TASK") * 20L));
        }
    }

    public static String getScoreboardTitle() {
        return title;
    }

    public static String getScoreboardFooter() {
        return footer;
    }

}
