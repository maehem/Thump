# Thump
Java port of Doom from original C source code.

Thump is an attempt to port Doom to Java using object oriented methodology and adapting the original source code from C-structs to objects and methods.

The archived branch called "pre-maven" is a Netbeans, Ant-based project.  This "master" branch is a Maven based project and comes with many code refactors and new module considerations, which are subject to change as development continues.

This branch will also focus on porting the AWT/Swing usage over to Java 13+, OpenJavaFX 13+ and Netbeans 12.1+.

The author has created this project for no other reason other than to study and transform the original Doom C-source to Java as well as stay versed on the use of JavaFX in a novel way.  This software is not intended to be used, sold or even ever finished.  The author reserves the right to abandon or "park" this code for indefinite amounts of time to go work on other interests.  You may do what you want with this code within the limits of the software license.  Learn, study, enjoy!

## "If I said it was 'Alpha', I'd be lying" edition.  Build 20.002

This release will compile and run under Java 14.
Menus work and a new game can be started.
Renderer renders "stuff".   Some portions of floors and ceilings seem to render properly.
Automap (tab key) renders OK.
Walls don't render at all.
User can move around the map a little, but game will go into an undetermined state rather quickly.

The next focus will be to get "Columns" (or slivers of wall textures) to render properly.

## Doom WAD files not included.
Even though this game barely runs, it still needs the DOOM1.WAD (etc) files in order to get very far.  I don't include them here since I don't know what the copyright is regarding them.  So you'll need to use your Googles and find the original WAD files to play with.   The game looks in your <user>/Documents/Doom/ directory for those files, so put them there.
  
  
