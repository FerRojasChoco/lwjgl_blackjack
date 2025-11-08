package blackjack.engine.scene;

import java.util.Collection;
import java.util.List;

import org.joml.Intersectionf;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import blackjack.engine.Window;
import blackjack.engine.graph.Material;
import blackjack.engine.graph.Mesh;
import blackjack.engine.graph.Model;

public class EntityLoader {

    private Entity cubeEntity;
    private Entity chairEntity;
    private Entity tableEntity;

    public void loadEntities(Scene scene){

        // define models to be rendered
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

        Model tableModel = ModelLoader.loadModel(
            "table-model",
            "resources/models/table/blackjack_table.obj",
            scene.getTextureCache()
        );

        //render the model in the scene

        scene.addModel(cubeModel);
        scene.addModel(chairModel);
        scene.addModel(tableModel); 
        
        cubeEntity = new Entity("cube-entity", cubeModel.getId(), true);
        cubeEntity.setPosition(0.0f, 0.0f, -2.0f);
        
        chairEntity = new Entity("chair-entity", chairModel.getId(), true);
        chairEntity.setPosition(0.0f, 0.0f, -2.0f);
        
        tableEntity = new Entity("table-entity", tableModel.getId(), false);

        scene.addEntity(cubeEntity);
        scene.addEntity(chairEntity);
        scene.addEntity(tableEntity);


        cubeEntity.updateModelMatrix();
        chairEntity.updateModelMatrix();
        tableEntity.updateModelMatrix();

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

}
