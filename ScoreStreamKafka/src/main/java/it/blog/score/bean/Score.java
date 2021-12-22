package it.blog.score.bean;

public class Score {
	String title;
	double sum;
	double count;
	double value;

	public Score() {
	}

	public Score(String title, double count, double sum, double value) {
		this.title = title;
		this.count = count;
		this.sum = sum;
		this.value = value;
	}

	public double getSum() {
		return sum;
	}

	public void setSum(double sum) {
		this.sum = sum;
	}

	public double getCount() {
		return count;
	}

	public void setCount(double count) {
		this.count = count;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String toString() {
		return "Score [title=" + title + ", sum=" + sum + ", count=" + count + ", value=" + value + "]";
	}
}