package gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.*;

import javax.swing.*;

import log.Logger;

/**
 * Что требуется сделать:
 * 1. Метод создания меню перегружен функционалом и трудно читается.
 * Следует разделить его на серию более простых методов (или вообще выделить отдельный класс).
 *
 */
public class MainApplicationFrame extends JFrame
{
    private final JDesktopPane desktopPane = new JDesktopPane();

    public void closingWindow() {
        int result = JOptionPane.showConfirmDialog(
                MainApplicationFrame.this,
                "Закрыть приложение?",
                "Окно подтверждения",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if (result == JOptionPane.YES_OPTION) {
            dispose();
            System.exit(0);
        }
    }

    public MainApplicationFrame() {
        //Make the big window be indented 50 pixels from each edge
        //of the screen.
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
                screenSize.width  - inset*2,
                screenSize.height - inset*2);

        setContentPane(desktopPane);

        LogWindow logWindow = createLogWindow();
        addWindow(logWindow);

        GameWindow gameWindow = new GameWindow();
        gameWindow.setSize(400,  400);
        addWindow(gameWindow);

        setJMenuBar(generateMenuBar());
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        UIManager.put("OptionPane.yesButtonText", "Да");
        UIManager.put("OptionPane.noButtonText", "Нет");

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent ev) {
                new WindowListener() {
                    @Override
                    public void windowOpened(WindowEvent e) {}

                    @Override
                    public void windowClosing(WindowEvent e) {
                        closingWindow();
                    }

                    @Override
                    public void windowClosed(WindowEvent e) {}

                    @Override
                    public void windowIconified(WindowEvent e) {}

                    @Override
                    public void windowDeiconified(WindowEvent e) {}

                    @Override
                    public void windowActivated(WindowEvent e) {}

                    @Override
                    public void windowDeactivated(WindowEvent e) {}
                };
            }
        });
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    }

    protected LogWindow createLogWindow()
    {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        logWindow.setLocation(10,10);
        logWindow.setSize(300, 800);
        setMinimumSize(logWindow.getSize());
        logWindow.pack();
        Logger.debug("Протокол работает");
        return logWindow;
    }

    protected void addWindow(JInternalFrame frame)
    {
        desktopPane.add(frame);
        frame.setVisible(true);
    }

    private void addMenu(JMenuBar menuBar, String name, int mnemonic, String description, JMenuItem... menuItems) {
        JMenu menu = new JMenu(name);
        menu.setMnemonic(mnemonic);
        menu.getAccessibleContext().setAccessibleDescription(description);
        for (JMenuItem menuItem : menuItems)
            menu.add(menuItem);
        menuBar.add(menu);
    }

    private JMenuItem createMenuItem(String name, int keyEvent, ActionListener actionListener) {
        JMenuItem jMenuItem = new JMenuItem(name, keyEvent);
        jMenuItem.addActionListener(actionListener);
        return jMenuItem;
    }

    private JMenuBar generateMenuBar()
    {
        JMenuBar menuBar = new JMenuBar();

        JMenuItem systemLookAndFeel = createMenuItem("Системная схема", KeyEvent.VK_S, (event) -> {
            setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            this.invalidate();
        });
        JMenuItem crossplatformLookAndFeel = createMenuItem("Универсальная схема", KeyEvent.VK_S, (event) -> {
            setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            this.invalidate();
        });
        JMenuItem addLogMessageItem = createMenuItem("Сообщение в лог", KeyEvent.VK_S, (event) ->
                Logger.debug("Новая строка"));
        JMenuItem quitMenuItem = createMenuItem("Закрыть приложение", KeyEvent.VK_Q, (event) -> closingWindow());

        addMenu(menuBar, "Режим отображения", KeyEvent.VK_V, "Управление режимом отображения приложения",
                systemLookAndFeel, crossplatformLookAndFeel);
        addMenu(menuBar, "Тесты", KeyEvent.VK_T, "Тестовые команды", addLogMessageItem);
        addMenu(menuBar, "Другое", KeyEvent.VK_O, "Другие действия", quitMenuItem);

        return menuBar;
    }

    private void setLookAndFeel(String className)
    {
        try
        {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        }
        catch (ClassNotFoundException | InstantiationException
               | IllegalAccessException | UnsupportedLookAndFeelException e)
        {
            // just ignore
        }
    }
}
