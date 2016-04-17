package net.minelink.ctplus.task;

import net.minelink.ctplus.CombatTagPlus;

/**
 * Tracks current server tick time relative to when the task starts.
 */
public class TickTask implements Runnable {

    private int currentTick;

    public TickTask(CombatTagPlus plugin) {
        plugin.getServer().getScheduler().runTaskTimer(plugin, this, 1, 1);
    }

    /**
     * Gets the current relative tick time.
     *
     * @return the current tick.
     */
    public int getCurrentTick() {
        return currentTick;
    }

    @Override
    public void run() {
        currentTick++;
    }

}
