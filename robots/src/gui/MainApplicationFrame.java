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
public class MainApplicationFrame extends JFrame implements ActionListener
{
    private final JDesktopPane desktopPane = new JDesktopPane();

    @Override
    public void actionPerformed(ActionEvent e) {
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
                actionPerformed(new ActionEvent(desktopPane, 0, "quit"));
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

    private void addMenu(JMenuBar menuBar, String name, int mnemonic, String description, JMenuItem[] menuItems) {
        JMenu menu = new JMenu(name);
        menu.setMnemonic(mnemonic);
        menu.getAccessibleContext().setAccessibleDescription(description);
        for (JMenuItem menuItem : menuItems)
            menu.add(menuItem);
        menuBar.add(menu);
    }

    private JMenuBar generateMenuBar()
    {
        JMenuBar menuBar = new JMenuBar();

        JMenuItem quitMenuItem = new JMenuItem("Закрыть приложение", KeyEvent.VK_Q);
        quitMenuItem.addActionListener(this);

        JMenuItem systemLookAndFeel = new JMenuItem("Системная схема", KeyEvent.VK_S);
        systemLookAndFeel.addActionListener((event) -> {
            setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            this.invalidate();
        });

        JMenuItem crossplatformLookAndFeel = new JMenuItem("Универсальная схема", KeyEvent.VK_S);
        crossplatformLookAndFeel.addActionListener((event) -> {
            setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            this.invalidate();
        });

        JMenuItem addLogMessageItem = new JMenuItem("Сообщение в лог", KeyEvent.VK_S);
        addLogMessageItem.addActionListener((event) -> Logger.debug("Новая строка"));

        addMenu(menuBar, "Режим отображения", KeyEvent.VK_V, "Управление режимом отображения приложения",
                new JMenuItem[]{systemLookAndFeel, crossplatformLookAndFeel});
        addMenu(menuBar, "Тесты", KeyEvent.VK_T, "Тестовые команды", new JMenuItem[]{addLogMessageItem});
        addMenu(menuBar, "Другое", KeyEvent.VK_O, "Другие действия", new JMenuItem[]{quitMenuItem});

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
