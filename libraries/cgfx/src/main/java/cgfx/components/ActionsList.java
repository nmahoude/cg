package cgfx.components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import cgfx.wrappers.GameWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

public class ActionsList extends ListView<String>{
	private List<String> actions = new ArrayList<>();
	private ObservableList<String> whatIfItems = FXCollections.observableArrayList ();
	private GameWrapper gameWrapper;
	
	public ActionsList(GameWrapper gameWrapper) {

		this.gameWrapper = gameWrapper;
		
		this.setItems(whatIfItems);
		
		this.setOnKeyPressed(event -> {
			KeyCodeCombination pasteKeyCodeCompination = new KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_ANY);
			if (pasteKeyCodeCompination.match(event)) {
				String pasteString = Clipboard.getSystemClipboard().getString();
				setActions(gameWrapper, pasteString);
			}
			KeyCodeCombination copyKeyCodeCompination = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_ANY);
			if (copyKeyCodeCompination.match(event)) {
				String allActions = getActions();
				
				final ClipboardContent content = new ClipboardContent();
		    content.putString(allActions);
				Clipboard.getSystemClipboard().setContent(content);
			}
		});
	
		
		this.getSelectionModel().selectedItemProperty().addListener(event -> {
    	int depth  = this.getSelectionModel().getSelectedIndex();
    	
    	gameWrapper.resetFromBase();
    	for (int i=0;i<=depth;i++) {
    		if (whatIfItems.get(i).contains(GameWrapper.CURRENT)) continue;
    		gameWrapper.applyAction(whatIfItems.get(i).split("\\|")[1].trim());
    	}
    	
		});
		

		
	}

  private String getActions() {
    String allActions = whatIfItems.stream()
    							   .filter(item -> !item.contains("Current"))
    							   .map(item -> item.split("\\|")[1].trim())
    							   .peek((String item) -> System.out.println(item))
    							   .collect(Collectors.joining(";"));
    return allActions;
  }

  private void setActions(GameWrapper gameWrapper, String pasteString) {
    actions = gameWrapper.getActionsFromString(pasteString);
    
    whatIfItems.clear();
    for (int i=0;i<actions.size();i++) {
    	whatIfItems.add("D"+i+"| "+actions.get(i));
    }
  }

	public void refreshActions() {
		whatIfItems.clear();
		
		actions.clear();
		actions.addAll(gameWrapper.calculateAIListOfActions());
		
		int d= 0;
		for (String action : actions) {
			whatIfItems.add("D"+(d++)+"| " + action);
		}
	}

	public List<String> actions() {
		return Collections.unmodifiableList(actions);
	}
}
