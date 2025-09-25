package blackjack.engine.graph;

import java.util.*;

import org.joml.Vector4f;

import blackjack.engine.Consts;

public class Material {

    private Vector4f diffusecolor;
    private List<Mesh> meshList;
    private String texturePath;

    public Material(){
        diffusecolor = Consts.DEFAULT_COLOR;
        meshList = new ArrayList<>();
    }

    //free resources
    public void cleanup(){
        meshList.forEach(Mesh::cleanup);
    }

    //getters and setters
    public List<Mesh> getMeshList() {
        return meshList;
    }

    public String getTexturePath() {
        return texturePath;
    }

    public void setTexturePath(String texturePath) {
        this.texturePath = texturePath;
    }

    public Vector4f getDiffusecolor() {
        return diffusecolor;
    }

    public void setDiffusecolor(Vector4f diffusecolor) {
        this.diffusecolor = diffusecolor;
    }
    
}
