package it.blog.score.bean;

import java.time.Instant;

public class Movie {

	String title;
	int stars;
	long timeStamp;
	
	public Movie() {}
	
	public Movie(String title, int stars)
	{
		this(title, stars, Instant.now().toEpochMilli());
	}
	
	public Movie(String title, int stars, long timeStamp)
	{
		this.title = title;
		this.stars = stars;
		this.timeStamp = timeStamp;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getStars() {
		return stars;
	}

	public void setStars(int stars) {
		this.stars = stars;
	}
	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	@Override
	public String toString() {
		return "Movie [title=" + title + ", stars=" + stars + ", timeStamp=" + timeStamp + "]";
	}
}