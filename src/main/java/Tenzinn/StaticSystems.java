package Tenzinn;

import com.hypixel.hytale.protocol.Position;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.protocol.packets.world.SpawnParticleSystem;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

public class StaticSystems {

    private static Method writeMethod;

    public StaticSystems() {
        try {
            Class<?> toClientPacketClass = Class.forName("com.hypixel.hytale.protocol.ToClientPacket");
            writeMethod = com.hypixel.hytale.server.core.io.PacketHandler.class.getMethod("write", toClientPacketClass);
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static void createParticleInPosition(Vector3d pos, String particle, PlayerRef playerRef){
        SpawnParticleSystem packet = new SpawnParticleSystem();

        switch (particle.toLowerCase()) {
            case "flairs" -> packet.particleSystemId = "flairs";
        }

        packet.position = new Position(pos.x, pos.y, pos.z);
        packet.scale = 1.0f;

        try { writeMethod.invoke(playerRef.getPacketHandler(), packet); }
        catch (IllegalAccessException | InvocationTargetException e) { throw new RuntimeException(e); }
    }
}
