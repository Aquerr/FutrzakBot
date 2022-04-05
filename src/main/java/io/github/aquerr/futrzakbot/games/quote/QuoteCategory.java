package io.github.aquerr.futrzakbot.games.quote;

import lombok.Data;

import java.util.List;

@Data
public class QuoteCategory
{
    private String name;
    private List<String> aliases;
    private List<String> quotes;
}
