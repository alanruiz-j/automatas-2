/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javaapplication4;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.awt.*;

/**
 * A panel that displays line numbers for a JTextArea.
 * Synchronizes scrolling and updates automatically.
 * Highlights the current line.
 * @author Gerardo
 */
public class LineNumberPanel extends JPanel implements DocumentListener, CaretListener {
    
    private final JTextArea textArea;
    private final JScrollPane scrollPane;
    private int currentLine = 1;
    
    public LineNumberPanel(JTextArea textArea, JScrollPane scrollPane) {
        this.textArea = textArea;
        this.scrollPane = scrollPane;
        
        setBackground(new Color(240, 240, 240));
        setFont(textArea.getFont());
        
        // Add listeners
        textArea.getDocument().addDocumentListener(this);
        textArea.addCaretListener(this);
        
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

        // Match insets to prevent initial offset drift
        Insets textInsets = textArea.getInsets();
        int topInset = textInsets.top;

        // Get the visible rectangle of the text area
        Rectangle visibleRect = textArea.getVisibleRect();

        // Calculate which lines are visible
        int startLine = (int) ((visibleRect.y - topInset) / lineHeight) + 1;
        int endLine = startLine + (int) (visibleRect.height / lineHeight) + 2;
        int totalLines = getLineCount();
        endLine = Math.min(endLine, totalLines);

        if (startLine < 1) startLine = 1;

        // Draw line numbers
        for (int line = startLine; line <= endLine; line++) {
            // Calculate Y position with matching insets
            int y = topInset + (line - 1) * lineHeight - visibleRect.y;

            // Highlight current line
            if (line == currentLine) {
                g.setColor(new Color(220, 220, 220));
                g.fillRect(0, y, getWidth(), lineHeight);
            }

            // Draw line number using textArea's FontMetrics for perfect alignment
            g.setColor(Color.DARK_GRAY);
            String lineNum = String.valueOf(line);
            int x = getWidth() - textFm.stringWidth(lineNum) - 8;
            g.drawString(lineNum, x, y + ascent);
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
