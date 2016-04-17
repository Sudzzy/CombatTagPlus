package net.minelink.ctplus;

import net.minelink.ctplus.compat.api.NpcPlayerHelper;
import org.bukkit.entity.Player;

import java.util.UUID;

public final class Tag {

    private UUID victimId;

    private String victimName;

    private UUID attackerId;

    private String attackerName;

    Tag(NpcPlayerHelper helper, Player victim, Player attacker) {
        // Determine victim identity
        if (victim != null) {
            if (helper.isNpc(victim)) {
                this.victimId = helper.getIdentity(victim).getId();
                this.victimName = helper.getIdentity(victim).getName();
            } else {
                this.victimId = victim.getUniqueId();
                this.victimName = victim.getName();
            }
        }

        // Determine attacker identity
        if (attacker != null) {
            if (helper.isNpc(attacker)) {
                this.attackerId = helper.getIdentity(attacker).getId();
                this.attackerName = helper.getIdentity(attacker).getName();
            } else {
                this.attackerId = attacker.getUniqueId();
                this.attackerName = attacker.getName();
            }
        }
    }

    public UUID getVictimId() {
        return victimId;
    }

    public String getVictimName() {
        return victimName;
    }

    public UUID getAttackerId() {
        return attackerId;
    }

    public String getAttackerName() {
        return attackerName;
    }

}
