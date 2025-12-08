package blackjack.gui;

import blackjack.engine.IGuiInstance;
import blackjack.engine.Window;
import blackjack.engine.MouseInput;
import blackjack.engine.scene.Scene;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiWindowFlags;

import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class BlackJackGui implements IGuiInstance {

    public static class GuiMessage {
        public final String id; 
        public String text;
        public float x, y;      // percent of window: 0.0 to 1.0
        public float scale;     // font size scale
        public float r, g, b, a;

        public GuiMessage(String id, String text, float x, float y, float scale,
                          float r, float g, float b, float a) {
            this.id = id;  
            this.text = text;
            this.x = x;
            this.y = y;
            this.scale = scale;
            this.r = r;
            this.g = g;
            this.b = b;
            this.a = a;
        }
    }

    private final List<GuiMessage> messages = new ArrayList<>();

    public void addMessage(GuiMessage msg) {
        messages.add(msg);
    }

    public void clearMessages() {
        messages.clear();
    }

    public void removeMessageById(String id) {
        messages.removeIf(msg -> msg.id.equals(id));
    }

    @Override
    public void drawGui() {

        ImGui.newFrame();

        if (!messages.isEmpty()) {

            // Fullscreen overlay window
            ImGui.setNextWindowPos(0, 0);
            ImGui.setNextWindowSize(ImGui.getIO().getDisplaySize());
            ImGui.begin("Blackjack Messages",
                ImGuiWindowFlags.NoTitleBar |
                ImGuiWindowFlags.NoResize   |
                ImGuiWindowFlags.NoMove     |
                ImGuiWindowFlags.NoBackground
            );

            float w = ImGui.getWindowSizeX();
            float h = ImGui.getWindowSizeY();

            for (GuiMessage msg : messages) {

                float textW = ImGui.calcTextSize(msg.text).x;

                float posX = msg.x * w - textW / 2f; // centered at X
                float posY = msg.y * h;

                ImGui.setCursorPos(posX, posY);

                ImGui.setWindowFontScale(msg.scale);
                ImGui.textColored(msg.r, msg.g, msg.b, msg.a, msg.text);
                ImGui.setWindowFontScale(1f);
            }

            ImGui.end();
        }

        ImGui.endFrame();
        ImGui.render();
    }

    @Override
    public boolean handleGuiInput(Scene scene, Window window) {

        ImGuiIO io = ImGui.getIO();
        MouseInput mouseInput = window.getMouseInput();
        Vector2f pos = mouseInput.getCurrentPos();

        io.addMousePosEvent(pos.x, pos.y);
        io.addMouseButtonEvent(0, mouseInput.isLeftButtonPressed());
        io.addMouseButtonEvent(1, mouseInput.isRightButtonPressed());

        return false;
    }
}
