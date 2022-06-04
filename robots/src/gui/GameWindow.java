package gui;

import java.awt.BorderLayout;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import java.util.Map;
import java.util.Properties;

public class GameWindow extends JInternalFrame implements WindowState
{
    private final GameVisualizer m_visualizer;
    public GameWindow() 
    {
        super("Игровое поле", true, true, true, true);
        m_visualizer = new GameVisualizer();
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_visualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
    }

    @Override
    public Properties getWindowState() {
        return WindowState.super.getWindowState();
    }

    @Override
    public void restoreWindowState(Map<String, String> properties) {
        WindowState.super.restoreWindowState(properties);
    }
}
