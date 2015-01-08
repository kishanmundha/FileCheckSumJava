/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package filechecksum;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;

/**
 *
 * @author Mundha
 */
public class DisplayListItem {
    private StringProperty msg;
    public String path;
    public Color color;
    
    public DisplayListItem(String msg, String path, Color color) {
        this.msg = new SimpleStringProperty(msg);
        this.path = path;
        this.color = color;
    }
    
    public String getMsg() {
        return msg.get();
    }

    public void seMsg(String msg) {
        this.msg.set(msg);
    }

    public String getPath() {
        return path;
    }

    public void sePath(String path) {
        this.path = path;
    }
}
