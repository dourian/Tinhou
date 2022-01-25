# notes & bugs

The audio processing was specifically designed for Bag Raiders - Shooting Stars and songs similar to it. Pieces of music like piano solos or ambient music that sound too different can give the audio processor a hard time. If more time was allowed for this project, it would be possible to mitigate this flaw by adjusting certain coefficients and thresholds on the fly. 

there appear to be some stuttering and jittery movement sometimes. typically this is either a slow computer or external monitor

the multithreading caused by calling repaint() instead of paintImmediately for the sake of performance can lead to some synchronization issues, mainly pertaining to entity list synchronization

this means that on rare occasions some entities can just disappear

the menu is 700x1000 but the game is resized to 1600x900 for more dodging room. 

#responsibilities

Maxwell
 - sound processing and interpreting
 - Game engine
 - playtesting

Dorian
 - Sprite and graphic drawing
 - GUI
 - Scoreboard
 - playtesting
 
#hints

NO PAUSING.

there is a bullet spawner that follows you around. it is only powered by the bassy end of the audio.

#additional function

we removed the pause screen because it was holding us back.

mouse input support

#important info

sometimes there is a rare win/lose screen. see if you can get it :)
