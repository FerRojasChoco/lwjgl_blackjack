TODO para mis compas:

1- Actualmente para mover la camera hay que mantener apretado un boton (L-Shift o RighClick ver input) y luego mover el mouse
cambiar eso a que la camara se mueva sola con el movimiento del mouse (como en un juego normal).

2- Relacionado en parte a la camara pero mas bien al movimiento del personaje (WASD), ahora mismo el movimiento camara/personaje es 
como una "free cam", hay que cambiar para que sea fijo esto. O sea, actualmente si el personaje mira hacia abajo y oprime W para 
moverse hacia delante, el personaje se va a ir en esa direccion. Pero lo que deberia ocurrir es que se mueva en direccion 
x o z nomas, no en x. Por que o sino el pj va a poder volar XD.
Una opcion es hacer que el movimiento sea fijo y exclusivamente entre los ejes x o z y que no se pueda mover en el eje y.


3- Usando ImGUI. Preparar el HUD para el juego, que incluya el dinero que tiene el jugador, hacer un menu de pausa (ahora mismo si se aprieta escape el juego se cierra, ojo con eso) y que el menu incluya botones de resumir juego, salir del juego, configuraciones de audio, ayuda/tutorial. 

4- Preparar como van a funcionar las conversaciones, que boton oprimir para hablar con otra persona, decidir y disenhar como va a ser
el dialogo (popups encima de la cabeza del personaje, o un cuadro con texto abajo, decidir si el personaje puede hablar o 
darle opciones para responder, etc)

5- Buscar o disenhar e ir cargando texturas que se vayan a usar en el juego, cartas de blackjack, chips de apuesta, mesa etc etc.
Guardar todas las referencias para dar creditos en un txt o pdf para poder agregar luego al reporte y a los creditos del juego
NOTA: por ahora el engine soporta la carga de texturas de .obj con .mtl nomas por lo que mejor si se busca esos tipos de archivos.
NOTA2: se puede optar por cambiar tambien la manera de loadear las textures pero es bastante complejo, si no se encuentran los
recursos necesarios en los formatos mencionados, se va a considerar cambiar a .blend, hablar del tema



6- Preparar la musica y efectos de sonido del juego, asi como preparar una manera de cambiar el volumen 
LUEGO IMPLEMENTAR CON EL PUNTO 3 que es incluir eso en el menu -> audio settings
guardar todas las referencias y demas para dar los creditos en el juego y en el reporte

7- Crear el menu de inicio del juego, con las opciones de iniciar juego, salir de juego, settings, creditos 
(esto trata de escenas y cambio de escenas y game states y demas que es logica muy core del juego, por lo que puede ser un poco
mas complejo que el resto de tareas, creo que igual se puede hacer con IMGUI tambien pero hay que ver como hacer)