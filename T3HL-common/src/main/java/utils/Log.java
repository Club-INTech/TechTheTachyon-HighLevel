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
public enum Log {
    COMMUNICATION(true),
    DATA_HANDLER(true),
    LOCOMOTION(true),
    STRATEGY(true),
    LIDAR(true),
    PATHFINDING(true),
    GRAPHE(false),
    HOOK(true),
    TABLE(true),
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
     * Builder pour former les messages de logs (plus rapide que String + String)
     */
    private StringBuilder toLog;

    /**
     * Pour chaque canaux, on peut spécifier une couleur d'affichage
     * @param defaultActive     true si par défault affiché
     */
    Log(boolean defaultActive) {
        this.active = defaultActive;
        this.toLog = new StringBuilder();
    }

    /**
     * Méthode standard de log
     *
     * @param message   message à logger
     */
    public void debug(Object message) {
        writeToLog(DEBUG, message.toString(), this.active);
    }

    /**
     * Méthode warning de log
     *
     * @param message   message à logger
     */
    public void warning(Object message) {
        writeToLog(WARNING, message.toString(), this.active);
    }

    /**
     * Méthode warning de log
     *
     * @param message   message à logger
     */
    public void critical(Object message) {
        writeToLog(CRITICAL, message.toString(), true);
    }

    /**
     * Log du message
     *
     * @param color     le préfixe pour la couleur en sortie standart
     * @param message   message à affiché
     */
    private synchronized void writeToLog(String color, String message, boolean active) {
        String hour = String.format("[%sh%d:%d,%d]", calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                calendar.get(Calendar.SECOND),
                calendar.get(Calendar.MILLISECOND));

        if(active & printLogs) {
            StackTraceElement elem = Thread.currentThread().getStackTrace()[3];
            System.out.println(String.format("%s%s %s %s.%s:%d > %s%s", color, hour, this.name(),
                    elem.getClassName(), elem.getMethodName(), elem.getLineNumber(), message, RESET));
        }

        if(saveLogs) {
            writeToFile(String.format("%s %s > %s\n",hour, this.name(), message));
        }
    }

    /**
     * Ecrit le message spécifié dans le fichier de log
     *
     * @param message le message a logguer
     */
    private synchronized void writeToFile(String message) {
        // chaque message sur sa propre ligne
        try {
            writer.write(message);
            writer.flush();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Initialise les flux d'entrée/sortie
     */
    public static void init(Config config) {
        boolean ret = true;
        try {
            calendar = new GregorianCalendar();
            String hour = String.format("%s:%d:%d", calendar.get(Calendar.HOUR),
                    calendar.get(Calendar.MINUTE),
                    calendar.get(Calendar.SECOND));
            File testFinalRepertoire = new File("../logs");
            finalSaveFile = String.format("../logs/LOG-%s.txt", hour);
            if (!testFinalRepertoire.exists())
                ret = testFinalRepertoire.mkdir();
            if (!ret) {
                throw new IOException("Impossible de créer le répertoire logs");
            }
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(finalSaveFile), StandardCharsets.UTF_8));
            saveLogs = config.getBoolean(ConfigData.SAVE_LOG);
            printLogs = config.getBoolean(ConfigData.PRINT_LOG);
            System.out.println(String.format("%sDEMARRAGE DU SERVICE DE LOG", LOG_INFO));
            System.out.println(RESET);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Ferme le log et sauvegarde dans un fichier si besoin
     */
    public static void close() {
        System.out.println(String.format("%sFERMETURE DU SERVICE DE LOG", LOG_INFO));
        if(saveLogs)
            try {
                System.out.println(String.format("%sSAUVEGARDE DES FICHIERS DE LOG", LOG_INFO));
                System.out.println(RESET);
                synchronized (values()) {
                    if (writer != null)
                        writer.close();
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
    }

    /**
     * Active tous les channels
     */
    public static void activeAllChannels() {
        for (Log log : values()) {
            log.setActive(true);
        }
    }

    /**
     * Désactive tout les channels
     */
    public static void disableAllChannels() {
        for (Log log : values()) {
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
