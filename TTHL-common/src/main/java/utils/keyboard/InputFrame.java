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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Cette classe ne sert uniquement qu'à changer des booléens en fonctions des inputs utilisés
 *
 * @author nayth
 */
class InputFrame extends JFrame implements KeyListener {

    boolean isUpPressed = false;
    boolean isDownPressed = false;
    boolean isLeftPressed = false;
    boolean isRightPressed = false;
    boolean isEscapePressed = false;
    boolean isSpacePressed = false;
    boolean isCPressed = false;
    boolean isPPressed = false;
    boolean isVPressed = false;
    boolean isWPressed = false;
    boolean isXPressed = false;


    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                this.isUpPressed=true;
                break;
            case KeyEvent.VK_DOWN:
                this.isDownPressed=true;
                break;
            case KeyEvent.VK_LEFT:
                this.isLeftPressed=true;
                break;
            case KeyEvent.VK_RIGHT:
                this.isRightPressed=true;
                break;
            case KeyEvent.VK_ESCAPE:
                this.isEscapePressed=true;
                break;
            case KeyEvent.VK_SPACE:
                this.isSpacePressed=true;
                break;
            case KeyEvent.VK_C:
                this.isCPressed=true;
                break;
            case KeyEvent.VK_P:
                this.isPPressed=true;
                break;
            case KeyEvent.VK_V:
                this.isVPressed=true;
                break;
            case KeyEvent.VK_W:
                this.isWPressed=true;
                break;
            case KeyEvent.VK_X:
                this.isXPressed=true;
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                this.isUpPressed=false;
                break;
            case KeyEvent.VK_DOWN:
                this.isDownPressed=false;
                break;
            case KeyEvent.VK_LEFT:
                this.isLeftPressed=false;
                break;
            case KeyEvent.VK_RIGHT:
                this.isRightPressed=false;
                break;
            case KeyEvent.VK_ESCAPE:
                this.isEscapePressed=false;
                break;
            case KeyEvent.VK_SPACE:
                this.isSpacePressed=false;
                break;
            case KeyEvent.VK_C:
                this.isCPressed=false;
                break;
            case KeyEvent.VK_P:
                this.isPPressed=false;
                break;
            case KeyEvent.VK_V:
                this.isVPressed=false;
                break;
            case KeyEvent.VK_W:
                this.isWPressed=false;
                break;
            case KeyEvent.VK_X:
                this.isXPressed=false;
                break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    InputFrame(){
        addKeyListener(this);
    }
}
