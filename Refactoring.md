Refactoring du HL - Chronologie
===

02/09/2019
----
+ Ajout de nouvelles méthodes de raccourci et utilisation

28/07/2019
----
+ Tentative de nouveau système pour créer des ordres unifiés

15/07/2019
----
* `Container` est devenu `HLInstance`. Les différents champs et variables sont nommés `hl` maintenant.
* Début de programmation évènementielle pour gérer les messages des différentes connexions. Utilise un Executor sous le capot pour éviter de générer 30000 threads (en moyenne ~26 sur le script de match sur le simulateur)


14/07/2019
----
* Les scripts prennent maintenant que `Container` en argument, ça permet d'initialiser le robot, la table et les actuateurs (cf. en-dessous) dans tous les scripts sans devoir le faire manuellement.
+ Couche "d'abstraction" des actuateurs (cf package `lowlevel`) qui se base sur les anciens ordres
    + Actuateurs à deux états (`OnOffActuator`) tels que les électrovannes ou les pompes
        + Méthode `activate(boolean = false)` avec overloading pour attendre confirmation du LL
        + Méthode `desactivate(boolean = false)` avec overloading pour attendre confirmation du LL
    + Actuateurs asynchrones: pour les actuateurs qui ont un processus asynchrone dans le LL
        + Méthode `isFinished()` pour savoir si l'actuateur a fini
        + Méthode `waitFor()` pour attendre que l'actuateur ait fini
    + Ascenseurs (asynchrones):
        + Méthodes `up(boolean = false)` `down(boolean = false)` `updown(boolean = false)` `downup(boolean = false)` pour les ordres correspondants
* Utilisation de cette nouvelle couche dans les anciens scripts pour les quelques ordres implémentés.
* Ajout d'une méthode de raccourci `turn(double)` dans `Script` pour ne pas avoir à écrire `robot.` à chaque fois (test, sûrement plein d'autre méthodes comme celle-ci à ajouter).
* `ContainerException` est maintenant une `RuntimeException`, plus besoin de la catch tout le temps. Si ça plante, c'est qu'il y a une bonne raison
* `ContainerException` appropriée levée lorsqu'on essaie d'appeler `Container#module(Class<? extends Module>)` avec une classe abstraite et qu'aucune instance n'a déjà été chargée directement.
* Changement des signatures de certaines des méthodes `Script` (`Integer` devient `int`).
* `X6Alter` est devenu `PaletsX6` (il le méritait)
* Léger ménage des scripts non utilisés.

11/07/2019
----
* Utilisation de `@Configurable` partout où possible, pour remplacer les trop nombreux `Module#updateConfig`
* Prise en compte des recalages mécaniques en simulation
* `container.module(Robot.class)` renvoit maintenant la dernière instance de `Robot.class` chargée (ie `Master` sur le principal, `Slave` sur le secondaire)

11/07/2019 - 00h08
----
+ Ajout de la génération automatique des options de lancement sous IDEA

10/07/2019 - 21h27
----
* Passage sous Gradle pour simplifier la lecture et le rajout de dépendances

19/06/2019 - 22h33
----
+ Création de ce document
+ Remplacement de Config v1.4 par Config v2.1
    + Refactoring de ConfigData et Offsets pour convenir à la nouvelle architecture. (C'est moins joli qu'un enum, 
    mais ça permet d'avoir des types génériques et donc évite de se tromper entre 'getBoolean', 'getString', etc.)
    + Utilisation du système d'attributs dérivés pour savoir si on est en symétrie ou non, plus besoin de vérifier la couleur "à la main"
    + Utilisation de @Configurable pour la symétrie