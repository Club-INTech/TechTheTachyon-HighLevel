package data.controlers;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

/**
 * Permet de gérer les messages arrivants de manière générique
 */
public class ChannelHandler{

    private Consumer<String> function;
    private ConcurrentLinkedQueue<String> queue;

    /**
     * Constructeur
     * @param listener listener dans lequel on va s'abonner
     * @param channel channel auquel on s'abonne
     * @param function fonction qu'on apelle en cas de réception de message
     */
    public ChannelHandler(Listener listener, Channel channel, Consumer<String> function){
        this.function = function;
        this.queue = new ConcurrentLinkedQueue<>();
        listener.addQueue(channel, this.queue);
    }

    /**
     * On regarde si la file contient des messages, et si c'est le cas, on les traite
     */
    public void checkAndHandle(){
        if (!this.queue.isEmpty()){
            this.function.accept(this.queue.poll());
        }
    }
}
