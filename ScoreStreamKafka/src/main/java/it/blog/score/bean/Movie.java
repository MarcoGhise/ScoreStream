package it.blog.score.bean;

public class Movie {

	String title;
	Integer stars;
	

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getStars() {
		return stars;
	}

	public void setStars(Integer stars) {
		this.stars = stars;
	}

	@Override
	public String toString() {
		return "Movie [title=" + title + ", stars=" + stars + "]";
	}
}