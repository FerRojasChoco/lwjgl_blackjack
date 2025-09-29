package blackjack.engine.scene;

import blackjack.engine.graph.Model;

public class EntityLoader {

    private Entity cubeEntity;
    private Entity chairEntity;


    public void loadEntities(Scene scene){

        //define models to be rendered
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

        //render the model in the scene

        scene.addModel(cubeModel);
        scene.addModel(chairModel); 
        
        cubeEntity = new Entity("cube-entity", cubeModel.getId());
        cubeEntity.setPosition(0.0f, -2.0f, -2.0f);
        
        chairEntity = new Entity("chair-entity", chairModel.getId());
        chairEntity.setPosition(0.0f, -1.5f, -2.0f);        

        scene.addEntity(cubeEntity);
        scene.addEntity(chairEntity);

        cubeEntity.updateModelMatrix();
        chairEntity.updateModelMatrix();

    }

    

    //getters for entities in case some class needs them for updating
    public Entity getChairEntity() {
        return chairEntity;
    }

    public Entity getCubeEntity() {
        return cubeEntity;
    }

}
