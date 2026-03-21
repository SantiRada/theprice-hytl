package Tenzinn.Systems.Camera;

import com.hypixel.hytale.protocol.*;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.event.EventRegistry;
import com.hypixel.hytale.event.IEventRegistry;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.protocol.packets.camera.SetServerCamera;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.entity.entities.player.CameraManager;

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
    public void activate(String plane) {
        if (isActive) {
            deactivate();
            activate(plane);
        }

        switch(plane.toLowerCase()) {
            case "terrenal": this.cameraSettings = buildTerrenalSettings(); break;
            case "celestial": this.cameraSettings = buildCelestialSettings(); break;
            case "infernal":
                this.cameraSettings = buildInfernalSettings();
                break;
            case "astral": this.cameraSettings = buildAstralSettings(); break;
            case "inframundo": this.cameraSettings = buildInframundoSettings(); break;
        }

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
    private void sendCameraPacket(PlayerRef playerRef) {
        ToClientPacket packet = new SetServerCamera(ClientCameraView.Custom, true, cameraSettings);
        playerRef.getPacketHandler().writeNoCache(packet);
    }
    public boolean getState() { return isActive; }
    //////////////////////////////////////////////////////
    private ServerCameraSettings buildInfernalSettings() {
        ServerCameraSettings s = new ServerCameraSettings();

        s.positionLerpSpeed = 1.0f;
        s.rotationLerpSpeed = 1.0f;

        s.distance = distance;

        s.eyeOffset = true;
        s.displayCursor = true;
        s.isFirstPerson = false;
        s.sendMouseMotion = true;

        s.movementForceRotationType = MovementForceRotationType.AttachedToHead;
        s.movementForceRotation = new Direction(0.0f, 0.0f, 0.0f);

        s.positionDistanceOffsetType = PositionDistanceOffsetType.DistanceOffset;
        s.rotationType = RotationType.Custom;
        s.rotation = new Direction(0.0f, rotationCamera, 0.0f);

        s.mouseInputType = MouseInputType.LookAtPlane;
        s.planeNormal = new Vector3f(0.0f, 2.0f, 0.0f);
        return s;
    }
    private ServerCameraSettings buildTerrenalSettings() {
        ServerCameraSettings s = new ServerCameraSettings();

        s.positionLerpSpeed = 0.05f;
        s.rotationLerpSpeed = 0.05f;

        s.distance = distance;

        s.eyeOffset = true;
        s.displayCursor = true;
        s.isFirstPerson = false;
        s.sendMouseMotion = true;

        s.movementForceRotationType = MovementForceRotationType.AttachedToHead;
        s.movementForceRotation = new Direction(0.0f, 0.0f, 0.0f);

        s.positionDistanceOffsetType = PositionDistanceOffsetType.DistanceOffset;
        s.rotationType = RotationType.Custom;
        s.rotation = new Direction(0.0f, rotationCamera, 0.0f);

        s.mouseInputType = MouseInputType.LookAtPlane;
        s.planeNormal = new Vector3f(0.0f, 2.0f, 0.0f);

        return s;
    }
    private ServerCameraSettings buildCelestialSettings() {
        ServerCameraSettings s = new ServerCameraSettings();

        s.positionLerpSpeed = 0.05f;
        s.rotationLerpSpeed = 0.05f;

        s.distance = distance - 8.0f;

        s.isFirstPerson = false;
        s.eyeOffset = true;

        s.displayCursor = true;
        s.sendMouseMotion = true;

        s.movementForceRotationType = MovementForceRotationType.CameraRotation;
        s.movementForceRotation = new Direction(0.0f, 0.0f, 0.0f);

        s.positionOffset = new Position(1.0f, 0, 0);
        s.positionDistanceOffsetType = PositionDistanceOffsetType.DistanceOffset;

        s.rotationType = RotationType.Custom;
        s.rotation = new Direction(0.15f, rotationCamera + 0.15f, 0.0f);

        s.mouseInputType = MouseInputType.LookAtPlane;
        s.planeNormal = new Vector3f(0.0f, 2.0f, 0.0f);

        return s;
    }
    private ServerCameraSettings buildAstralSettings() {
        ServerCameraSettings s = new ServerCameraSettings();

        s.positionLerpSpeed = 0.2F;
        s.rotationLerpSpeed = 0.2F;

        s.distance = distance;

        s.eyeOffset = true;
        s.displayCursor = true;
        s.isFirstPerson = false;

        s.movementForceRotationType = MovementForceRotationType.Custom;
        s.movementMultiplier = new Vector3f(1.0F, 1.0F, 0.0F);
        s.positionDistanceOffsetType = PositionDistanceOffsetType.DistanceOffset;

        s.rotationType = RotationType.Custom;

        s.mouseInputType = MouseInputType.LookAtPlane;
        s.planeNormal = new Vector3f(0.0F, 0.0F, 1.0F);

        return s;
    }
    private ServerCameraSettings buildInframundoSettings() {
        ServerCameraSettings s = new ServerCameraSettings();

        s.positionLerpSpeed = 0.06f;
        s.rotationLerpSpeed = 0.06f;

        s.distance = distance * 2;

        s.isFirstPerson = false;
        s.sendMouseMotion = true;
        s.displayCursor = true;
        s.eyeOffset = false;

        s.movementForceRotationType = MovementForceRotationType.AttachedToHead;
        s.movementForceRotation = new Direction(0.0f, 0.0f, 0.0f);

        s.positionDistanceOffsetType = PositionDistanceOffsetType.DistanceOffset;
        s.rotationType = RotationType.Custom;
        s.rotation = new Direction(0.0f, rotationCamera, 0.0f);

        s.mouseInputType = MouseInputType.LookAtPlane;
        s.planeNormal = new Vector3f(0.0f, 2.0f, 0.0f);

        return s;
    }
    ///////////////////////////////////////////////////
}