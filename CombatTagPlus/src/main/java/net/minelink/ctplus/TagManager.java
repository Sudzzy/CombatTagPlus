package net.minelink.ctplus;

import net.minelink.ctplus.compat.api.NpcPlayerHelper;
import net.minelink.ctplus.event.CombatUntagEvent;
import net.minelink.ctplus.event.CombatUntagReason;
import net.minelink.ctplus.event.PlayerCombatTagEvent;
import net.minelink.ctplus.task.TagExpireTask;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class TagManager {

    private final CombatTagPlus plugin;

    private final Map<UUID, Tag> tags = new HashMap<>();

    private final Map<UUID, TagExpireTask> expireTasks = new HashMap<>();

    TagManager(CombatTagPlus plugin) {
        this.plugin = plugin;
    }

    public void tag(Player victim, Player attacker) {
        NpcPlayerHelper helper = plugin.getNpcPlayerHelper();

        // Determine victim identity
        UUID victimId = null;
        if (victim != null) {
            if (victim.getHealth() <= 0 || victim.isDead()) {
                victim = null;
            } else if (helper.isNpc(victim)) {
                victimId = helper.getIdentity(victim).getId();
            } else if (!victim.hasPermission("ctplus.bypass.tag")) {
                victimId = victim.getUniqueId();
            } else {
                victim = null;
            }
        }

        // Determine attacker identity
        UUID attackerId = null;
        if (attacker != null) {
            if (attacker.getHealth() <= 0 || attacker.isDead() || attacker == victim) {
                attacker = null;
            } else if (helper.isNpc(attacker)) {
                attackerId = helper.getIdentity(attacker).getId();
            } else if (!attacker.hasPermission("ctplus.bypass.tag")) {
                attackerId = attacker.getUniqueId();
            } else {
                attacker = null;
            }
        }

        // Do nothing if both victim and attacker are blank
        if (victim == null && attacker == null) return;

        // Call tag event
        int tagDuration = plugin.getSettings().getTagDuration();
        PlayerCombatTagEvent event = new PlayerCombatTagEvent(victim, attacker, tagDuration);
        plugin.getServer().getPluginManager().callEvent(event);

        // Do nothing if event was cancelled
        if (event.isCancelled()) return;

        // Create new tag
        Tag tag = new Tag(helper, victim, attacker);

        // Add victim to tagged players
        if (victim != null) {
            tags.put(victimId, tag);
            expireTasks.put(victimId, new TagExpireTask(plugin, victimId));
        }

        // Add attacker to tagged players
        if (attacker != null) {
            tags.put(attackerId, tag);
            expireTasks.put(attackerId, new TagExpireTask(plugin, attackerId));
        }
    }

    public boolean untag(UUID playerId) {
        return untag(playerId, CombatUntagReason.UNKNOWN);
    }

    public boolean untag(UUID playerId, CombatUntagReason reason) {
        // Do nothing if player does not currently have a tag.
        if (!tags.containsKey(playerId)) {
            return false;
        }

        // Fire untag event and remove the tag.
        CombatUntagEvent untagEvent = new CombatUntagEvent(playerId, reason);
        plugin.getServer().getPluginManager().callEvent(untagEvent);
        tags.remove(playerId);

        // Stop the tag expire task if still running.
        TagExpireTask task = expireTasks.remove(playerId);
        if (task != null && task.isRunning()) {
            task.stop();
        }
        return true;
    }

    public Tag getTag(UUID playerId) {
        return getTag(playerId, false);
    }

    public Tag getTag(UUID playerId, boolean includeHidden) {
        Tag tag = tags.get(playerId);

        if (tag == null || !includeHidden && plugin.getSettings().onlyTagAttacker() && tag.getVictimId() == playerId) {
            return null;
        }

        return tag;
    }

    public boolean isTagged(UUID playerId) {
        Tag tag = tags.get(playerId);
        boolean tagged = tag != null;

        if (tagged && plugin.getSettings().onlyTagAttacker()) {
            return !tag.getVictimId().equals(playerId);
        }

        return tagged;
    }

    public TagExpireTask getTagExpireTask(UUID playerId) {
        return expireTasks.get(playerId);
    }

}
