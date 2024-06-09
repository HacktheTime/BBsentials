package de.hype.bbsentials.fabric;

import io.github.moulberry.repo.NEUItems;
import io.github.moulberry.repo.NEURepository;
import io.github.moulberry.repo.constants.Islands;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class NeuRepoManager {
    private static final NEURepository repository = NEURepository.of(Paths.get("NotEnoughUpdates-REPO"));

    public static NEURepository getRepository() {
        return repository;
    }
    public static NEUItems getItems(){
        return repository.getItems();
    }
    public static @NotNull List<Islands.Warp> getWarps(){
        return repository.getConstants().getIslands().getWarps();
    }

    public static List<String> getItemIds() {
        return new ArrayList<>(getItems().getItems().keySet());
    }
}
