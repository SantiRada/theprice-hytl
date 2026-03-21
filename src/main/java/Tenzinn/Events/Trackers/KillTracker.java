package Tenzinn.Events.Trackers;

import Tenzinn.ThePrice;
import Tenzinn.UI.Pages.FlairUI;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.SystemGroup;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.NameMatching;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageModule;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathSystems;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KillTracker extends DeathSystems.OnDeathSystem {

    private int killsRequired = 3;
    private final Map<UUID, Integer> killMap = new HashMap<>();
    private ThePrice main;

    public KillTracker(ThePrice main) { this.main = main; }

    @Nonnull @Override
    public Query<EntityStore> getQuery() { return Query.and(Query.not(Player.getComponentType())); }

    @Override
    public SystemGroup<EntityStore> getGroup() { return DamageModule.get().getInspectDamageGroup(); }

    @Override
    public void onComponentAdded(@Nonnull Ref ref, @Nonnull DeathComponent component, @Nonnull Store store, @Nonnull CommandBuffer commandBuffer) {
        Damage lastDamage = component.getDeathInfo();
        if (lastDamage == null) return;

        Damage.Source source = lastDamage.getSource();
        if (!(source instanceof Damage.EntitySource entitySource)) return;

        Ref<EntityStore> attackerRef = entitySource.getRef();
        Player attackerPlayer = (Player) store.getComponent(attackerRef, Player.getComponentType());
        if (attackerPlayer == null) return;

        UUID playerUUID = Universe.get().getPlayerByUsername(attackerPlayer.getDisplayName(), NameMatching.EXACT).getUuid();
        int kills = killMap.getOrDefault(playerUUID, 0) + 1;
        killMap.put(playerUUID, kills);

        if(isRoomCleared()) { main.recreateRoom(); }
    }
    public boolean isRoomCleared() {
        int total = killMap.values().stream().mapToInt(Integer::intValue).sum();
        return killsRequired > 0 && total >= killsRequired;
    }
    public void setKillsRequired(int amount) {
        killMap.clear();
        this.killsRequired = amount;
    }
}