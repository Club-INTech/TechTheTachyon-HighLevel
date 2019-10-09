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
import pfg.config.ConfigInfo;
import robot.Robots;
import utils.container.ContainerException;
import utils.container.Module;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Gestionnaire des singletons et dépendances entre les classes du code.
 * Un singleton est une classe implémentant "Module", et permettant d'instancier les autres modules dépendant.
 * Il stocke également toute les références des services à partir d'un dictionnaire.
 *
 * @author pf, rem, jglrxavpok
 */
public class HLInstance implements Module {
    /**
     * Instance du container (Singleton comme tous les services)
     */
    private volatile static HLInstance instance       = null;
    private final ExecutorService threadPool;

    /**
     * La config, qui ici n'implémente pas service
     */
    private Config config                   = null;

    /**
     * Liste des services déjà instanciés. Contient au moins Config et Log.
     * Les autres services appelables seront présents quand ils auront été appelés
     */
    private HashMap<String, Module> instanciedServices = new HashMap<>();

    /**
     * Liste des threads instanciés
     */
    private HashMap<String, Thread> instanciedThreads = new HashMap<>();

    /**
     * Lie un nom de classe à la **dernière** instance de module de ce type chargée. Prend en compte les sous-classes.
     * Permet donc de récupérer <pre>Master.class</pre> sur le principal avec <pre>container.module(Robot.class)</pre>
     */
    private Map<String, Module> lastInstance = new HashMap<>();

    /**
     * Instancie le gestionnaire de dépendances ainsi que la config
     */
    private HLInstance(String profile) {
        /* Affichage du message de bienvenue */
        printMessage("../resources/intro_colored.txt");

        Log.init();

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
        System.out.println("Chargement de la config...");
        ConfigInfo[] values = new ConfigInfo[ConfigData.values().length + Offsets.values().length];
        System.arraycopy(ConfigData.values(), 0, values, 0, ConfigData.values().length);
        System.arraycopy(Offsets.values(), 0, values, ConfigData.values().length, Offsets.values().length);
        config = new Config(values, true, "../config/config.txt", "Common", profile, "Offsets");

        Offsets.loadFromConfig(config);

        threadPool = Executors.newCachedThreadPool();
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

    public static HLInstance get(Robots type) {
        return getInstance(type.id());
    }

    /**
     * Getter pour instanciation du singleton
     */
    public static HLInstance getInstance(String configProfile) {
        if (instance == null) {
            instance = new HLInstance(configProfile);
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
     * Méthode initialisant un module sans le retourner. C'est strictement équivalent à <pre>module(TaClasse.class)</pre>,
     * c'est juste pour être plus lisible et compréhensible
     * @param moduleClass classe du module à initialiser
     * @throws ContainerException
     */
    public synchronized <S extends Module> void initModule(Class<S> moduleClass) throws ContainerException {
        module(moduleClass, new Stack<>());
    }

    /**
     * Méthode retournant une référence d'une classe demandée.
     *
     * @param   moduleClass classe demandée
     * @return  référene de l'instance de la classe demandée
     * @throws  ContainerException
     */
    public synchronized <S extends Module> S module(Class<S> moduleClass) throws ContainerException {
        return module(moduleClass, new Stack<String>());
    }

    /**
     * Méthode récursive créer une instance de la classe demandée s'il n'en existe pas déjà, auquel cas cette méthode
     * renvoie la référence de l'objet créé.
     * Gère automatiquement les dépendances entre les classes, et détecte automatiquement les dépendances circulaires
     *
     * @param   moduleClass   classe demandée
     * @param   stack     pile de services servant à détecter les dépendances circulaires
     * @return  référence de l'objet créé
     * @throws  ContainerException  en cas d'exception relative à l'instanciation d'objet ou de détection de dépendance
     *                              circulaire
     */
    @SuppressWarnings("unchecked")
    private synchronized <S extends Module> S module(Class<S> moduleClass, Stack<String> stack) throws ContainerException {
        try {
            /* Si l'objet à déjà été instancié, on renvoie la référence */
            if (instanciedServices.containsKey(moduleClass.getSimpleName()))
            {
                return (S) instanciedServices.get(moduleClass.getSimpleName());
            }

            // On vérifie si une des classes filles correspond
            Optional<Module> lastInstance = checkLastInstance(moduleClass);
            if(lastInstance.isPresent())
                return (S) lastInstance.get();

            if((moduleClass.getModifiers() & Modifier.ABSTRACT) != 0) {
                throw new ContainerException("Tried to instanciate module with abstract class '"+moduleClass.getCanonicalName()+"' but no instance has already been loaded.");
            }

            /* Détection des dépendances circulaires */
            if (stack.contains(moduleClass.getSimpleName()))
            {
                StringBuilder buf = new StringBuilder();
                for (String stk : stack)
                    buf.append(String.format(Locale.US, "%s -> ", stk));
                buf.append(moduleClass.getSimpleName());
                throw new ContainerException(buf.toString());
            }



            /* Mise à jour de la pile */
            stack.push(moduleClass.getSimpleName());

            /* Un moduleClass n'a qu'un seul constructeur */
            if (moduleClass.getConstructors().length > 1)
            {
                throw new ContainerException(moduleClass.getSimpleName() + " a plusieurs constructeurs !");
            }

            /* Récupération du constructeur & des paramètres */
            Constructor<S> constructor = (Constructor<S>) moduleClass.getDeclaredConstructors()[0];
            Class<Module>[] param = (Class<Module>[]) constructor.getParameterTypes();

            /* On demande récursivement chacun des paramètres */
            Object[] paramObject = new Object[param.length];
            for (int i = 0; i < param.length; i++)
            {
                paramObject[i] = module(param[i], stack);
            }

            /* On instancie l'objet ou on le stocke dans le dico */
            constructor.setAccessible(true);    // Petit hack, ne faite pas ca chez vous !
            Log.DATA_HANDLER.debug("Initialisation du moduleClass "+moduleClass.getSimpleName());
            S s = constructor.newInstance(paramObject);
            constructor.setAccessible(false);

            updateSubInstances(moduleClass, (Module)s);

            instanciedServices.put(moduleClass.getSimpleName(), (Module) s);

            /* Si c'est un Thread, on l'ajoute dans une liste à part */
            if (s instanceof Thread)
            {
                instanciedThreads.put(moduleClass.getSimpleName(), (Thread) s);
            }

            Log.DATA_HANDLER.debug("Mise à jour de la config du moduleClass "+moduleClass.getSimpleName()+" après son initialisation.");
            /* Mise à jour de la config */

            try {
                config.loadInto(s);
            } catch (ReflectiveOperationException e) {
                e.printStackTrace();
            }
            s.updateConfig(this.config);

            s.onInit(this);

            Log.DATA_HANDLER.debug("Module "+moduleClass.getSimpleName()+" prêt");

            /* Mise à jour de la pile */
            stack.pop();

            return s;

        } catch (IllegalAccessException
                | IllegalArgumentException
                | InvocationTargetException
                | SecurityException
                | InstantiationException e) {
            e.printStackTrace();
            throw new ContainerException(e);
        }
    }

    /**
     * Sauvegarde la dernière instance de <pre>moduleClass</pre> instanciée, et sauvegarde cette instance aussi pour les classes mères.
     * Permet de faire <pre>module(Robot.class)</pre> et de récupérer l'instance de Master ou Slave instanciée
     * @param moduleClass
     *      La classe du module
     * @param moduleInstance
     *      L'instance du module
     */
    private void updateSubInstances(Class<?> moduleClass, Module moduleInstance) {
        lastInstance.put(moduleClass.getSimpleName(), moduleInstance);
        if(Module.class.isAssignableFrom(moduleClass.getSuperclass())) {
            updateSubInstances(moduleClass.getSuperclass(), moduleInstance);
        }
    }

    /**
     * Vérifie si la dernière instance de 'service' chargée ne serait pas déjà présente sous la forme d'une instance de classe fille
     * @param service
     *      La classe du module
     * @param <S>
     *      Le type de module
     * @return
     *      <pre>Optional.empty()</pre> si rien n'a été trouvé, <pre>Optional.of(module)</pre> si on a trouvé une instance déjà chargée
     */
    private <S extends Module> Optional<Module> checkLastInstance(Class<S> service) {
        if(lastInstance.containsKey(service.getSimpleName())) {
            return Optional.of(lastInstance.get(service.getSimpleName()));
        }
        return Optional.empty();
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
                System.out.println(
                        line
                                .replace("<CJ>", "\u001B[33m") // jaune
                                .replace("<CB>",  "\u001B[34m") // bleu
                );

            reader.close();
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    /**
     * @see Module
     */
    @Override
    public void updateConfig(Config config) {
        for(Module module : instanciedServices.values()){
            if(module instanceof HLInstance)
                continue;
            try {
                config.loadInto(module);
            } catch (ReflectiveOperationException e) {
                e.printStackTrace();
            }
            module.updateConfig(config);
        }

        Offsets.loadFromConfig(config);
    }

    public <T> Future<T> async(String name, Callable<T> action) {
        return threadPool.submit(action);
    }

    public Future<Void> async(String name, Runnable action) {
        return async(name, () -> { action.run(); return null;});
    }

    /**
     * Getters (utilisé)
     */
    public Config getConfig() {
        return config;
    }
    public HashMap<String, Module> getInstanciedServices() {
        return instanciedServices;
    }
}
