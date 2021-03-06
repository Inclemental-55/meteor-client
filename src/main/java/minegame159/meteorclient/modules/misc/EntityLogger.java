/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2020 Meteor Development.
 */

package minegame159.meteorclient.modules.misc;

import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import minegame159.meteorclient.events.entity.EntityAddedEvent;
import minegame159.meteorclient.friends.FriendManager;
import minegame159.meteorclient.modules.Category;
import minegame159.meteorclient.modules.Module;
import minegame159.meteorclient.settings.BoolSetting;
import minegame159.meteorclient.settings.EntityTypeListSetting;
import minegame159.meteorclient.settings.Setting;
import minegame159.meteorclient.settings.SettingGroup;
import minegame159.meteorclient.utils.player.Chat;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;

public class EntityLogger extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Object2BooleanMap<EntityType<?>>> entities = sgGeneral.add(new EntityTypeListSetting.Builder()
            .name("entites")
            .description("Select specific entities.")
            .defaultValue(new Object2BooleanOpenHashMap<>(0))
            .build()
    );

    private final Setting<Boolean> playerNames = sgGeneral.add(new BoolSetting.Builder()
            .name("player-names")
            .description("Shows the player's name.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> friends = sgGeneral.add(new BoolSetting.Builder()
            .name("friends")
            .description("Logs friends.")
            .defaultValue(true)
            .build()
    );

    public EntityLogger() {
        super(Category.Misc, "entity-logger", "Sends a client-side chat alert if a specified entity appears in render distance.");
    }

    @EventHandler
    private final Listener<EntityAddedEvent> onEntityAdded = new Listener<>(event -> {
        if (event.entity.getUuid().equals(mc.player.getUuid())) return;

        if (entities.get().getBoolean(event.entity.getType())) {
            if (event.entity instanceof PlayerEntity) {
                if (!friends.get() && FriendManager.INSTANCE.get((PlayerEntity) event.entity) != null) return;
            }

            String name;
            if (playerNames.get() && event.entity instanceof PlayerEntity) name = ((PlayerEntity) event.entity).getGameProfile().getName() + " (Player)";
            else name = event.entity.getType().getName().getString();

            Chat.info(this, "(highlight)%s (default)has spawned at (highlight)%.0f(default), (highlight)%.0f(default), (highlight)%.0f(default).", name, event.entity.getX(), event.entity.getY(), event.entity.getZ());
        }
    });
}
