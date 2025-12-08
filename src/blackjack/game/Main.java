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
import blackjack.gui.BlackJackGui;
import blackjack.logic.*;

import static org.lwjgl.glfw.GLFW.*;

public class Main implements IAppLogic {

    private EntityLoader entityLoader = new EntityLoader();

    private LightControls lightControls;
    private boolean enableLightControl = false;

    private SoundSource playerSoundSource;
    private SoundManager soundManager;

    public static void main(String[] args) {
        Main main = new Main();
        Window.WindowOptions opts = new Window.WindowOptions();
        opts.antiAliasing = true;
        opts.borderless = true;
        Engine gameEngine = new Engine("Blackjack LWJGL", opts, main);
        gameEngine.start();
    }

/*~~~ INITIALIZATION BLOCK ~~~*/
    @Override
    public void init(Window window, Scene scene, Render render) {
        /*~~~ LOAD ENTITIES (with models and textures) ~~~*/
        //scene.setGuiInstance(new BlackJackGui());

        entityLoader.loadEntities(scene);
        
        BlackJackLogic.keyCallBack(window, scene); // This is to show cards
        //EntityLoader.clickChips(window.getWindowHandle(), scene); // This is to select chips
        /*~~~ LIGHT SETTINGS ~~~*/ 
        initLights(scene);

        /*~~~ SKYBOX + FOG SETTINGS ~~~*/
        initSkyBoxAndFog(scene);

        /*~~~ PLAYER SPAWN POINT ~~~*/
        Camera camera = scene.getCamera();
        camera.setPosition(-1.5f, 2.0f, 4.5f);
        camera.addRotation((float) Math.toRadians(15.0f), (float) Math.toRadians(390.f));

        initSounds(entityLoader.getBobEntity().getPosition(), camera);
        scene.setGuiInstance(new BlackJackGui());

    }
    
    private void initLights(Scene scene) {
        SceneLights sceneLights = new SceneLights();
        
        /*~~~ AMBIENT ~~~*/
        AmbientLight ambientLight= sceneLights.getAmbientLight();
        ambientLight.setIntensity(0.5f);
        ambientLight.setColor(0.3f, 0.3f, 0.3f);
        
        /*~~~ DIRECTIONAL ~~~*/
        DirLight dirLight = sceneLights.getDirLight();
        dirLight.setPosition(0.0f, 1.0f, 0.79f);
        dirLight.setIntensity(0.88f);
        
        /*~~~ POINTLIGHT ~~~*/
        PointLight pointLight_1 = new PointLight();
        pointLight_1.setPosition(0.0f, 0.0f, 2.61f);
        pointLight_1.setIntensity(0.88f);

        sceneLights.getPointLights().add(pointLight_1);

        /*~~~ SPOTLIGHT ~~~*/
        SpotLight spotLight_1 = new SpotLight();
        PointLight spotLight_pointLight = spotLight_1.getPointLight();
        spotLight_pointLight.setPosition(0.0f, 1.09f, 2.03f);
        spotLight_pointLight.setIntensity(1.0f);
        spotLight_1.setCutOffAngle(227);

        sceneLights.getSpotLights().add(spotLight_1);
        
        scene.setSceneLights(sceneLights);
    }

    private void initSounds(Vector3f position, Camera camera){
        soundManager = new SoundManager();
        soundManager.setAttenuationModel(AL11.AL_EXPONENT_DISTANCE);
        soundManager.setListener(new SoundListener(camera.getPosition()));

        //audio 1
        // SoundBuffer buffer = new SoundBuffer("resources/sounds/creak1.ogg");
        // soundManager.addSoundBuffer(buffer);

        // playerSoundSource = new SoundSource(false, false);
        // playerSoundSource.setPosition(position);
        // playerSoundSource.setBuffer(buffer.getBufferId());

        // soundManager.addSoundSource("CREAK", playerSoundSource);

        //audio 2
        SoundBuffer buffer = new SoundBuffer("resources/sounds/6.ogg");
        soundManager.addSoundBuffer(buffer);

        SoundSource source = new SoundSource(true, true);
        source.setBuffer(buffer.getBufferId());

        soundManager.addSoundSource("MUSIC", source);
        source.play();
    }

    private void initSkyBoxAndFog(Scene scene) {
        SkyBox skyBox = new SkyBox("resources/models/skybox/skybox.obj", scene.getTextureCache());
        skyBox.getSkyBoxEntity().setScale(100);
        skyBox.getSkyBoxEntity().updateModelMatrix();
        scene.setSkyBox(skyBox);
        scene.setFog(new Fog(true, new Vector3f(0.5f, 0.5f, 0.5f), 0.02f));
    }
/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/


/*~~~ INPUT BLOCK ~~~*/
    @Override
    public void input(Window window, Scene scene, long diffTimeMillis, boolean inputConsumed) {
        if (inputConsumed) {
            return;
        }
        float move = diffTimeMillis * Consts.MOVEMENT_SPEED;
        Camera camera = scene.getCamera();

        /*~~~ WASD MOVEMENT ~~~*/
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
        if (window.isKeyPressed(GLFW_KEY_UP)) {
            camera.moveUp(move);;
        } else if (window.isKeyPressed(GLFW_KEY_DOWN)) {
            camera.moveDown(move);;
        }
        soundManager.updateListenerPosition(camera);        //Adapt listener position to player position


        /*~~~ MOUSE INPUT ~~~*/
        MouseInput mouseInput = window.getMouseInput();
        Vector2f displVec = mouseInput.getDisplVec();
        camera.addRotation((float) Math.toRadians(displVec.x * Consts.MOUSE_SENS), (float) Math.toRadians(displVec.y * Consts.MOUSE_SENS));
        
        // I did this change so the hover feature in EntityLoader can be refresh every frame
        // Needed this to make chips work properly

        // Before
        // if (mouseInput.isLeftButtonPressed()){
        //     entityLoader.selectEntity(window, scene, mouseInput.getCurrentPos());
        // }
        
        // After
        Vector2f mousePos = mouseInput.getCurrentPos(); 

        if (mouseInput.isLeftClicked()) {
            entityLoader.selectEntity(window, scene, mousePos);
        }

        if (mouseInput.isRightClicked()) {
            entityLoader.selectEntity(window, scene, mousePos);
        }

        if (mouseInput.isLeftButtonPressed()) {
            EntityLoader.clickChips(scene, true);   // bet chip
        }
        if (mouseInput.isRightButtonPressed()) {
            EntityLoader.clickChips(scene, false);  // undo bet
}
        

        /*~~~ TOGGLE LIGHTS UI ~~~~*/
        if (window.isKeyPressed(GLFW_KEY_O)) {
            toggleLightControls(scene);
        }
    }

    private void toggleLightControls(Scene scene) {
        lightControls = new LightControls(scene);
        enableLightControl = !enableLightControl;
        if (enableLightControl) {
            scene.setGuiInstance(lightControls);
        } else {
            scene.setGuiInstance(null);
        }
    }

/*~~~~~~~~~~~~~~~~~~ */    

/*~~~ FREE RESOURCES ~~~*/
    @Override
    public void cleanup() {
        soundManager.cleanup();
    }

/*~~~ UPDATE PER FRAME ~~~*/
    @Override
    public void update(Window window, Scene scene, long diffTimeMillis) {
        entityLoader.getAnimationData().nextFrame();
        // if (entityLoader.getAnimationData().getCurrentFrameIdx() == 45){
        //     playerSoundSource.play();
        // }
        if (BlackJackLogic.pendingButtonUpdate) {
            BlackJackLogic.pendingButtonUpdate = false;
            BlackJackLogic.getInstance().manageButtons(scene);
        }
    }

}