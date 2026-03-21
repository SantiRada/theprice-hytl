package Tenzinn.UI.Pages;

import Tenzinn.StaticSystems;
import Tenzinn.UI.Data.FlairsData;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import Tenzinn.UI.EventData.FlairEventData;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.protocol.packets.interface_.Page;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.Modifier;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.StaticModifier;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;

import com.hypixel.hytale.server.npc.util.InventoryHelper;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.awt.*;
import java.util.Random;
import java.util.ArrayList;

public class FlairUI extends InteractiveCustomUIPage<FlairEventData> {

    private UICommandBuilder uiBuilder;
    private int[][] selectedIndices = new int[3][2];
    private int[][] selectedValues = new int[3][2];
    private final Random random = new Random();

    private static final String[] ARMOR_TIERS = {
            "Copper",
            "Leather_Light",
            "Iron",
            "Bronze",
            "Steel_Ancient",
            "Cloth_Cindercloth",
            "Thorium",
            "Cobalt",
            "Onyxium",
            "Adamantite",
            "Mithril"
    };
    private static final String[] ARMOR_SLOT_IDS = { "Head", "Chest", "Hands", "Legs" };
    private static final int ARMOR_PIECE_COUNT = 4;

    public FlairUI(PlayerRef playerRef) { super(playerRef, CustomPageLifetime.CanDismiss, Tenzinn.UI.EventData.FlairEventData.CODEC); }
    // ------------------------------------------------------ //
    @Override
    public void build(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl UICommandBuilder uiCommandBuilder, @NonNullDecl UIEventBuilder uiEventBuilder, @NonNullDecl Store<EntityStore> store) {
        playerRef.sendMessage(Message.raw("TestUI: Flair").color(Color.yellow));

        uiCommandBuilder.append("Flair.ui");
        uiBuilder = uiCommandBuilder;

        setData();
        setListeners(uiEventBuilder);
        sendUpdate();
    }
    private void setData() {
        ArrayList<String> keys = FlairsData.getKeys();
        int total = keys.size();

        for (int i = 0; i < 3; i++) {
            int randomValue = random.nextInt(10);
            if(randomValue <= 1) randomValue = 2;
            if(randomValue % 2 != 0) randomValue++;

            int halfValue = randomValue / 2;

            selectedValues[i][0] = randomValue;
            selectedValues[i][1] = halfValue;

            int positiveIdx = random.nextInt(total);
            int negativeIdx;
            do { negativeIdx = random.nextInt(total); } while (negativeIdx == positiveIdx);

            selectedIndices[i][0] = positiveIdx;
            selectedIndices[i][1] = negativeIdx;

            String slot = String.format("%02d", i + 1);
            uiBuilder.set("#Positive" + slot + ".TextSpans", Message.raw("+" + randomValue + " " + keys.get(positiveIdx)));
            uiBuilder.set("#Negative" + slot + ".TextSpans", Message.raw("-" + halfValue + " " + keys.get(negativeIdx)));
        }
    }
    private void setListeners(UIEventBuilder uiEventBuilder) {
        uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#Flair01", EventData.of("Action", "1"));
        uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#Flair02", EventData.of("Action", "2"));
        uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#Flair03", EventData.of("Action", "3"));
    }
    // ------------------------------------------------------ //
    @Override
    public void handleDataEvent(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl Store<EntityStore> store, FlairEventData data) {
        String action = data.getAction();
        int flair = Integer.parseInt(action) - 1;

        int positiveIdx = selectedIndices[flair][0];
        int negativeIdx = selectedIndices[flair][1];

        String positiveKey = FlairsData.getKeys().get(positiveIdx);
        String negativeKey = FlairsData.getKeys().get(negativeIdx);

        int positiveVal = selectedValues[flair][0];
        int negativeVal = selectedValues[flair][1];

        playerRef.sendMessage(Message.raw("Seleccionaste: +" + positiveVal + " " + positiveKey + " | -" + negativeVal + " " + negativeKey).color(Color.green));

        // 1. Crear partícula en la posición del Player
        Vector3d posPlayer = playerRef.getTransform().getPosition();
        StaticSystems.createParticleInPosition(posPlayer, "flairs", playerRef);

        // 2. Sumar el valor positivo // 3. Restar el valor negativo
        applyFlair(ref, store, positiveIdx, negativeIdx, positiveVal, negativeVal);

        // 4. Quitar la page
        Player player = store.getComponent(ref, Player.getComponentType());
        assert player != null;
        player.getPageManager().setPage(ref, store, Page.None);
    }
    private void applyFlair(Ref<EntityStore> playerRef, Store<EntityStore> store, int positiveIdx, int negativeIdx, int positiveVal, int negativeVal) {
        String positiveKey = FlairsData.getKeys().get(positiveIdx);
        String negativeKey  = FlairsData.getKeys().get(negativeIdx);

        EntityStore entityStore = store.getExternalData();
        World world = entityStore.getWorld();

        Player player = store.getComponent(playerRef, Player.getComponentType());
        assert player != null;

        world.execute(() -> {
            EntityStatMap statMap = store.getComponent(playerRef, EntityStatMap.getComponentType());
            if (statMap == null) return;

            applyStatChange(statMap, positiveKey,  positiveVal, true, player);
            applyStatChange(statMap, negativeKey,  negativeVal, false, player);

            player.sendMessage(Message.raw(positiveKey + ": " + positiveVal));
            player.sendMessage(Message.raw(negativeKey + ": " + negativeVal));
        });
    }
    private void applyStatChange(EntityStatMap statMap, String key, float amount, boolean isPositive, Player player) {
        float delta = isPositive ? amount : -amount;

        switch (key.toLowerCase()) {
            case "health" -> {
                int statIdx = DefaultEntityStatTypes.getHealth();

                applyAccumulatedModifier(statMap, statIdx, "flair_health_max", delta);

                statMap.subtractStatValue(EntityStatMap.Predictable.ALL, statIdx, 0.001f);
                statMap.maximizeStatValue(statIdx);

                float afterVal = statMap.get(statIdx).get();
                statMap.setStatValue(EntityStatMap.Predictable.ALL, statIdx, afterVal);

                System.out.println("[FLAIR] HEALTH DESPUÉS → val=" + afterVal + " max=" + statMap.get(statIdx).getMax());
            }
            case "stamina" -> {
                int statIdx = DefaultEntityStatTypes.getStamina();

                applyAccumulatedModifier(statMap, statIdx, "flair_stamina_max", delta);

                statMap.subtractStatValue(EntityStatMap.Predictable.ALL, statIdx, 0.001f);
                statMap.maximizeStatValue(statIdx);

                float afterVal = statMap.get(statIdx).get();
                statMap.setStatValue(EntityStatMap.Predictable.ALL, statIdx, afterVal);

                System.out.println("[FLAIR] STAMINA DESPUÉS → val=" + afterVal + " max=" + statMap.get(statIdx).getMax());
            }
            case "defense" -> {
                applyArmorFlair(player, delta > 0, (int)amount);
            }
        }
    }
    private void applyAccumulatedModifier(EntityStatMap statMap, int statIdx, String modifierKey, float delta) {
        Modifier existing = statMap.getModifier(statIdx, modifierKey);
        float currentBonus = 0f;

        if (existing instanceof StaticModifier staticMod) { currentBonus = staticMod.getAmount(); }
        float newBonus = currentBonus + delta;

        StaticModifier newModifier = new StaticModifier(Modifier.ModifierTarget.MAX, StaticModifier.CalculationType.ADDITIVE, newBonus);

        statMap.removeModifier(statIdx, modifierKey);
        statMap.putModifier(statIdx, modifierKey, newModifier);
    }
    // ------------------------------------------------------ //
    private void applyArmorFlair(Player player, boolean isPositive, int amount) {
        Inventory inventory = player.getInventory();
        ItemContainer armorContainer = inventory.getArmor();

        int[] tiers = new int[ARMOR_PIECE_COUNT];
        for (int i = 0; i < ARMOR_PIECE_COUNT; i++) {
            tiers[i] = getArmorTierAtSlot(armorContainer, (short) i);
        }

        boolean hitLimit = false;
        for (int step = 0; step < amount; step++) {
            boolean applied = isPositive ? applyOneUpgrade(tiers) : applyOneDowngrade(tiers);
            if (!applied) { hitLimit = true; break; }
        }

        removeAllArmor(armorContainer);
        equipByTiers(inventory, tiers);

        if (hitLimit) {
            if (isPositive) player.sendMessage(Message.raw("¡Ya tienes la armadura máxima!").color(Color.yellow));
            else player.sendMessage(Message.raw("No tienes armadura para reducir.").color(Color.red));
        }

        StringBuilder sb = new StringBuilder(isPositive ? "▲ Armadura: " : "▼ Armadura: ");
        for (int i = 0; i < ARMOR_PIECE_COUNT; i++) {
            sb.append(ARMOR_SLOT_IDS[i]).append("=").append(tiers[i] == -1 ? "Ninguna" : ARMOR_TIERS[tiers[i]]);
            if (i < ARMOR_PIECE_COUNT - 1) sb.append(" | ");
        }
        player.sendMessage(Message.raw(sb.toString()).color(isPositive ? Color.green : Color.orange));
    }
    private boolean applyOneUpgrade(int[] tiers) {
        int maxTier = ARMOR_TIERS.length - 1;
        boolean allMax = true;
        for (int t : tiers) { if (t < maxTier) { allMax = false; break; } }
        if (allMax) return false;

        int minTier = Integer.MAX_VALUE;
        for (int t : tiers) { if (t < minTier) minTier = t; }

        for (int i = 0; i < ARMOR_PIECE_COUNT; i++) {
            if (tiers[i] == minTier) { tiers[i]++; return true; }
        }
        return false;
    }
    private boolean applyOneDowngrade(int[] tiers) {
        boolean allEmpty = true;
        for (int t : tiers) { if (t != -1) { allEmpty = false; break; } }
        if (allEmpty) return false;

        int maxTier = Integer.MIN_VALUE;
        for (int t : tiers) { if (t > maxTier) maxTier = t; }

        for (int i = 0; i < ARMOR_PIECE_COUNT; i++) {
            if (tiers[i] == maxTier) { tiers[i]--; return true; }
        }
        return false;
    }
    private int getArmorTierAtSlot(ItemContainer armorContainer, short slot) {
        ItemStack item = armorContainer.getItemStack(slot);
        if (item == null || item.isEmpty()) return -1;
        String itemId = item.getItemId();
        for (int t = 0; t < ARMOR_TIERS.length; t++) {
            if (itemId.contains(ARMOR_TIERS[t])) return t;
        }
        return -1;
    }
    private void removeAllArmor(ItemContainer armorContainer) {
        for (short slot = 0; slot < armorContainer.getCapacity(); slot++) {
            ItemStack item = armorContainer.getItemStack(slot);
            if (item != null && !item.isEmpty()) armorContainer.removeItemStackFromSlot(slot);
        }
    }
    private void equipByTiers(Inventory inventory, int[] tiers) {
        for (int i = 0; i < ARMOR_PIECE_COUNT; i++) {
            if (tiers[i] >= 0) {
                String itemId = "Armor_" + ARMOR_TIERS[tiers[i]] + "_" + ARMOR_SLOT_IDS[i];
                InventoryHelper.useArmor(inventory.getArmor(), itemId);
            }
        }
    }
    // ------------------------------------------------------ //
}