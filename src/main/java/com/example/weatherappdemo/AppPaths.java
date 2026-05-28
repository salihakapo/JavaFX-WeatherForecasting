package com.example.weatherappdemo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/** Stores user data in the project folder and history in the home data folder. */
public final class AppPaths {

    private static final Path DATA_DIR =
            Path.of(System.getProperty("user.home"), ".weatherappdemo");
    private static final Path PROJECT_DIR =
            Path.of(System.getProperty("user.dir")).toAbsolutePath().normalize();

    private AppPaths() {
    }

    public static Path dataDirectory() throws IOException {
        Files.createDirectories(DATA_DIR);
        return DATA_DIR;
    }

    public static Path userFile() {
        return PROJECT_DIR.resolve("user.dat");
    }

    public static Path legacyProjectUsersFile() {
        return PROJECT_DIR.resolve("users.dat");
    }

    public static Path legacyHomeUsersFile() {
        return DATA_DIR.resolve("users.dat");
    }

    public static Path historyFile() throws IOException {
        Path target = dataDirectory().resolve("history.txt");
        migrateLegacyFile(Path.of("history.txt"), target);
        return target;
    }

    private static void migrateLegacyFile(Path legacy, Path target) throws IOException {
        if (Files.exists(legacy) && !Files.exists(target)) {
            Files.copy(legacy, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
