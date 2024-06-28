package fall2022;

public class TTSlice {
	public State state = new State();
	// List<Ilot> ilots = new ArrayList<>();

	public void copyFrom(State original) {
		this.state.copyFrom(original);
	}

	public void copyFrom(TTSlice model) {
		this.state.copyFrom(model.state);
	}
	
	
	
}
