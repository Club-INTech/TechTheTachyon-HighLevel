## MEMO
### MEMO - Java
#### Java - Language
* Les **types primitifs** des variables en java sont : `boolean`, `byte`(1 octet), `char`(2 octets), `short`(entier 2 octets), `int`(entier 4 octets), `long`(entier 8 octets),
  `float`(flottant 4 octets) et `double`(flottant 8 octets)

* Les **références** en java sont tous les types qui n'appartiennent pas aux 8 types primitifs cités dessus.

* Le passage dans les fonctions se fait soit par référénce soit par type primitif, quand il se fait par type primitif,
  la fonction crée une copie de la variable, agit sur cette variable et la renvoit, donc la variable 
  donnée en paramètre ne se retrouve pas modifiée. 
  Quand le passage se fait par référence, la variable passée en paramètre se rerouve modifiée, il faut donc 
  la cloner si on ne veut pas qu'elle soit modifiée.

* La **programmation orientée objet** consiste à penser l'application qu'on souhaite coder en terme de fonctionnalités, ou plutôt de concepts,
 en partant d'un diagramme UML. Il s'agit donc de cerner les entités qui présentent des caractéristiques et de construire un modèle à partir 
 de cela. 

* Une **classe** c'est un moule qui permettra d'instancier plusieurs objets présentant des caractéristiques qu'on nommera
 attributs de la classe, ces objets doivent bien évidemment intéragir entre eux à travers des méthodes. 

* Il ne faut pas oublier d'instancier son objet sinon vous aurez une erreur d'éxecution (NullPointerException) !

* Il existe deux types de méthodes : **méthodes de classe (`static`)**, il n'y a pas besoin d'instancier les objets pour les utiliser,
 et les méthodes d'instance qui doivent être appelés sur un **objet instancié (avec le `new`)**, l'appel se fait ainsi :
        
      Objet objet = new Objet();
      objet.maMethode();

* Il existe quatre attributs utilisés pour gérer la visibilité : `public`, `private`, (rien), `protected`, donc quatre déclarations possibles de méthodes :
        
      public void methode(){....}
      private void methode(){....}
      void methode(){....}
      protected void methode(){....}

    - `public` : attribut ou méthode visible partout
    - `private` : attribut ou méthode visible dans la classe : fonctionnalités internes à la classe
    - `protected` : attribut ou méthode visible dans les classes filles  ( voir héritage en dessous)
    - rien : attribut ou méthode visible dans le package
 
* Une classe peut hériter d'autres classes avec le mot `extends`, une classe fille est du même type que sa classe mère, possède ses mêmes attributs et
  méthodes qu'elle peut modifier en les `@Override`. Une classe mère n'est pas du même type que sa classe fille, mais on peut le transtyper en faisant :
 
        ClasseMere classeMere = (ClasseFille) new ClasseFille();
 
* Une classe ne peut hériter que d'une seule classe, d'où l'intérêt des interfaces.
* Les `interface`s définissent un comportement de la classe qui les implémentent. Les classes peuvent implémenter plusieurs interfaces.
* Les `enum`s : c'est une classe comme les autres mais avec la particularité de lister toutes ses instances à l’intérieur de la classe.
  Les instances sont visibles partout et accessibles de partout dans le code via une référence.
* Les exceptions servent à gérer les erreurs via un système de `try` `catch`.
* Une classe d'un package `java` inconnue ? Aller sur https://docs.oracle.com/javase/8/docs/api/, et chercher la documentation sur la
  classe inconnue

#### Java - Convention
* Une Classe commence toujours par une majuscule, une instance par une minuscule, pas d'accent dans les noms :

      MaClasse monInstance = new MaClasse(...);

* Exception faite des champs `final` et des instances d'`enum`, qui doivent être en majuscule :

        public enum MonEnum {
            PREMIERE_INSTANCE,
            D_INSTANCE,
            ;

            public static final TIME_STEP   = 58;
        }

* Les méthodes doivent être **documentées** : une documentation doit expliquer à quoi sert la méthode,
  pas son fonctionnement interne. Si son fonctionnement interne est complexe et mérite d'être
  détaillée, on ajoute du commentaire dans le corps de la méthode. Exemple de utils.math.Rectangle.java :

        /**
         * Ceci est une documentation (javadoc)
         * Cette méthode retourne true si notre rectangle contient un point
         * @param point point
         * @return      true si le rectangle contient un point
         */
        @Override
        public boolean isInShape(Vec2 point){
            return Math.abs(point.getX() - center.getX()) < this.length/2 &&
                    Math.abs(point.getY() - center.getY()) < this.width/2;
        }

        // Ceci est un commentaire
        /* Ca aussi, c'est
        un commentaire */

* Les fichiers sources de ce projet doivent avoir en entête le copyright Intech 2018 - GNU License (c'est mieux):

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

### MEMO - Git
* On travaille toujours dans la branche **dev**, la branche **master** est réservée à ce qui **fonctionne** !
* On **commit** TOUJOURS quelque chose qui compile, avec un message CLAIR (git commit -m "Mon message"),
  pour vérifier, dans le terminal :

        mvn compile

* On **push** quelque chose d'à peu près terminé !
* On **merge** sur **master** des features dont les **tests** fonctionnent,
  pour vérifier, dans le terminal :

        mvn clean install
        // ou
        mvn test

### MEMO - IntelliJ
1. Le mode debug : IntelliJ intègre un debuggeur très performant, qui permet d'executer du code pas à pas.
   Pour ce faire, il faut créer des "break point", c'est-à-dire des lignes de codes ou le programme va
   s'arréter et commencer une execution pas à pas. Pour placer un break point, il suffit de cliquer gauche
   **à droite du numero** de la ligne de code sur laquelle vous voulez créer le break point :

   ![Break Point](images/IntelliJ-breakpoint.png)

   Puis executer le test ou main en mode debug :

   ![Debug Mode](images/IntelliJ-debugmode.png)

   Ce qui vous donne quelque chose comme ca :

   ![Debug Mode - Run](images/IntelliJ-debugmode-run.png)

2. Code couleur git
    * rouge : fichier non suivi
    * vert : nouveau fichier suivi
    * bleue : fichier modifié indexé ou non
    * blanc : fichier non modifié

### MEMO - UML
![Exemple UML - utils.communication](uml/utils.communication.png)
1. Classes
    * Cercle (CommunicationInterface & Service) : **Interface** - `interface`
    * _Intalique_ (SocketInterface) : **Classe Abstraite** `abstract class`
    * "Enum" Class (Connection) : **Enum** `enum`
    * Classe : **Classe** `class`

2. Champs & Methodes
    * \- private
    * \+ public
    * \# protected
    * \~ package-private
    * <u>FieldOrMethods</u> static
    * @ surcharge
