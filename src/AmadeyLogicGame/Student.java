package AmadeyLogicGame;

public class Student {

	private int practiseCompleteCount;
	private int testScore;
	private int testAttemptCount;
	private double TestTime;
	
	public Student(){
		
	}
	
	public void AdjustPractiseCount() { practiseCompleteCount += 1;}
	
	public void setScore(int Score) {testScore = Score;}
	
	public void AdjustTestAttemptCount() {testAttemptCount += 1;}
	
	public void setTestTime(double time) {TestTime = time;}
	
}
