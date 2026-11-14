# Realistic Cannonball Mod

[**Türkçe**](docs/README.tr.md) · [**Builds on GitHub Actions**](https://github.com/KaanKaya17/MinecraftCannonballMod/actions/workflows/build.yml?query=branch%3Amain)

A Fabric mod for Minecraft 1.20.1 that adds a mountable field cannon, cannonballs, and a ramrod. Projectiles can ricochet or break blocks depending on what they hit. The cannon has a custom model, recoil animation, sounds, and smoke effects.

## Install

1. Set up Minecraft Java Edition **1.20.1** with **Fabric Loader 0.19.5 or later** and Java **17 or later**.
2. Put the [Fabric API for Minecraft 1.20.1](https://modrinth.com/mod/fabric-api/versions?g=1.20.1) in your instance's `mods` folder. The project builds against Fabric API `0.92.12+1.20.1`.
3. Build the mod using the steps below or open a successful [GitHub Actions build](https://github.com/KaanKaya17/MinecraftCannonballMod/actions/workflows/build.yml?query=branch%3Amain) and download **Artifacts** if it is still available. GitHub requires sign-in for workflow artifacts.
4. Copy `cannonball-1.0.0.jar` into `mods`, then launch the Fabric profile. For a dedicated server, install the mod and Fabric API on the server and on each connecting client.

## Play

- **Place and move.** Use the Field Cannon item on a block. Right-click the placed cannon with an empty hand to mount it. Look around to aim, use the forward and backward keys to move, and use the sneak key to dismount.
- **Load and light.** Use one to three gunpowder items, then add an Iron Cannonball. A Ramrod packs the round. Flint and Steel or a Fire Charge packs it automatically if needed and lights the fuse.
- **Use redstone.** A loaded and rammed cannon fires when it detects a signal at its position or the block below. Vanilla dispensers can launch Iron Cannonballs directly.

## Crafting

| Item | Ingredients | Pattern, top to bottom |
| --- | --- | --- |
| Field Cannon | 4 iron blocks (`B`), 2 iron ingots (`I`), 1 oak log (`L`) | `.B.` / `BBB` / `ILI` |
| Iron Cannonball ×4 | 4 iron ingots (`I`), 1 coal (`C`) | `.I.` / `ICI` / `.I.` |
| Ramrod | 1 iron ingot (`I`), 2 sticks (`S`) | `..I` / `.S.` / `S..` |

Dots indicate empty crafting slots. [Recipe files](src/main/resources/data/cannonball/recipes)

## Build from source

Clone the repository and run the Gradle wrapper from the project root. Use **JDK 25** for the build to match GitHub Actions. The compiled mod targets Java 17.

```sh
git clone https://github.com/KaanKaya17/MinecraftCannonballMod.git
cd MinecraftCannonballMod
./gradlew build
```

On Windows, run `.\gradlew.bat build`. Use `build/libs/cannonball-1.0.0.jar` to install the mod. Run `./gradlew runClient` or `.\gradlew.bat runClient` to start a development client.

When reporting a bug, include your Minecraft, Fabric Loader, and Fabric API versions, whether it happened in single-player or on a server, and steps to reproduce. [GitHub Issues](https://github.com/KaanKaya17/MinecraftCannonballMod/issues)

## License

[MIT](LICENSE) · Kaan Kaya
