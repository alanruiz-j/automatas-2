/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javaapplication4;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Rectangle2D;

/**
 * A panel that displays line numbers for a JTextArea.
 * Synchronizes scrolling and updates automatically.
 * Highlights the current line.
 * @author Gerardo
 */
public class LineNumberPanel extends JPanel implements DocumentListener, CaretListener {

    private final JTextArea textArea;
    private int currentLine = 1;

    public LineNumberPanel(JTextArea textArea) {
        this.textArea = textArea;

        setBackground(new Color(240, 240, 240));
        setFont(textArea.getFont());

        // Add listeners
        textArea.getDocument().addDocumentListener(this);
        textArea.addCaretListener(this);

        // Add component listener to track text area resize events
        textArea.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updatePreferredWidth();
                repaint();
            }
        });

        // Set preferred width based on initial line count
        updatePreferredWidth();
    }
    
    private void updatePreferredWidth() {
        int lineCount = getLineCount();
        int digits = String.valueOf(lineCount).length();
        // Minimum 2 digits width
        digits = Math.max(digits, 2);
        
        FontMetrics fm = getFontMetrics(getFont());
        int charWidth = fm.charWidth('0');
        int width = charWidth * digits + 20; // 20px padding
        
        // Use textArea's preferred size for proper height synchronization
        setPreferredSize(new Dimension(width, textArea.getPreferredSize().height));
        revalidate();
        repaint();
    }
    
    private int getLineCount() {
        return textArea.getLineCount();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                            RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

        // Use FontMetrics from the text area for consistent line height calculations
        FontMetrics textFm = textArea.getFontMetrics(textArea.getFont());
        int lineHeight = textFm.getHeight();
        int ascent = textFm.getAscent();

        // Get the visible rectangle to determine which lines are currently visible
        Rectangle visibleRect = textArea.getVisibleRect();

        try {
            // Use viewToModel to identify which range of text lines are visible
            int startOffset = textArea.viewToModel(new Point(0, visibleRect.y));
            int endOffset = textArea.viewToModel(new Point(0, visibleRect.y + visibleRect.height));
            
            // Convert offsets to line numbers
            int startLine = textArea.getLineOfOffset(startOffset) + 1;
            int endLine = textArea.getLineOfOffset(endOffset) + 1;
            int totalLines = textArea.getLineCount();
            
            // Ensure bounds are valid
            if (startLine < 1) startLine = 1;
            if (endLine > totalLines) endLine = totalLines;

            // Iterate over visible lines using modelToView2D
            for (int line = startLine; line <= endLine; line++) {
                try {
                    // Get the document offset at the start of this line
                    int lineStartOffset = textArea.getLineStartOffset(line - 1);
                    
                    // Use modelToView2D to get the exact rectangle where this line is rendered
                    Rectangle2D viewRect = textArea.modelToView2D(lineStartOffset);
                    
                    // Simplify coordinate calculation by using modelToView2D() Y directly
                    // The Y coordinate is relative to the text area, which matches our row header
                    int y = (int) viewRect.getY();
                    
                    // Highlight current line
                    if (line == currentLine) {
                        g.setColor(new Color(220, 220, 220));
                        g.fillRect(0, y, getWidth(), lineHeight);
                    }

                    // Draw line number at the exact Y position of the text line
                    g.setColor(Color.DARK_GRAY);
                    String lineNum = String.valueOf(line);
                    int x = getWidth() - textFm.stringWidth(lineNum) - 8;
                    // Add ascent for proper baseline alignment
                    g.drawString(lineNum, x, y + ascent);
                    
                } catch (BadLocationException ex) {
                    // Skip this line if we can't determine its position
                    continue;
                }
            }
        } catch (BadLocationException ex) {
            // If we can't determine the visible range, fall back to drawing nothing
        }
    }
    
    // DocumentListener methods
    @Override
    public void insertUpdate(DocumentEvent e) {
        updatePreferredWidth();
        repaint();
    }
    
    @Override
    public void removeUpdate(DocumentEvent e) {
        updatePreferredWidth();
        repaint();
    }
    
    @Override
    public void changedUpdate(DocumentEvent e) {
        updatePreferredWidth();
        repaint();
    }
    
    // CaretListener method
    @Override
    public void caretUpdate(CaretEvent e) {
        try {
            int caretPosition = textArea.getCaretPosition();
            currentLine = textArea.getLineOfOffset(caretPosition) + 1;
            repaint();
        } catch (BadLocationException ex) {
            // Ignore
        }
    }
}
