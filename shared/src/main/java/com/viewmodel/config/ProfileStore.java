package com.viewmodel.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.viewmodel.ViewModelProfile;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public final class ProfileStore {
    private static final Logger LOGGER = LoggerFactory.getLogger("ViewModel/ProfileStore");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Path path;

    public ProfileStore() {
        this(FabricLoader.getInstance().getConfigDir().resolve("viewmodel-viewmodel.json"));
    }

    ProfileStore(Path path) {
        this.path = path;
    }

    public Snapshot load() {
        if (Files.notExists(path)) {
            Snapshot defaults = Snapshot.defaults();
            save(defaults);
            return defaults;
        }
        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            Payload payload = GSON.fromJson(reader, Payload.class);
            if (payload == null || payload.profiles == null || payload.profiles.isEmpty()) {
                throw new JsonParseException("Config contains no profiles");
            }
            return payload.toSnapshot();
        } catch (Exception exception) {
            LOGGER.error("Could not load {}; defaults will be used", path, exception);
            Snapshot defaults = Snapshot.defaults();
            save(defaults);
            return defaults;
        }
    }

    public void save(Snapshot snapshot) {
        try {
            Files.createDirectories(path.getParent());
            try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)) {
                GSON.toJson(Payload.from(snapshot), writer);
            }
        } catch (Exception exception) {
            LOGGER.error("Could not save {}", path, exception);
        }
    }

    public record Snapshot(String activeProfile, List<ViewModelProfile> profiles) {
        public Snapshot {
            profiles = List.copyOf(profiles);
        }

        public static Snapshot defaults() {
            return new Snapshot("Default", List.of(ViewModelProfile.defaults("Default")));
        }
    }

    private record Payload(String activeProfile, List<ProfilePayload> profiles) {
        static Payload from(Snapshot snapshot) {
            return new Payload(snapshot.activeProfile(), snapshot.profiles().stream().map(ProfilePayload::from).toList());
        }

        Snapshot toSnapshot() {
            List<ViewModelProfile> decoded = new ArrayList<>(profiles.size());
            for (ProfilePayload profile : profiles) {
                if (profile != null) decoded.add(profile.toProfile());
            }
            if (decoded.isEmpty()) return Snapshot.defaults();
            return new Snapshot(activeProfile, decoded);
        }
    }

    private record ProfilePayload(String name, float size, float posX, float posY, float posZ,
                                  float yaw, float pitch, float roll, boolean noSwing, boolean scaleSwing,
                                  Boolean skipEquipAnimation) {
        static ProfilePayload from(ViewModelProfile profile) {
            return new ProfilePayload(profile.name(), profile.size(), profile.positionX(), profile.positionY(),
                profile.positionZ(), profile.rotationYaw(), profile.rotationPitch(), profile.rotationRoll(),
                profile.noSwing(), profile.scaleSwing(), profile.skipEquipAnimation());
        }

        ViewModelProfile toProfile() {
            return new ViewModelProfile(name, size, posX, posY, posZ, yaw, pitch, roll, noSwing, scaleSwing,
                skipEquipAnimation == null || skipEquipAnimation);
        }
    }
}
