/**
 * Copyright (c) 2018, INTech.
 * this file is part of INTech's HighLevel.
 *
 * INTech's HighLevel is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * INTech's HighLevel is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with it.  If not, see <http://www.gnu.org/licenses/>.
 **/

package utils.keyboard;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

public class KeyboardHandler extends Thread {

    private static InputFrame frame;
    public KeyboardHandler() {
        try {
            SwingUtilities.invokeAndWait(() -> {
                frame = new InputFrame();
                frame.setFocusable(true);
                frame.setFocusTraversalKeysEnabled(false);
                frame.setTitle("Robot control interface");
                frame.setResizable(false);
                frame.setSize(300, 200);
                frame.setMinimumSize(new Dimension(300, 200));
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
                frame.setVisible(true);
            });
        } catch (InterruptedException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public JFrame getFrame(){
        return frame;
    }

    public boolean isUpPressed(){
        return frame.isUpPressed;
    }
    public boolean isDownPressed(){ return frame.isDownPressed; }
    public boolean isLeftPressed(){ return frame.isLeftPressed; }
    public boolean isRightPressed(){
        return frame.isRightPressed;
    }
    public boolean isSpacePressed(){ return frame.isSpacePressed; }
    public boolean isCPressed(){ return frame.isCPressed; }
    public boolean isPPressed(){ return frame.isPPressed; }
    public boolean isVPressed(){ return frame.isVPressed; }
    public boolean isWPressed(){ return frame.isWPressed; }
    public boolean isXPressed(){ return frame.isXPressed; }

    public boolean isEscapePressed(){ return frame.isEscapePressed; }

}
