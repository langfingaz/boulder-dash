package fr.enssat.BoulderDash.controllers;

import java.awt.EventQueue;

import fr.enssat.BoulderDash.views.GameView;
import fr.enssat.BoulderDash.views.LevelEditorView;
//gère l'éditeur de LVL
public class LevelEditorController {

    LevelEditorView view = null;

    public void LevelEditorController(LevelEditorView view) {
        // Initialize view
        this.setView(view);
    }

    public LevelEditorView getView() {
        return this.view;
    }

    private void setView(LevelEditorView gameView) {
        this.view = gameView;
    }

    public void displayView() {
    	final LevelEditorController _this = this;
    	
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Set view visible
            	_this.getView().setVisible(true);
            }
        });
    }
}