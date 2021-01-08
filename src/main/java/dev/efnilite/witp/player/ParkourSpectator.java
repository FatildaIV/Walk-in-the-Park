package dev.efnilite.witp.player;

import dev.efnilite.witp.WITP;
import dev.efnilite.witp.generator.ParkourGenerator;
import dev.efnilite.witp.util.Verbose;
import org.bukkit.GameMode;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ParkourSpectator extends ParkourUser {

    protected final ParkourPlayer watching;
    protected final ParkourGenerator watchingGenerator;

    public ParkourSpectator(@NotNull ParkourUser player, @NotNull ParkourPlayer watching) {
        super(player.getPlayer());
        Verbose.verbose("New ParkourSpectator init " + this.player.getName());

        if (player instanceof ParkourPlayer) {
            try {
                ParkourPlayer.unregister((ParkourPlayer) player, false);
            } catch (IOException ex) {
                ex.printStackTrace();
                Verbose.error("Error while trying to unregister");
            }
        } else if (player instanceof ParkourSpectator) {
            ParkourSpectator spectator = (ParkourSpectator) player;
            spectator.watching.removeSpectators(spectator);
        }
        users.put(this.player.getName(), this);

        this.watching = watching;
        this.watchingGenerator = watching.getGenerator();
        this.player.setGameMode(GameMode.SPECTATOR);
        double offset = watching.getGenerator().borderOffset * 2;
        WITP.getVersionManager().setWorldBorder(this.player, WITP.getDivider().getPoint(watching).getEstimatedCenter(offset), offset);
        watching.addSpectator(this);
        this.player.teleport(watching.getPlayer().getLocation());
        sendTranslated("spectator");
    }

    public void checkDistance() {
        if (watching.getPlayer().getLocation().distance(player.getLocation()) > 30) {
            player.teleport(watching.getPlayer().getLocation());
        }
    }

    @Override
    protected void updateScoreboard() {
        board.updateTitle(ParkourGenerator.Configurable.SCOREBOARD_TITLE);
        List<String> list = new ArrayList<>();
        List<String> lines = ParkourGenerator.Configurable.SCOREBOARD_LINES;
        if (lines == null) {
            Verbose.error("Scoreboard lines are null! Check your config!");
            return;
        }
        for (String s : lines) {
            list.add(s
                    .replaceAll("%score%", Integer.toString(watchingGenerator.score))
                    .replaceAll("%time%", watchingGenerator.time));
        }

        board.updateLines(list);
    }
}