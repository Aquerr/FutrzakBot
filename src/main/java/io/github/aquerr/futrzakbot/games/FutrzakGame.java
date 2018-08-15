package io.github.aquerr.futrzakbot.games;

import io.github.aquerr.futrzakbot.FutrzakBot;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import org.json.JSONObject;

import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class FutrzakGame
{
    private static final Path futrzaksDirPath = FutrzakBot.botDir.resolve("futrzaki");

    public static void setup()
    {
        if(!dirExists())
        {
            try
            {
                Files.createDirectory(futrzaksDirPath);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public static void createFutrzak(String guildId, String userId) throws IOException
    {
        createDirForGuildIfNeeded(guildId);

        Path futrzakPath = futrzaksDirPath.resolve(guildId).resolve(userId + ".json");

        if (!Files.exists(futrzakPath))
            Files.createFile(futrzakPath);

        //Predefined futrzak
        JSONObject futrzakJson = new JSONObject();
        futrzakJson.append("Name", "Futrzak");
        futrzakJson.append("Exp", 0);
        futrzakJson.append("Feeling", "Zadowolony");
        //TODO: Set up a default imagepath.
        futrzakJson.append("ImagePath", "");

        FileWriter fileWriter = null;
        try
        {
            fileWriter = new FileWriter(futrzakPath.toFile());
            fileWriter.write(futrzakJson.toString());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        if (fileWriter != null)
            fileWriter.close();
    }

    private static void createDirForGuildIfNeeded(String guildId)
    {
        if (!Files.exists(futrzaksDirPath.resolve(guildId)))
        {
            try
            {
                Files.createDirectory(futrzaksDirPath.resolve(guildId));
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public static boolean checkIfFutrzakExists(String guildId, String userId)
    {
        return Files.exists(futrzaksDirPath.resolve(guildId).resolve(userId + ".json"));
    }

    public static MessageEmbed displayFutrzak(String guildId, String userId)
    {
        Path futrzakPath = futrzaksDirPath.resolve(guildId).resolve(userId + ".json");

        try
        {
            File futrzakFile = futrzakPath.toFile();
            FileInputStream fileReader = new FileInputStream(futrzakFile);
            byte[] data = new byte[(int) futrzakFile.length()];
            fileReader.read(data);

            JSONObject jsonObject = new JSONObject(new String(data, "UTF-8"));

            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setImage(jsonObject.get("ImagePath").toString());
            embedBuilder.setTitle(jsonObject.get("name").toString());
//            embedBuilder.addField("");

//            embedBuilder.setAuthor("Futrzak został stworzyony przez Nerdiego", "https://github.com/Aquerr/FutrzakBot");
//            embedBuilder.setColor(Color.GREEN);
////            embedBuilder.setTitle("Lista komend");
//            embedBuilder.setDescription("Oto spis komend, dostępnych u futrzaka: ");
//
//            embedBuilder.addField(new MessageEmbed.Field(":boom: Ruletka: ", "!futrzak ruletka", false));
//            embedBuilder.addField(new MessageEmbed.Field(":thought_balloon: Cytat: ", "!futrzak cytat", false));
//            embedBuilder.addField(new MessageEmbed.Field(":question: 8Ball: ", "!futrzak 8ball", false));
//            embedBuilder.addField(new MessageEmbed.Field(":microphone2: Dołącz na kanał głosowy: ", "!futrzak join", false));
//            //embedBuilder.addBlankField(false);
//            embedBuilder.addField(new MessageEmbed.Field(":heart: Licznik miłości: ", "!futrzak love", false));
//            embedBuilder.addField(new MessageEmbed.Field(":tiger: Stworz swojego futrzaka: ", "!futrzak stworz", false));
//            embedBuilder.addField(new MessageEmbed.Field(":crossed_swords: Walcz z innym futrzakiem: ", "!futrzak walka", false));

            return embedBuilder.build();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    private static boolean dirExists()
    {
        return Files.exists(futrzaksDirPath);
    }

    private class Futrzak
    {
        private String name;
        private String imagepath;
        private int exp;
        private String mood;
    }
}
