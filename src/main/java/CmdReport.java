import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CmdReport implements CommandExecutor {

    public static Map<String, List<String>> reportList = new HashMap<>();
    public static Map<String, String> reporting = new HashMap<>();

    public CmdReport() {}

    public static CommandSpec cmdReport = CommandSpec.builder()
            .description(Text.of("Report command"))
            .permission("chatcontrol.report")
            .arguments(GenericArguments.onlyOne(GenericArguments.string(Text.of("playerName"))))
            .executor(new CmdReport())
            .build();

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Player player = (Player) src;
        if (args.<String>getOne("playerName").isPresent()) {
            String playerReportedName = args.<String>getOne("playerName").get();
            System.out.println("reported player " + playerReportedName);
            ItemStack wool = ItemStack.builder()
                    .itemType(ItemTypes.WOOL)
                    .quantity(1)
                    .build();
            Inventory report = Inventory.builder()
                    .of(InventoryArchetypes.ANVIL)
                    .build(Sponge.getPluginManager().getPlugin("spongechatcontrol").get().getInstance().get());
            report.set(wool);
            reporting.put(player.getName(), playerReportedName);
            player.openInventory(report);
            if (!reportList.containsKey(playerReportedName)) {
                List<String> noReason = new ArrayList<>();
                noReason.add(player.getName() + "-");
                reportList.put(playerReportedName, noReason);
                System.out.println("creating entry for player");
            } else {
                boolean found = false;
                for (int i = 0; i < reportList.get(playerReportedName).size(); i++) {
                    String[] parts = reportList.get(playerReportedName).get(i).split("-");
                    if (parts[0].equals(player.getName())) {
                        found = true;
                        if (!(reportList.get(playerReportedName).get(i).charAt(reportList.get(playerReportedName).get(i).length() - 1) == '-'))
                            reportList.get(playerReportedName).set(i, reportList.get(playerReportedName).get(i) + "-");
                        System.out.println("adding - to player report list: " + reportList.get(playerReportedName).get(i));
                    }
                }
                if (!found) {
                    reportList.get(playerReportedName).add(player.getName() + "-");
                    System.out.println("adding new player entry to reported player");
                }
            }
        }
        return CommandResult.success();
    }
}
