package blackjack.engine.graph;

import blackjack.engine.scene.Entity;

import java.util.*;

/*
 * a model is an structure which glues together vertices, colors, textures and materials
 * a model may be composed of several meshes and can be used by several game entities
 * a game entity represents a player and enemy, and obstacle, anything that is part of the 3D scene
 */

public class Model {

    private final String id;
    private List<Entity> entitiesList;
    private List<Mesh> meshList;

    public Model(String id, List<Mesh> meshList){

        this.id = id;
        this.meshList = meshList;
        entitiesList = new ArrayList<>();

    }

    //free resources
    public void cleanup(){
        meshList.forEach(Mesh::cleanup);
    }


    //getters
    public List<Entity> getEntitiesList() {
        return entitiesList;
    }

    public String getId() {
        return id;
    }

    public List<Mesh> getMeshList() {
        return meshList;
    }
    

}
