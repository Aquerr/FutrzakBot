package io.github.aquerr.futrzakbot.discord.games;

import io.github.aquerr.futrzakbot.FutrzakBot;
import io.github.aquerr.futrzakbot.discord.model.Futrzak;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.json.JSONObject;

import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FutrzakGame
{
    private final FutrzakBot futrzakBot;
    private final Path futrzaksDirPath;

    public FutrzakGame(FutrzakBot futrzakBot)
    {
        this.futrzakBot = futrzakBot;
        this.futrzaksDirPath = futrzakBot.getBotDirectory().resolve("futrzaki");
    }

    public void setup()
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

    public void createFutrzak(long guildId, String userId) throws IOException
    {
        createDirForGuildIfNeeded(guildId);

        Path futrzakPath = Paths.get(futrzaksDirPath.resolve(String.valueOf(guildId)) + "/" + userId + ".json");

        if (!Files.exists(futrzakPath))
            Files.createFile(futrzakPath);

        //Predefined futrzak
        JSONObject futrzakJson = new JSONObject();
        futrzakJson.put("Name", "Futrzak");
        futrzakJson.put("Exp", 0);
        futrzakJson.put("Mood", "Zadowolony ❤");
        //TODO: Set up a default imagepath.
        futrzakJson.put("ImagePath", "");

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

    private void createDirForGuildIfNeeded(long guildId)
    {
        if (!Files.exists(futrzaksDirPath.resolve(String.valueOf(guildId))))
        {
            try
            {
                Files.createDirectory(futrzaksDirPath.resolve(String.valueOf(guildId)));
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public boolean checkIfFutrzakExists(long guildId, String userId)
    {
        return Files.exists(Paths.get(futrzaksDirPath.resolve(String.valueOf(guildId)) + "/" + userId + ".json"));
    }

    public MessageEmbed displayFutrzak(long guildId, Member member)
    {
        Path futrzakPath = Paths.get(futrzaksDirPath.resolve(String.valueOf(guildId)) + "/" + member.getId() + ".json");

        try
        {
            File futrzakFile = futrzakPath.toFile();
            FileInputStream fileReader = new FileInputStream(futrzakFile);
            byte[] data = new byte[(int) futrzakFile.length()];
            fileReader.read(data);

            JSONObject jsonObject = new JSONObject(new String(data, "UTF-8"));
            Futrzak futrzak = Futrzak.fromJSONObject(jsonObject);

            EmbedBuilder embedBuilder = new EmbedBuilder();
            if(!futrzak.getImagepath().equals(""))
                embedBuilder.setImage(jsonObject.get("ImagePath").toString());

            embedBuilder.setTitle(futrzak.getName());
            embedBuilder.addField("Właściciel: ", member.getEffectiveName(), false);
            embedBuilder.addField("Doświadczenie: ", String.valueOf(futrzak.getExp()), false);
            embedBuilder.addField("Samopoczucie: ", futrzak.getMood(), false);
            embedBuilder.setColor(Color.GREEN);

            return embedBuilder.build();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    private boolean dirExists()
    {
        return Files.exists(futrzaksDirPath);
    }
}
