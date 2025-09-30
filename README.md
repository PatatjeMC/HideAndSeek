# Hide and Seek Plugin

## Over
Deze Minecraft-plugin voegt een Hide and Seek-spelmodus toe. Spelers worden verdeeld in twee teams: zoekers en verstoppers. Verstoppers kunnen zich verstoppen door te transformeren in blokken, terwijl zoekers pas na een aftelklok kunnen gaan zoeken. Deze plugin is in ongeveer 1 week gemaakt als challenge.

## Gameplay
- Verstoppers kunnen kiezen met welk blok ze moeten gaan verstoppen.
- Als een verstopper stil staat veranderd de verstopper in een fysiek blok inplaats van een entity.
- Verstoppers moeten 3 keer geraakt worden om af te gaan.
- Het spel eindigt als alle verstoppers zijn gevonden of als de timer is afgelopen.

## Technisch
- Maakt gebruik van ProtocolLib om packets te onderscheppen en aan te maken.
- Aan het eind van de game kan er een POST request gestuurd worden naar een API om game informatie te sturen.
