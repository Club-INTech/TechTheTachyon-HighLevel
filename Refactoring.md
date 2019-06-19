Refactoring du HL - Chronologie
===

19/06/2019 - 22h33
----
+ Création de ce document
+ Remplacement de Config v1.4 par Config v2.1
    + Refactoring de ConfigData et Offsets pour convenir à la nouvelle architecture. (C'est moins joli qu'un enum, 
    mais ça permet d'avoir des types génériques et donc évite de se tromper entre 'getBoolean', 'getString', etc.)
    + Utilisation du système d'attributs dérivés pour savoir si on est en symétrie ou non, plus besoin de vérifier la couleur "à la main"
