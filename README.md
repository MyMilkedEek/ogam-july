= ogam-july =

This is a game project for the *One Game a Month* challenge
(July-13). This is a collaborative effort between Eek and Claus.

The basic idea of the game is to be a Qix clone.

== Game Outline ==
=== Gameplay ===

The game is a Qix Clone. A small ship moves on a "catwalk" around a
dangerous area. The ship can cut the dangerous area, but it is
vulnerable as it does so. The goal of each stage is to reduce the
dangerous area to a certain percentage of its original size.

=== Goal of the game ===

Accumulating points. Points are accumulated by:
+ Time to complete the stage
+ Enemies Killed
+ Bonus Grabbed

=== How to die ===

The player has a number of lives. There is a timer when cutting the
danger zone - if this timer reaches zero, the player dies

=== Gimmicks ===
+ Upgrades?
+ High Scores?
+ Infinite levels, or ending (based on theme) 

== Game Interface ==

1. *Touch Based*:

Touch the screen to move the "spaceship". There are two touch
states: Cut and Move. States are switched by pressing a button in an
interface

2. *Other ideas*:

Since this is an action game, we should think about better
interfaces if possible.

== Game Calculus  ==

=== How the Ship Moves ===
+ Movement on the catwalk:

The player touches on a point on the screen: the ship will move to the
point in the catwalk closest to that point.

The catwalk is composed of vectors and points. The ship is always on a
vector or point. 

The ship calculates which os the neighboring points is closes to the
goal point, and moves in that direction.



== Lifecycle ==

- Splash Screen:
  Loads assets.

- Menu Screen:
  Goes to: Start Game, Settings, High Scores, Help

- Main Game:
  Goes to Start Game, High Scores, Next Level, End Game (endless?)