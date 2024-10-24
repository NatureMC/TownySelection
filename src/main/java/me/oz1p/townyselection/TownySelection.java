package me.oz1p.townyselection;

import com.palmergames.bukkit.towny.TownyCommandAddonAPI;
import com.palmergames.bukkit.towny.object.AddonCommand;
import me.oz1p.townyselection.commands.NationSelectionCommand;
import me.oz1p.townyselection.commands.TownSelectionCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class TownySelection extends JavaPlugin {
    private AddonCommand townSelection;
    private AddonCommand nationSelection;
    private static TownySelection instance;
    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        this.townSelection = new AddonCommand(TownyCommandAddonAPI.CommandType.TOWN, "selection", new TownSelectionCommand());
        this.nationSelection = new AddonCommand(TownyCommandAddonAPI.CommandType.NATION, "selection", new NationSelectionCommand());
        TownyCommandAddonAPI.addSubCommand(townSelection);
        TownyCommandAddonAPI.addSubCommand(nationSelection);
    }

    public AddonCommand getTownSelection() {
        return townSelection;
    }

    public AddonCommand getNationSelection() {
        return nationSelection;
    }

    public static TownySelection getInstance() {
        return instance;
    }

    @Override
    public void onDisable() {
        TownyCommandAddonAPI.removeSubCommand(townSelection);
        TownyCommandAddonAPI.removeSubCommand(nationSelection);
    }
}
