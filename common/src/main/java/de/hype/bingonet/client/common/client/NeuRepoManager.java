package de.hype.bingonet.client.common.client;

import de.hype.bingonet.client.common.mclibraries.EnvironmentCore;
import io.github.moulberry.repo.NEUItems;
import io.github.moulberry.repo.NEURepository;
import io.github.moulberry.repo.NEURepositoryException;
import io.github.moulberry.repo.constants.Islands;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class NeuRepoManager {
    private static final String LOCAL_PATH = EnvironmentCore.utils.getConfigPath().toPath()+"/repos/neu";
    private static final String REPO_URL = "https://github.com/NotEnoughUpdates/NotEnoughUpdates-REPO.git";
    private static final NEURepository repository = NEURepository.of(Path.of(LOCAL_PATH));

    public NeuRepoManager() throws NEURepositoryException {
        try {
            updateRepo();
        } catch (Exception e) {

        }
        repository.reload();
    }



    private static void updateRepo() throws GitAPIException, IOException {
        File repoDir = new File(LOCAL_PATH);
        if (!repoDir.exists() ||  (repoDir.isDirectory() && repoDir.listFiles().length==0)) {
            repoDir.mkdirs();
            cloneRepo(repoDir);
        } else {
            fetchChanges(repoDir);
        }
    }

    private static void cloneRepo(File repoDir) throws GitAPIException {
        System.out.println("BingoNet: Cloning NEU Repo: Start");
        Git.cloneRepository()
                .setURI(REPO_URL)
                .setDirectory(repoDir)
                .call();
        System.out.println("BingoNet: Cloning NEU Repo: Done");
    }

    private static void fetchChanges(File repoDir) throws IOException, GitAPIException {
        try (Git git = Git.open(repoDir)) {
            System.out.println("BingoNet: Fetching latest NEU Repo changes...");
            git.fetch().call();
            System.out.println("BingoNet: Checking out the latest NEU Repo changes...");
            git.pull().call();
            System.out.println("BingoNet: NEU Repo updated.");
        }
    }

    public NEURepository getRepository() {
        return repository;
    }
    public NEUItems getItems(){
        return repository.getItems();
    }
    public @NotNull List<Islands.Warp> getWarps(){
        return repository.getConstants().getIslands().getWarps();
    }

    public List<String> getItemIds() {
        return new ArrayList<>(getItems().getItems().keySet());
    }
}
