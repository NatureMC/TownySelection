package me.oz1p.townyselection.utils;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class CooldownUtil {
    private static final HashMap<UUID, Map.Entry<CooldownType, Long>> cooldowns = new HashMap<>();
    public static void addCooldown(UUID uuid, CooldownType type, int minutes) {
        long cooldown = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(minutes);
        cooldowns.put(uuid, new AbstractMap.SimpleEntry<>(type, cooldown));
    }
    public static boolean isCooldown(UUID uuid, CooldownType type) {
        if (!cooldowns.containsKey(uuid)) {
            return false;
        }
        Map.Entry<CooldownType, Long> entry = cooldowns.get(uuid);
        if (entry.getKey() != type) {
            return false;
        }
        long cooldownEnd = entry.getValue();
        if (System.currentTimeMillis() > cooldownEnd) {
            cooldowns.remove(uuid);
            return false;
        }
        return true;
    }
    public static long getMinutesLeft(UUID uuid, CooldownType type) {
        if(!isCooldown(uuid, type)) {
            return 0;
        }
        long cooldownEnd = cooldowns.get(uuid).getValue();
        long timeLeft = cooldownEnd - System.currentTimeMillis();
        return TimeUnit.MILLISECONDS.toMinutes(timeLeft);
    }
}
