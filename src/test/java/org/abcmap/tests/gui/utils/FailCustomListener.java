package org.abcmap.tests.gui.utils;

import javax.swing.event.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import static org.junit.Assert.fail;


/**
 * Custom listener that make test fail if event is received
 *
 * @author remipassmoilesel
 */
public class FailCustomListener implements DocumentListener, KeyListener,
        ActionListener, ListSelectionListener, CaretListener, ChangeListener {

    private static int staticCount = 0;
    private int id;

    public FailCustomListener() {
        staticCount++;
        id = staticCount;
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        fail();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        fail();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        fail();
    }

    @Override
    public void keyTyped(KeyEvent e) {
        fail();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        fail();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        fail();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        fail();
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        fail();
    }

    @Override
    public void caretUpdate(CaretEvent e) {
        fail();
    }

    @Override
    public String toString() {
        return "#" + id + "_" + super.toString();
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        fail();
    }

}