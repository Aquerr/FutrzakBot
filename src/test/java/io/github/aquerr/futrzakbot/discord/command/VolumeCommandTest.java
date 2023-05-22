package io.github.aquerr.futrzakbot.discord.command;

import io.github.aquerr.futrzakbot.discord.message.MessageSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class VolumeCommandTest {

    private static final String VOLUME_COMMAND_NAME_KEY = "command.volume.name";
    private static final String VOLUME_COMMAND_DESCRIPTION_KEY = "command.volume.description";
    private static final String VOLUME_COMMAND_NAME = "Volume command name";
    private static final String COMMAND_VOLUME_DESCRIPTION = "Volume command description";
    

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private VolumeCommand volumeCommand;
    
    @Test
    void getNameShouldReturnCorrectName()
    {
        given(messageSource.getMessage(VOLUME_COMMAND_NAME_KEY)).willReturn(VOLUME_COMMAND_NAME);

        assertThat(volumeCommand.getName()).isEqualTo(VOLUME_COMMAND_NAME);
    }

    @Test
    void getDescriptionShouldReturnCorrectDescription()
    {
        given(messageSource.getMessage(VOLUME_COMMAND_DESCRIPTION_KEY)).willReturn(COMMAND_VOLUME_DESCRIPTION);

        assertThat(volumeCommand.getDescription()).isEqualTo(COMMAND_VOLUME_DESCRIPTION);
    }

}