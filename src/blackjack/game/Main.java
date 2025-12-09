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
    
    /* AMBIENT */
    AmbientLight ambientLight = sceneLights.getAmbientLight();
    ambientLight.setIntensity(1.45f);
    ambientLight.setColor(0.1f, 0.1f, 0.15f);
    
    /* DIRECTIONAL*/
    DirLight dirLight = sceneLights.getDirLight();
    dirLight.setPosition(0.0f, -1.0f, 0.0f);
    dirLight.setIntensity(0.4f);
    
    /* POINTLIGHT */
    PointLight pointLight_1 = new PointLight();
    pointLight_1.setPosition(0.0f, 0.0f, 2.61f);
    pointLight_1.setIntensity(0.88f);
    pointLight_1.getAttenuation().setConstant(1.0f);
    pointLight_1.getAttenuation().setLinear(0.1f);
    pointLight_1.getAttenuation().setExponent(0.0f);
    sceneLights.getPointLights().add(pointLight_1);

    /* SPOTLIGHTS*/
    sceneLights.getSpotLights().clear();
    
    // casino coordinates at eye level (y=2):
    // (6, 2, 5)      - front right
    // (-6.8, 2, 5)   - front left  
    // (-8, 2, -2.7)  - back left
    // (6.5, 2, -2.8) - back right
    // Ceiling height: 6.5
    
    // min/max
    float x_beginning =  -8.0f;
    float x_ending =  6.5f;
    float z_beginning =  -2.8f;
    float z_ending =  5.0f;
    float ceilingHeight = 6.0f;
    
    // 3x3 grid
    int rows = 3;
    int columns = 3;
    
    // Calculate jumps
    float x_jump = (x_ending - x_beginning) / columns;
    float z_jump = (z_ending - z_beginning) / rows;
    
    // Loop via x and z
    for(float x = x_beginning; x < x_ending; x+=x_jump){
        for(float z = z_beginning; z < z_ending; z+=z_jump){
            SpotLight spotlight = new SpotLight();
            PointLight pointLight = spotlight.getPointLight();
            
            // Position at ceiling height
            pointLight.setPosition(x, ceilingHeight, z);
            
            // Casino lights - warm white
            pointLight.setColor(1.0f, 0.95f, 0.8f);
            pointLight.setIntensity(3.0f);
            
            // Attenuation
            pointLight.getAttenuation().setConstant(1.0f);
            pointLight.getAttenuation().setLinear(0.1f);
            pointLight.getAttenuation().setExponent(0.02f);
            
            // Spotlight parameters
            spotlight.setCutOffAngle(45.0f);
            spotlight.setConeDirection(0.0f, -1.0f, 0.0f);
            
            sceneLights.getSpotLights().add(spotlight);
            
        }
    }
    
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