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

* [ ] Enable Alpha/Indev/Islands/Sky in world type creating new world.
* [ ] Add optional (controllable) armor to entities spawned in spawners.
* [ ] The "good" command parser.

* [ ] Block for general grass (tall grass, deadbush, barley, etc)
* [ ] Floating gen
* [ ] Separate rain / snow / thunderbolts like in infhell. Integrate the Weather class in world.
	* [X] rather than use baked in values for next rain etc, use values from the Weather class in updateWeather.
	* [ ] 
* [ ] Basic ramps. 
	* [ ] Use temperature to decide rain/snow and to add snow (you can switch this off)
* [ ] Vines & Waterlilies COME BACK! Add them by temperature or humidity ramp?
* [ ] Weather & Seasons (you can swith this off).
	* [ ] If seasons enabled...
		* [ ] getSkyColor hook to Seasons.getSkyColorForToday().
		* [ ] while updating daily tasks at the end of world.tick, calculate hour of the day, and on 18000 advance dayOfTheYear, change season if needed and adjust related shit.
		* [ ] Do related cover with snow shit.
		* [ ] receive dayOfTheYear from the server in NetClientHandler, Packet95UpdateDayOfTheYear, etc.
* [ ] Quiver.

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
