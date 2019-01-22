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

package utils;

import pfg.config.Config;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Service de Log par canaux
 *
 * @author rem
 */
public enum Log
{
    COMMUNICATION(true),
    DATA_HANDLER(true),
    LOCOMOTION(true),
    STRATEGY(true),
    LIDAR(true),
    PATHFINDING(true),
    GRAPHE(true),
    HOOK(true),
    ;

    /**
     * Préfixes de couleurs pour l'affichage (Debug, Warning & Critical)
     */
    private static final String DEBUG       = "\u001B[32m";
    private static final String WARNING     = "\u001B[33m";
    private static final String CRITICAL    = "\u001B[31m";
    private static final String LOG_INFO    = "\u001B[34m";
    private static final String RESET       = "\u001B[0m";

    /**
     * Instance permettant d'avoir la date et l'heure
     */
    private static GregorianCalendar calendar;

    /**
     * Buffer d'écriture dans un fichier de log
     */
    private static BufferedWriter writer;

    /**
     * True pour sauvegarder les logs
     * override par la config
     */
    private static boolean saveLogs     = true;

    /**
     * True pour afficher les logs
     * override par la config
     */
    private static boolean printLogs    = true;

    /**
     * Nom du fichier de sauvegarde des logs
     */
    private static String finalSaveFile;

    /**
     * Spécifie si les logs du canal doivent être activés ou non
     */
    private boolean active;

    /**
     * Pour chaque canaux, on peut spécifier une couleur d'affichage
     * @param defaultActive     true si par défault affiché
     */
    Log(boolean defaultActive)
    {
        active = defaultActive;
    }

    /**
     * Méthode standard de log
     *
     * @param message   message à logger
     */
    public void debug(Object message)
    {
        writeToLog(DEBUG, message.toString(), this.active);
    }

    /**
     * Méthode warning de log
     *
     * @param message   message à logger
     */
    public void warning(Object message)
    {
        writeToLog(WARNING, message.toString(), this.active);
    }

    /**
     * Méthode warning de log
     *
     * @param message   message à logger
     */
    public void critical(Object message)
    {
        writeToLog(CRITICAL, message.toString(), true);
    }


    /**
     * Log du message
     *
     * @param color     le préfixe pour la couleur en sortie standart
     * @param message   message à affiché
     */
    private synchronized void writeToLog(String color, String message, boolean active)
    {
        String hour = calendar.get(Calendar.HOUR_OF_DAY) + "h" +
                calendar.get(Calendar.MINUTE) + ":" +
                calendar.get(Calendar.SECOND) + "," +
                calendar.get(Calendar.MILLISECOND);

        if(active & printLogs)
        {
            StackTraceElement elem = Thread.currentThread().getStackTrace()[3];
            System.out.println(color + hour + " " + this.name() + " " +
                    elem.getClassName() + "." + elem.getMethodName() + ":" + elem.getLineNumber() + " > " + message);
        }

        if(saveLogs)
        {
            writeToFile(hour + " " + this.name() + " > " + message);
        }
    }

    /**
     * Ecrit le message spécifié dans le fichier de log
     *
     * @param message le message a logguer
     */
    private synchronized void writeToFile(String message)
    {
        // chaque message sur sa propre ligne
        message += "\n";
        try
        {
            writer.write(message);
            writer.flush();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Initialise les flux d'entrée/sortie
     */
    public static void init(Config config)
    {
        boolean ret = true;
        try {
            calendar = new GregorianCalendar();
            String hour = calendar.get(Calendar.HOUR) + ":" +
                    calendar.get(Calendar.MINUTE) + ":" +
                    calendar.get(Calendar.SECOND);
            File testFinalRepertoire = new File("../logs");
            finalSaveFile = "../logs/LOG-" + hour + ".txt";
            if (!testFinalRepertoire.exists())
                ret = testFinalRepertoire.mkdir();
            if (!ret) {
                throw new IOException("Impossible de créer le répertoire logs");
            }
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(finalSaveFile), StandardCharsets.UTF_8));
            saveLogs = config.getBoolean(ConfigData.SAVE_LOG);
            printLogs = config.getBoolean(ConfigData.PRINT_LOG);
            System.out.println(LOG_INFO + "DEMARRAGE DU SERVICE DE LOG");
            System.out.println(RESET);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Ferme le log et sauvegarde dans un fichier si besoin
     */
    public static void close()
    {
        System.out.println(LOG_INFO + "FERMETURE DU SERVICE DE LOG");
        if(saveLogs)
            try {
                System.out.println(LOG_INFO + "SAUVEGARDE DES FICHIERS DE LOG");
                System.out.println(RESET);
                synchronized (values()) {
                    if (writer != null)
                        writer.close();
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
    }

    /**
     * Active tous les channels
     */
    public static void activeAllChannels()
    {
        for (Log log : values())
        {
            log.setActive(true);
        }
    }

    /**
     * Désactive tout les channels
     */
    public static void disableAllChannels()
    {
        for (Log log : values())
        {
            log.setActive(false);
        }
    }

    /**
     * Getters & Setters
     */
    public boolean isActive() {
        return active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }
}
