package com.example.weatherappdemo;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.StandardCopyOption;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Persists users with PBKDF2-hashed passwords in readable {@code user.dat}. */
public final class UserRepository {
    private static final String TEMP_FILE_NAME = "user.dat.tmp";
    private static final char DELIMITER = '|';

    public enum RegisterStatus {
        SUCCESS,
        ALREADY_REGISTERED,
        USERNAME_TAKEN,
        INVALID_USERNAME
    }

    private UserRepository() {
    }

    public static List<User> loadAll() {
        try {
            Path file = AppPaths.userFile();
            migrateLegacyUsers(file);
            if (!Files.exists(file)) {
                return new ArrayList<>();
            }
            List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
            List<User> users = new ArrayList<>();
            for (String line : lines) {
                if (line == null || line.isBlank()) {
                    continue;
                }
                int separatorIndex = line.indexOf(DELIMITER);
                if (separatorIndex <= 0 || separatorIndex == line.length() - 1) {
                    continue;
                }
                String username = line.substring(0, separatorIndex).trim();
                String passwordHash = line.substring(separatorIndex + 1).trim();
                if (!username.isEmpty() && !passwordHash.isEmpty()) {
                    users.add(new User(username, passwordHash));
                }
            }
            return users;
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    public static void saveAll(List<User> users) throws IOException {
        Path file = AppPaths.userFile();
        Path temp = file.resolveSibling(TEMP_FILE_NAME);
        try (BufferedWriter writer = Files.newBufferedWriter(
                temp,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING)) {
            for (User user : users) {
                writer.write(user.getUsername() + DELIMITER + user.getPasswordHash());
                writer.newLine();
            }
        }
        Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
    }

    public static RegisterStatus register(String username, String plainPassword) throws IOException {
        if (!isUsernameSupported(username)) {
            return RegisterStatus.INVALID_USERNAME;
        }
        List<User> users = loadAll();
        for (User user : users) {
            if (user.getUsername().equalsIgnoreCase(username)) {
                if (PasswordUtil.verifyPassword(plainPassword, user.getPasswordHash())) {
                    return RegisterStatus.ALREADY_REGISTERED;
                }
                return RegisterStatus.USERNAME_TAKEN;
            }
        }
        String hash = PasswordUtil.hashPassword(plainPassword);
        users.add(new User(username, hash));
        saveAll(users);
        return RegisterStatus.SUCCESS;
    }

    public static Optional<String> authenticate(String username, String plainPassword) {
        for (User user : loadAll()) {
            if (user.getUsername().equalsIgnoreCase(username)
                    && PasswordUtil.verifyPassword(plainPassword, user.getPasswordHash())) {
                return Optional.of(user.getUsername());
            }
        }
        return Optional.empty();
    }

    public static boolean isUsernameSupported(String username) {
        return username != null
                && username.indexOf(DELIMITER) < 0
                && username.indexOf('\n') < 0
                && username.indexOf('\r') < 0;
    }

    private static void migrateLegacyUsers(Path target) throws IOException {
        if (Files.exists(target)) {
            return;
        }
        List<User> legacyUsers = loadLegacySerializedUsers(AppPaths.legacyProjectUsersFile());
        if (legacyUsers.isEmpty()) {
            legacyUsers = loadLegacySerializedUsers(AppPaths.legacyHomeUsersFile());
        }
        if (!legacyUsers.isEmpty()) {
            saveAll(legacyUsers);
        }
    }

    private static List<User> loadLegacySerializedUsers(Path legacyFile) {
        if (legacyFile == null || !Files.exists(legacyFile)) {
            return new ArrayList<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(legacyFile.toFile()))) {
            Object data = ois.readObject();
            if (!(data instanceof List<?>)) {
                return new ArrayList<>();
            }
            List<?> list = (List<?>) data;
            List<User> users = new ArrayList<>();
            for (Object item : list) {
                if (item instanceof User) {
                    users.add((User) item);
                }
            }
            return users;
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }
}
