package net.minelink.ctplus.task;

import net.minelink.ctplus.CombatTagPlus;
import net.minelink.ctplus.Npc;

public class NpcDespawnTask implements Runnable {

    private final CombatTagPlus plugin;

    private final Npc npc;

    private int taskId;

    public NpcDespawnTask(CombatTagPlus plugin, Npc npc) {
        this.plugin = plugin;
        this.npc = npc;
    }

    public Npc getNpc() {
        return npc;
    }

    public void start(int ticks) {
        taskId = plugin.getServer().getScheduler().runTaskLater(plugin, this, ticks).getTaskId();
    }

    public void stop() {
        plugin.getServer().getScheduler().cancelTask(taskId);
    }

    @Override
    public void run() {
        plugin.getNpcManager().despawn(npc);
    }

}
