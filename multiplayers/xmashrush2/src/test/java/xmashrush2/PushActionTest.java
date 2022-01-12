package xmashrush2;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class PushActionTest {

	
	@Test
	void actionAreCompatibleWhenAction2IsNull() throws Exception {
		PushAction sut = PushAction.actions(1, Direction.UP);
		
		Assertions.assertThat(sut.isCompatibleWith(null)).isTrue();
	}
	
	@Test
	void actionsAreCompatibleWhenSameDirectionButDifferentOffsets() throws Exception {
		PushAction sut = PushAction.actions(1, Direction.UP);
		PushAction other = PushAction.actions(2, Direction.UP);
		
		Assertions.assertThat(sut.isCompatibleWith(other)).isTrue();
	}
	
	@Test
	void sameActionAreNotCompatible() throws Exception {
		PushAction sut = PushAction.actions(1, Direction.UP);
		PushAction other = PushAction.actions(1, Direction.UP);
		
		Assertions.assertThat(sut.isCompatibleWith(other)).isFalse();
	}
	
	@Test
	void sameLineActionsAreNotCompatible() throws Exception {
		PushAction sut = PushAction.actions(1, Direction.UP);
		PushAction other = PushAction.actions(1, Direction.DOWN);
		
		Assertions.assertThat(sut.isCompatibleWith(other)).isFalse();
		
	}
	
}
