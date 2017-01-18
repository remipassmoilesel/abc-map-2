package org.abcmap;/*
Definitive Guide to Swing for Java 2, Second Edition
By John Zukowski     
ISBN: 1-893115-78-X
Publisher: APress
*/

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

public class UndoSample {
  public static void main(String args[]) {
    JFrame frame = new JFrame("Undo Sample");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    JTextArea textArea = new JTextArea();
    JScrollPane scrollPane = new JScrollPane(textArea);

    UndoManager manager = new UndoManager();
    textArea.getDocument().addUndoableEditListener(manager);

    JToolBar toolbar = new JToolBar();
    toolbar.add(UndoManagerHelper.getUndoAction(manager));
    toolbar.add(UndoManagerHelper.getRedoAction(manager));

    Container content = frame.getContentPane();
    content.add(toolbar, BorderLayout.NORTH);
    content.add(scrollPane, BorderLayout.CENTER);
    frame.setSize(300, 150);
    frame.setVisible(true);
  }
}

class UndoManagerHelper {

  public static Action getUndoAction(UndoManager manager, String label) {
    return new UndoAction(manager, label);
  }

  public static Action getUndoAction(UndoManager manager) {
    return new UndoAction(manager, "Undo");
  }

  public static Action getRedoAction(UndoManager manager, String label) {
    return new RedoAction(manager, label);
  }

  public static Action getRedoAction(UndoManager manager) {
    return new RedoAction(manager, "Redo");
  }

  private abstract static class UndoRedoAction extends AbstractAction {
    UndoManager undoManager = new UndoManager();

    String errorMessage = "Cannot undo";

    String errorTitle = "Undo Problem";

    protected UndoRedoAction(UndoManager manager, String name) {
      super(name);
      undoManager = manager;
    }

    public void setErrorMessage(String newValue) {
      errorMessage = newValue;
    }

    public void setErrorTitle(String newValue) {
      errorTitle = newValue;
    }

    protected void showMessage(Object source) {
      if (source instanceof Component) {
        JOptionPane.showMessageDialog((Component) source, errorMessage,
            errorTitle, JOptionPane.WARNING_MESSAGE);
      } else {
        System.err.println(errorMessage);
      }
    }
  }

  public static class UndoAction extends UndoRedoAction {
    public UndoAction(UndoManager manager, String name) {
      super(manager, name);
      setErrorMessage("Cannot undo");
      setErrorTitle("Undo Problem");
    }

    public void actionPerformed(ActionEvent actionEvent) {
      try {
        undoManager.undo();
      } catch (CannotUndoException cannotUndoException) {
        showMessage(actionEvent.getSource());
      }
    }
  }

  public static class RedoAction extends UndoRedoAction {
    String errorMessage = "Cannot redo";

    String errorTitle = "Redo Problem";

    public RedoAction(UndoManager manager, String name) {
      super(manager, name);
      setErrorMessage("Cannot redo");
      setErrorTitle("Redo Problem");
    }

    public void actionPerformed(ActionEvent actionEvent) {
      try {
        undoManager.redo();
      } catch (CannotRedoException cannotRedoException) {
        showMessage(actionEvent.getSource());
      }
    }
  }

}

           
         
  