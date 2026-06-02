package org.cloudburstmc.protocol.bedrock.codec.v1001.serializer;

import io.netty.buffer.ByteBuf;
import org.cloudburstmc.protocol.bedrock.codec.BedrockCodecHelper;
import org.cloudburstmc.protocol.bedrock.codec.v944.serializer.StartGameSerializer_v944;
import org.cloudburstmc.protocol.bedrock.data.GatheringsConfigurationJoinInfo;
import org.cloudburstmc.protocol.bedrock.packet.StartGamePacket;
import org.cloudburstmc.protocol.common.util.VarInts;

public class StartGameSerializer_v1001 extends StartGameSerializer_v944 {

    public static final StartGameSerializer_v1001 INSTANCE = new StartGameSerializer_v1001();

    @Override
    protected void writeLevelSettings(ByteBuf buffer, BedrockCodecHelper helper, StartGamePacket packet) {
        super.writeLevelSettings(buffer, helper, packet);

        VarInts.writeInt(buffer, packet.getServerEditorConnectionPolicy());
        buffer.writeBoolean(packet.isAllowAnonymousBlockDropsInEditorWorlds());
    }

    @Override
    protected void readLevelSettings(ByteBuf buffer, BedrockCodecHelper helper, StartGamePacket packet) {
        super.readLevelSettings(buffer, helper, packet);

        packet.setServerEditorConnectionPolicy(VarInts.readInt(buffer));
        packet.setAllowAnonymousBlockDropsInEditorWorlds(buffer.readBoolean());
    }

    @Override
    protected void readBeforeNetworkPermissions(ByteBuf buffer, BedrockCodecHelper helper, StartGamePacket packet) {
        packet.setNetworkPermissions(this.readNetworkPermissions(buffer, helper));

        packet.setLoggingChat(buffer.readBoolean());
    }

    @Override
    protected void writeBeforeNetworkPermissions(ByteBuf buffer, BedrockCodecHelper helper, StartGamePacket packet) {
        this.writeNetworkPermissions(buffer, helper, packet.getNetworkPermissions());

        buffer.writeBoolean(packet.isLoggingChat());
    }

    @Override
    protected void writeGatheringsConfiguration(ByteBuf buf, BedrockCodecHelper h, GatheringsConfigurationJoinInfo info) {
        h.writeUuid(buf, info.getExperienceId());
        h.writeOptionalNull(buf, info.getExperienceName(), h::writeString);
        h.writeUuid(buf, info.getWorldId());
        h.writeOptionalNull(buf, info.getWorldName(), h::writeString);
        h.writeString(buf, info.getCreatorId());
        h.writeUuid(buf, info.getTargetId());
        h.writeString(buf, info.getScenarioId());
        h.writeString(buf, info.getServerId());
    }

    @Override
    protected GatheringsConfigurationJoinInfo readGatheringsConfiguration(ByteBuf buf, BedrockCodecHelper h) {
        return new GatheringsConfigurationJoinInfo(
                h.readUuid(buf),
                h.readOptional(buf, null, h::readString),
                h.readUuid(buf),
                h.readOptional(buf, null, h::readString),
                h.readString(buf),
                h.readUuid(buf),
                h.readString(buf),
                h.readString(buf)
        );
    }
}
