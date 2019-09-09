package SnakeBot;

public enum States { //determines how various components work
	
	DEFAULT{
		@Override
		public void enter() {}
	},
	
	ONE_NET{
		@Override
		public void enter() {
			states[HUMAN] = false;
			states[SINGLE] = true;
		}
	},
	
	EVOLVING{
		@Override
		public void enter() {
			states[HUMAN] = false;
			states[SINGLE] = false;
		}
	},
	
	HUMAN_PLAYING{
		@Override
		public void enter() {
			states[HUMAN] = true;
			states[SINGLE] = true;
		}
	};
	
	private static final int HUMAN = 0;
	private static final int SINGLE = 1;
	private static final boolean[] states = new boolean[2];
	public boolean humanPlaying() {
		return states[HUMAN];
	}
	
	public boolean singleRun() {
		return states[SINGLE];
	}
	abstract void enter();
}
