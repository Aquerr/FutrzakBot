package io.github.aquerr.futrzakbot.entities;

import org.json.JSONObject;

public class Futrzak
{
    private String name;
    private String imagepath;
    private int exp;
    private String mood;

    public String getName()
    {
        return name;
    }

    public int getExp()
    {
        return exp;
    }

    public String getMood()
    {
        return mood;
    }

    public String getImagepath()
    {
        return imagepath;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setExp(int exp)
    {
        this.exp = exp;
    }

    public void setImagepath(String imagepath)
    {
        this.imagepath = imagepath;
    }

    public void setMood(String mood)
    {
        this.mood = mood;
    }

    public static Futrzak fromJSONObject(JSONObject jsonObject)
    {
        if (!jsonObject.has("Name") || !jsonObject.has("Exp") || !jsonObject.has("Mood") || !jsonObject.has("ImagePath"))
            throw new IllegalArgumentException("Provided JSONObject does not contain all properties needed to create a futrzak!");

        Futrzak futrzak = new Futrzak();
        futrzak.setName(jsonObject.getString("Name"));
        futrzak.setExp(jsonObject.getInt("Exp"));
        futrzak.setMood(jsonObject.getString("Mood"));
        futrzak.setImagepath(jsonObject.getString("ImagePath"));

        return futrzak;
    }
}
