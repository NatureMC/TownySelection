package me.oz1p.townyselection.commands;


import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Translatable;
import com.palmergames.bukkit.towny.object.Translation;
import me.oz1p.townyselection.TownySelection;
import me.oz1p.townyselection.utils.CooldownType;
import me.oz1p.townyselection.utils.CooldownUtil;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class NationSelectionCommand implements CommandExecutor
{
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
        if(!resident.hasNation()){
            player.sendMessage(townyPrefix + "§cВы не в нации!");
            return true;
        }

        Nation nation = resident.getNationOrNull();
        String nationName = nation.getName().replace("_", " ");
        String nationPrefix = Translation.of("default_nation_prefix", nationName);
        if(CooldownUtil.isCooldown(uuid, CooldownType.NATION)){
            player.sendMessage(nationPrefix+ String.format("§cПопробуй через %s минут.", CooldownUtil.getMinutesLeft(uuid, CooldownType.NATION)));
            return true;
        }
        if (!(resident.hasNationRank("заместитель") ||
                resident.hasNationRank("дипломат") ||
                resident.hasNationRank("вербовщик") ||
                resident.isKing())) {
            player.sendMessage(nationPrefix + "§cУ вас нету прав!");
            return true;
        }
        if(!nation.isOpen()){
            player.sendMessage(nationPrefix + "§cВаша нация не может принимать города!");
            player.sendMessage(nationPrefix + "§cПропишите: /n toggle open");
            return true;
        }

        Component inviteButton = Component.newline().append(Component.text("             §8➥ ")
                .append(Component.text("§f[Присоединиться]"))
                .clickEvent(ClickEvent.runCommand("/n join " + nation.getName())));
        Component broadcastMessage = Component.text("§7Нация " + nationName + " ищет города!")
                .append(inviteButton);
        for(Audience p : Bukkit.getOnlinePlayers()){
            p.sendMessage(LegacyComponentSerializer.legacySection().deserialize(townyPrefix).append(broadcastMessage));
        }
CooldownUtil.addCooldown(uuid, CooldownType.NATION, 15);
        return true;
    }
}
