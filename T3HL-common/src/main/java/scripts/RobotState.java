package scripts;

/**
 * Etablit l'état du robot non détectable avec des capteurs
 *
 * @author rem
 */
public interface RobotState {

    /**
     * @return  l'état de la donnée
     */
    Object getData();

    /**
     * Set l'état d'un donnée
     * @param data  la donnée
     */
    void setData(Object data);
}
