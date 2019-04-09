package ru.nsu.fit.g16201.galieva.Isolines;

import ru.nsu.fit.g16201.galieva.Isolines.Model.Model;
import ru.nsu.fit.g16201.galieva.Isolines.View.GUI;

public class Isolines {
    public static void main(String[] args) {
        Model m = new Model();
        GUI view = new GUI(m);
        m.setView(view);
        view.setVisible(true);
    }
}
