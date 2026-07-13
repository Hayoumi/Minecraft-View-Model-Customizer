# View Model Customizer

<p align="center">
  <img src="https://raw.githubusercontent.com/Hayoumi/Minecraft-View-Model-Customizer/refs/heads/main/src/main/resources/javaw_rs75vGoha5.png" alt="View Model Customizer in game" width="600"/>
</p>

Client-side Fabric mod for adjusting how held items look in first person. Move, rotate and scale the viewmodel, save presets, and tune swing/equip animations without touching the server.

## Install

1. Install [Fabric Loader](https://fabricmc.net/use/installer/) and Fabric API for your Minecraft version.
2. Download the matching mod jar:
   - `View Model Customizer-mc1.21.11-3.3.jar` for Minecraft **1.21.11**;
   - `View Model Customizer-mc26.1.2-3.3.jar` for Minecraft **26.1.2**.
3. Put it in your instance's `mods` folder and launch the game.

The mod is client-side only; the server does not need to install it.

## Use

Press `V` to open the editor. The keybind can be changed in Minecraft's controls.

- **Size** - changes held-item scale.
- **Position X / Y / Z** - moves the item; position range is `-100` to `100`.
- **Yaw / Pitch / Roll** - rotates the item around its own axis.
- **Reset icon** - resets one setting; **Reset Profile** restores the active profile's defaults.
- **Profiles** - create, rename, delete and instantly switch between saved setups. Changes are saved automatically.
- **No Swing** - removes the side movement from the swing animation while keeping the item rotation.
- **Scale Swing** - makes swing animation respect your item scale.
- **No Equip** - disables the item draw/equip animation when switching items.

## Features

- Clean V3-style NanoVG interface with vector controls and Pixelify Sans.
- Named profiles with automatic debounced saving.
- Per-setting and full-profile reset controls.
- Local-axis item rotation, so Yaw, Pitch and Roll do not throw the item across the screen.
- Separate builds for Minecraft 1.21.11 and 26.1.2.

## Building

The 1.21.11 target uses Java 21 and Yarn mappings:

```powershell
.\gradlew.bat clean build
```

The 26.1.2 target is a separate Java 25 project because Minecraft 26.1.2 is unobfuscated and is not binary-compatible with 1.21.11:

```powershell
.\gradlew.bat -p versions\mc26 clean build
```

Run the second command with a Java 25 `JAVA_HOME`.

Artifacts are written to `build/libs` and `versions/mc26/build/libs` respectively.

## License

[MIT](LICENSE)
