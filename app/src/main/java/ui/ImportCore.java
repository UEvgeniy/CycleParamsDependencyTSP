package ui;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;


class ImportCore {
    
    static EventHandler<ActionEvent> onCancel(Task task){
        return (actionEvent) -> {
            if (task != null && task.isRunning())
                task.cancel();
            ((javafx.scene.control.Button) actionEvent.getSource()).setVisible(false);
        };
    }

}
