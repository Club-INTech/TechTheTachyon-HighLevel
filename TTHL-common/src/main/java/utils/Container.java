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
import utils.container.ContainerException;
import utils.container.Service;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Stack;

/**
 * Gestionnaire des singletons et dépendances entre les classes du code.
 * Un singleton est une classe implémentant "Service", et permettant d'instancier les autres services dépendant.
 * Il stocke également toute les références des services à partir d'un dictionnaire.
 *
 * @author pf, rem
 */
public class Container implements Service {
    /**
     * Instance du container (Singleton comme tous les services)
     */
    private volatile static Container instance       = null;

    /**
     * La config, qui ici n'implémente pas service
     */
    private Config config                   = null;

    /**
     * Liste des services déjà instanciés. Contient au moins Config et Log.
     * Les autres services appelables seront présents quand ils auront été appelés
     */
    private HashMap<String, Service> instanciedServices;

    /**
     * Liste des threads instanciés
     */
    private HashMap<String, Thread> instanciedThreads;

    /**
     * Instancie le gestionnaire de dépendances ainsi que la config
     */
    private Container(String profile) {
        /* Affichage du message de bienvenue */
        printMessage("../resources/intro.txt");

        /* Affiche la version du programme (dernier commit et sa branche) */
        try {
            Process process_log = Runtime.getRuntime().exec("git log -1 --oneline");
            Process process_git = Runtime.getRuntime().exec("git branch");
            BufferedReader input_log = new BufferedReader(new InputStreamReader(process_log.getInputStream(), StandardCharsets.UTF_8));
            BufferedReader input_git = new BufferedReader(new InputStreamReader(process_git.getInputStream(), StandardCharsets.UTF_8));
            String toprint_log = input_log.readLine();
            if (toprint_log == null) {
                System.out.println("Projet non-versionné");
            } else {
                int index = toprint_log.indexOf(" ");
                input_log.close();
                String toprint_git = input_git.readLine();

                while (!toprint_git.contains("*"))
                    toprint_git = input_git.readLine();

                int index2 = toprint_git.indexOf(" ");
                System.out.println("Version : " + toprint_log.substring(0, index) + " on " + toprint_git.substring(index2 + 1) + " - [" + toprint_log.substring(index + 1) + "]");
                input_git.close();
            }
        } catch(IOException e1){
            System.out.println(e1);
        }

        /* Infos diverses */
        System.out.println("System : " + System.getProperty("os.name") + " " +
                System.getProperty("os.version") + " " + System.getProperty("os.arch"));
        System.out.println("Java : " + System.getProperty("java.vendor") + " " +
                System.getProperty("java.version") + ", max memory : " + Math.round(100.*Runtime.getRuntime().maxMemory()/(1024.*1024.*1024.))/100. +
                "G, available processors : " + Runtime.getRuntime().availableProcessors());
        System.out.println();

        System.out.println("   Remember, with great power comes great current squared times resistance !\n");

        /* Instanciation des attributs & de la config */
        instanciedServices = new HashMap<>();
        instanciedThreads = new HashMap<>();
        config = new Config(ConfigData.values(), true, "../config/config.txt", "Common", profile);
        Log.init(config);

        /* Le container est un service ! */
        instanciedServices.put(getClass().getSimpleName(), this);
    }

    /**
     * Méthode appelée juste avant la destruction de l'objet
     */
    @Override
    protected void finalize() {
        Log.close();
        printMessage("../resources/outro.txt");
        try {
            for (Thread thread : instanciedThreads.values()) {
                thread.interrupt();
                thread.join(100);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Getter pour instanciation du singleton
     */
    public static Container getInstance(String ConfigProfile) {
        if (instance == null) {
            instance = new Container(ConfigProfile);
        }
        return instance;
    }

    /**
     * WARNING : Utilisé UNIQUEMENT pour les tests !!! Jamais on fait appel au Garbage Collector, c'est sale !
     */
    @SuppressWarnings("")
    public static void resetInstance() {
        instance = null;
        System.gc();
    }

    /**
     * Méthode appelée au début du programme après instanciation des services,
     * elle démarre tout les Threads instanciés, dans un certain ordre s'il faut
     */
    public void startInstanciedThreads()
    {
        // TODO : A compléter au fur et à mesure que l'on implémente les différents Threads
        for (Thread thread : instanciedThreads.values()) {
            thread.start();
        }
    }

    /**
     * Méthode retournant une référence d'une classe demandée.
     *
     * @param   service classe demandée
     * @return  référene de l'instance de la classe demandée
     * @throws  ContainerException
     */
    public synchronized <S extends Service> S getService(Class<S> service) throws ContainerException {
        return getService(service, new Stack<String>());
    }

    /**
     * Méthode récursive créer une instance de la classe demandée s'il n'en existe pas déjà, auquel cas cette méthode
     * renvoie la référence de l'objet créé.
     * Gère automatiquement les dépendances entre les classes, et détecte automatiquement les dépendances circulaires
     *
     * @param   service   classe demandée
     * @param   stack     pile de services servant à détecter les dépendances circulaire
     * @return  référence de l'objet créé
     * @throws  ContainerException  en cas d'exception relative à l'instanciation d'objet ou de détection de dépendance
     *                              circulaire
     */
    @SuppressWarnings("unchecked")
    private synchronized <S extends Service> S getService(Class<S> service, Stack<String> stack) throws ContainerException {
        try {
            /* Si l'objet à déjà été instancié, on renvoie la référence */
            if (instanciedServices.containsKey(service.getSimpleName()))
            {
                return (S) instanciedServices.get(service.getSimpleName());
            }

            /* Détection des dépendances circulaires */
            if (stack.contains(service.getSimpleName()))
            {
                StringBuffer buf = new StringBuffer();
                for (String stk : stack)
                    buf.append(String.format(Locale.US, "%s -> ", stk));
                buf.append(service.getSimpleName());
                throw new ContainerException(buf.toString());
            }

            /* Mise à jour de la pile */
            stack.push(service.getSimpleName());

            /* Un service n'a qu'un seul constructeur */
            if (service.getConstructors().length > 1)
            {
                throw new ContainerException(service.getSimpleName() + " a plusieurs constructeurs !");
            }

            /* Récupération du constructeur & des paramètres */
            Constructor<S> constructor = (Constructor<S>) service.getDeclaredConstructors()[0];
            Class<Service>[] param = (Class<Service>[]) constructor.getParameterTypes();

            /* On demande récursivement chacun des paramètres */
            Object[] paramObject = new Object[param.length];
            for (int i = 0; i < param.length; i++)
            {
                paramObject[i] = getService(param[i], stack);
            }

            /* On instancie l'objet ou on le stocke dans le dico */
            constructor.setAccessible(true);    // Petit hack, ne faite pas ca chez vous !
            S s = constructor.newInstance(paramObject);
            constructor.setAccessible(false);
            instanciedServices.put(service.getSimpleName(), (Service) s);

            /* Si c'est un Thread, on l'ajoute dans une liste à part */
            if (s instanceof Thread)
            {
                instanciedThreads.put(service.getSimpleName(), (Thread) s);
            }

            /* Mise à jour de la config */
            s.updateConfig(this.config);

            /* Mise à jour de la pile */
            stack.pop();

            return s;

        } catch (IllegalAccessException
                | IllegalArgumentException
                | InvocationTargetException
                | SecurityException
                | InstantiationException e) {
            e.printStackTrace();
            throw new ContainerException(e.getMessage());
        }
    }

    /**
     * Affichage d'un fichier
     * @param filename  nom du fichier à afficher
     */
    private void printMessage(String filename)
    {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename), StandardCharsets.UTF_8));
            String line;

            while((line = reader.readLine()) != null)
                System.out.println(line);

            reader.close();
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    /**
     * @see Service
     */
    @Override
    public void updateConfig(Config config) {}

    /**
     * Getters (utilisé)
     */
    public Config getConfig() {
        return config;
    }
    public HashMap<String, Service> getInstanciedServices() {
        return instanciedServices;
    }
}
