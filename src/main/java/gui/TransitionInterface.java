package gui;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.Transition;
import javafx.scene.Node;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Interface which has to be implemented by classes that use transitions
 */
public interface TransitionInterface {
    /**
     * Displays a transition which fades in the content of a scene
     * @param time      the time it takes for content to fade in
     * @param nodes     the nodes in the scene that need to fade in
     */
    default void loadSceneTransition(double time, Node @NotNull ... nodes) {
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(time), nodes[0]);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        nodes[0].setDisable(true);
        for (int i = 1; i < nodes.length; i++) {
            nodes[i].opacityProperty().bind(nodes[0].opacityProperty());
            nodes[i].setDisable(true);
        }

        addTransition(getTransitions(), fadeTransition);
        fadeTransition.play();
        fadeTransition.setOnFinished(e -> {
            removeTransition(getTransitions(), fadeTransition);
            for (Node node : nodes) {
                node.setDisable(false);
            }
        });
    };

    default void loadSceneTransition(Node @NotNull ... nodes) {
        loadSceneTransition(0.3, nodes);
    };

    /**
     * Displays a transition which fades out the content of a scene
     * @param runnable      runnable which is executed after finishing the fade out
     * @param nodes         the nodes in the scene that need to fade out
     */
    default void quitSceneTransition(Runnable runnable, Node @NotNull ... nodes) {
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(.3), nodes[0]);
        for (int i = 1; i < nodes.length; i++) {
            nodes[i].opacityProperty().bind(nodes[0].opacityProperty());
        }
        fadeTransition.setFromValue(1);
        fadeTransition.setToValue(0);

        addTransition(getTransitions(), fadeTransition);
        fadeTransition.play();
        fadeTransition.setOnFinished(e -> {
            runnable.run();
        });
    };

    /**
     * Method to continue the transitions
     * @param transitions       list with the transitions
     */
    default void continueTransitions(List<Transition> transitions) {
        for (Transition transition : transitions) {
            if (transition.statusProperty().getValue() == Animation.Status.PAUSED) {
                transition.play();
            }
        }
    };

    /**
     * Method to pause the transitions
     * @param transitions       list with the transitions
     */
    default void pauseTransitions(List<Transition> transitions){
        for (Transition transition : transitions) {
            if (transition.statusProperty().getValue() == Animation.Status.RUNNING) {
                transition.pause();
            }
        }
    };

    default void addTransition(List<Transition> transitions, Transition transition) {
        transitions.add(transition);
    };

    default void removeTransition(List<Transition> transitions, Transition transition) {
        transitions.remove(transition);
    }

    List<Transition> getTransitions();
}
