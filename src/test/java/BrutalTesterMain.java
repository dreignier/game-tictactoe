import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Properties;

import com.codingame.gameengine.runner.GameRunner;
import com.codingame.gameengine.runner.dto.GameResult;

public class BrutalTesterMain {
	
	public static void main(String[] args) throws Exception {
		GameRunner runner = new GameRunner();
		
		for (String arg: args) {
			runner.addAgent(arg);
		}
		
		Method initialize = GameRunner.class.getDeclaredMethod("initialize", Properties.class);
		initialize.setAccessible(true);
		initialize.invoke(runner, new Properties());
		
		Method run = GameRunner.class.getDeclaredMethod("run");
		run.setAccessible(true);
		run.invoke(runner);
		
 		Field field = GameRunner.class.getDeclaredField("gameResult");
 		field.setAccessible(true);
 		
 		GameResult result = (GameResult) field.get(runner);
	}

}
