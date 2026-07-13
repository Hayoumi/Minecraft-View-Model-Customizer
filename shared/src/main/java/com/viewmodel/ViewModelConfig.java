package com.viewmodel;

import com.viewmodel.config.ProfileStore;

public final class ViewModelConfig {
    public static final ViewModelConfig current = new ViewModelConfig();

    private static final ProfileStore STORE = new ProfileStore();
    private static final ViewModelProfileManager PROFILES = new ViewModelProfileManager();
    private static int saveDelay;

    private float size = 1.0f;
    private float positionX;
    private float positionY;
    private float positionZ;
    private float rotationYaw;
    private float rotationPitch;
    private float rotationRoll;
    private boolean noSwing;
    private boolean scaleSwing;
    private boolean skipEquipAnimation = true;

    private ViewModelConfig() {}

    public static ViewModelProfileManager profiles() { return PROFILES; }

    public static synchronized void load() {
        ProfileStore.Snapshot snapshot = STORE.load();
        PROFILES.loadProfiles(snapshot.profiles(), snapshot.activeProfile());
    }

    public static synchronized void save() {
        STORE.save(new ProfileStore.Snapshot(PROFILES.getActiveProfile().name(), PROFILES.snapshot()));
        saveDelay = 0;
    }

    public static synchronized void requestSave() { saveDelay = 10; }

    public static synchronized void tick() {
        if (saveDelay > 0 && --saveDelay == 0) save();
    }

    public void resetToDefaults() { ViewModelProfile.baseline().apply(this); }
    public float getSize() { return size; }
    public void setSize(float value) { size = value; }
    public float getPositionX() { return positionX; }
    public void setPositionX(float value) { positionX = value; }
    public float getPositionY() { return positionY; }
    public void setPositionY(float value) { positionY = value; }
    public float getPositionZ() { return positionZ; }
    public void setPositionZ(float value) { positionZ = value; }
    public float getRotationYaw() { return rotationYaw; }
    public void setRotationYaw(float value) { rotationYaw = value; }
    public float getRotationPitch() { return rotationPitch; }
    public void setRotationPitch(float value) { rotationPitch = value; }
    public float getRotationRoll() { return rotationRoll; }
    public void setRotationRoll(float value) { rotationRoll = value; }
    public boolean getNoSwing() { return noSwing; }
    public void setNoSwing(boolean value) { noSwing = value; }
    public boolean getScaleSwing() { return scaleSwing; }
    public void setScaleSwing(boolean value) { scaleSwing = value; }
    public boolean getSkipEquipAnimation() { return skipEquipAnimation; }
    public void setSkipEquipAnimation(boolean value) { skipEquipAnimation = value; }

    static { PROFILES.bootstrap(current); }
}
