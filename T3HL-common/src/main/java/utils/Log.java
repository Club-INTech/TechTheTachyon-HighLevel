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

import java.io.*;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Module de Log par canaux
 *
 * @author rem, jglrxavpok
 */
public enum Log
{
    COMMUNICATION(true),
    DATA_HANDLER(true),
    LOCOMOTION(true),
    STRATEGY(true),
    ORDERS(true),
    LIDAR(false),
    LIDAR_PROCESS(true),
    PATHFINDING(true),
    GRAPHE(false),
    HOOK(true),
    TABLE(false),
    AI(true),
    ELECTRON(true),
    POSITION(false),
    LL_DEBUG(false),
    DYNAMIXEL_COM(false),

    // sorties standard
    STDOUT(true),
    STDERR(true, false),
    ;

    /**
     * Instance permettant d'avoir la date et l'heure
     */
    private static Calendar calendar;

    /**
     * Préfixes de couleurs pour l'affichage (Debug, Warning & Critical)
     */
    private static final String DEBUG       = "\u001B[34m";
    private static final String WARNING     = "\u001B[33m";
    private static final String CRITICAL    = "\u001B[31m";
    private static final String LOG_INFO    = "\u001B[32m";
    private static final String RESET       = "\u001B[0m";

    // Sorties standard. Initialisés lors du chargement de la classe Log, ils ne pointeront donc que vers ces sorties (non pas celles redéfinies avec System.setOut))
    /**
     * Sortie standard
     */
    private static PrintStream stdout = System.out;

    /**
     * Sortie standard d'erreur
     */
    private static PrintStream stderr = System.err;
    private static int counter;
    private static long startTime = -1;

    /**
     * Faut-il afficher le header complet dans les logs pour ce logger? ('false' uniquement pour STDERR)
     */
    private final boolean useLongHeader;
    private static final long MATCH_LENGTH = 100*1000;

    private enum Severity {
        CRITICAL(Log.CRITICAL),
        WARNING(Log.WARNING),
        DEBUG(Log.DEBUG),
        ;

        private final String color;
        private PrintStream activeOutput = System.out;
        private PrintStream nonactiveOutput = System.out;

        Severity(String color) {
            this.color = color;
        }
    }

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
    Log(boolean defaultActive)
    {
        this(defaultActive, true);
    }

    /**
     * Pour chaque canaux, on peut spécifier une couleur d'affichage
     * @param defaultActive     true si par défault affiché
     * @param useLongHeader     false si on utilise le header compact
     */
    Log(boolean defaultActive, boolean useLongHeader)
    {
        this.useLongHeader = useLongHeader;
        this.active = defaultActive;
        this.toLog = new StringBuilder();
    }


    public void debug(Object message)
    {
        writeToLog(Severity.DEBUG, message.toString(), this.active, 0);
    }

    /**
     * Méthode standard de log
     *
     * @param message   message à logger
     * @param stackOffset un offset dans la stack de la JVM, utile pour prendre en compte la redirection par les LogPrintStream
     */
    public void debug(Object message, int stackOffset)
    {
        writeToLog(Severity.DEBUG, message.toString(), this.active, stackOffset);
    }

    public void warning(Object message)
    {
        writeToLog(Severity.WARNING, message.toString(), this.active, 0);
    }

    /**
     * Méthode warning de log
     *
     * @param message   message à logger
     * @param stackOffset un offset dans la stack de la JVM, utile pour prendre en compte la redirection par les LogPrintStream
     */
    public void warning(Object message, int stackOffset)
    {
        writeToLog(Severity.WARNING, message.toString(), this.active, stackOffset);
    }

    public void critical(Object message)
    {
        writeToLog(Severity.CRITICAL, message.toString(), this.active, 0);
    }

    /**
     * Méthode warning de log
     *
     * @param message   message à logger
     * @param stackOffset un offset dans la stack de la JVM, utile pour prendre en compte la redirection par les LogPrintStream
     */
    public void critical(Object message, int stackOffset)
    {
        writeToLog(Severity.CRITICAL, message.toString(), true, stackOffset);
    }


    /**
     * Log du message
     *
     * @param severity  la sévérité du message
     * @param message   message à afficher
     * @param active    doit-on mettre ce message dans les logs actifs?
     * @param stackOffset   un offset dans la stack de la JVM pour savoir quelle méthode a demandé un log
     */
    private synchronized void writeToLog(Log.Severity severity, String message, boolean active, int stackOffset)
    {
        this.toLog.setLength(0);
        calendar = Calendar.getInstance();
        toLog
                .append(String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY)))
                .append(":")
                .append(String.format("%02d", calendar.get(Calendar.MINUTE)))
                .append(":")
                .append(String.format("%02d", calendar.get(Calendar.SECOND)))
                .append(".")
                .append(String.format("%03d", calendar.get(Calendar.MILLISECOND)))
        ;
        if(startTime > 0) {
            long elapsedTime = System.currentTimeMillis()-startTime;
            long remaining = MATCH_LENGTH-elapsedTime;
            toLog
                    .append(" (Time: ")
                    .append(String.format("%03d", elapsedTime / 1000))
                    .append(".")
                    .append(String.format("%03d", elapsedTime % 1000))

                    .append(" / ")

                    .append(String.format("%03d", MATCH_LENGTH / 1000))
                    .append(".")
                    .append(String.format("%03d", MATCH_LENGTH % 1000))

                    .append(" -> ")

                    .append(String.format("%03d", remaining / 1000))
                    .append(".")
                    .append(String.format("%03d", remaining % 1000))
                    .append(" left)")
            ;
        }
        String hour = toLog.toString();
        String color = severity.color;

        Thread currentThread = Thread.currentThread();
        if(3+stackOffset >= currentThread.getStackTrace().length) {
            stackOffset = currentThread.getStackTrace().length-3-1;
        }
        StackTraceElement elem = currentThread.getStackTrace()[3+stackOffset]; // appelant
        this.toLog.setLength(0);
        this.toLog
                .append("[")
                .append(color)
                .append(hour)
                .append(" ")
                .append(this.name())
                .append(" (")
                .append(severity.name())
                .append(") ")
                .append("on ")
                .append(currentThread.getName())
        ;

        if(useLongHeader) {
            toLog
                    // liens vers le code source cliquables (dans la console d'IDEA, et peut-être Eclipse)

                    // cf https://stackoverflow.com/questions/5232925/eclipse-console-what-are-the-rules-that-make-stack-traces-clickable
                    /*
                    "%s.%s(%s:%s)%n", s.getClassName(), s.getMethodName(), s.getFileName(), s.getLineNumber()
                     */
                    .append(" ")
                    .append(elem.getClassName())
                    .append(".")
                    .append(elem.getMethodName())
                    .append("(")
                    .append(elem.getFileName())
                    .append(":")
                    .append(elem.getLineNumber())
                    .append(")")
            ;
        }
        toLog
                .append("] ")
                .append(message )
                .append(RESET)
        ;
        if(active) {
            severity.activeOutput.println(this.toLog.toString());
            severity.activeOutput.flush();
        } else {
            severity.nonactiveOutput.println(this.toLog.toString());
            severity.nonactiveOutput.flush();
        }
    }

    /**
     * Initialise les flux d'entrée/sortie
     */
    public static void init()
    {
        System.out.println(LOG_INFO + "Démarrage du service de log");
        System.out.println(RESET);

        calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC+1:00"));
        // sorties standard (console)
        if(stdout == null) {
            stdout = System.out;
        }
        if(stderr == null) {
            stderr = System.err;
        }

        stdout.println("Initialisation du compteur...");
        File counterFile = new File("../logs/counter");

        counter = 0;
        if(counterFile.exists()) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(counterFile));
                try {
                    counter = Integer.parseInt(reader.readLine());
                } catch (NumberFormatException e) {
                    stderr.println("Erreur lors de la lecture du compteur");
                    e.printStackTrace();
                }
            } catch (IOException e) {
                stderr.println("Erreur lors de l'initialisation du compteur");
                e.printStackTrace();
            }
        } else {
            try {
                counterFile.createNewFile();
            } catch (IOException e) {
                stderr.println("Erreur lors de la création du fichier du compteur");
                e.printStackTrace();
            }
        }

        // création du dossier de logs si besoin
        File logFolder = new File("../logs/");
        if(!logFolder.exists()) {
            System.out.println("Le dossier de logs n'existe pas, tentative de création du dossier à "+logFolder.getAbsolutePath());
            if(!logFolder.mkdirs()) {
                String canonicalName;
                try {
                    canonicalName = logFolder.getCanonicalPath();
                } catch (IOException e) {
                    e.printStackTrace();
                    canonicalName = "ERREUR LORS DE LA RECUPERATION DU NOM => "+e.getMessage();
                }
                System.err.println("Erreur critique: Impossible de créer un dossier de log à '"+logFolder.getAbsolutePath()+"' (nom canonique: "+canonicalName+")!");
            } else {
                System.out.println("Réussite de la création du dossier de logs");
            }
        }

        try {
            PrintStream printer = new PrintStream(counterFile);
            printer.println(counter+1);
            printer.close();
        } catch (FileNotFoundException e) {
            stderr.println("Erreur lors de l'incrémentation du compteur");
            e.printStackTrace();
        }

        // sorties vers des fichiers
        OutputStream fullOutput = attemptInitLogOutput("everything");
        OutputStream activeOutput = attemptInitLogOutput("");
        OutputStream errorOnlyOutput = attemptInitLogOutput("errors");

        // initialisation des différents types de sorties


        // Sorties des Logs non actifs ("everything" seulement)
        MultiOutputStream logNonactiveOutputs = new MultiOutputStream(fullOutput);

        // Sorties des Logs actifs ("everything", la console et le log de base)
        MultiOutputStream logActiveOutputs = new MultiOutputStream(stdout, activeOutput, logNonactiveOutputs);

        //Sorties des Logs d'erreur ("everything", console, log de base et "errors")
        MultiOutputStream errorOutputs = new MultiOutputStream(stderr, fullOutput, activeOutput, errorOnlyOutput);

        PrintStream nonActiveStream = new PrintStream(logNonactiveOutputs);
        PrintStream activeStream = new PrintStream(logActiveOutputs);
        PrintStream errorStream = new PrintStream(errorOutputs);

        // préparation des sorties à utiliser pour les différentes sévérités
        Severity.DEBUG.nonactiveOutput = nonActiveStream;
        Severity.DEBUG.activeOutput = activeStream;
        Severity.WARNING.nonactiveOutput = nonActiveStream;
        Severity.WARNING.activeOutput = activeStream;

        // print et sauvegarde quoi qu'il arrive
        Severity.CRITICAL.activeOutput = errorStream;
        Severity.CRITICAL.nonactiveOutput = errorStream;

        // Gestion des System.out.print* et System.err.print*
        System.setOut(new LogPrintStream(Log.STDOUT, Log::debug));
        System.setErr(new LogPrintStream(Log.STDERR, Log::critical));
    }

    /**
     * Essaie de créer la sortie correspondante au suffixe donné. Renvoie 'null' et affiche des erreurs si la tentative échoue.
     * <br/>
     * Format du nom du fichier créé: "jour mois n°jour heure:minutes:secondes timezone année". Exemple: "Fri Apr 19 22:51:42 CEST 2019"
     * @param suffix le suffixe du fichier à créer
     * @return un {@link OutputStream} si la tentative réussi, 'null' sinon
     */
    private static OutputStream attemptInitLogOutput(String suffix) {
        // dossier d'exécution usuel: 'bin/'
        String date = calendar.getTime().toString()+" #"+counter;
        String filename;
        if(suffix == null || suffix.isEmpty()) {
            filename = "./../logs/"+date+".log";
        } else {
            filename = "./../logs/"+date+" - "+suffix+".log";
        }
        try {
            // tentative de création du fichier
            File file = new File(filename);
            if(!file.exists()) {
                System.out.println("Création du fichier de log '"+filename+"' à '"+file.getCanonicalPath()+"'");
                if(!file.createNewFile()) {
                    throw new IOException("Impossible de créer le fichier "+file.getAbsolutePath());
                } else {
                    System.out.println("Réussite de la création du fichier de log '"+filename+"");
                }
            }
            OutputStream out = new FileOutputStream(file);
            System.out.println("Ouverture du fichier de log '"+filename+"'");
            return out;
        } catch (IOException e) {
            System.err.println("Erreur Critique: Impossible de créer de fichier de log '"+filename+"' !!!");
        }

        return null;
    }

    /**
     * Ferme le log et sauvegarde dans un fichier si besoin
     */
    public static void close()
    {
        System.out.println(LOG_INFO + "Fermeture du service de log");
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
     * Active l'affichage du temps de match et du temps restant dans les logs
     */
    public static void setStartTime() {
        Log.STRATEGY.debug("Set match start time");
        startTime = System.currentTimeMillis();
    }

    public static long getStartTime() {
        return startTime;
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
