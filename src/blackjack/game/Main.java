package blackjack.game;
//CHECK NOTE IN THE MOUSE INPUT SECTION ~~~!!!!!!!!!!!!!!!!!!!!!!!!!!
//also separate the initialization of objects (entities with models) in another file so it wont bloat this file

import org.lwjgl.glfw.GLFW;


import org.joml.Vector2f;

import blackjack.engine.*;
import blackjack.engine.graph.Model;
import blackjack.engine.graph.Render;
import blackjack.engine.scene.Camera;
import blackjack.engine.scene.Entity;
import blackjack.engine.scene.ModelLoader;
import blackjack.engine.scene.Scene;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiCond;

//create the Engine instance and start it up in the main method
//this class also implements app logic but is empty for now
public class Main implements IAppLogic, IGuiInstance{


    private Entity cubeEntity;
    private Entity chairEntity;
    // private Vector4f displInc = new Vector4f();
    private float rotation;

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

        Model cubeModel = ModelLoader.loadModel(
            "cube-model",
            "resources/models/cube/cube.obj",
            scene.getTextureCache()
        );

        Model chairModel = ModelLoader.loadModel(
            "chair-model",
            "resources/models/wooden_chair/Wooden_Chair.obj",
            scene.getTextureCache()
        );

        scene.addModel(cubeModel);
        scene.addModel(chairModel); 

        cubeEntity = new Entity("cube-entity", cubeModel.getId());
        cubeEntity.setPosition(0, 0, -2);
        
        chairEntity = new Entity("chair-entity", chairModel.getId());
        chairEntity.setPosition(2, 0, -3);        

        scene.addEntity(cubeEntity);
        scene.addEntity(chairEntity);


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
        if (window.isKeyPressed(GLFW.GLFW_KEY_LEFT_SHIFT)){
            Vector2f displVec = mouseInput.getDisplVec();
            camera.addRotation((float) Math.toRadians(displVec.x * Consts.MOUSE_SENS),
                    (float) Math.toRadians(displVec.y * Consts.MOUSE_SENS));
        }
    }

    @Override
    public void update(Window window, Scene scene, long diffTimeMillis) {

        // rotation += 1.5;

        // if (rotation > 360){
        //     rotation = 0;
        // }

        // cubeEntity.setRotation(1, 1, 1, (float) Math.toRadians(rotation));
        cubeEntity.updateModelMatrix();

    }

    @Override
    public void drawGui(){
        ImGui.newFrame();
        ImGui.setNextWindowPos(0, 0, ImGuiCond.Always);
        ImGui.showDemoWindow();
        ImGui.endFrame();
        ImGui.render();
    }

    @Override
    public boolean handleGuiInput(Scene scene, Window window){

        ImGuiIO imGuiIO = ImGui.getIO();
        MouseInput mouseInput = window.getMouseInput();
        Vector2f mousePos = mouseInput.getCurrentPos();

        imGuiIO.addMousePosEvent(mousePos.x, mousePos.y);
        imGuiIO.addMouseButtonEvent(0, mouseInput.isLeftButtonPressed());
        imGuiIO.addMouseButtonEvent(1, mouseInput.isRightButtonPressed());

        return imGuiIO.getWantCaptureMouse() || imGuiIO.getWantCaptureKeyboard();
    }

}
