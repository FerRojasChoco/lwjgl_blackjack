package blackjack.game;
//CHECK NOTE IN THE MOUSE INPUT SECTION ~~~!!!!!!!!!!!!!!!!!!!!!!!!!!
//also separate the initialization of objects (entities with models) in another file so it wont bloat this file

import org.lwjgl.glfw.GLFW;


import org.joml.Vector2f;
import org.joml.Vector3f;

import blackjack.engine.*;
import blackjack.engine.graph.Render;
import blackjack.engine.scene.Camera;
import blackjack.engine.scene.EntityLoader;
import blackjack.engine.scene.Scene;
import blackjack.engine.scene.lights.PointLight;
import blackjack.engine.scene.lights.SceneLights;
import blackjack.engine.scene.lights.SpotLight;

//create the Engine instance and start it up in the main method
//this class also implements app logic but is empty for now
public class Main implements IAppLogic {

    private LightControls lightControls;

    public static void main(String[] args){

        Main main = new Main();
        
        Engine gameEngine = new Engine("Blackjack LWJGL", new Window.WindowOptions(), main);

        gameEngine.start();

    }

    @Override
    public void cleanup() {
        //TODO
    }

    @Override
    public void init(Window window, Scene scene, Render render) {

        //Load entities (with models and textures)
        EntityLoader entityLoader = new EntityLoader();
        entityLoader.loadEntities(scene);

        //Light control 
        SceneLights sceneLights = new SceneLights();
        
        sceneLights.getAmbientLight().setIntensity(0.3f);
        
        scene.setSceneLights(sceneLights);
        
        sceneLights.getPointLights().add(new PointLight(new Vector3f(1, 1, 1),
                new Vector3f(0, 0, -1.4f), 1.0f));
        
        Vector3f coneDir = new Vector3f(0, 0, -1);
        sceneLights.getSpotLights().add(new SpotLight(new PointLight(new Vector3f(1, 1, 1),
                new Vector3f(0, 0, -1.4f), 0.0f), coneDir, 140.0f));
        
        lightControls = new LightControls(scene);
        scene.setGuiInstance(lightControls);
    }

    @Override
    public void input(Window window, Scene scene, long diffTimeMillis, boolean inputConsumed) {
        
        if (inputConsumed){
            return;
        }

        float move = diffTimeMillis * Consts.MOVEMENT_SPEED;
        
        Camera camera = scene.getCamera();
        
        if (window.isKeyPressed(GLFW.GLFW_KEY_W)) {
            camera.moveForward(move);
        } 
        else if (window.isKeyPressed(GLFW.GLFW_KEY_S)) {
            camera.moveBackwards(move);
        }

        if (window.isKeyPressed(GLFW.GLFW_KEY_A)) {
            camera.moveLeft(move);
        } 
        else if (window.isKeyPressed(GLFW.GLFW_KEY_D)) {
            camera.moveRight(move);
        }

        if (window.isKeyPressed(GLFW.GLFW_KEY_UP)) {
            camera.moveUp(move);
        } 
        else if (window.isKeyPressed(GLFW.GLFW_KEY_DOWN)) {
            camera.moveDown(move);
        }

        MouseInput mouseInput = window.getMouseInput();
        //note: add a - sign to displVec x and y if inverted camera axis is needed
        //for now you have to right click and drag to move the camera, this should be changed to move the camera
        //alongside the cursor
        // if (mouseInput.isRightButtonPressed()) {

        //ATTENTION: YOU CAN USE ACTIVATE THE SHIFT BY ELIMINATING THE COMMENTED LINES FOR DEBUGGING. FINAL PRODUCT WILL NOT USE SHIFT
        //if (window.isKeyPressed(GLFW.GLFW_KEY_LEFT_SHIFT)){
            Vector2f displVec = mouseInput.getDisplVec();
            camera.addRotation((float) Math.toRadians(displVec.x * Consts.MOUSE_SENS),
                    (float) Math.toRadians(displVec.y * Consts.MOUSE_SENS));
        //}
    }

    @Override
    public void update(Window window, Scene scene, long diffTimeMillis) {

        // rotation += 1.5;

        // if (rotation > 360){
        //     rotation = 0;
        // }

        // cubeEntity.setRotation(1, 1, 1, (float) Math.toRadians(rotation));
        // cubeEntity.updateModelMatrix();

    }

}
