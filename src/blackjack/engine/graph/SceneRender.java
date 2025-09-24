package blackjack.engine.graph;

// import blackjack.engine.Window;
import blackjack.engine.scene.Scene;

import java.util.*;

import static org.lwjgl.opengl.GL30.*;

public class SceneRender {

    private ShaderProgram shaderProgram;

    private UniformsMap uniformsMap;

    public SceneRender(){

        //create two shader module data instances (one for each shader module) and with them create a shader program
        List<ShaderProgram.ShaderModuleData> shaderModuleDataList = new ArrayList<>();

        shaderModuleDataList.add(new ShaderProgram.ShaderModuleData("resources/shaders/scene.vert", GL_VERTEX_SHADER));
        shaderModuleDataList.add(new ShaderProgram.ShaderModuleData("resources/shaders/scene.frag", GL_FRAGMENT_SHADER));

        shaderProgram = new ShaderProgram(shaderModuleDataList);

        createUniforms();
    }


    //draw the mesh into the screen
    //iterate over the meshes stored in the scene instance, bind them and draw the vertices of the VAO
    public void render(Scene scene){

        shaderProgram.bind();

        //set uniforms before drawing elements
        uniformsMap.setUniform("projectionMatrix", scene.getProjection().getProjMatrix());


        scene.getMeshMap().values().forEach(mesh -> {
            glBindVertexArray(mesh.getVaoId());

            //parameters of this method are: mode, count, type, indices
            glDrawElements(GL_TRIANGLES, mesh.getNumVertices(), GL_UNSIGNED_INT, 0);
        });


        glBindVertexArray(0);

        shaderProgram.unbind();

    }


    public void createUniforms(){

        uniformsMap = new UniformsMap(shaderProgram.getProgramId());
        uniformsMap.createUniform("projectionMatrix");

    }

    //free resources
    public void cleanup(){
        shaderProgram.cleanup();
    }

}
