package Tenzinn.Systems.Camera;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.*;
import com.hypixel.hytale.event.EventRegistry;
import com.hypixel.hytale.event.IEventRegistry;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.entity.entities.player.CameraManager;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.protocol.packets.camera.SetServerCamera;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import java.util.concurrent.CopyOnWriteArrayList;

public class CameraThePrice {

    private final EventRegistry eventRegistry;
    private ServerCameraSettings cameraSettings;
    private boolean isActive = false;

    public float distance = 10.0f;
    private float rotationCamera = 5.4f;

    public CameraThePrice() {
        IEventRegistry eventBus = HytaleServer.get().getEventBus();
        this.eventRegistry = new EventRegistry(new CopyOnWriteArrayList<>(), () -> this.isActive, "CameraDemo", eventBus);
    }

    public void setDistance(float value) {
        distance = value;
        activate(rotationCamera);
    }

    public void activate(float rotation) {
        if (isActive) {
            deactivate();
            activate(rotation);
        }

        rotationCamera = rotation;
        this.cameraSettings = buildTopDownSettings();
        this.isActive = true;
        this.eventRegistry.enable();
    }

    public void deactivate() {
        if (!isActive) return;

        isActive = false;
        eventRegistry.shutdownAndCleanup(true);
        com.hypixel.hytale.server.core.universe.Universe.get().getPlayers().forEach(this::resetPlayerCamera);
    }

    public void resetPlayerCamera(PlayerRef playerRef) {
        Ref<EntityStore> ref = playerRef.getReference();
        if (!ref.isValid()) return;

        Store<EntityStore> store = ref.getStore();
        CameraManager cameraManager = (CameraManager) store.getComponent(ref, CameraManager.getComponentType());

        if (cameraManager != null) {
            cameraManager.resetCamera(playerRef);
        }
    }

    public void applyToPlayer(PlayerRef playerRef) {
        if (!isActive) { throw new IllegalStateException("CameraDemo no está activa. Llamá activate() primero."); }

        sendCameraPacket(playerRef);
    }

    private ServerCameraSettings buildTopDownSettings() {
        ServerCameraSettings s = new ServerCameraSettings();

        s.positionLerpSpeed = 0.08f;
        s.rotationLerpSpeed = 0.08f;
        s.distance = distance;
        s.isFirstPerson = false;
        s.eyeOffset = false;
        s.displayCursor = true;
        s.sendMouseMotion = true;

        s.movementForceRotationType = MovementForceRotationType.AttachedToHead;
        s.movementForceRotation = new Direction(0.0f, 0.0f, 0.0f);

        s.positionDistanceOffsetType = PositionDistanceOffsetType.DistanceOffset;
        s.rotationType = RotationType.Custom;
        s.rotation = new Direction(0.0f, rotationCamera, 0.0f);

        s.mouseInputType = MouseInputType.LookAtPlane;
        s.planeNormal = new Vector3f(0.0f, 1.0f, 0.0f);

        return s;
    }

    private void sendCameraPacket(PlayerRef playerRef) {
        ToClientPacket packet = new SetServerCamera(ClientCameraView.Custom, true, cameraSettings);
        playerRef.getPacketHandler().writeNoCache(packet);
    }

    public boolean getState() { return isActive; }
}