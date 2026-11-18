package com.pvpquiz;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

/** The forced question. Can't be closed with ESC - you have to answer correctly. */
public class QuizScreen extends Screen {
    private final QuizStore.QA qa;
    private EditBox box;
    private String typed = "";
    private Component feedback = Component.empty();

    public QuizScreen(QuizStore.QA qa) {
        super(Component.literal("PvP finished! Answer to continue"));
        this.qa = qa;
    }

    @Override
    protected void init() {
        box = new EditBox(this.font, this.width / 2 - 100, this.height / 2, 200, 20, Component.literal("Answer"));
        box.setMaxLength(200);
        box.setValue(typed);
        box.setResponder(s -> typed = s);
        addRenderableWidget(box);
        setInitialFocus(box);

        addRenderableWidget(Button.builder(Component.literal("Submit"), b -> submit())
                .bounds(this.width / 2 - 50, this.height / 2 + 28, 100, 20).build());
    }

    private void submit() {
        if (QuizStore.check(qa, box.getValue())) {
            this.minecraft.setScreen(null);
        } else {
            feedback = Component.literal("Wrong! Try again.");
            box.setValue("");
        }
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float delta) {
        super.render(g, mouseX, mouseY, delta);
        g.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - 70, 0xFFFFAA00);
        List<FormattedCharSequence> lines = this.font.split(Component.literal(qa.q), 300);
        int y = this.height / 2 - 40;
        for (FormattedCharSequence line : lines) {
            g.drawCenteredString(this.font, line, this.width / 2, y, 0xFFFFFFFF);
            y += 10;
        }
        g.drawCenteredString(this.font, feedback, this.width / 2, this.height / 2 + 56, 0xFFFF5555);
    }

    @Override
    public boolean shouldCloseOnEsc() { return false; }

    @Override
    public boolean isPauseScreen() { return false; }
}
