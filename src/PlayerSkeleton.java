import java.util.List;

public class PlayerSkeleton {

	//implement this function to have a working system
	public int pickMove(State s, int[][] legalMoves) {
		
		return 0;
	}
	
	public static void main(String[] args) {
		State s = new State();
		new TFrame(s);
		PlayerSkeleton p = new PlayerSkeleton();
		while(!s.hasLost()) {
			s.makeMove(p.pickMove(s,s.legalMoves()));
			s.draw();
			s.drawNext(0,0);
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("You have completed "+s.getRowsCleared()+" rows.");
	}
	
}

class Heuristics{
	private int lineCleared;
	private List<Hole> holes;
	private int sumOfAdjacentColumnHeightDifference;
	private int landingHeight;
	private int totalHeight;
	private int rangeOfHeight;
	
	public Heuristics(State thisState, State nextState) {
		lineCleared = nextState.getRowsCleared();
	}
	
	public int getLineCleared() {
		return lineCleared;
	}
	
	public void setLineCleared(int lineCleared) {
		this.lineCleared = lineCleared;
	}
	
	public List<Hole> getHoles() {
		return holes;
	}
	
	public void setHoles(List<Hole> holes) {
		this.holes = holes;
	}

	public int getSumOfAdjacentColumnHeightDifference() {
		return sumOfAdjacentColumnHeightDifference;
	}

	public void setSumOfAdjacentColumnHeightDifference(int sumOfAdjacentColumnHeightDifference) {
		this.sumOfAdjacentColumnHeightDifference = sumOfAdjacentColumnHeightDifference;
	}

	public int getLandingHeight() {
		return landingHeight;
	}

	public void setLandingHeight(int landingHeight) {
		this.landingHeight = landingHeight;
	}

	public int getTotalHeight() {
		return totalHeight;
	}

	public void setTotalHeight(int totalHeight) {
		this.totalHeight = totalHeight;
	}

	public int getRangeOfHeight() {
		return rangeOfHeight;
	}

	public void setRangeOfHeight(int rangeOfHeight) {
		this.rangeOfHeight = rangeOfHeight;
	}
	
}

class Hole{
	private int height;
	private int weight;
}