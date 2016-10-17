# Setting up an environment for BWU-Java

## 1. For who

I wrote this tutorial for people that want to build their own bot for [Student StarCraft AI Tournament](http://sscaitournament.com) or 
[StarCraft Micro AI Tournament](http://scmai.hackcraft.sk). These tournaments use the latest JNIBWAPI as I think many tournaments do too. I
think it is possible to use *BWU-Java* for other versions/tournaments too, but there may be little tweaks required.

## 2. Prerequisites / downloads

First, you need few things to download before you even start to configure your environment.

1. **StarCraft: BroodWar** Obviously you need the game to test your own bot, so please obtain one, it is really cheap these days.
2. **Java SE JDK** You need java development kit installed to write and build your JAR files (traditional runnable JARs generated from your favourite IDE). If you already wrote something in Java and ran it, you probably have this already.
3. **BWU-Java** latest release, which can be found in repository folder ``release``. It already contains other libraries and tools that are needed.
4. **Eclipse/IDE** latest release. I use eclipse during my development, but I don't see any reason why shoudln't you use other IDE. However, all my
tutorials will be explained using Eclipse examples.

## 3. Installation / configuration

This is the hard part and if you do something different (and you are not entirely sure why you do so and what are the consequences) you can
end up with broken development environment, so be careful.

To be finished.
1. **Installing StarCraft** You need to install the game. Nothing special here, just remember where you installed it (let's say C:\StarCraft for further reference). Don't forget to install the BroodWar extension of course.

2. TBC BWAPI unzip to proper folders
3. TBC Chaoslauncher linking
4. Configure IDE to properly locate JNIBWAPI and client-bridge
5. Try to compile the testing bot.

## 4. Congratulations

If you reached this point of the tutorial and you had no problems or you solved them already, you are ready to start building your own bot for the tournament. You can now continue to next tutorial where I will explain basic bot structure in **BWU-Java**.

# Questions and answers

## Can I create my bot on Linux?

Yes, it's possible, but I don't recommend it. I myself am a linux user and I don't use Windows unless I need to,
but this is the case where it is much more comfortable to run (for example) VirtualBox machine and develop your bot
on Windows. It is possible to develop with ``wine`` under linux without virtualization, but from my own experience 
it is much harder and slower (particulary in cases when you need debugging and fast rebuilds) than coding in virtual machine.

So, my advice: Use Windows, Windows XP is recommended but newer would be just fine. It doesn't matter if it is virtualized,
StarCraft, Java and IDE don't take much resources, so it won't be so slower than native OS.

## Can't I use another programming language?

*BWU-Java* is pure Java library aimed at Java AI development. There are many possibilities if you want to use C++ for example, but not with this library.
