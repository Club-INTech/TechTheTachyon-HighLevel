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

package utils.container;

import data.SensorState;
import pfg.config.Config;
import utils.HLInstance;
import utils.TimeoutError;

import java.util.concurrent.*;
import java.util.function.Supplier;

/**
 * Interface servant à définir un service : un service est un singleton qui doit implémenter la méthode updateConfig,
 * et être instancié par le container
 *
 * @author pf, jglrxavpok
 */
public interface Module
{
    int WAIT_LOOP_DELAY = 5;

    default int getWaitLoopDelay() {
        return WAIT_LOOP_DELAY;
    }

    /**
     * Cette méthode est appelée par le container après instanciation du service, ou rechargement de la config.
     * Elle sert à attribuer à des attributs des valeurs contenues dans la config, quand {@link pfg.config.Configurable} ne suffit pas.
     */
    default void updateConfig(Config config) {}

    /**
     * Méthode appelée lorsque le module est instancié par le HL pour la première fois. Plus propre que d'utiliser le constructeur
     */
    default void onInit(HLInstance hl) {}

    // TODO: Move somewhere else
    static void waitWhileTrue(SensorState<Boolean> condition) {
        waitWhileTrue(condition::getData, () -> {});
    }

    /**
     * Boucle qui dure tant que la condition donnée est à 'true'
     * @param condition une fonction renvoyant un booléen
     */
    static void waitWhileTrue(Supplier<Boolean> condition) {
        waitWhileTrue(condition, () -> {});
    }

    static <ExceptionType extends Throwable> void waitWhileTrue(SensorState<Boolean> condition, AdditionalAction<ExceptionType> additionalAction) throws ExceptionType {
        waitWhileTrue(condition::getData, additionalAction);
    }

    /**
     * Boucle qui dure tant que la condition donnée est à 'true'
     * @param condition une fonction renvoyant un booléen
     * @param additionalAction une fonction ou lambda qui apporte un comportement additionnel à l'attente
     */
    static <ExceptionType extends Throwable> void waitWhileTrue(Supplier<Boolean> condition, AdditionalAction<ExceptionType> additionalAction) throws ExceptionType {
        while(condition.get()) {
            try {
                Thread.sleep(WAIT_LOOP_DELAY);
                additionalAction.act();
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    interface AdditionalAction<ExceptionType extends Throwable> {
        void act() throws ExceptionType;
    }

    static void withTimeout(long timeoutMillis, Runnable runnable) throws TimeoutError {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        final Future<Void> handler = executor.submit(() -> {
            runnable.run();
            return null;
        });

        try {
            handler.get(timeoutMillis, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            handler.cancel(true);
            throw new TimeoutError("Timeout of "+timeoutMillis+" expired!");
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            throw new TimeoutError("Timeout of "+timeoutMillis+" expired due to an error: ", e);
        }

        executor.shutdownNow();
    }

}
