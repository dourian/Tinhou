# notes & bugs

there appear to be some stuttering and jittery movement sometimes. typically this is either a slow computer or external monitor

the multithreading caused by calling repaint() instead of paintImmediately for the sake of performance can lead to some synchronization issues, mainly pertaining to entity list synchronization

this means that sometimes some entities can just disappear
