package io.github.aquerr.futrzakbot.config;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.jayway.jsonpath.spi.mapper.JakartaMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;

import java.util.EnumSet;
import java.util.Set;

public class JsonPathConfiguration
{
    public void configure()
    {
        Configuration.setDefaults(new Configuration.Defaults()
        {
            private final JsonProvider jsonProvider = new JacksonJsonProvider();
            private final MappingProvider mappingProvider = new JacksonMappingProvider();

            @Override
            public JsonProvider jsonProvider()
            {
                return jsonProvider;
            }

            @Override
            public Set<Option> options()
            {
                return EnumSet.noneOf(Option.class);
            }

            @Override
            public MappingProvider mappingProvider()
            {
                return mappingProvider;
            }
        });
    }
}
