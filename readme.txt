README
------

Ce projet fournit un framework simple [1] pour l'execution de programmes
accessibles en tant que ressources REST.

Les ressources se programment comment des classes annotees avec l'API JAX-RS.
Voir un example avec la classe car.tp2.HelloWorldResource.

Pour pouvoir etre prises en compte par le framework, les ressources doivent etre
declarees dans la classe car.tp2.Config, methode addResources. La declaration se
fait en ajoutant une ligne de la forme :

	resources.add( new MaClasseDeResource() )
	
Autant de ressources que necessaire peuvent etre declarees.

Le lancement du framework se fait en invoquant la methode Main de la classe
car.tp2.Starter.

Une fois lancees, les ressources sont accessibles, par exemple via un
navigateur, en chargeant une URL de la forme :

	http://localhost:8080/rest/tp2/_ressource_
	par exemple : http://localhost:8080/rest/tp2/helloworld
	

Lionel Seinturier.
23 juillet 2015.


[1] http://aredko.blogspot.fr/2013/01/going-rest-embedding-jetty-with-spring.html

Commentaires :
	-Manque de documentations sur le TP. 
	-Objectifs trop peu clairs et explicites. 
	-Aucun TD de préparation. 
	-Poly de cours trop rapide sur le sujet.
	-Proposer un serveur fonctionnel pour les personnes n'ayant pas fini le premier TP serait appréciable
	-parser incompatible pour récupérer la liste des fichiers
	-il faut ajouter des /r pour les messages d'erreurs du serveur et cela n'est expliqué nulle part.
	-Malgré cela un TP interessant une fois que l'on a compris ce qu'on attendait de nous.
