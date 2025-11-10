package blackjack.game;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.openal.AL11;
import blackjack.engine.*;
import blackjack.engine.graph.*;
import blackjack.engine.scene.*;
import blackjack.engine.scene.lights.*;
import blackjack.engine.sound.SoundBuffer;
import blackjack.engine.sound.SoundListener;
import blackjack.engine.sound.SoundManager;
import blackjack.engine.sound.SoundSource;

import static org.lwjgl.glfw.GLFW.*;

public class Main implements IAppLogic {
    private float lightAngle;

    private LightControls lightControls;
    private EntityLoader entityLoader = new EntityLoader();

    private SoundSource playerSoundSource;
    private SoundManager soundManager;

    public static void main(String[] args) {
        Main main = new Main();
        Window.WindowOptions opts = new Window.WindowOptions();
        opts.antiAliasing = true;
        Engine gameEngine = new Engine("Blackjack LWJGL", opts, main);
        gameEngine.start();
    }

    @Override
    public void cleanup() {
        soundManager.cleanup();
    }

    @Override
    public void init(Window window, Scene scene, Render render) {

        //Load entities (with models and textures)
        entityLoader.loadEntities(scene);

        //Light control 
        SceneLights sceneLights = new SceneLights();
        sceneLights.getAmbientLight().setIntensity(0.5f);
        sceneLights.getAmbientLight().setColor(0.3f, 0.3f, 0.3f);

        sceneLights.getDirLight().setPosition(0, 1, 0);
        sceneLights.getDirLight().setIntensity(1.0f);
        
        sceneLights.getPointLights().add(new PointLight());

        sceneLights.getSpotLights().add(new SpotLight());
        
        scene.setSceneLights(sceneLights);

        SkyBox skyBox = new SkyBox("resources/models/skybox/skybox.obj", scene.getTextureCache());
        skyBox.getSkyBoxEntity().setScale(100);
        skyBox.getSkyBoxEntity().updateModelMatrix();
        scene.setSkyBox(skyBox);

        scene.setFog(new Fog(true, new Vector3f(0.5f, 0.5f, 0.5f), 0.02f));

        Camera camera = scene.getCamera();
        camera.setPosition(-1.5f, 3.0f, 4.5f);
        camera.addRotation((float) Math.toRadians(15.0f), (float) Math.toRadians(390.f));

        lightAngle = 45;
        initSounds(entityLoader.getBobEntity().getPosition(), camera);
        
        lightControls = new LightControls(scene);
        scene.setGuiInstance(lightControls);
    }
    

    @Override
    public void input(Window window, Scene scene, long diffTimeMillis, boolean inputConsumed) {
        if (inputConsumed) {
            return;
        }
        float move = diffTimeMillis * Consts.MOVEMENT_SPEED;
        Camera camera = scene.getCamera();
        if (window.isKeyPressed(GLFW_KEY_W)) {
            camera.moveForward(move);
        } else if (window.isKeyPressed(GLFW_KEY_S)) {
            camera.moveBackwards(move);
        }
        if (window.isKeyPressed(GLFW_KEY_A)) {
            camera.moveLeft(move);
        } else if (window.isKeyPressed(GLFW_KEY_D)) {
            camera.moveRight(move);
        }
        //ARROWS LEFT AND RIGHT FOR LIGHT CONTROL
        if (window.isKeyPressed(GLFW_KEY_LEFT)) {
            lightAngle -= 2.5f;
            if (lightAngle < -90) {
                lightAngle = -90;
            }
        } else if (window.isKeyPressed(GLFW_KEY_RIGHT)) {
            lightAngle += 2.5f;
            if (lightAngle > 90) {
                lightAngle = 90;
            }
        }

        MouseInput mouseInput = window.getMouseInput();
        if (mouseInput.isRightButtonPressed()) {
            Vector2f displVec = mouseInput.getDisplVec();
            camera.addRotation(
                (float) Math.toRadians(displVec.x * Consts.MOUSE_SENS),
                (float) Math.toRadians(displVec.y * Consts.MOUSE_SENS));
        }
        if (mouseInput.isLeftButtonPressed()){
            entityLoader.selectEntity(window, scene, mouseInput.getCurrentPos());
        }
        
        SceneLights sceneLights = scene.getSceneLights();
        DirLight dirLight = sceneLights.getDirLight();
        double angRad = Math.toRadians(lightAngle);
        dirLight.getDirection().x = (float) Math.sin(angRad);
        dirLight.getDirection().y = (float) Math.cos(angRad);

        soundManager.updateListenerPosition(camera);
    }

    @Override
    public void update(Window window, Scene scene, long diffTimeMillis) {
        entityLoader.getAnimationData().nextFrame();
        if (entityLoader.getAnimationData().getCurrentFrameIdx() == 45){
            playerSoundSource.play();
        }
    }

    private void initSounds(Vector3f position, Camera camera){
        soundManager = new SoundManager();
        soundManager.setAttenuationModel(AL11.AL_EXPONENT_DISTANCE);
        soundManager.setListener(new SoundListener(camera.getPosition()));

        //audio 1
        SoundBuffer buffer = new SoundBuffer("resources/sounds/creak1.ogg");
        soundManager.addSoundBuffer(buffer);

        playerSoundSource = new SoundSource(false, false);
        playerSoundSource.setPosition(position);
        playerSoundSource.setBuffer(buffer.getBufferId());

        soundManager.addSoundSource("CREAK", playerSoundSource);

        //audio 2
        buffer = new SoundBuffer("resources/sounds/woo_scary.ogg");
        soundManager.addSoundBuffer(buffer);

        SoundSource source = new SoundSource(true, true);
        source.setBuffer(buffer.getBufferId());

        soundManager.addSoundSource("MUSIC", source);
        source.play();
    }
}