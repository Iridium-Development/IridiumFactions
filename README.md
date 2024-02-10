# IridiumFactions
![GitHub](https://img.shields.io/github/license/Iridium-Development/IridiumFactions?color=FF5555)

## Introduction

IridiumFactions is a Factions plugin designed from the ground up for speed, performance, and excellence. It features an advanced missions system, a shop, island upgrades and boosters, teamwork and cooperation features, and so much more.

This plugin, as with all of Iridium Development's software, aims to deliver a premium image, streamlining the setup process and eliminating the headache of having to figure out the details, while also proving to be extremely configurable to create an unparalleled experience. 

Every aspect of the plugin has been carefully crafted to suit the server's needs, and between the extensive GUI customization and plethora of extra options, server owners can rest easy knowing that there's a Factions plugin out there that fits their needs. It's this one, if you were wondering.

## Getting Started

Currently, the only method of downloading the plugin is to compile it yourself.

Once you have a copy of the plugin (it should be a ``.jar`` file), simply place it in the ``server/plugins`` folder.

### Requirements
- [Vault](https://www.spigotmc.org/resources/vault.34315/) - Required Dependency

### Recomendations
- [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/)
- [DecentHolograms](https://www.spigotmc.org/resources/decentholograms-1-8-1-19-4-papi-support-no-dependencies.96927/)
- A spawn plugin 
- An economy plugin (features will be limited without one)
- [EssentialsX](https://essentialsx.net/)

### Compiling

Clone the repo, and run the [build.gradle.kts](https://github.com/Iridium-Development/IridiumFactions/blob/master/build.gradle.kts) script.

## Development

You may notice when compiling and developing against IridiumFactions that there is a significant portion of code that isn't located in this repo. That's because IridiumFactions is an extension of IridiumTeams, and also uses functions from IridiumCore.

- [IridiumCore](https://github.com/Iridium-Development/IridiumCore)
  - A sort of library for all of Iridium Development's plugins
- [IridiumTeams](https://github.com/Iridium-Development/IridiumTeams)
  - The generic plugin, which extends IridiumCore, and involves all of the code for team management, including leveling, missions, team members, the bank, etc.
- [IridiumFactions](https://github.com/Iridium-Development/IridiumFactions)
  - This plugin, which extends IridiumTeams, and houses its own code specific to Factions, such as chunk claiming.

When developing with IridiumFactions, you may have to reference all three repos for your purposes.

## Support

If you think you've found a bug, please make sure you isolate the issue down to IridiumFactions and its dependencies before posting an issue in our [Issues](https://github.com/Iridium-Development/IridiumFactions/issues) tab. While you're there, please follow our issues guidelines.

If you encounter any issues while using the plugin, feel free to join our support [Discord](https://discord.gg/6HJ73mWE7P).
