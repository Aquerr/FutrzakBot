package io.github.aquerr.futrzakbot.discord.util;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PaginatedMessageEmbed
{
    private final List<MessageEmbed> pages;

    public PaginatedMessageEmbed(List<MessageEmbed> pages)
    {
        this.pages = pages;
    }

    public MessageEmbed getPage(int page)
    {
        if (page < 1)
            page = 1;
        else if (page > pages.size())
            page = pages.size();

        return this.pages.get(page - 1);
    }

    public List<MessageEmbed> getPages()
    {
        return pages;
    }

    public static PaginatedDescriptionMessageEmbedBuilder ofDescription(Collection<String> contents)
    {
        return new PaginatedDescriptionMessageEmbedBuilder(contents);
    }

    public static <T> PaginatedFieldsMessageEmbedBuilder<T> ofFields(Collection<T> contents)
    {
        return new PaginatedFieldsMessageEmbedBuilder<>(contents);
    }

    public interface PaginatedMessageEmbedBuilder
    {
        PaginatedMessageEmbed build();
    }

    public static class PaginatedDescriptionMessageEmbedBuilder implements PaginatedMessageEmbedBuilder
    {
        private Collection<String> contents;
        private Color color = Color.GREEN;
        private int linesPerPage = 10;

        public PaginatedDescriptionMessageEmbedBuilder(Collection<String> contents)
        {
            this.contents = contents;
        }

        public PaginatedDescriptionMessageEmbedBuilder color(Color color)
        {
            this.color = color;
            return this;
        }

        public PaginatedDescriptionMessageEmbedBuilder linesPerPage(int linesPerPage)
        {
            this.linesPerPage = linesPerPage;
            return this;
        }

        @Override
        public PaginatedMessageEmbed build()
        {
            if (contents == null)
                throw new IllegalArgumentException("Contents cannot be null!");


            List<EmbedBuilder> pages = new LinkedList<>();

            int page = 1;
            int totalPages = (int)Math.ceil((double) this.contents.size() / linesPerPage);
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setColor(color);
            embedBuilder.setFooter(page + "/" + totalPages);
            pages.add(embedBuilder);

            int alreadyAddedFieldsCount = 0;
            for (final String object : contents)
            {
                embedBuilder.appendDescription(object).appendDescription("\n");
                alreadyAddedFieldsCount++;

                if (alreadyAddedFieldsCount == linesPerPage)
                {
                    page++;
                    alreadyAddedFieldsCount = 0;

                    embedBuilder = new EmbedBuilder();
                    embedBuilder.setColor(color);
                    embedBuilder.setFooter(page + "/" + totalPages);
                    pages.add(embedBuilder);
                }
            }

            return new PaginatedMessageEmbed(pages.stream().map(EmbedBuilder::build).collect(Collectors.toList()));
        }
    }

    public static class PaginatedFieldsMessageEmbedBuilder<T> implements PaginatedMessageEmbedBuilder
    {
        private Collection<T> contents;
        private String title;
        private String titleUrl;
        private Color color = Color.GREEN;
        private int linesPerPage = 10;
        private Function<T, String> fieldNamePopulator;
        private Function<T, String> fieldValuePopulator;

        PaginatedFieldsMessageEmbedBuilder(Collection<T> contents)
        {
            this.contents = contents;
        }

        public PaginatedFieldsMessageEmbedBuilder<T> color(Color color)
        {
            this.color = color;
            return this;
        }

        public PaginatedFieldsMessageEmbedBuilder<T> linesPerPage(int linesPerPage)
        {
            this.linesPerPage = linesPerPage;
            return this;
        }

        public PaginatedFieldsMessageEmbedBuilder<T> fieldNamePopulator(Function<T, String> fieldNamePopulator)
        {
            this.fieldNamePopulator = fieldNamePopulator;
            return this;
        }

        public PaginatedFieldsMessageEmbedBuilder<T> fieldValuePopulator(Function<T, String> fieldValuePopulator)
        {
            this.fieldValuePopulator = fieldValuePopulator;
            return this;
        }

        public PaginatedFieldsMessageEmbedBuilder<T> title(String title, String url)
        {
            this.title = title;
            this.titleUrl = url;
            return this;
        }

        @Override
        public PaginatedMessageEmbed build()
        {
            if (contents == null || fieldNamePopulator == null || fieldValuePopulator == null)
                throw new IllegalArgumentException("Parameters cannot be null!");


            List<EmbedBuilder> pages = new LinkedList<>();
            int page = 1;
            int totalPages = (int)Math.ceil((double) this.contents.size() / linesPerPage);

            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setColor(color);
            embedBuilder.setTitle(title, titleUrl);
            embedBuilder.setFooter(page + "/" + totalPages);
            pages.add(embedBuilder);

            int alreadyAddedFieldsCount = 0;
            for (final T object : contents)
            {
                embedBuilder.addField(new MessageEmbed.Field(fieldNamePopulator.apply(object), fieldValuePopulator.apply(object), false));
                alreadyAddedFieldsCount++;

                if (alreadyAddedFieldsCount == linesPerPage)
                {
                    page++;
                    alreadyAddedFieldsCount = 0;

                    embedBuilder = new EmbedBuilder();
                    embedBuilder.setColor(color);
                    embedBuilder.setTitle(title, titleUrl);
                    embedBuilder.setFooter(page + "/" + totalPages);
                    pages.add(embedBuilder);
                }
            }

            return new PaginatedMessageEmbed(pages.stream().map(EmbedBuilder::build).collect(Collectors.toList()));
        }
    }
}
