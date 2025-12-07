package blackjack.engine.scene;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joml.Intersectionf;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import blackjack.engine.Window;
import blackjack.engine.graph.Material;
import blackjack.engine.graph.Mesh;
import blackjack.engine.graph.Model;
import imgui.ImGuiIO;

// Testeo
import org.lwjgl.glfw.GLFWKeyCallback;
import blackjack.logic.*;
import blackjack.engine.MouseInput;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
public class EntityLoader {

    private AnimationData animationData;
    private Entity terrainEntity;
    private Entity bobEntity;
    private Entity cubeEntity;
    private Entity chairEntity;
    private Entity tableEntity;
    private Entity[] chipsEntities;
    private Entity chipEntity;
    private static Entity hiddenCardEntity;
    private static Entity backCardEntity;
    private static Entity chip10Entity;
    private static Entity chip50Entity;
    private static Entity chip100Entity;
    private static Entity chip500Entity;
    private static Entity chip1000Entity;
    private static Entity hoveredEntity = null;
    private static String ChipSelected = null;

    public static void getChipSelected(String chipSelected) {
        BlackJackLogic.betChips(chipSelected);
    }


    private static Map<String, Model> cardModels = new HashMap<>();
    public static Map<String, Model> getCardModels() {
        return cardModels;
    }

    private static float cardScale = 0.06f;
    private static final float HIDDEN_X = -0.42f;
    private static final float DEALER_START_X = -0.60f;
    private static final float PLAYER_START_X = -0.60f;

    private static final float Y = 0.9f;
    private static final float DEALER_Z = 0.65f;
    private static final float PLAYER_Z = 1.05f;

    private static float dealerOffsetX = 0f;
    private static float playerOffsetX = 0f;

    public enum CardType {
        HIDDEN,
        DEALER,
        PLAYER
    }
    // Temporal
    private class Card {
        String value;
        String type; 

        // Now we created a constructor
        Card(String value, String type) {
            this.value = value;
            this.type = type;
        }

        public String toString() {
            return type + "_" + value;
        }

        public int getValue() {
            if(value == "1") {
                return 11;
            }
            else if(value == "11" || value == "12" || value == "13") {
                return 10;
            }
            else {
                return Integer.parseInt(value); // 2 ~ 10
            }
            
        }

        public boolean isAce() {
            if(value == "1") {
                return true;
            }
            else {
                return false;
            }
        }

        public String getPath() {
            return "resources\\models\\cards\\" + toString() + ".obj";
        }
    }
    
    public void loadEntities(Scene scene){
        

        // define models to be rendered
        Model terrainModel = ModelLoader.loadModel(
            "terrain-model", 
            "resources/models/terrain/terrain.obj",
            scene.getTextureCache(),
            false
        );

        Model bobModel = ModelLoader.loadModel(
            "bob-model",
            "resources/models/bob/boblamp.md5mesh",
            scene.getTextureCache(),
            true
        );

        Model cubeModel = ModelLoader.loadModel(
            "cube-model",
            "resources/models/cube/cube.obj",
            scene.getTextureCache(),
            false
        );

        Model chairModel = ModelLoader.loadModel(
            "chair-model",
            "resources/models/wooden_chair/Wooden_Chair.obj",
            scene.getTextureCache(),
            false
        );

        Model tableModel = ModelLoader.loadModel(
            "table-model",
            "resources/models/table/blackjack_table.obj",
            scene.getTextureCache(),
            false
        );

        scene.addModel(terrainModel); 
        scene.addModel(bobModel);
        scene.addModel(cubeModel);
        scene.addModel(chairModel);
        scene.addModel(tableModel);

        //define entity properties
        terrainEntity = new Entity("terrain-entity", terrainModel.getId(), false);
        terrainEntity.setScale(100.0f);

        bobEntity = new Entity("bob-entity", bobModel.getId(), false);
        bobEntity.setScale(0.05f);
        animationData = new AnimationData(bobModel.getAnimationList().get(0));
        bobEntity.setAnimationData(animationData);
                
        cubeEntity = new Entity("cube-entity", cubeModel.getId(), true);
        cubeEntity.setPosition(0.0f, 0.0f, -2.0f);
        
        chairEntity = new Entity("chair-entity", chairModel.getId(), true);
        chairEntity.setPosition(0.0f, 0.0f, -2.0f);
        
        tableEntity = new Entity("table-entity", tableModel.getId(), false);

        terrainEntity.updateModelMatrix();
        bobEntity.updateModelMatrix();
        cubeEntity.updateModelMatrix();
        chairEntity.updateModelMatrix();
        tableEntity.updateModelMatrix();

        scene.addEntity(terrainEntity);
        scene.addEntity(bobEntity);
        scene.addEntity(cubeEntity);
        scene.addEntity(chairEntity);
        scene.addEntity(tableEntity);

        // Dynamically add the chips
        //String[] chipValues = {"1", "5", "10", "20", "50", "100", "500", "1000", "5000"};
        String[] chipValues = {"10", "50", "100", "500", "1000"};
        chipsEntities = new Entity[chipValues.length];

        for(int i = 0; i < chipValues.length; i++) {
            
            String modelId =  "chip" + chipValues[i] + "Model";
            String modelPath = "resources\\models\\blackjack chips\\poker_chip_" + chipValues[i] + "\\poker_chip_" + chipValues[i] + ".obj";
            Model chipModel = ModelLoader.loadModel(
                modelId,
                modelPath,
                scene.getTextureCache(),
                false
            );

            scene.addModel(chipModel);

            // Match entities with their models and assign a position
            String entityId = chipValues[i];
            chipEntity = new Entity(entityId, chipModel.getId(), true);
            float offsetX = (i - chipValues.length / 2f) * 0.1f;
            chipEntity.setPosition(offsetX, 1f, 1.0f);

            scene.addEntity(chipEntity);
            chipEntity.updateModelMatrix();

            int chipValue = Integer.parseInt(chipValues[i]);
            switch (chipValue) {
                case 10:
                    chip10Entity=chipEntity;
                    break;
            
                case 50:
                    chip50Entity=chipEntity;
                    break;

                case 100:
                    chip100Entity=chipEntity;
                    break;

                case 500:
                    chip500Entity=chipEntity;
                    break;

                case 1000:
                    chip1000Entity=chipEntity;
                    break;
            }


        }

        // Dynamically add the cards
        String[] values = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13"};
        String [] types = {"clubs", "diamonds", "hearts", "spades"}; 
        
        // Load all cards models
        for (String type : types) {
            for (String value : values) {

                Card card = new Card(value, type);

                String modelId = card.toString();   
                String modelPath = card.getPath();  

                // Load model ONCE per card
                Model model = ModelLoader.loadModel(
                    modelId,
                    modelPath,
                    scene.getTextureCache(),
                    false
                );

                cardModels.put(modelId, model);
                scene.addModel(model);
            }
        }
    }

    public void selectEntity(Window window, Scene scene, Vector2f mousePos){
        int wdwWidth = window.getWidth();
        int wdwHeight = window.getHeight();

        float x = (2 * mousePos.x) / wdwWidth - 1.0f;
        float y = 1.0f - (2 * mousePos.y) / wdwHeight;
        float z = -1.0f;

        Matrix4f invProjMatrix = scene.getProjection().getInvProjMatrix();
        Vector4f mouseDir = new Vector4f(x, y, z, 1.0f);

        mouseDir.mul(invProjMatrix);
        mouseDir.z = -1.0f;
        mouseDir.w = 0.0f;

        Matrix4f invViewMatrix = scene.getCamera().getInvViewMatrix();
        mouseDir.mul(invViewMatrix);

        Vector4f min = new Vector4f(0.0f, 0.0f, 0.0f, 1.0f);
        Vector4f max = new Vector4f(0.0f, 0.0f, 0.0f, 1.0f);
        Vector2f nearFar = new Vector2f();

        Entity selectedEntity = null;
        float closestDistance = Float.POSITIVE_INFINITY;
        Vector3f center = scene.getCamera().getPosition();

        Collection<Model> models = scene.getModelMap().values();
        Matrix4f modelMatrix = new Matrix4f();

        for (Model model : models){
            List<Entity> entities = model.getEntitiesList();

            for (Entity entity : entities){

                if (!entity.isSelectable()){
                    continue;
                }
                modelMatrix.translate(entity.getPosition()).scale(entity.getScale());
                
                for (Material material : model.getMaterialList()){
                    for (Mesh mesh : material.getMeshList()){
                        
                        Vector3f aabbMin = mesh.getAabbMin();
                        min.set(aabbMin.x, aabbMin.y, aabbMin.z, 1.0f);
                        min.mul(modelMatrix);

                        Vector3f aabbMax = mesh.getAabbMax();
                        max.set(aabbMax.x, aabbMax.y, aabbMax.z, 1.0f);
                        max.mul(modelMatrix);

                        if (Intersectionf.intersectRayAab(center.x, center.y, center.z, mouseDir.x, mouseDir.y, mouseDir.z,
                                min.x, min.y, min.z, max.x, max.y, max.z, nearFar) && nearFar.x < closestDistance) {
                            closestDistance = nearFar.x;
                            selectedEntity = entity;
                        }
                    }
                }
                modelMatrix.identity();
            }
        }
        scene.setSelectedEntity(selectedEntity);
        hoveredEntity = selectedEntity; 

    }

    public static void clickChips(long windowHandle) {
        glfwSetMouseButtonCallback(windowHandle, (handle, button, action, mods) -> {
            if (button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS) {
                if (hoveredEntity != null) {
                    ChipSelected = hoveredEntity.getId();
                    getChipSelected(ChipSelected);
                }
            }
        });
    }


    public static void loadCard(String card, Scene scene, CardType type) {
        Model model = cardModels.get(card);
        if (model == null) {
            System.err.println("card wrong idk" + card);
            return;
        }
        
        
        Entity entity = new Entity("card-" + System.nanoTime(), model.getId(), false);
        entity.setScale(cardScale);

        switch (type) {

            case HIDDEN:
                System.out.println("Hidden card: " +card);
                hiddenCardEntity = entity;

                Model backModel = ModelLoader.loadModel(
                    "back-model",
                    "resources\\models\\backCard\\back.obj",
                    scene.getTextureCache(),
                    false
                );
                scene.addModel(backModel);

                backCardEntity = new Entity("back-entity", backModel.getId(), false);
                backCardEntity.setScale(cardScale);
                backCardEntity.setPosition(DEALER_START_X, Y, DEALER_Z);
                
                dealerOffsetX += 0.30f;

                backCardEntity.updateModelMatrix();
                scene.addEntity(backCardEntity);
                return; 

            case DEALER:
                System.out.println("Dealer card: " +card);
                entity.setPosition(DEALER_START_X + dealerOffsetX, Y, DEALER_Z);
                dealerOffsetX += 0.30f;
                break;

            case PLAYER:
                System.out.println("Player card: " +card);
                entity.setPosition(PLAYER_START_X + playerOffsetX, Y, PLAYER_Z);
                playerOffsetX += 0.30f;
                break;
        }
        

        entity.updateModelMatrix();
        scene.addEntity(entity);
    }

    public static void removeHiddedCard(String card, Scene scene) {
        if (hiddenCardEntity != null) {
            scene.removeEntity(hiddenCardEntity);
            hiddenCardEntity = null;
        }

        Model card1 = cardModels.get(card); 
        Entity cardEntity = new Entity("card-" + System.nanoTime(), card1.getId(), false);
        cardEntity.setPosition(-0.60f, 0.9f, 0.65f);
        cardEntity.setScale(cardScale);
        cardEntity.updateModelMatrix();
        scene.addEntity(cardEntity);
        
    }

    public static void resetOffsets() {
        dealerOffsetX = 0f;
        playerOffsetX = 0f;
        hiddenCardEntity = null;
    }
    
    // getters for entities in case some class needs them for updating
    public Entity getChairEntity() {
        return chairEntity;
    }

    public Entity getCubeEntity() {
        return cubeEntity;
    }

    public Entity getTableEntity() {
        return tableEntity;
    }

    public Entity getBobEntity(){
        return bobEntity;
    }

    public Entity[] getChipsEntities() {
        return chipsEntities;
    }
    
    public AnimationData getAnimationData() {
        return animationData;
    }

}
