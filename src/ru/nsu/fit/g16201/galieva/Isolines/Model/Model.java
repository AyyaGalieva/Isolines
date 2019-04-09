package ru.nsu.fit.g16201.galieva.Isolines.Model;

import ru.nsu.fit.g16201.galieva.Isolines.View.GUI;
import java.awt.*;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Scanner;

public class Model {
    private GUI view;
    private LineField lineField = null;

    public Model(){}

    public void setView(GUI view) {
        this.view = view;
    }

    public void loadLineField(String path) {
        try {
            Scanner in = new Scanner(new FileInputStream(path));
            int gridSizeX = getNextInt(in);
            int gridSizeY = getNextInt(in);
            int levelCount = getNextInt(in);
            ArrayList<Color> levelColors = new ArrayList<>();
            for (int i = 0; i <= levelCount; ++i) {
                int r = getNextInt(in);
                int g = getNextInt(in);
                int b = getNextInt(in);
                levelColors.add(new Color(r,g,b));
            }

            if (gridSizeX < 2 || gridSizeY < 2 || levelCount < 1) {
                view.showFileIncorrect();
                return;
            }

            int r = getNextInt(in);
            int g = getNextInt(in);
            int b = getNextInt(in);
            Color isolineColor = new Color(r,g,b);
            lineField = new LineField(gridSizeX, gridSizeY, levelColors, isolineColor, new Function());
            view.update();
        } catch (Exception e) {
            view.showFileIncorrect();
        }
    }

    private int getNextInt(Scanner in) {
        if (!in.hasNextInt()) {
            in.nextLine();
        }
        return in.nextInt();
    }

    public void loadFirstField() {
        int gridSizeX = 15;
        int gridSizeY = 15;
        ArrayList<Color> levelColors = new ArrayList<>();
        levelColors.add(new Color(0, 0, 255));
        levelColors.add(new Color(0, 45, 230));
        levelColors.add(new Color(0, 90, 200));
        levelColors.add(new Color(0, 135, 170));
        levelColors.add(new Color(0, 180, 140));
        levelColors.add(new Color(0, 225, 110));
        levelColors.add(new Color(0, 255, 80));

        lineField = new LineField(gridSizeX, gridSizeY, levelColors, Color.black, new Function());
        view.update();
    }

    public LineField getLineField() {
        return lineField;
    }
}
