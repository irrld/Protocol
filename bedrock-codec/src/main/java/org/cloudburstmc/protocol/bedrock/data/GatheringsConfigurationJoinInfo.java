package org.cloudburstmc.protocol.bedrock.data;

import lombok.Value;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.UUID;

@Value
public class GatheringsConfigurationJoinInfo {

    UUID experienceId;
    @Nullable // since v1001
    String experienceName;
    UUID worldId;
    @Nullable // since v1001
    String worldName;
    String creatorId;
    UUID targetId;
    String scenarioId;
    String serverId;
}
