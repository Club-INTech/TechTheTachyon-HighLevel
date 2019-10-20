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

package unitaires;

import org.junit.*;
import pfg.config.Config;
import utils.ConfigData;
import utils.Log;

import java.io.*;

// FIXME: à refaire éventuellement. System.setOut() est réécrit dans Log.init()
public class Test_Log {
    /**
     * Stream & Fichier de sortie pour contrôler le résultat des tests
     */
    private PrintStream output;
    private PrintStream systemOut;
    private File file;
    private BufferedReader input;
    private static final String RESET       = "\u001B[0m";

    @Before
    public void setUp() throws Exception {
        file = new File("LogTest");
        output = new PrintStream(file);
        input = new BufferedReader(new FileReader(file));
        systemOut = System.out;
        System.setOut(output);
        Log.activeAllChannels();
        Log.init();
    }

    @After
    public void tearDown() throws Exception {
        file.delete();
        file = null;
        output = null;
        input = null;
        System.setOut(systemOut);
        Log.disableAllChannels();
        Log.close();
    }

    @Ignore
    @Test
    public void testLogDebug() throws Exception {
        Log.COMMUNICATION.debug("TC");
        Log.LOCOMOTION.debug("TL");
        Log.DATA_HANDLER.debug("TD");
        Log.STRATEGY.debug("TS");
        output.flush();

        Assert.assertTrue(input.readLine().endsWith("Démarrage du service de log"));
        Assert.assertTrue(input.readLine().endsWith(RESET));
        Assert.assertTrue(input.readLine().endsWith("TC" + RESET));
        Assert.assertTrue(input.readLine().endsWith("TL" + RESET));
        Assert.assertTrue(input.readLine().endsWith("TD" + RESET));
        Assert.assertTrue(input.readLine().endsWith("TS" + RESET));
    }

    @Ignore
    @Test
    public void testLogChannel() throws Exception {
        Log.COMMUNICATION.setActive(false);
        Log.DATA_HANDLER.setActive(false);

        Log.COMMUNICATION.debug("TC");
        Log.LOCOMOTION.debug("TL");
        Log.DATA_HANDLER.debug("TD");
        Log.STRATEGY.debug("TS");
        output.flush();

        Assert.assertTrue(input.readLine().endsWith("Démarrage du service de log"));
        Assert.assertTrue(input.readLine().endsWith(RESET));
        Assert.assertTrue(input.readLine().endsWith("TL" + RESET));
        Assert.assertTrue(input.readLine().endsWith("TS" + RESET));
    }

    @Ignore
    @Test
    public void testLogCritical() throws Exception {
        Log.COMMUNICATION.setActive(false);
        Log.LOCOMOTION.setActive(false);

        Log.COMMUNICATION.critical("SUUS");
        Log.LOCOMOTION.critical("SU");

        Assert.assertTrue(input.readLine().endsWith("Démarrage du service de log"));
        Assert.assertTrue(input.readLine().endsWith(RESET));
        Assert.assertTrue(input.readLine().endsWith("SUUS" + RESET));
        Assert.assertTrue(input.readLine().endsWith("SU" + RESET));
    }
}
