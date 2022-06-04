package gui;

import java.beans.PropertyVetoException;
import java.util.Map;
import java.util.Properties;

public interface WindowState {
    void setBounds(int x, int y, int w, int h);
    int getX();
    int getY();
    int getWidth();
    int getHeight();
    boolean isIcon();
    void setIcon(boolean b) throws PropertyVetoException;
    String getName();

    default void restoreWindowState(Map<String, String> properties) {
        setBounds(Integer.parseInt(properties.get("x")),
                Integer.parseInt(properties.get("y")),
                Integer.parseInt(properties.get("w")),
                Integer.parseInt(properties.get("h")));
        try {
            setIcon(Boolean.parseBoolean(properties.get("icon")));
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }
    }

    default Properties getWindowState() {
        Properties properties = new Properties();
        properties.setProperty("x", String.valueOf(getX()));
        properties.setProperty("y", String.valueOf(getY()));
        properties.setProperty("h", String.valueOf(getHeight()));
        properties.setProperty("w", String.valueOf(getWidth()));
        properties.setProperty("icon", String.valueOf(isIcon()));
        return properties;
    }
}
