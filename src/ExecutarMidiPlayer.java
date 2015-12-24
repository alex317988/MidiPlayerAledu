
public class ExecutarMidiPlayer {
	public static void main(String[] args) {
		ViewMidiPlayer midiplayer = new ViewMidiPlayer();
		midiplayer.executar();
		Thread thread = new Thread(midiplayer);
		thread.start();
	}
}
