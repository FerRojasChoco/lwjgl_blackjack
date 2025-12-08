package blackjack.gui;

import blackjack.engine.IGuiInstance;
import blackjack.engine.Window;
import blackjack.engine.MouseInput;
import blackjack.engine.scene.Scene;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;

import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;

public class BlackJackGui implements IGuiInstance {

    // Message Class
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

    // Button Class
    public static class GuiButton {
        public final String id;
        public String text;
        public float x, y;       // percent of screen
        public float width, height;
        public float r, g, b, a;
        public Runnable callback;

        public GuiButton(String id, String text,
                         float x, float y,
                         float width, float height,
                         float r, float g, float b, float a,
                         Runnable callback) {
            this.id = id;
            this.text = text;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.r = r;
            this.g = g;
            this.b = b;
            this.a = a;
            this.callback = callback;
        }
    }

    private final List<GuiButton> buttons = new ArrayList<>();

    public void addButton(GuiButton btn) { buttons.add(btn); }

    public void removeButtonById(String id) {
        buttons.removeIf(b -> b.id.equals(id));
    }

    public void clearButtons() { buttons.clear(); }

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

           // Draw buttons
           
        for (GuiButton btn : buttons) {
            float posX = btn.x * w;
            float posY = btn.y * h;

            ImGui.setCursorPos(posX, posY);

            // ---- PRETTY BUTTON STYLE ----
            ImGui.pushStyleVar(ImGuiStyleVar.FrameRounding, 15f);   // rounded corners
            ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 14f, 10f);

            ImGui.pushStyleColor(ImGuiCol.Button, btn.r, btn.g, btn.b, btn.a);
            ImGui.pushStyleColor(ImGuiCol.ButtonHovered, btn.r, btn.g + 0.2f, btn.b, btn.a);
            ImGui.pushStyleColor(ImGuiCol.ButtonActive, btn.r - 0.2f, btn.g, btn.b, btn.a);

            // Big text (5×)
            ImGui.setWindowFontScale(2f);

            if (ImGui.button(btn.text, btn.width, btn.height)) {
                if (btn.callback != null) btn.callback.run();
            }

            ImGui.setWindowFontScale(1f);
            ImGui.popStyleColor(3);
            ImGui.popStyleVar(2);
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
