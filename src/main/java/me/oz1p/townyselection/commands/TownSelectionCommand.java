package me.oz1p.townyselection.commands;


import com.palmergames.adventure.platform.bukkit.BukkitAudiences;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.Translatable;
import com.palmergames.bukkit.towny.object.Translation;
import me.oz1p.townyselection.TownySelection;
import me.oz1p.townyselection.utils.CooldownType;
import me.oz1p.townyselection.utils.CooldownUtil;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class TownSelectionCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        String townyPrefix = Translation.of("default_towny_prefix");
        if(!(commandSender instanceof Player)){
            commandSender.sendMessage("§cВы консоль!");
            return true;
        }
        Player player = (Player) commandSender;
        UUID uuid = player.getUniqueId();

        Resident resident = TownyAPI.getInstance().getResident(player);
        if(!resident.hasTown()){
            player.sendMessage(townyPrefix + "§cВы не в городе!");
            return true;
        }
        Town town = resident.getTownOrNull();
        String townName = town.getName().replace("_", " ");
        String townPrefix = Translation.of("default_town_prefix", townName);
        if(CooldownUtil.isCooldown(uuid, CooldownType.TOWN)){
            player.sendMessage(townPrefix+ String.format("§cПопробуй через %s минут.", CooldownUtil.getMinutesLeft(uuid, CooldownType.TOWN)));
            return true;
        }
        if (!(resident.hasTownRank("заместитель_мэра") ||
                resident.hasTownRank("депутат") ||
                resident.hasTownRank("управляющий") ||
                resident.isMayor())) {
            player.sendMessage(townPrefix + "§cУ вас нету прав!");
            return true;
        }

        if(!town.isOpen()){
            player.sendMessage(townPrefix + "§cВаш город не может принимать жителей!");
            player.sendMessage(townPrefix + "§cПропишите: /t toggle open");
            return true;
        }
        Component inviteButton = Component.newline().append(Component.text("             §8➥ ")
                .append(Component.text("§f[Присоединиться]"))
                .clickEvent(ClickEvent.runCommand("/t join " + town.getName())));
        Component broadcastMessage = Component.text("§7Город " + townName + " ищет жителей!")
                .append(inviteButton);
       for(Audience p : Bukkit.getOnlinePlayers()){
           p.sendMessage(LegacyComponentSerializer.legacySection().deserialize(townyPrefix).append(broadcastMessage));
       }
        CooldownUtil.addCooldown(uuid, CooldownType.TOWN, 15);

        return true;
    }
}
