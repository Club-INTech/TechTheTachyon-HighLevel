Refactoring du HL - Chronologie
===

11/07/2019 - 20h07
----
* Utilisation de `@Configurable` partout sauf dans les scripts pour remplacer `Module#updateConfig` quand possible
* Prise en compte des recalages mécaniques en simulation

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
