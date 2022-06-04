package gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import javax.swing.*;

import log.Logger;

public class MainApplicationFrame extends JFrame implements WindowState
{
    private final JDesktopPane desktopPane = new JDesktopPane();
    private final ArrayList<WindowState> windowsList = new ArrayList<>();
    private final String filePath = System.getProperty("user.home") +
            System.getProperty("file.separator") + "robots_config.xml";

    public MainApplicationFrame() {
        windowsList.add(this);
        //Make the big window be indented 50 pixels from each edge
        //of the screen.
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
                screenSize.width  - inset*2,
                screenSize.height - inset*2);

        setContentPane(desktopPane);

        LogWindow logWindow = createLogWindow();
        logWindow.setName("LogWindow");
        addWindow(logWindow);

        GameWindow gameWindow = new GameWindow();
        gameWindow.setSize(400,  400);
        gameWindow.setName("GameWindow");
        addWindow(gameWindow);

        setJMenuBar(generateMenuBar());

        UIManager.put("OptionPane.yesButtonText", "Да");
        UIManager.put("OptionPane.noButtonText", "Нет");

        InputStream inputStream;
        Properties properties = null;
        try {
            inputStream = new FileInputStream(filePath);
            properties = new Properties();
            properties.loadFromXML(inputStream);
            inputStream.close();
        } catch (IOException e) {
            showMessage("Восстановить состояние окон не получилось.");
        }
        if (properties != null)
            for (WindowState window : windowsList)
            {
                Map<String, String> mapProperties = new HashMap<>();
                Set<String> keys = properties.stringPropertyNames();
                for (String key : keys)
                    if (key.startsWith(window.getName()))
                        mapProperties.put(key.substring(window.getName().length() + 1), properties.getProperty(key));
                window.restoreWindowState(mapProperties);
            }

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closingWindow();
            }
        });
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(MainApplicationFrame.this, message);
    }

    private void closingWindow() {
        int result = JOptionPane.showConfirmDialog(
                MainApplicationFrame.this,
                "Закрыть приложение?",
                "Окно подтверждения",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if (result == JOptionPane.YES_OPTION) {
            Properties properties = new Properties();
            for (WindowState window : windowsList) {
                Properties windowPosition = window.getWindowState();
                Set<String> keys = windowPosition.stringPropertyNames();
                for (String key : keys)
                    properties.setProperty(window.getName() + "." + key, windowPosition.getProperty(key));
            }
            try {
                OutputStream outputStream = new FileOutputStream(filePath);
                properties.storeToXML(outputStream, null);
                outputStream.close();
            } catch (IOException e) {
                showMessage("Сохранить состояние окон не получилось.");
            }
            dispose();
            System.exit(0);
        }
    }

    protected LogWindow createLogWindow()
    {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        logWindow.setLocation(10,10);
        logWindow.setSize(300, 800);
//        setMinimumSize(logWindow.getSize());
        logWindow.pack();
        Logger.debug("Протокол работает");
        return logWindow;
    }

    protected void addWindow(JInternalFrame frame)
    {
        desktopPane.add(frame);
        if (frame instanceof WindowState)
            windowsList.add((WindowState) frame);
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

    @Override
    public boolean isIcon() {
        return getExtendedState() == Frame.ICONIFIED;
    }

    @Override
    public void setIcon(boolean b) {
        if (b) setExtendedState(Frame.ICONIFIED);
    }
}
