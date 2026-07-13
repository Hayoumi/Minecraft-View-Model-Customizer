package com.viewmodel.client.gui;

import com.viewmodel.ViewModelConfig;
import com.viewmodel.ViewModelProfile;

import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;

enum ViewModelOption {
    SIZE("Size", 0, 2, .01, () -> ViewModelConfig.current.getSize(), ViewModelProfile.baseline().size(), v -> ViewModelConfig.current.setSize((float) v)),
    X("Position X", -100, 100, .5, () -> ViewModelConfig.current.getPositionX(), ViewModelProfile.baseline().positionX(), v -> ViewModelConfig.current.setPositionX((float) v)),
    Y("Position Y", -100, 100, .5, () -> ViewModelConfig.current.getPositionY(), ViewModelProfile.baseline().positionY(), v -> ViewModelConfig.current.setPositionY((float) v)),
    Z("Position Z", -100, 100, .5, () -> ViewModelConfig.current.getPositionZ(), ViewModelProfile.baseline().positionZ(), v -> ViewModelConfig.current.setPositionZ((float) v)),
    YAW("Yaw", -180, 180, 1, () -> ViewModelConfig.current.getRotationYaw(), ViewModelProfile.baseline().rotationYaw(), v -> ViewModelConfig.current.setRotationYaw((float) v)),
    PITCH("Pitch", -180, 180, 1, () -> ViewModelConfig.current.getRotationPitch(), ViewModelProfile.baseline().rotationPitch(), v -> ViewModelConfig.current.setRotationPitch((float) v)),
    ROLL("Roll", -180, 180, 1, () -> ViewModelConfig.current.getRotationRoll(), ViewModelProfile.baseline().rotationRoll(), v -> ViewModelConfig.current.setRotationRoll((float) v));

    final String label;
    final double min;
    final double max;
    final double step;
    final DoubleSupplier value;
    final double baseline;
    final DoubleConsumer setter;

    ViewModelOption(String label, double min, double max, double step, DoubleSupplier value, double baseline, DoubleConsumer setter) {
        this.label = label;
        this.min = min;
        this.max = max;
        this.step = step;
        this.value = value;
        this.baseline = baseline;
        this.setter = setter;
    }
}
