import static org.junit.jupiter.api.Assertions.*;

import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.DisplayName;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class MyTest {

	@BeforeAll
	static void initJfxRuntime() {
		Platform.startup(() -> {});
	}
	@Test
	void GameButtonConstructor() {
		GameButton gButton = new GameButton(1, 1);

	}


}
