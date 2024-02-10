package com.iridium.iridiumfactions.managers;

import com.iridium.iridiumcore.dependencies.nbtapi.NBTCompound;
import com.iridium.iridiumcore.dependencies.nbtapi.NBTItem;
import com.iridium.iridiumcore.dependencies.paperlib.PaperLib;
import com.iridium.iridiumcore.dependencies.xseries.XMaterial;
import com.iridium.iridiumcore.utils.ItemStackUtils;
import com.iridium.iridiumcore.utils.Placeholder;
import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumfactions.FactionType;
import com.iridium.iridiumfactions.IridiumFactions;
import com.iridium.iridiumfactions.RelationshipType;
import com.iridium.iridiumfactions.api.FactionCreateEvent;
import com.iridium.iridiumfactions.api.FactionDeleteEvent;
import com.iridium.iridiumfactions.database.Faction;
import com.iridium.iridiumfactions.database.FactionClaim;
import com.iridium.iridiumfactions.database.User;
import com.iridium.iridiumfactions.utils.LocationUtils;
import com.iridium.iridiumteams.PermissionType;
import com.iridium.iridiumteams.Rank;
import com.iridium.iridiumteams.Setting;
import com.iridium.iridiumteams.database.*;
import com.iridium.iridiumteams.managers.TeamManager;
import com.iridium.iridiumteams.missions.Mission;
import com.iridium.iridiumteams.missions.MissionData;
import com.iridium.iridiumteams.missions.MissionType;
import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class FactionManager extends TeamManager<Faction, User> {

    public FactionManager() {
        super(IridiumFactions.getInstance());
    }

    public Faction getFactionViaID(int id) {
        switch (id) {
            case -1:
                return new Faction(FactionType.WILDERNESS);
            case -2:
                return new Faction(FactionType.WARZONE);
            case -3:
                return new Faction(FactionType.SAFEZONE);
            default:
                return IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().getFaction(id).orElse(getFactionViaID(-1));
        }
    }

    @Override
    public Optional<Faction> getTeamViaID(int id) {
        return IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().getFaction(id);
    }

    @Override
    public Optional<Faction> getTeamViaName(String name) {
        return IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().getFaction(name);
    }

    @Override
    public Optional<Faction> getTeamViaLocation(Location location) {
        return Optional.of(getFactionViaLocation(location));
    }

    public Faction getFactionViaLocation(Location location) {
        return getFactionViaChunk(location.getChunk());
    }

    @NotNull
    public Faction getFactionViaChunk(Chunk chunk) {
        return getFactionViaChunk(chunk.getWorld(), chunk.getX(), chunk.getZ());
    }

    @NotNull
    public Faction getFactionViaChunk(World world, int x, int z) {
        return getFactionViaID(getFactionClaimViaChunk(world, x, z).map(FactionClaim::getTeamID).orElse(-1));
    }

    public Optional<FactionClaim> getFactionClaimViaChunk(Chunk chunk) {
        return getFactionClaimViaChunk(chunk.getWorld(), chunk.getX(), chunk.getZ());
    }

    public Optional<FactionClaim> getFactionClaimViaChunk(World world, int x, int z) {
        return IridiumFactions.getInstance().getDatabaseManager().getFactionClaimsTableManager()
                .getEntry(new FactionClaim(new Faction(""), world.getName(), x, z));
    }

    @Override
    public Optional<Faction> getTeamViaNameOrPlayer(String name) {
        if (name == null || name.equals("")) return Optional.empty();
        OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(name);
        Faction team = IridiumFactions.getInstance().getUserManager().getUser(targetPlayer).getFaction();
        if (team.getFactionType() != FactionType.PLAYER_FACTION) {
            return getTeamViaName(name);
        }
        return Optional.of(team);
    }

    @Override
    public void sendTeamTitle(Player player, Faction faction) {
        User user = IridiumFactions.getInstance().getUserManager().getUser(player);
        List<Placeholder> placeholders = IridiumFactions.getInstance().getTeamsPlaceholderBuilder().getPlaceholders(faction);
        placeholders.add(new Placeholder("faction_relationship_color", getFactionRelationship(user, faction).getColor()));
        String top = StringUtils.processMultiplePlaceholders(IridiumFactions.getInstance().getConfiguration().factionTitleTop, placeholders);
        String bottom = StringUtils.processMultiplePlaceholders(IridiumFactions.getInstance().getConfiguration().factionTitleBottom, placeholders);
        IridiumFactions.getInstance().getNms().sendTitle(player, StringUtils.color(top), StringUtils.color(bottom), 20, 40, 20);
    }

    @Override
    public List<Faction> getTeams() {
        return IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().getEntries();
    }

    @Override
    public CompletableFuture<Faction> createTeam(@NotNull Player owner, String name) {
        return CompletableFuture.supplyAsync(() -> {
            User user = IridiumFactions.getInstance().getUserManager().getUser(owner);

            FactionCreateEvent factionCreateEvent = getFactionCreateEvent(user, name).join();
            if (factionCreateEvent.isCancelled()) return null;

            owner.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().creatingFaction
                    .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
            ));

            Faction faction = new Faction(factionCreateEvent.getFactionName());

            IridiumFactions.getInstance().getDatabaseManager().registerFaction(faction).join();

            user.setTeam(faction);
            user.setUserRank(Rank.OWNER.getId());

            Bukkit.getScheduler().runTask(IridiumFactions.getInstance(), () -> {
                IridiumFactions.getInstance().getNms().sendTitle(owner, IridiumFactions.getInstance().getConfiguration().factionCreateTitle, IridiumFactions.getInstance().getConfiguration().factionCreateSubTitle, 20, 40, 20);
            });

            return faction;
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
    }

    private CompletableFuture<FactionCreateEvent> getFactionCreateEvent(@NotNull User user, @Nullable String factionName) {
        CompletableFuture<FactionCreateEvent> completableFuture = new CompletableFuture<>();
        Bukkit.getScheduler().runTask(IridiumFactions.getInstance(), () -> {
            FactionCreateEvent factionCreateEvent = new FactionCreateEvent(user, factionName);
            Bukkit.getPluginManager().callEvent(factionCreateEvent);
            completableFuture.complete(factionCreateEvent);

        });
        return completableFuture;
    }

    @Override
    public boolean deleteTeam(Faction faction, User user) {
        FactionDeleteEvent factionDeleteEvent = new FactionDeleteEvent(faction, user);
        Bukkit.getPluginManager().callEvent(factionDeleteEvent);
        if (factionDeleteEvent.isCancelled()) return false;

        IridiumFactions.getInstance().getDatabaseManager().getFactionTableManager().delete(faction);

        return true;
    }

    @Override
    public synchronized boolean getTeamPermission(Faction faction, int rank, String permission) {
        if(faction.getFactionType() == FactionType.WILDERNESS) return true;
        if (rank == Rank.OWNER.getId()) return true;
        return IridiumFactions.getInstance().getDatabaseManager().getPermissionsTableManager().getEntry(new TeamPermission(faction, permission, rank, true))
                .map(TeamPermission::isAllowed)
                .orElse(IridiumFactions.getInstance().getPermissionList().get(permission).getDefaultRank() <= rank);
    }

    @Override
    public synchronized void setTeamPermission(Faction faction, int rank, String permission, boolean allowed) {
        TeamPermission factionPermission = new TeamPermission(faction, permission, rank, allowed);
        Optional<TeamPermission> teamPermission = IridiumFactions.getInstance().getDatabaseManager().getPermissionsTableManager().getEntry(factionPermission);
        if (teamPermission.isPresent()) {
            teamPermission.get().setAllowed(allowed);
        } else {
            IridiumFactions.getInstance().getDatabaseManager().getPermissionsTableManager().addEntry(factionPermission);
        }
    }

    @Override
    public Optional<TeamInvite> getTeamInvite(Faction team, User user) {
        return IridiumFactions.getInstance().getDatabaseManager().getInvitesTableManager().getEntry(new TeamInvite(team, user.getUuid(), user.getUuid()));
    }

    @Override
    public List<TeamInvite> getTeamInvites(Faction team) {
        return IridiumFactions.getInstance().getDatabaseManager().getInvitesTableManager().getEntries(team);
    }

    @Override
    public void createTeamInvite(Faction team, User user, User invitee) {
        IridiumFactions.getInstance().getDatabaseManager().getInvitesTableManager().addEntry(new TeamInvite(team, user.getUuid(), invitee.getUuid()));
    }

    @Override
    public void deleteTeamInvite(TeamInvite teamInvite) {
        IridiumFactions.getInstance().getDatabaseManager().getInvitesTableManager().delete(teamInvite);
    }

    @Override
    public Optional<TeamTrust> getTeamTrust(Faction faction, User user) {
        return IridiumFactions.getInstance().getDatabaseManager().getTrustTableManager().getEntry(new TeamTrust(faction, user.getUuid(), user.getUuid()));
    }

    @Override
    public List<TeamTrust> getTeamTrusts(Faction faction) {
        return IridiumFactions.getInstance().getDatabaseManager().getTrustTableManager().getEntries(faction);
    }

    @Override
    public void createTeamTrust(Faction faction, User user, User invitee) {
        IridiumFactions.getInstance().getDatabaseManager().getTrustTableManager().addEntry(new TeamTrust(faction, user.getUuid(), invitee.getUuid()));
    }

    @Override
    public void deleteTeamTrust(TeamTrust teamTrust) {
        IridiumFactions.getInstance().getDatabaseManager().getTrustTableManager().delete(teamTrust);
    }

    @Override
    public synchronized TeamBank getTeamBank(Faction faction, String bankItem) {
        Optional<TeamBank> teamBank = IridiumFactions.getInstance().getDatabaseManager().getBankTableManager().getEntry(new TeamBank(faction, bankItem, 0));
        if (teamBank.isPresent()) {
            return teamBank.get();
        } else {
            TeamBank bank = new TeamBank(faction, bankItem, 0);
            IridiumFactions.getInstance().getDatabaseManager().getBankTableManager().addEntry(bank);
            return bank;
        }
    }

    @Override
    public synchronized TeamSpawners getTeamSpawners(Faction faction, EntityType entityType) {
        Optional<TeamSpawners> teamSpawner = IridiumFactions.getInstance().getDatabaseManager().getTeamSpawnerTableManager().getEntry(new TeamSpawners(faction, entityType, 0));
        if (teamSpawner.isPresent()) {
            return teamSpawner.get();
        } else {
            TeamSpawners spawner = new TeamSpawners(faction, entityType, 0);
            IridiumFactions.getInstance().getDatabaseManager().getTeamSpawnerTableManager().addEntry(spawner);
            return spawner;
        }
    }

    @Override
    public synchronized TeamBlock getTeamBlock(Faction faction, XMaterial xMaterial) {
        Optional<TeamBlock> teamBlock = IridiumFactions.getInstance().getDatabaseManager().getTeamBlockTableManager().getEntry(new TeamBlock(faction, xMaterial, 0));
        if (teamBlock.isPresent()) {
            return teamBlock.get();
        } else {
            TeamBlock block = new TeamBlock(faction, xMaterial, 0);
            IridiumFactions.getInstance().getDatabaseManager().getTeamBlockTableManager().addEntry(block);
            return block;
        }
    }

    @Override
    public synchronized TeamSetting getTeamSetting(Faction faction, String settingKey) {
        Setting settingConfig = IridiumFactions.getInstance().getSettingsList().get(settingKey);
        String defaultValue = settingConfig == null ? "" : settingConfig.getDefaultValue();
        Optional<TeamSetting> teamSetting = IridiumFactions.getInstance().getDatabaseManager().getTeamSettingsTableManager().getEntry(new TeamSetting(faction, settingKey, defaultValue));
        if (teamSetting.isPresent()) {
            return teamSetting.get();
        } else {
            TeamSetting setting = new TeamSetting(faction, settingKey, defaultValue);
            IridiumFactions.getInstance().getDatabaseManager().getTeamSettingsTableManager().addEntry(setting);
            return setting;
        }
    }

    @Override
    public synchronized TeamEnhancement getTeamEnhancement(Faction faction, String enhancementName) {
        Optional<TeamEnhancement> teamEnhancement = IridiumFactions.getInstance().getDatabaseManager().getEnhancementTableManager().getEntry(new TeamEnhancement(faction, enhancementName, 0));
        if (teamEnhancement.isPresent()) {
            return teamEnhancement.get();
        } else {
            TeamEnhancement enhancement = new TeamEnhancement(faction, enhancementName, 0);
            IridiumFactions.getInstance().getDatabaseManager().getEnhancementTableManager().addEntry(enhancement);
            return enhancement;
        }
    }

    @Override
    public CompletableFuture<Void> recalculateTeam(Faction faction) {
        Map<XMaterial, Integer> teamBlocks = new HashMap<>();
        Map<EntityType, Integer> teamSpawners = new HashMap<>();
        return CompletableFuture.runAsync(() -> {
            List<Chunk> chunks = getFactionChunks(faction).join();
            for (Chunk chunk : chunks) {
                ChunkSnapshot chunkSnapshot = chunk.getChunkSnapshot(true, false, false);
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        final int maxy = chunkSnapshot.getHighestBlockYAt(x, z);
                        for (int y = 0; y <= maxy; y++) {
                            XMaterial material = XMaterial.matchXMaterial(chunkSnapshot.getBlockType(x, y, z));
                            teamBlocks.put(material, teamBlocks.getOrDefault(material, 0) + 1);
                        }
                    }
                }
                getSpawners(chunk).join().forEach(creatureSpawner ->
                        teamSpawners.put(creatureSpawner.getSpawnedType(), teamSpawners.getOrDefault(creatureSpawner.getSpawnedType(), 0) + 1)
                );
            }
        }).thenRun(() -> Bukkit.getScheduler().runTask(IridiumFactions.getInstance(), () -> {
            List<TeamBlock> blocks = IridiumFactions.getInstance().getDatabaseManager().getTeamBlockTableManager().getEntries(faction);
            List<TeamSpawners> spawners = IridiumFactions.getInstance().getDatabaseManager().getTeamSpawnerTableManager().getEntries(faction);
            for (TeamBlock teamBlock : blocks) {
                teamBlock.setAmount(teamBlocks.getOrDefault(teamBlock.getXMaterial(), 0));
            }
            for (TeamSpawners teamSpawner : spawners) {
                teamSpawner.setAmount(teamSpawners.getOrDefault(teamSpawner.getEntityType(), 0));
            }
        }));
    }

    public CompletableFuture<List<CreatureSpawner>> getSpawners(Chunk chunk) {
        CompletableFuture<List<CreatureSpawner>> completableFuture = new CompletableFuture<>();
        Bukkit.getScheduler().runTask(IridiumFactions.getInstance(), () -> {
            List<CreatureSpawner> creatureSpawners = new ArrayList<>();
            for (BlockState blockState : chunk.getTileEntities()) {
                if (!(blockState instanceof CreatureSpawner)) continue;
                creatureSpawners.add((CreatureSpawner) blockState);
            }
            completableFuture.complete(creatureSpawners);
        });
        return completableFuture;
    }

    public List<FactionClaim> getFactionClaims(Faction faction) {
        return IridiumFactions.getInstance().getDatabaseManager().getFactionClaimsTableManager().getEntries().stream()
                        .filter(factionClaim -> factionClaim.getTeamID() == faction.getId())
                        .collect(Collectors.toList());
    }

    public CompletableFuture<List<Chunk>> getFactionChunks(Faction faction) {
        return CompletableFuture.supplyAsync(() ->
                IridiumFactions.getInstance().getDatabaseManager().getFactionClaimsTableManager().getEntries().stream()
                        .filter(factionClaim -> factionClaim.getTeamID() == faction.getId())
                        .map(FactionClaim::getChunk)
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList())
        ).exceptionally(throwable -> {
            throwable.printStackTrace();
            return Collections.emptyList();
        });
    }

    @Override
    public void createWarp(Faction faction, UUID creator, Location location, String name, String password) {
        IridiumFactions.getInstance().getDatabaseManager().getTeamWarpTableManager().addEntry(new TeamWarp(faction, creator, location, name, password));
    }

    @Override
    public void deleteWarp(TeamWarp teamWarp) {
        IridiumFactions.getInstance().getDatabaseManager().getTeamWarpTableManager().delete(teamWarp);
    }

    @Override
    public List<TeamWarp> getTeamWarps(Faction faction) {
        return IridiumFactions.getInstance().getDatabaseManager().getTeamWarpTableManager().getEntries(faction);
    }

    @Override
    public Optional<TeamWarp> getTeamWarp(Faction faction, String name) {
        return IridiumFactions.getInstance().getDatabaseManager().getTeamWarpTableManager().getEntry(new TeamWarp(faction, UUID.randomUUID(), null, name));
    }

    @Override
    public List<TeamMission> getTeamMissions(Faction faction) {
        return IridiumFactions.getInstance().getDatabaseManager().getTeamMissionTableManager().getEntries(faction);
    }

    @Override
    public synchronized TeamMission getTeamMission(Faction faction, String missionName) {
        Mission mission = IridiumFactions.getInstance().getMissions().missions.get(missionName);
        LocalDateTime localDateTime = IridiumFactions.getInstance().getMissionManager().getExpirationTime(mission == null ? MissionType.ONCE : mission.getMissionType(), LocalDateTime.now());

        TeamMission newTeamMission = new TeamMission(faction, missionName, localDateTime);
        Optional<TeamMission> teamMission = IridiumFactions.getInstance().getDatabaseManager().getTeamMissionTableManager().getEntry(newTeamMission);
        if (teamMission.isPresent()) {
            return teamMission.get();
        } else {
            //TODO need to consider reworking this, it could generate some lag
            IridiumFactions.getInstance().getDatabaseManager().getTeamMissionTableManager().save(newTeamMission);
            IridiumFactions.getInstance().getDatabaseManager().getTeamMissionTableManager().addEntry(newTeamMission);
            return newTeamMission;
        }
    }

    @Override
    public synchronized TeamMissionData getTeamMissionData(TeamMission teamMission, int missionIndex) {
        Optional<TeamMissionData> teamMissionData = IridiumFactions.getInstance().getDatabaseManager().getTeamMissionDataTableManager().getEntry(new TeamMissionData(teamMission, missionIndex));
        if (teamMissionData.isPresent()) {
            return teamMissionData.get();
        } else {
            TeamMissionData missionData = new TeamMissionData(teamMission, missionIndex);
            IridiumFactions.getInstance().getDatabaseManager().getTeamMissionDataTableManager().addEntry(missionData);
            return missionData;
        }
    }

    @Override
    public List<TeamMissionData> getTeamMissionData(TeamMission teamMission) {
        MissionData missionData = IridiumFactions.getInstance().getMissions().missions.get(teamMission.getMissionName()).getMissionData().get(teamMission.getMissionLevel());

        List<TeamMissionData> list = new ArrayList<>();
        for (int i = 0; i < missionData.getMissions().size(); i++) {
            list.add(getTeamMissionData(teamMission, i));
        }
        return list;
    }

    @Override
    public void deleteTeamMission(TeamMission teamMission) {
        IridiumFactions.getInstance().getDatabaseManager().getTeamMissionTableManager().delete(teamMission);
    }

    @Override
    public List<TeamReward> getTeamRewards(Faction faction) {
        return IridiumFactions.getInstance().getDatabaseManager().getTeamRewardsTableManager().getEntries(faction);
    }

    @Override
    public void addTeamReward(TeamReward teamReward) {
        IridiumFactions.getInstance().getDatabaseManager().getTeamRewardsTableManager().addEntry(teamReward);
    }

    @Override
    public void deleteTeamReward(TeamReward teamReward) {
        IridiumFactions.getInstance().getDatabaseManager().getTeamRewardsTableManager().delete(teamReward);
    }

    public ItemStack getFactionCrystal(int amount) {
        ItemStack itemStack = ItemStackUtils.makeItem(IridiumFactions.getInstance().getConfiguration().factionCrystal, Collections.singletonList(
                new Placeholder("amount", String.valueOf(amount))
        ));
        NBTItem nbtItem = new NBTItem(itemStack);
        NBTCompound nbtCompound = nbtItem.getOrCreateCompound("com/iridium/iridiumfactions");
        nbtCompound.setInteger("factionCrystals", amount);
        return nbtItem.getItem();
    }

    public int getFactionCrystals(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() == Material.AIR) return 0;
        NBTCompound nbtCompound = new NBTItem(itemStack).getOrCreateCompound("com/iridium/iridiumfactions");
        if (nbtCompound.hasKey("factionCrystals")) {
            return nbtCompound.getInteger("factionCrystals");
        }
        return 0;
    }

    @Override
    public boolean teleport(Player player, Location location, Faction team) {
        Location safeLocation = LocationUtils.getSafeLocation(location, team);
        if (safeLocation == null) {
            player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().noSafeLocation
                    .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
            ));
            return false;
        }
        player.setFallDistance(0.0F);
        PaperLib.teleportAsync(player, safeLocation);
        return true;
    }


    public CompletableFuture<Void> claimFactionLand(Faction faction, Chunk chunk, Player player) {
        return claimFactionLand(faction, chunk.getWorld(), chunk.getX(), chunk.getZ(), player);
    }

    public CompletableFuture<Void> claimFactionLand(Faction faction, World world, int x, int z, Player player) {
        return CompletableFuture.runAsync(() -> {
            User user = IridiumFactions.getInstance().getUserManager().getUser(player);
            if (!getTeamPermission(faction, user, PermissionType.CLAIM)) {
                player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().cannotClaimLand
                        .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                ));
                return;
            }
            if (faction.getRemainingPower() < 1 && !user.isBypassing()) {
                player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().notEnoughPowerToClaim
                        .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                ));
                return;
            }
            Optional<FactionClaim> factionClaim = getFactionClaimViaChunk(world, x, z);
            if (factionClaim.isPresent() && !user.isBypassing() && factionClaim.get().getFaction().getRemainingPower() >= 0) {
                player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().landAlreadyClaimed
                        .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                        .replace("%faction%", factionClaim.get().getFaction().getName())
                ));
                return;
            }
            getTeamMembers(faction).forEach(user1 -> {
                Player p = user1.getPlayer();
                if (p != null) {
                    p.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().factionClaimedLand
                            .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                            .replace("%player%", user.getName())
                            .replace("%faction%", faction.getName())
                            .replace("%x%", String.valueOf(x))
                            .replace("%z%", String.valueOf(z))
                    ));
                }
            });
            if (user.getTeamID() != faction.getId()) {
                player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().factionClaimedLand
                        .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                        .replace("%player%", user.getName())
                        .replace("%faction%", faction.getName())
                        .replace("%x%", String.valueOf(x))
                        .replace("%z%", String.valueOf(z))
                ));
            }
            if (factionClaim.isPresent()) {
                if (faction.getFactionType() == FactionType.WILDERNESS) {
                    IridiumFactions.getInstance().getDatabaseManager().getFactionClaimsTableManager().delete(factionClaim.get());
                } else {
                    factionClaim.get().setFaction(faction);
                }
            } else {
                IridiumFactions.getInstance().getDatabaseManager().getFactionClaimsTableManager().addEntry(new FactionClaim(faction, world.getName(), x, z));
            }
        });
    }

    public CompletableFuture<Void> claimFactionLand(Faction faction, Chunk centerChunk, int radius, Player player) {
        return CompletableFuture.runAsync(() -> {
            User user = IridiumFactions.getInstance().getUserManager().getUser(player);
            if (!getTeamPermission(faction, user, PermissionType.CLAIM)) {
                player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().cannotClaimLand
                        .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                ));
                return;
            }
            World world = centerChunk.getWorld();
            for (int x = centerChunk.getX() - (radius - 1); x <= centerChunk.getX() + (radius - 1); x++) {
                for (int z = centerChunk.getZ() - (radius - 1); z <= centerChunk.getZ() + (radius - 1); z++) {
                    if (faction.getRemainingPower() < 1 && !user.isBypassing()) {
                        player.sendMessage(StringUtils.color(IridiumFactions.getInstance().getMessages().notEnoughPowerToClaim
                                .replace("%prefix%", IridiumFactions.getInstance().getConfiguration().prefix)
                        ));
                        return;
                    }
                    claimFactionLand(faction, world, x, z, player).join();
                }
            }
        });
    }

    public RelationshipType getFactionRelationship(@NotNull Faction a, @NotNull Faction b) {
        if (b.getFactionType() == FactionType.WILDERNESS) {
            return RelationshipType.WILDERNESS;
        }
        if (b.getFactionType() == FactionType.WARZONE) {
            return RelationshipType.WARZONE;
        }
        if (b.getFactionType() == FactionType.SAFEZONE) {
            return RelationshipType.SAFEZONE;
        }
        if (a.getFactionType() != FactionType.PLAYER_FACTION) {
            return RelationshipType.TRUCE;
        }
        if (a == b) {
            return RelationshipType.OWN;
        }
//        Optional<FactionRelationship> factionRelationshipA = IridiumFactions.getInstance().getDatabaseManager().getFactionRelationshipTableManager().getEntry(new FactionRelationship(a, b));
//        if (factionRelationshipA.isPresent()) {
//            return factionRelationshipA.get().getRelationshipType();
//        }
//        Optional<FactionRelationship> factionRelationshipB = IridiumFactions.getInstance().getDatabaseManager().getFactionRelationshipTableManager().getEntry(new FactionRelationship(b, a));
//        if (factionRelationshipB.isPresent()) {
//            return factionRelationshipB.get().getRelationshipType();
//        }
        return RelationshipType.TRUCE;
    }

    public RelationshipType getFactionRelationship(User user, @NotNull Faction faction) {
        return getFactionRelationship(user.getFaction(), faction);
    }

}
