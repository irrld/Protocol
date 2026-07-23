package org.cloudburstmc.protocol.bedrock.codec.v2168.serializer;

import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import org.cloudburstmc.protocol.bedrock.codec.BedrockCodecHelper;
import org.cloudburstmc.protocol.bedrock.codec.v544.serializer.ClientboundMapItemDataSerializer_v544;
import org.cloudburstmc.protocol.bedrock.data.MapDecoration;
import org.cloudburstmc.protocol.bedrock.data.MapTrackedObject;
import org.cloudburstmc.protocol.bedrock.packet.ClientboundMapItemDataPacket;
import org.cloudburstmc.protocol.common.util.Preconditions;
import org.cloudburstmc.protocol.common.util.VarInts;

import java.util.ArrayList;
import java.util.List;

public class ClientboundMapItemDataSerializer_v2168 extends ClientboundMapItemDataSerializer_v544 {

    public static final ClientboundMapItemDataSerializer_v2168 INSTANCE = new ClientboundMapItemDataSerializer_v2168();

    @Override
    public void serialize(ByteBuf buffer, BedrockCodecHelper helper, ClientboundMapItemDataPacket packet) {
        VarInts.writeLong(buffer, packet.getUniqueMapId());
        buffer.writeByte(packet.getDimensionId());
        buffer.writeBoolean(packet.isLocked());
        helper.writeVector3i(buffer, packet.getOrigin());

        int[] colors = packet.getColors();
        List<MapDecoration> decorations = packet.getDecorations();
        List<MapTrackedObject> trackedObjects = packet.getTrackedObjects();
        LongList trackedEntityIds = packet.getTrackedEntityIds();

        if (trackedEntityIds != null) {
            buffer.writeBoolean(true);
            VarInts.writeUnsignedInt(buffer, packet.getTrackedEntityIds().size());
            for (long trackedEntityId : packet.getTrackedEntityIds()) {
                VarInts.writeLong(buffer, trackedEntityId);
            }
        } else {
            buffer.writeBoolean(false);
        }

        if (packet.getScale() != null) {
            buffer.writeBoolean(true);
            buffer.writeByte(packet.getScale());
        } else {
            buffer.writeBoolean(false);
        }

        if (trackedObjects != null) {
            buffer.writeBoolean(true);
            VarInts.writeUnsignedInt(buffer, trackedObjects.size());
            for (MapTrackedObject object : trackedObjects) {
                buffer.writeIntLE(object.getType().ordinal());
                switch (object.getType()) {
                    case ENTITY:
                        buffer.writeBoolean(true);
                        VarInts.writeLong(buffer, object.getEntityId());
                        buffer.writeBoolean(false);
                        break;
                    case BLOCK:
                        buffer.writeBoolean(false);
                        buffer.writeBoolean(true);
                        helper.writeBlockPosition(buffer, object.getPosition());
                        break;
                    default:
                        throw new IllegalArgumentException();
                }
            }
        } else {
            buffer.writeBoolean(false);
        }

        if (decorations != null) {
            buffer.writeBoolean(true);
            VarInts.writeUnsignedInt(buffer, decorations.size());
            for (MapDecoration decoration : decorations) {
                buffer.writeByte(decoration.getImage());
                buffer.writeByte(decoration.getRotation());
                buffer.writeByte(decoration.getXOffset());
                buffer.writeByte(decoration.getYOffset());
                helper.writeString(buffer, decoration.getLabel());
                buffer.writeIntLE(decoration.getColor());
            }
        } else {
            buffer.writeBoolean(false);
        }

        if (packet.getWidth() != null) {
            buffer.writeBoolean(true);
            VarInts.writeInt(buffer, packet.getWidth());
        } else {
            buffer.writeBoolean(false);
        }

        if (packet.getHeight() != null) {
            buffer.writeBoolean(true);
            VarInts.writeInt(buffer, packet.getHeight());
        } else {
            buffer.writeBoolean(false);
        }

        if (packet.getXOffset() != null) {
            buffer.writeBoolean(true);
            VarInts.writeInt(buffer, packet.getXOffset());
        } else {
            buffer.writeBoolean(false);
        }

        if (packet.getYOffset() != null) {
            buffer.writeBoolean(true);
            VarInts.writeInt(buffer, packet.getYOffset());
        } else {
            buffer.writeBoolean(false);
        }

        if (colors != null) {
            buffer.writeBoolean(true);
            VarInts.writeUnsignedInt(buffer, colors.length);
            for (int color : colors) {
                buffer.writeIntLE(color);
            }
        } else {
            buffer.writeBoolean(false);
        }
    }

    @Override
    public void deserialize(ByteBuf buffer, BedrockCodecHelper helper, ClientboundMapItemDataPacket packet) {
        packet.setUniqueMapId(VarInts.readLong(buffer));
        packet.setDimensionId(buffer.readUnsignedByte());
        packet.setLocked(buffer.readBoolean());
        packet.setOrigin(helper.readVector3i(buffer));

        if (buffer.readBoolean()) {
            LongList trackedEntityIds = new LongArrayList();
            int length = VarInts.readUnsignedInt(buffer);
            for (int i = 0; i < length; i++) {
                trackedEntityIds.add(VarInts.readLong(buffer));
            }
            packet.setTrackedEntityIds(trackedEntityIds);
        }

        if (buffer.readBoolean()) {
            packet.setScale(buffer.readByte());
        }

        if (buffer.readBoolean()) {
            List<MapTrackedObject> trackedObjects = new ArrayList<>();
            int length = VarInts.readUnsignedInt(buffer);
            for (int i = 0; i < length; i++) {
                MapTrackedObject.Type objectType = MapTrackedObject.Type.values()[buffer.readIntLE()];
                if (buffer.readBoolean()) {
                    trackedObjects.add(new MapTrackedObject(VarInts.readLong(buffer)));
                }
                if (buffer.readBoolean()) {
                    trackedObjects.add(new MapTrackedObject(helper.readBlockPosition(buffer)));
                }
            }
            packet.setTrackedObjects(trackedObjects);
        }

        if (buffer.readBoolean()) {
            List<MapDecoration> decorations = new ArrayList<>();
            int length = VarInts.readUnsignedInt(buffer);
            for (int i = 0; i < length; i++) {
                int image = buffer.readByte();
                int rotation = buffer.readUnsignedByte();
                int xOffset = buffer.readUnsignedByte();
                int yOffset = buffer.readUnsignedByte();
                String label = helper.readString(buffer);
                int color = buffer.readIntLE();
                decorations.add(new MapDecoration(image, rotation, xOffset, yOffset, label, color));
            }
            packet.setDecorations(decorations);
        }

        if (buffer.readBoolean()) {
            packet.setWidth(VarInts.readInt(buffer));
        }

        if (buffer.readBoolean()) {
            packet.setHeight(VarInts.readInt(buffer));
        }

        if (buffer.readBoolean()) {
            packet.setXOffset(VarInts.readInt(buffer));
        }

        if (buffer.readBoolean()) {
            packet.setYOffset(VarInts.readInt(buffer));
        }

        if (buffer.readBoolean()) {
            int length = VarInts.readUnsignedInt(buffer);
            Preconditions.checkArgument(buffer.isReadable(length), "Not enough readable bytes");
            int[] colors = new int[length];
            for (int i = 0; i < length; i++) {
                colors[i] = buffer.readIntLE();
            }
            packet.setColors(colors);
        }
    }
}
