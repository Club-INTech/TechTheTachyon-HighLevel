Refactoring du HL - Chronologie
===

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
