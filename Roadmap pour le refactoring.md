# Roadmap pour le refactoring

Passer sous Gradle
- Tâches pour compiler, générer la structure correcte pour les robots
- Repo Maven local?
- 

Renommages
- Container => Robot
- Service => Module

Qualité de vie
- `getModule(Module.class)` doit pouvoir chercher dans les instances des classes filles (`getModule(RobotCore.class)` doit pouvoir renvoyer `PrincipalCore` ou `SecondaryCore`)
- Simplifier l'écriture des scripts:
    - Accès simplifié aux ordres?
    - Configuration simple à base d'annotations (plus d'oublis d'ajout dans `updateConfig`)
  
Exemples:
- Garder le script d'homologation comme script d'exemple
- 
  
IA
- Réfléchir à une intégration Compute Shader ou CUDA (si les Jetson Nano ça va)