# Selecting units in BWU-Java

I developed selection framework in BWU to make it really easy selecting any units
depending on what you need, without any need for looping over all units
and searching for the right ones.

Anytime you want some units, you need to select the widest range of units
which you will scrape of to the perfect selection of what you need by methods
introduced in this framework.

Good starting point for selecting units is ``game.getMyUnits()`` for all my units (including
buildings!) or ``game.getEnemyUnits()`` for enemy units (also including buildings). These methods
returns an instances of ``UnitSet``, which is the backbone of whole framework. Anytime you do
something with ``UnitSet``, another (changed and new) ``UnitSet`` is returned to you that contains
units you requestd. Of course, these methods will return you unitsets containing all your units (or enemy units respectively).

## UnitSet architecture

Almost every method in ``UnitSet`` is used along with some sort of selector. Selector i