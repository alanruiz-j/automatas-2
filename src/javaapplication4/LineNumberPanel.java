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
        
        setPreferredSize(new Dimension(width, textArea.getHeight()));
        revalidate();
        repaint();
    }
    
    private int getLineCount() {
        String text = textArea.getText();
        if (text.isEmpty()) return 1;
        
        int lines = 1;
        for (char c : text.toCharArray()) {
            if (c == '\n') lines++;
        }
        return lines;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                            RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

        // Use the EXACT same FontMetrics as the textArea for consistency
        FontMetrics textFm = textArea.getFontMetrics(textArea.getFont());
        int lineHeight = textFm.getHeight();
        int ascent = textFm.getAscent();

        // Step 1: Determine the current visible area using getClipBounds
        Rectangle clip = g.getClipBounds();
        if (clip == null) {
            clip = getBounds();
        }

        try {
            // Step 2: Use viewToModel to identify which range of text lines are visible
            // Convert clip bounds Y coordinates to document positions
            Point topPoint = new Point(0, clip.y);
            Point bottomPoint = new Point(0, clip.y + clip.height);
            
            int startOffset = textArea.viewToModel(topPoint);
            int endOffset = textArea.viewToModel(bottomPoint);
            
            // Convert offsets to line numbers
            int startLine = textArea.getLineOfOffset(startOffset) + 1;
            int endLine = textArea.getLineOfOffset(endOffset) + 1;
            int totalLines = getLineCount();
            
            // Ensure bounds are valid
            if (startLine < 1) startLine = 1;
            if (endLine > totalLines) endLine = totalLines;

            // Step 3: Iterate over visible lines using modelToView2D
            for (int line = startLine; line <= endLine; line++) {
                try {
                    // Get the document offset at the start of this line
                    int lineStartOffset = textArea.getLineStartOffset(line - 1);
                    
                    // Use modelToView2D to get the exact rectangle where this line is rendered
                    Rectangle2D viewRect = textArea.modelToView2D(lineStartOffset);
                    
                    // Step 4: Use the Y coordinate (adjusted for ascent) to draw the line number
                    int y = (int) viewRect.getY();
                    
                    // Highlight current line
                    if (line == currentLine) {
                        g.setColor(new Color(220, 220, 220));
                        g.fillRect(0, y - clip.y, getWidth(), lineHeight);
                    }

                    // Draw line number at the exact Y position of the text line
                    g.setColor(Color.DARK_GRAY);
                    String lineNum = String.valueOf(line);
                    int x = getWidth() - textFm.stringWidth(lineNum) - 8;
                    // Adjust Y for panel coordinate space and add ascent for baseline alignment
                    g.drawString(lineNum, x, y - clip.y + ascent);
                    
                } catch (BadLocationException ex) {
                    // Skip this line if we can't determine its position
                    continue;
                }
            }
        } catch (BadLocationException ex) {
            // If we can't determine the visible range, fall back to drawing nothing
            // or handle the error as appropriate
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
