# SuperStripped

## Make Infhell / early alpha (refined)

* [X] Compare stripped/terrain.png to a1.1.2/terrain.png and clean it up!

* [X] Original cactus block.

* [ ] Block capabilities rather than blocID == checks.
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
		* [ ] make sure leaf piles disappear.
		* [X] Make weather affect crops and saplings.
* [X] Show time in GUI only if sundial in inventory.
* [ ] Gui for Game Rules.
* [X] Remove all languages.
* [ ] Reorganize class tree & packages like in old Indev.
* [X] Add old ways to get seeds until no tall grass
* [ ] Restore water looks

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
* [ ] Recipe book recipe not working. -- It works but the icon is missing, also the tooltip.
* [X] Air hud wrong place (too low, overwriting hearts).
* [X] Armor hud reversed icons.
* [ ] Recipe book breaks on one of the recipes involving cloth? with an out of bounds meta/damage when trying to find tint color?

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
