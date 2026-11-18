package com.pvpquiz;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class QuizStore {
    public static class QA {
        public String q;
        public String a;
        public QA() {}
        public QA(String q, String a) { this.q = q; this.a = a; }
    }

    public static class Data {
        public List<QA> questions = new ArrayList<>();
        public List<String> keywords = new ArrayList<>(List.of(
                "killed", "slain", "defeated", "eliminated", "won the", "victory"));
    }

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Random RANDOM = new Random();
    private static Data data = new Data();

    private static Path file() {
        return FabricLoader.getInstance().getConfigDir().resolve("pvpquiz.json");
    }

    public static void load() {
        Path f = file();
        if (Files.exists(f)) {
            try (Reader r = Files.newBufferedReader(f)) {
                Data d = GSON.fromJson(r, Data.class);
                if (d != null) {
                    if (d.questions == null) d.questions = new ArrayList<>();
                    if (d.keywords == null) d.keywords = new ArrayList<>();
                    data = d;
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        data = new Data();
        data.questions.add(new QA("What is the capital of France?", "Paris"));
        data.questions.add(new QA("What is 7 x 8?", "56"));
        save();
    }

    public static void save() {
        try (Writer w = Files.newBufferedWriter(file())) {
            GSON.toJson(data, w);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<QA> questions() { return data.questions; }
    public static List<String> keywords() { return data.keywords; }

    public static void add(String q, String a) {
        data.questions.add(new QA(q.trim(), a.trim()));
        save();
    }

    public static void remove(int index) {
        if (index >= 0 && index < data.questions.size()) {
            data.questions.remove(index);
            save();
        }
    }

    public static void addKeyword(String k) {
        if (!data.keywords.contains(k)) { data.keywords.add(k); save(); }
    }

    public static boolean removeKeyword(String k) {
        boolean ok = data.keywords.remove(k);
        if (ok) save();
        return ok;
    }

    public static QA random() {
        if (data.questions.isEmpty()) return null;
        return data.questions.get(RANDOM.nextInt(data.questions.size()));
    }

    /** Answer may contain several accepted answers separated by | */
    public static boolean check(QA qa, String given) {
        String g = given.trim();
        for (String option : qa.a.split("\\|")) {
            if (option.trim().equalsIgnoreCase(g)) return true;
        }
        return false;
    }
}
