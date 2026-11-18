package com.pvpquiz;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

/** GUI to add / delete questions. Open with /pvpquiz */
public class QuestionManagerScreen extends Screen {
    private static final int PER_PAGE = 5;
    private String qText = "";
    private String aText = "";
    private int page = 0;
    private EditBox qBox, aBox;

    public QuestionManagerScreen() {
        super(Component.literal("PvP Quiz - Questions"));
    }

    private int pages() {
        return Math.max(1, (QuizStore.questions().size() + PER_PAGE - 1) / PER_PAGE);
    }

    @Override
    protected void init() {
        int left = this.width / 2 - 150;

        qBox = new EditBox(this.font, left, 40, 300, 20, Component.literal("Question"));
        qBox.setMaxLength(200);
        qBox.setHint(Component.literal("Question"));
        qBox.setValue(qText);
        qBox.setResponder(s -> qText = s);
        addRenderableWidget(qBox);

        aBox = new EditBox(this.font, left, 66, 300, 20, Component.literal("Answer"));
        aBox.setMaxLength(200);
        aBox.setHint(Component.literal("Answer (use | for several accepted answers)"));
        aBox.setValue(aText);
        aBox.setResponder(s -> aText = s);
        addRenderableWidget(aBox);

        addRenderableWidget(Button.builder(Component.literal("Add question"), b -> {
            if (!qText.isBlank() && !aText.isBlank()) {
                QuizStore.add(qText, aText);
                qText = "";
                aText = "";
                page = pages() - 1;
                rebuildWidgets();
            }
        }).bounds(left, 92, 300, 20).build());

        List<QuizStore.QA> qs = QuizStore.questions();
        if (page >= pages()) page = pages() - 1;
        int start = page * PER_PAGE;
        for (int i = 0; i < PER_PAGE && start + i < qs.size(); i++) {
            final int index = start + i;
            addRenderableWidget(Button.builder(Component.literal("X"), b -> {
                QuizStore.remove(index);
                rebuildWidgets();
            }).bounds(left + 270, 132 + i * 24, 30, 20).build());
        }

        addRenderableWidget(Button.builder(Component.literal("<"), b -> {
            if (page > 0) { page--; rebuildWidgets(); }
        }).bounds(left, this.height - 52, 40, 20).build());
        addRenderableWidget(Button.builder(Component.literal(">"), b -> {
            if (page < pages() - 1) { page++; rebuildWidgets(); }
        }).bounds(left + 260, this.height - 52, 40, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Done"), b -> onClose())
                .bounds(this.width / 2 - 50, this.height - 28, 100, 20).build());
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float delta) {
        super.render(g, mouseX, mouseY, delta);
        int left = this.width / 2 - 150;
        g.drawCenteredString(this.font, this.title, this.width / 2, 15, 0xFFFFFFFF);
        g.drawString(this.font, "Questions: " + QuizStore.questions().size()
                + "  (page " + (page + 1) + "/" + pages() + ")", left, 118, 0xFFAAAAAA);

        List<QuizStore.QA> qs = QuizStore.questions();
        int start = page * PER_PAGE;
        for (int i = 0; i < PER_PAGE && start + i < qs.size(); i++) {
            QuizStore.QA qa = qs.get(start + i);
            String line = (start + i + 1) + ". " + qa.q + "  ->  " + qa.a;
            g.drawString(this.font, this.font.plainSubstrByWidth(line, 262), left, 138 + i * 24, 0xFFFFFFFF);
        }
    }
}
