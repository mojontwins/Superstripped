# SuperStripped

## Make Infhell / early alpha (refined)

* [X] Compare stripped/terrain.png to a1.1.2/terrain.png and clean it up!

* [X] Original cactus block.

* [X] Block capabilities rather than blocID == checks.
	Base case in Block.java must return the most general check done originally.

	* [X] .canGrowPlants ()
	* [X] .canGrowCacti ()
	* [X] .canGrowMushrooms ()
	* [X] .supportsRedstone ()
	* [X] .supportsTorch () - For fences or upper slabs only on top, for example.
	* [ ] .isCrop () ? needed in tilledfield, handy if we are adding new kinds of growable shit.

* [X] ItemBlocks
	* [X] ItemDyeable. All dyeable shit must extend this, starting with cloth.
		To add more dyeable blocks, just make them implement IDyeableBlock and assign ItemDyeable as their associated ItemBlock in Block.java.

	* [X] ItemMetadata. All blocks that are different based upon metadata (simple) must use this.
		* Changed `ItemSapling` to extend `ItemMetadata`.
		* Added constructor (int itemID, Block block, int defaultSideForIcon). If the old constructor is used, defaultSideForIcon is 2.

	* [X] Items that must face you when placed like pumpkins must extend `BlockDirectional`. This makes you have to declare five methods that return which texture index use from top, botom, sides, face and back.
		* [X] Adapted pumpkins.

	* [X] Items that can be placed in three different axes such as logs bust extend `Block3Axes` and implement getTextureSides and getTextureEnds. Press LCTRL to override orientation and use meta 0 like in alpha/beta.

* [X] implement ILiquid / ISurface / IGround, blocks diggable by caves and ravines.

* [X] Sheep are dyeable. Must implement a IDyeableEntity so I can make more dyeable entities if needed.
	* Entity must implement admitsDyeing() -> in the case of EntitySheep it will return !theSheep.getSheared().
	* Entity must implement getDyeColor() -> getFleeceColor in sheep.
	* Entity must implement setDyeColor() -> setDyeColor in sheep.

* [X] Make permanent softlock based stuff and remove all release vanilla shit I won't be using.

* [X] Added IFireEntity, currently implemented by none. Replaces `instanceof EntityBlaze` in case I need fire based entities (that for instance get very hurt by snowballs).

## Add ASAP

* [X] Floating gen
* [X] Enable Alpha/Indev/Islands/Sky in world type creating new world.
* [X] Also in the server (server.properties)
* [X] Add optional (controllable) armor to entities spawned in spawners.
* [X] Put all redstone logic back in.
* [X] The "good" command parser.
* [X] Sponges.
* [ ] 8 bit metadata? Snowlogged shit needs this.

	* [X] Before: Make this able to read old alpha worlds!

	* All meta writing is @ ExtendedBlockStorage.
	* Modify Chunk's getChunkData & setChunkData.
	* Packet51MapChunk -> size of chunk data arrays?
	* Packet52MultiBlockChange -> encoding uses shorts with 12 bit + 4 bit and this will be a problem.
	* NetClientHandler.handleMultiBlockChange -> decode the above encoded shit.

	1.- Deobfuscate encoder/decoder, think of a different encoder/decoder.
	2.- Modify Packet51MapChunk Packet52MultiBlockChange NetClientHandler.handleMultiBlockChange.
	3.- Modify getChunkData setChunkData 
	4.- Modify ExtendedBlockStorage
	5.- Test.
 
* [X] Separate rain / snow / thunderbolts like in infhell. Integrate the Weather class in world.
	* [X] rather than use baked in values for next rain etc, use values from the Weather class in updateWeather.
* [X] Weather & Seasons (you can swith this off). This is a world config shit.
	* [X] Add it to WorldSettings / worldInfo
	* [X] GUI when creating a world
	* [X] Sync "enable seasons" from the server.
	* [X] If seasons enabled...
		* [X] getSkyColor hook to Seasons.getSkyColorForToday().
		* [X] while updating daily tasks at the end of world.tick, calculate hour of the day, and on 18000 advance dayOfTheYear, change season if needed and adjust related shit.
		* [X] Do related cover with snow shit - Snow won't pile up until I have 8bit meta
		* [X] receive dayOfTheYear from the server in NetClientHandler, Packet95UpdateDayOfTheYear, etc.
		* [X] day cycle duration
		* [X] Weather is as follows: use only rain, if temperature < X || Seasons.WINTER -> snow <- Implement a new particleDecide. - Nah, it is simmilar to Infhell but better.
		* [X] Particles? & leaf piles in autumn. 
		* [X] "Super fog" event.
		* [X] make sure leaf piles disappear.
		* [X] Make weather affect crops and saplings.
* [X] Show time in GUI only if sundial in inventory.
* [X] Gui for Game Rules.
	* [X] Must support being called from the pause menu or when creating a new world.
	* [X] Creating a new world should load the default configuration from disk *i.e. reset it*, and save if modified.
	* [X] When saving a world, current config is saved to the worldInfo.
	* [X] When loading a world, corrent config is loaded from the worldInfo.
	* [X] Menu can be opened from the pause menu and show current config *and not reset it*

```
	 [CAT1] [CAT2] [CAT3]
	 --------------------
	     [Option 1: ON]
	     [Option 2: OFF]
	     ...

	        [  OK  ]
```

* [X] Remove all languages.
* [ ] Reorganize class tree & packages like in old Indev.
* [X] Add old ways to get seeds until no tall grass
* [X] Restore water looks
* [X] Enemy armor in the server should get to the client somehow.
	The process of getting aware that a new mob has been spawned and send it to the clients...

	World.spanwEntityInWorld->WorldServer.obtainEntitySkin->World.obtainEntitySkin-> 

	To all worldAccesses.obtainEntitySkin --> to al WorldManagers -> 

	EntityTracker.trackEntity!! <- this is it. There's a case for IAnimals, let's check if the entity
	is also a instance of EntityArmoredMob and send the inventory?
	[ ] Are tiered spawners sending the correct data when spawning mobs?

* [X] Integrate new command parser in the server via chat commands.

	This need some thought. Commands should be sent to the server alongside mouse coordinates, and the results must be sent back as chat messages to the client that issued the command. 1st of all - I must change the packet to include the mouse position. I need "validMouseCoords" as a boolean and separate x, y, z to build a `BlockPos` object that may equal null on the server.

	In NetServerHandler, `handleChat` sends the commands starting with "/" to handleSlashCommand that ends up handing the TEXT to MinecraftServer, which enqueues and then processes it. This should be made a bit more complex, an object including command and the BlockPos object should be passed to, encoded and processed by MinecraftServer rather than a single String.

	This will be done to effectively replace `ConsoleCommandHandler` with our system, so I'll have to check if I need to add any commands (I will surely need to do this).

	Pure server commands: 
	* list -> serverConfigurationManager.
	* stop -> minecraftServer.
	* save-all -> worldServerm etc.
	* save-off -> ...
	* save-on
	* op
	* deop
	* ban-ip
	* pardon-ip
	* ban
	* pardon
	* kick
	* say
	* tell
	* whitelist
	* banlist

	* tp <- remove from here
	* give <- ""
	* gamemode
	* time
	* summon
	* toggledownfall

	Hmmm - Maybe the idea is removing the second list and hooking my system when no server command was recognized.

	In this case, ConsoleCommandHandler will become the ICommandSender. 

	BTW, setting the game mode:

```java

	i20 = Integer.parseInt(gameMode);
	i20 = WorldSettings.validGameType(i20);
	if(entityPlayerMP18.itemInWorldManager.getGameType() != i20) {
		this.sendNoticeToOps(username, "Setting " + entityPlayerMP18.username + " to game mode " + i20);
		entityPlayerMP18.itemInWorldManager.toggleGameType(i20);
		entityPlayerMP18.playerNetServerHandler.sendPacket(new Packet70Bed(3, i20));
	} else {
		this.sendNoticeToOps(username, entityPlayerMP18.username + " already has game mode " + i20);
	}

```

	Problem /tp user1 user2, how to.

	Let me first hook it "basic mode", and then some. Done!

* [ ] Cleanup the command parser a bit so different stuff is on different files that only exist in the client or in the server.

* [X] Check alpha chunk generator with NSSS's --> I get chunk borders on imported a1.1.2 worlds and that's a bug.
* [X] Edit signs with feathers.
* [ ] Examine/study how you "deawt" minecraft.

## Diverge -> turn this into b1.6.6 *strict* (but with the goodies).

## Ideas for 1st update

* [ ] Double the slots for worlds.
* [ ] New building blocks
* [ ] Water/floating dungeons. 
* [ ] Ramp based biomes?
	* [ ] Block for general grass (tall grass, deadbush, barley, etc)
	* [ ] Vines & Waterlilies COME BACK! Add them by temperature or humidity ramp?

## Ideas for 2nd update

* [ ] Magical Quiver <- when special dungeons are added.
* [ ] Special (big) dungeons.
* [ ] My own mineshafts.
* [ ] Themed mobs: Cold cows from Infhell, rotten skeletons for swamps, zombies turn into drowned, husks in deserts, boars that fight back in forests.
* [ ] Goats.

## FIX BUGS

* [ ] Check angle of placed chests.
* [X] Fix HUD (move armor gauge to the right bottom, etc).
* [ ] Skeleton spawners spawn armored skeletons?
* [X] Won't snow in "alpha cold, no biomes"? Make sure if seasons are off, leaves are lime green!
* [X] Fix extra weapons icons.
* [X] Recipe book recipe not working. -- It works but the icon is missing, also the tooltip.
* [X] Air hud wrong place (too low, overwriting hearts).
* [X] Armor hud reversed icons.
* [X] Recipe book breaks on one of the recipes involving cloth? with an out of bounds meta/damage when trying to find tint color?
	[ ] Crarfting recipe for painting shows whool as "black whool"  and needs meta = -1, this is the cause! I think I introduced this meaning "any meta" for crafting? Check and fix at recipe book.

### Morning fog design

* fogIntensity is how much bring forward the far plane.
* maxFogIntensityForToday will return 0-1.0F depending on the day. Precalc values, so starts raising in autumn, peak @ autumn #3, goes to .5 to the end of the year and then decreases gradually to .2 during winter and to 0 during 1st half of spring.
* fogIntensity must peak at maxFogIntensityForToday at dawn <- what time is this? [ It should be 0 at noon, and keep 0 until dawn. <- check angles and lerp with that. Sunrise is a=0.75, noon is 0.99->1->0, midnigth =0.5

Proposal:
	- Lerp 0 to 1 from 0.625 to 0.75
	- Lerp 1 to 0 from 0.75 to 0.99
	- 0 otherwise.

## Check

* [ ] Exploits with boats and carts must be doable, do some research.
* [ ] "Delete world" actually works?

## New block variants

* [ ] Bookshelf -> Bookshelf with covered sides, directional.

## Bugs

### GeneratorName mal

- En server ha cascao al generar chunks porque estaba llamando a las historias en ChunkProviderGenerate embéz de a las historias de ChunkProviderAlpha.
- VALE, tengo que quitar las cosas normales de ChunkProviderGenerate, pero el caso es que NO DEBERÍA ESTAR EJECUTANDO ESE CÓDIGO.
- En el level.dat pone generatorName: default aunque en el properties ponga level-type=alpha. 
- Primero arreglo que no se jorronche y luego arreglo lo del default ese.
- OK en vanilla llama a calcular los biomesforgeneration en una escala 4x menor que necesita para el suavisador de ruido entre biomas y yo lo quite de listillo.
- No sé en qué momento cambió el generatorName, pero no puedo reproducirlo

### Errores de lus en el server ?!

En el cliente, en ChunkProvider, se llama a initLightingForRealNotJustHeightmap tras onChunkLoad  a la hora de cargar un chunk del archivo. A lo mejor hay que ponerlo tambien en ChunkProviderServer a ver... YESSSS
