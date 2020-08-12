package me.cheesyfreezy.hexrpg.main;

public class Plugin {
	private static Main m;
	
	public static void setMain(Main main) {
		m = main;
	}
	
	public static Main getMain() {
		return m;
	}
}