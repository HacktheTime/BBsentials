/*
 * Copyright (C) 2022 NotEnoughUpdates contributors
 *
 * This file is part of NotEnoughUpdates.
 *
 * NotEnoughUpdates is free software: you can redistribute it
 * and/or modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * NotEnoughUpdates is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with NotEnoughUpdates. If not, see <https://www.gnu.org/licenses/>.
 */

package de.hype.bbsentials.client.common.client;

import de.hype.bbsentials.client.common.chat.Chat;
import de.hype.bbsentials.client.common.mclibraries.EnvironmentCore;
import de.hype.bbsentials.client.common.mclibraries.interfaces.Vector3d;
import de.hype.bbsentials.client.common.mclibraries.interfaces.Vector3i;
import de.hype.bbsentials.shared.constants.Formatting;
import de.hype.bbsentials.shared.constants.Islands;
import de.hype.bbsentials.shared.constants.VanillaBlocks;
import de.hype.bbsentials.shared.constants.VanillaEntities;
import de.hype.bbsentials.shared.objects.Message;
import de.hype.bbsentials.shared.objects.MinecraftEntity;
import de.hype.bbsentials.shared.objects.Position;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This Class was copied from Skyhanni originally and modified to work in modern. which is under the GNU LESSER GENERAL PUBLIC LICENSE
 */
public class CrystalMetalDetectorSolver {
    private static final HashMap<Position, Double> evaluatedPlayerPositions = new HashMap<>();
    private static final HashSet<Position> openedChestPositions = new HashSet<>();
    private static final String KEEPER_OF_STRING = "Keeper of ";
    private static final String DIAMOND_STRING = "diamond";
    private static final String LAPIS_STRING = "lapis";
    private static final String EMERALD_STRING = "emerald";
    private static final String GOLD_STRING = "gold";
    private static final HashMap<String, Vector3d> keeperOffsets = new HashMap<String, Vector3d>() {{
        put(DIAMOND_STRING, new Vector3d(33, 0, 3));
        put(LAPIS_STRING, new Vector3d(-33, 0, -3));
        put(EMERALD_STRING, new Vector3d(-3, 0, 33));
        put(GOLD_STRING, new Vector3d(3, 0, -33));
    }};
    // Chest offsets from center
    private static final HashSet<Long> knownChestOffsets = new HashSet<>(Arrays.asList(
            -10171958951910L,  // x=-38, y=-22, z=26
            10718829084646L,  // x=38, y=-22, z=-26
            -10721714765806L,  // x=-40, y=-22, z=18
            -10996458455018L,  // x=-41, y=-20, z=22
            -1100920913904L,  // x=-5, y=-21, z=16
            11268584898530L,  // x=40, y=-22, z=-30
            -11271269253148L,  // x=-42, y=-20, z=-28
            -11546281377832L,  // x=-43, y=-22, z=-40
            11818542038999L,  // x=42, y=-19, z=-41
            12093285728240L,  // x=43, y=-21, z=-16
            -1409286164L,      // x=-1, y=-22, z=-20
            1922736062492L,    // x=6, y=-21, z=28
            2197613969419L,    // x=7, y=-21, z=11
            2197613969430L,    // x=7, y=-21, z=22
            -3024999153708L,  // x=-12, y=-21, z=-44
            3571936395295L,    // x=12, y=-22, z=31
            3572003504106L,    // x=12, y=-22, z=-22
            3572003504135L,    // x=12, y=-21, z=7
            3572070612949L,    // x=12, y=-21, z=-43
            -3574822076373L,  // x=-14, y=-21, z=43
            -3574822076394L,  // x=-14, y=-21, z=22
            -4399455797228L,  // x=-17, y=-21, z=20
            -5224156626944L,  // x=-20, y=-22, z=0
            548346527764L,    // x=1, y=-21, z=20
            5496081743901L,    // x=19, y=-22, z=29
            5770959650816L,    // x=20, y=-22, z=0
            5771093868518L,    // x=20, y=-21, z=-26
            -6048790347736L,  // x=-23, y=-22, z=40
            6320849682418L,    // x=22, y=-21, z=-14
            -6323668254708L,  // x=-24, y=-22, z=12
            6595593371674L,    // x=23, y=-22, z=26
            6595660480473L,    // x=23, y=-22, z=-39
            6870471278619L,    // x=24, y=-22, z=27
            7145349185553L,    // x=25, y=-22, z=17
            8244995030996L,    // x=29, y=-21, z=-44
            -8247679385612L,  // x=-31, y=-21, z=-12
            -8247679385640L,  // x=-31, y=-21, z=-40
            8519872937959L,    // x=30, y=-21, z=-25
            -8522557292584L,  // x=-32, y=-21, z=-40
            -9622068920278L,  // x=-36, y=-20, z=42
            -9896946827278L,  // x=-37, y=-21, z=-14
            -9896946827286L    // x=-37, y=-21, z=-22
    ));
    public static HashSet<Position> possibleBlocks = new HashSet<>();
    static SolutionState currentState = SolutionState.NOT_STARTED;
    static SolutionState previousState = SolutionState.NOT_STARTED;
    private static Position prevPlayerPos;
    private static double prevDistToTreasure;
    private static boolean chestRecentlyFound;
    private static long chestLastFoundMillis;
    // Keeper and Mines of Divan center location info
    private static Vector3d minesCenter = Vector3d.NULL_VECTOR;
    static Predicate<Position> treasureAllowedPredicate = CrystalMetalDetectorSolver::treasureAllowed;
    private static boolean debugDoNotUseCenter = false;
    private static boolean visitKeeperMessagePrinted;

    public static void process(Message message) {
        if (BBsentials.dataStorage.getIsland() != Islands.CRYSTAL_HOLLOWS || !message.getUnformattedString().contains("TREASURE: ")) {
            return;
        }

        boolean centerNewlyDiscovered = locateMinesCenterIfNeeded();

        double distToTreasure = Double.parseDouble(message.getUnformattedString().split("TREASURE: ")[1].split("m")[0].replaceAll("(?!\\.)\\D", ""));

        // Delay to keep old chest location from being treated as the new chest location
        if (chestRecentlyFound) {
            long currentTimeMillis = System.currentTimeMillis();
            if (chestLastFoundMillis == 0) {
                chestLastFoundMillis = currentTimeMillis;
                return;
            }
            else if (currentTimeMillis - chestLastFoundMillis < 1000 && distToTreasure < 5.0) {
                return;
            }

            chestLastFoundMillis = 0;
            chestRecentlyFound = false;
        }

        SolutionState originalState = currentState;
        int originalCount = possibleBlocks.size();
        Position adjustedPlayerPos = getPlayerPosAdjustedForEyeHeight();
        findPossibleSolutions(distToTreasure, adjustedPlayerPos, centerNewlyDiscovered);
        if (currentState != originalState || originalCount != possibleBlocks.size()) {
            switch (currentState) {
                case FOUND_KNOWN:
//                    NEUDebugLogger.log(NEUDebugFlag.METAL, "Known location identified.");
                    // falls through
                case FOUND:
//                    Utils.addChatMessage(Formatting.YELLOW + "[NEU] Found solution.");
//                    if (NEUDebugFlag.METAL.isSet() &&
//                            (previousState == SolutionState.INVALID || previousState == SolutionState.FAILED)) {
//                        NEUDebugLogger.log(
//                                NEUDebugFlag.METAL,
//                                Formatting.AQUA + "Solution coordinates: " +
//                                        Formatting.WHITE + possibleBlocks.iterator().next().toString()
//                        );
//                    }
                    break;
                case INVALID:
//                    Utils.addChatMessage(Formatting.RED + "[NEU] Previous solution is invalid.");
                    logDiagnosticData(false);
                    resetSolution(false);
                    break;
                case FAILED:
//                    Utils.addChatMessage(Formatting.RED + "[NEU] Failed to find a solution.");
                    logDiagnosticData(false);
                    resetSolution(false);
                    break;
                case MULTIPLE_KNOWN:
//                    NEUDebugLogger.log(NEUDebugFlag.METAL, "Multiple known locations identified:");
                    // falls through
                case MULTIPLE:
//                    Utils.addChatMessage(
//                            Formatting.YELLOW + "[NEU] Need another position to find solution. Possible blocks: " +
//                                    possibleBlocks.size());
                    break;
                default:
                    throw new IllegalStateException("Metal detector is in invalid state");
            }
        }
    }

    static void findPossibleSolutions(double distToTreasure, Position playerPos, boolean centerNewlyDiscovered) {
        if (prevDistToTreasure == distToTreasure && prevPlayerPos.equals(playerPos) &&
                !evaluatedPlayerPositions.containsKey(playerPos)) {
            evaluatedPlayerPositions.put(playerPos, distToTreasure);
            if (possibleBlocks.size() == 0) {
                for (int zOffset = (int) Math.floor(-distToTreasure); zOffset <= Math.ceil(distToTreasure); zOffset++) {
                    for (int y = 65; y <= 75; y++) {
                        double calculatedDist = 0;
                        int xOffset = 0;
                        while (calculatedDist < distToTreasure) {
                            Position pos = new Position((int) (Math.floor(playerPos.x) + xOffset),
                                    y, (int) (Math.floor(playerPos.z) + zOffset)
                            );
                            calculatedDist = playerPos.distanceTo(pos.addVector(0, 1, 0));
                            if (round(calculatedDist, 1) == distToTreasure && treasureAllowedPredicate.check(pos)) {
                                possibleBlocks.add(pos);
                            }
                            xOffset++;
                        }
                        xOffset = 0;
                        calculatedDist = 0;
                        while (calculatedDist < distToTreasure) {
                            Position pos = new Position((int) (Math.floor(playerPos.x) - xOffset),
                                    y, (int) (Math.floor(playerPos.z) + zOffset)
                            );
                            calculatedDist = playerPos.distanceTo(pos.addVector(0, 1, 0));
                            if (round(calculatedDist, 1) == distToTreasure && treasureAllowedPredicate.check(pos)) {
                                possibleBlocks.add(pos);
                            }
                            xOffset++;
                        }
                    }
                }

                updateSolutionState();
            }
            else if (possibleBlocks.size() != 1) {
                HashSet<Position> temp = new HashSet<>();
                for (Position pos : possibleBlocks) {
                    if (round(playerPos.distanceTo(pos.addVector(0, 1, 0)), 1) == distToTreasure) {
                        temp.add(pos);
                    }
                }

                possibleBlocks = temp;
                updateSolutionState();
            }
            else {
                Position pos = possibleBlocks.iterator().next();
                if (Math.abs(distToTreasure - (playerPos.distanceTo(pos))) > 5) {
                    currentState = SolutionState.INVALID;
                }
            }
        }
        else if (centerNewlyDiscovered && possibleBlocks.size() > 1) {
            updateSolutionState();
        }

        prevPlayerPos = playerPos;
        prevDistToTreasure = distToTreasure;
    }

    public static void setDebugDoNotUseCenter(boolean val) {
        debugDoNotUseCenter = val;
    }

    private static String getFriendlyPositionitions(Collection<Position> positions) {
        if (!BBsentials.visualConfig.showMODSolver || positions.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        for (Position position : positions) {
            sb.append("Absolute: ");
            sb.append(position.toString());
            if (minesCenter != Vector3d.NULL_VECTOR) {
                Vector3d relativeOffset = new Vector3d(position).subtract(minesCenter);
                sb.append(", Relative: ");
                sb.append(relativeOffset.toString());
                sb.append(" (" + relativeOffset.asLong() + ")");
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    private static String getFriendlyEvaluatedPositions() {
        if (!BBsentials.visualConfig.showMODSolver || evaluatedPlayerPositions.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        for (Position vec : evaluatedPlayerPositions.keySet()) {
            sb.append("Absolute: " + vec.toString());
            if (minesCenter != Vector3d.NULL_VECTOR) {
                Vector3d relativeOffset = new Vector3d(vec).subtract(minesCenter);
                sb.append(", Relative: " + relativeOffset.toString() + " (" + relativeOffset.asLong() + ")");
            }

            sb.append(" Distance: ");
            sb.append(evaluatedPlayerPositions.get(vec));

            sb.append("\n");
        }

        return sb.toString();
    }

    public static void resetSolution(Boolean chestFound) {
        if (chestFound) {
            prevPlayerPos = null;
            prevDistToTreasure = 0;
            if (possibleBlocks.size() == 1) {
                openedChestPositions.add(possibleBlocks.iterator().next());
            }
        }

        chestRecentlyFound = chestFound;
        possibleBlocks.clear();
        evaluatedPlayerPositions.clear();
        previousState = currentState;
        currentState = SolutionState.NOT_STARTED;
    }

    public static void initWorld() {
        minesCenter = Vector3d.NULL_VECTOR;
        visitKeeperMessagePrinted = false;
        openedChestPositions.clear();
        chestLastFoundMillis = 0;
        prevDistToTreasure = 0;
        prevPlayerPos = null;
        currentState = SolutionState.NOT_STARTED;
        resetSolution(false);
    }

//    public static void render(float partialTicks) {
//        int beaconRGB = 0x1fd8f1;
//
//        if (BBsentials.dataStorage.getIsland()==Islands.CRYSTAL_HOLLOWS &&
//                SBInfo.getInstance().location.equals("Mines of Divan")) {
//
//            if (possibleBlocks.size() == 1) {
//                Position block = possibleBlocks.iterator().next();
//
//                RenderUtils.renderBeaconBeam(block.add(0, 1, 0), beaconRGB, 1.0f, partialTicks);
//                RenderUtils.renderWayPoint("Treasure", possibleBlocks.iterator().next().add(0, 2.5, 0), partialTicks);
//            }
//            else if (possibleBlocks.size() > 1 && NotEnoughUpdates.INSTANCE.config.mining.metalDetectorShowPossible) {
//                for (Position block : possibleBlocks) {
//                    RenderUtils.renderBeaconBeam(block.add(0, 1, 0), beaconRGB, 1.0f, partialTicks);
//                    RenderUtils.renderWayPoint("Possible Treasure Location", block.add(0, 2.5, 0), partialTicks);
//                }
//            }
//        }
//    }

    private static boolean locateMinesCenterIfNeeded() {
        if (minesCenter != Vector3d.NULL_VECTOR) {
            return false;
        }

        List<MinecraftEntity> keeperEntities = EnvironmentCore.worldUtils.getEntities(VanillaEntities.ARMOR_STAND);
        keeperEntities.removeIf(e -> e.getCustomName() == null || !e.getCustomName().contains(KEEPER_OF_STRING));
        if (keeperEntities.isEmpty()) {
            if (!visitKeeperMessagePrinted) {
                Chat.sendPrivateMessageToSelfInfo(Formatting.YELLOW + "[BB: NEU] Approach a Keeper while holding the metal detector to enable faster treasure hunting.");
                visitKeeperMessagePrinted = true;
            }
            return false;
        }
        MinecraftEntity keeperEntity = keeperEntities.getFirst();
        String keeperName = keeperEntity.getCustomName();
        System.out.println("BB-NEU-MOD-HELPER Locating Center using Keeper " + keeperEntity.getCustomName());
        String keeperType = keeperName.substring(keeperName.indexOf(KEEPER_OF_STRING) + KEEPER_OF_STRING.length());
        minesCenter = new Vector3d(keeperEntity.getPosition()).add(keeperOffsets.get(keeperType.toLowerCase()));
        System.out.println("Located Mines center to be " + minesCenter);
        Chat.sendPrivateMessageToSelfInfo("[BB:NEU] Faster treasure hunting is now enabled based on Keeper location.");
        return true;
    }

    public static void setMinesCenter(Position center) {
        minesCenter = new Vector3d(center);
    }

    private static double round(double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }

    private static void updateSolutionState() {
        previousState = currentState;

        if (possibleBlocks.size() == 0) {
            currentState = SolutionState.FAILED;
            return;
        }

        if (possibleBlocks.size() == 1) {
            currentState = SolutionState.FOUND;
            return;
        }

        // Narrow solutions using known locations if the mines center is known
        if (minesCenter.equals(Position.NULL_VECTOR) || debugDoNotUseCenter) {
            currentState = SolutionState.MULTIPLE;
            return;
        }

        HashSet<Position> temp =
                possibleBlocks.stream()
                        .filter(block -> knownChestOffsets.contains(block.subtract(new Vector3i(minesCenter)).asLong()))
                        .collect(Collectors.toCollection(HashSet::new));
        if (temp.size() == 0) {
            currentState = SolutionState.MULTIPLE;
            return;
        }

        if (temp.size() == 1) {
            possibleBlocks = temp;
            currentState = SolutionState.FOUND_KNOWN;
            return;

        }

        currentState = SolutionState.MULTIPLE_KNOWN;
    }

    public static Position getSolution() {
        if (CrystalMetalDetectorSolver.possibleBlocks.size() != 1) {
            return Position.ORIGIN;
        }

        return CrystalMetalDetectorSolver.possibleBlocks.stream().iterator().next();
    }

    private static Position getPlayerPosAdjustedForEyeHeight() {
        return EnvironmentCore.worldUtils.getPlayerPosEyeHightAdjusted();
    }

    static boolean isKnownOffset(Position pos) {
        return knownChestOffsets.contains(pos.subtract(new Vector3i(minesCenter)).asLong());
    }

    static boolean isAllowedBlockType(Position pos) {
        return EnvironmentCore.worldUtils.isBlock(pos, VanillaBlocks.GOLD_BLOCK, VanillaBlocks.CHEST) || EnvironmentCore.worldUtils.isBlockPredicate(pos, vanillaBlocks -> vanillaBlocks.toString().toLowerCase().startsWith("prismarine"),
                vanillaBlocks -> vanillaBlocks.toString().toLowerCase().endsWith("stained_glass"),
                vanillaBlocks -> vanillaBlocks.toString().toLowerCase().endsWith("stained_glass_pane"),
                vanillaBlocks -> vanillaBlocks.toString().toLowerCase().endsWith("wool"),
                vanillaBlocks -> vanillaBlocks.toString().toLowerCase().endsWith("terracotta")
        );
    }

    static boolean isAirAbove(Position pos) {
        return EnvironmentCore.worldUtils.isBlockAir(new Position(pos.x, pos.y + 1, pos.z));
    }

    private static boolean treasureAllowed(Position pos) {
        boolean airAbove = isAirAbove(pos);
        boolean allowedBlockType = isAllowedBlockType(pos);
        return isKnownOffset(pos) || (airAbove && allowedBlockType);
    }

    static private String getDiagnosticMessage() {
        StringBuilder diagsMessage = new StringBuilder();

        diagsMessage.append(Formatting.AQUA);
        diagsMessage.append("Mines Center: ");
        diagsMessage.append(Formatting.WHITE);
        diagsMessage.append((minesCenter.equals(Vector3d.NULL_VECTOR)) ? "<NOT DISCOVERED>" : minesCenter.toString());
        diagsMessage.append("\n");

        diagsMessage.append(Formatting.AQUA);
        diagsMessage.append("Current Solution State: ");
        diagsMessage.append(Formatting.WHITE);
        diagsMessage.append(currentState.name());
        diagsMessage.append("\n");

        diagsMessage.append(Formatting.AQUA);
        diagsMessage.append("Previous Solution State: ");
        diagsMessage.append(Formatting.WHITE);
        diagsMessage.append(previousState.name());
        diagsMessage.append("\n");

        diagsMessage.append(Formatting.AQUA);
        diagsMessage.append("Previous Player Position: ");
        diagsMessage.append(Formatting.WHITE);
        diagsMessage.append((prevPlayerPos == null) ? "<NONE>" : prevPlayerPos.toString());
        diagsMessage.append("\n");

        diagsMessage.append(Formatting.AQUA);
        diagsMessage.append("Previous Distance To Treasure: ");
        diagsMessage.append(Formatting.WHITE);
        diagsMessage.append((prevDistToTreasure == 0) ? "<NONE>" : prevDistToTreasure);
        diagsMessage.append("\n");

        diagsMessage.append(Formatting.AQUA);
        diagsMessage.append("Current Possible Blocks: ");
        diagsMessage.append(Formatting.WHITE);
        diagsMessage.append(possibleBlocks.size());
        diagsMessage.append(getFriendlyPositionitions(possibleBlocks));
        diagsMessage.append("\n");

        diagsMessage.append(Formatting.AQUA);
        diagsMessage.append("Evaluated player positions: ");
        diagsMessage.append(Formatting.WHITE);
        diagsMessage.append(evaluatedPlayerPositions.size());
        diagsMessage.append(getFriendlyEvaluatedPositions());
        diagsMessage.append("\n");

        diagsMessage.append(Formatting.AQUA);
        diagsMessage.append("Chest locations not on known list:\n");
        diagsMessage.append(Formatting.WHITE);
        if (minesCenter != Vector3d.NULL_VECTOR) {
            HashSet<Position> locationsNotOnKnownList = openedChestPositions
                    .stream()
                    .filter(block -> !knownChestOffsets.contains(block.subtract(new Vector3i(minesCenter)).asLong()))
                    .map(block -> block.subtract(new Vector3i(minesCenter))).map(Position::new)
                    .collect(Collectors.toCollection(HashSet::new));
            if (!locationsNotOnKnownList.isEmpty()) {
                for (Position Position : locationsNotOnKnownList) {
                    diagsMessage.append(String.format(
                            "%dL,\t\t// %s",
                            Position.asLong(), Position.toFullString()
                    ));
                }
            }
        }
        else {
            diagsMessage.append("<REQUIRES MINES CENTER>");
        }

        return diagsMessage.toString();
    }

    public static void logDiagnosticData(boolean outputAlways) {
        System.err.println(getDiagnosticMessage());
    }

    enum SolutionState {
        NOT_STARTED,
        MULTIPLE,
        MULTIPLE_KNOWN,
        FOUND,
        FOUND_KNOWN,
        FAILED,
        INVALID,
    }

    public interface Predicate<Position> {
        boolean check(Position Position);
    }
}
