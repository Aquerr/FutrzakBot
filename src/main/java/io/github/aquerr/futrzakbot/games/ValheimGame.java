package io.github.aquerr.futrzakbot.games;

import com.ibasco.agql.protocols.valve.source.query.client.SourceQueryClient;
import com.ibasco.agql.protocols.valve.steam.master.MasterServerFilter;
import com.ibasco.agql.protocols.valve.steam.master.client.MasterServerQueryClient;
import com.ibasco.agql.protocols.valve.steam.master.enums.MasterServerRegion;
import com.ibasco.agql.protocols.valve.steam.master.enums.MasterServerType;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;

@AllArgsConstructor
public class ValheimGame
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ValheimGame.class);
    private static final int VALHEIM_APP_ID = 892970;

    private final String valheimServerIp;

    public void checkValheimServerStatus()
    {
        String[] ipAndPort = valheimServerIp.split(":");

        try (SourceQueryClient sourceQueryClient = new SourceQueryClient())
        {
//            InetSocketAddress serverAddress = new InetSocketAddress(ipAndPort[0], Integer.parseInt(ipAndPort[1]));
            InetSocketAddress serverAddress = new InetSocketAddress(ipAndPort[0], Integer.parseInt(ipAndPort[1]));
            sourceQueryClient.getServerInfo(serverAddress).whenComplete((sourceServer, serverInfoError) -> {
                //Check if we received an error
                if (serverInfoError != null)
                {
                    LOGGER.debug("[SERVER : ERROR] : {}", serverInfoError.getMessage());
                    return;
                }
                //Received a server info message successfully
                LOGGER.debug("[SERVER : INFO] : {}", sourceServer);
            });
        }
        catch (Exception exception)
        {
            LOGGER.error("Could not check Valheim server status!", exception);
        }

        try(MasterServerQueryClient client = new MasterServerQueryClient())
        {
            MasterServerFilter masterServerFilter = MasterServerFilter.create()
//                    .dedicated(true)
//                    .hasNoPlayers(true)
//                    .hasServerIp(valheimServerIp)
                    .allServers()
                    .appId(VALHEIM_APP_ID);
            client.getServerList(MasterServerType.SOURCE, MasterServerRegion.REGION_EUROPE, masterServerFilter).whenComplete(((inetSocketAddresses, throwable) ->
            {
                if (inetSocketAddresses != null)
                {
                    LOGGER.info(inetSocketAddresses.toString());
                }
                else
                {
                    LOGGER.error("ERROR: {}", throwable.getMessage());
                }

            }));
        }
        catch (IOException exception)
        {
            LOGGER.error("Could not find Valheim server!", exception);
        }
    }
}
