# CGFX


## Usage

First, create an implementation that extends CGFactory

#### GameWrapper
class that wraps the game you want to show (state, player, ...)

#### GameViewer
class that represents the view of the game (can be minimalistic or to par with real game)
	
#### GameOptionPane
if you want to show/hide some gfx in the gameviewer. not mandatory

#### EvaluationWrapper
wrapper of the evaluation (score) of the state of the game

## Applications

#### FXPlayer
Allows to navigate a game and see the 'what if' calculated by the ai (in GameWrapper#_calculateListOfActions)

## Runners

install javafx SDK (https://gluonhq.com/products/javafx/)

then add 
	
	--module-path xxxx\javafx-sdk-17.0.2\lib\ --add-modules javafx.controls,javafx.fxml

to VM arguments

