package goods.model;

import java.util.ArrayList;
import java.util.List;

public abstract class Goods {

	String barcode;
	String name;
	String brand;	
	Integer rateCount;
	Float rating;
	Float priceRangeLow;
	Float priceRangeHigh;
	GoodsCategory goodsCategory;
	List<Review> reviews;

	public Goods(String barcode, String name, String brand) {
		this.barcode = barcode;
		this.name = name;
		this.brand = brand;
		this.priceRangeLow = -1f;
		this.priceRangeHigh = -1f;
		this.rating = 0f;
		this.rateCount = 0;
		this.reviews = new ArrayList<Review>();
	}
	
	public void display() {
		StringBuffer sb = new StringBuffer();	
		sb.append("Category: " + getGoodsCategory() +
				  "\nUPC: " + barcode + 
				  "\nName: " + name + 
				  "\nBrand: " + brand); 
		
		if (priceRangeLow > 0 && priceRangeHigh > 0) {
			sb.append("\nPrice Range: " + priceRangeLow + " - " + priceRangeHigh);
		} else {
			sb.append("\nPrice Range: not set");
		}
		sb.append("\nRate Counts: " + rateCount + 
				  "\nRating: " + String.format("%.2f", rating) + 
				  "\nReviews: \n");
		
		for (Review review : reviews) {
			sb.append(review.toString() + "\n");
		}	
		System.out.println(sb.toString());
	};
	
	public enum GoodsCategory {
		// Easy to check how many categories, will add more later
		FOOD, CHILD
	}
	
	public abstract GoodsCategory getGoodsCategory();
	
	public String goodsToTxt() {
		// Convert Goods to String which can store in txt file
		String txt = barcode + "|" + getGoodsCategory() + "|" + name + "|" + brand + "|" + 
		             priceRangeLow + "|" + priceRangeHigh + "|" + rating + "|" + rateCount;
		if (this instanceof ChildGoods) {
			String ageRangeLow = "";
			if (((ChildGoods)this).getAgeRangeLow() != null ) {
				ageRangeLow = String.valueOf(((ChildGoods)this).getAgeRangeLow());
			}
			String ageRangeHigh = "";
			if (((ChildGoods)this).getAgeRangeHigh() != null ) {
				ageRangeHigh = String.valueOf(((ChildGoods)this).getAgeRangeHigh());
			}
			txt  = txt + "|" + ageRangeLow + "|" + ageRangeHigh;
		}		
		return txt;
	}
	
	public static Goods txtToGoods(String goodsString) throws GoodsException {
		// Convert String from txt to Goods
		Goods goods = null;
		try {
			String[] info = goodsString.split("\\|");
			if(info[1].equals(GoodsCategory.FOOD.name())) {
				// parse Food Goods
				goods = new FoodGoods(info[0], info[2], info[3]);
			} else {
				// parse Child Goods
				goods = new ChildGoods(info[0], info[2], info[3]);
				if (info.length > 8 && info[8] != null && !info[8].isEmpty()) {
					((ChildGoods)goods).setAgeRangeLow(Integer.valueOf(info[8]));
				}
				if (info.length > 9 && info[9] != null && !info[9].isEmpty()) {
					((ChildGoods)goods).setAgeRangeHigh(Integer.valueOf(info[9]));
				}
			}
			goods.priceRangeLow = Float.valueOf(info[4]);
			goods.priceRangeHigh = Float.valueOf(info[5]);
			goods.rating = Float.valueOf(info[6]);
			goods.rateCount = Integer.valueOf(info[7]);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new GoodsException("Get exception when parse txt to Goods " + e.getMessage());
		}		
		return goods;
	}
	
	public String getBarcode() {
		return barcode;
	}
	
	public String getName() {
		return name;
	}
	
	public String getBrand() {
		return brand;
	}

	public Integer getRateCount() {
		return rateCount;
	}

	public Float getRating() {
		return rating;
	}

	public Float getPriceRangeLow() {
		return priceRangeLow;
	}

	public Float getPriceRangeHigh() {
		return priceRangeHigh;
	}

	public List<Review> getReviews() {
		return reviews;
	}
	
	public void setRateCount(Integer rateCount) {
		this.rateCount = rateCount;
	}

	public void setRating(Float rating) {
		this.rating = rating;
	}

	public void setPriceRangeLow(Float priceRangeLow) {
		this.priceRangeLow = priceRangeLow;
	}

	public void setPriceRangeHigh(Float priceRangeHigh) {
		this.priceRangeHigh = priceRangeHigh;
	}

	public void setReviews(List<Review> reviews) {
		this.reviews = reviews;
	}

	public void addPrice(float price) {
		// when priceRangeLow higher than price, setting priceRangeLow
		if (this.priceRangeLow == -1f || this.priceRangeLow > price) { 
			this.priceRangeLow = price;
		}
		// when priceRangeHigh lower than price, setting priceRangeHigh
		if (this.priceRangeHigh == -1f || this.priceRangeHigh < price) {
			this.priceRangeHigh = price;
		}
	}
	
	public void addRating(float rating) {
		// only accept rating from 0 to 5
		if (rating < 0 || rating > 5) {
			return; 
		}
		this.rating = (this.rating * this.rateCount + rating) / (this.rateCount + 1);
		this.rateCount++;
	}

	public void addReview(Review review) {
		this.reviews.add(review);
	}

}
