package net.minelink.ctplus.task;

import net.minelink.ctplus.CombatTagPlus;
import net.minelink.ctplus.event.CombatUntagReason;

import java.util.UUID;

/**
 * Tick based task used for expiring old combat tags.
 */
public class TagExpireTask implements Runnable {

    private final CombatTagPlus plugin;

    private final UUID playerId;

    private boolean running;

    private int taskId;

    private int expireTick;

    public TagExpireTask(CombatTagPlus plugin, UUID playerId) {
        this.plugin = plugin;
        this.playerId = playerId;
    }

    /**
     * Gets if the task is currently running.
     *
     * @return true if the task is currently running.
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Gets the relative tick timestamp found in {@link TickTask} showing when
     * this task execute.
     *
     * @return the tick this task executes.
     */
    public int getExpireTick() {
        return expireTick;
    }

    /**
     * Gets the duration in ticks until this task will execute.
     *
     * @return the number of ticks.
     */
    public int getRemainingTicks() {
        return getExpireTick() - plugin.getTickTask().getCurrentTick();
    }

    /**
     * Starts this task to be executed in a specific number of ticks.
     *
     * @param ticks the number of ticks from now to expire this tag.
     */
    public void start(int ticks) {
        // Do not allow this task to be ran more than once.
        if (isRunning()) {
            throw new IllegalStateException("Tag expire task is already running");
        }

        // Start the tag expire task and update its state.
        taskId = plugin.getServer().getScheduler().runTaskLater(plugin, this, ticks).getTaskId();
        expireTick = plugin.getTickTask().getCurrentTick() + ticks;
        running = true;
    }

    /**
     * Stops this task without expiring the tag.
     */
    public void stop() {
        // Do not allow this task to be stopped if not already running.
        if (!isRunning()) {
            throw new IllegalStateException("No tag expire task was running");
        }

        // Stop the tag expire task and update its state.
        plugin.getServer().getScheduler().cancelTask(taskId);
        running = false;
    }

    /**
     * Updates this task if running with a new expiration date.
     *
     * @param ticks new duration in ticks when the tag should expire.
     */
    public void update(int ticks) {
        stop();
        start(ticks);
    }

    @Override
    public void run() {
        // Update the task state.
        running = false;

        // Remove the players tag with expired reason.
        plugin.getTagManager().untag(playerId, CombatUntagReason.EXPIRED);
    }

}
