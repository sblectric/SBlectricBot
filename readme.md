## SBlectricBot for Java/Twitch

SBlectricBot is a Twitch chat bot that implements the features of 
[Twitch Chat Bot Library](https://github.com/tylerhasman/Twitch-Bot-Library) and [PircBotX](https://github.com/thelq/pircbotx).

As such, these projects are required dependencies.

SBlectricBot comes with a wide array of of features.

### Getting Started

To get started, simply create a ./db folder in the working directory with the following files:

1. channel.txt, with the contents only being \#\<your twitch channel name\>
2. username.txt, with the contents only being your twitch bot account name
3. oath.txt, with the contents only being oauth:\<your twitch bot account oauth key\>

And optionally:

4. mentions.txt, with each line being something the bot will say randomly when mentioned in chat