# notes & bugs

there appear to be some stuttering and jittery movement sometimes. typically this is either a slow computer or external monitor

the multithreading caused by calling repaint() instead of paintImmediately for the sake of performance can lead to some synchronization issues, mainly pertaining to entity list synchronization

this means that sometimes some entities can just disappear

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

none. we removed the pause screen because it was holding us back.

#important info

sometimes there is a rare win/lose screen. see if you can get it :)
