package goods.model;

public class ChildGoods extends Goods{
	
	private Integer ageRangeLow;
	private Integer ageRangeHigh;
	
	public ChildGoods(String barcode, String name, String brand) {
		super(barcode, name, brand);
	}
	
	public void setAgeRangeLow(Integer ageRangeLow) {
		this.ageRangeLow = ageRangeLow;
	}

	public void setAgeRangeHigh(Integer ageRangeHigh) {
		this.ageRangeHigh = ageRangeHigh;
	}
	
	/**
	 * Only safe when child age in the ageRangeLow to ageRangeHigh range
	 * @param childage: the age of child who uses goods
	 * @return true if child age is in age range, false if not
	 */
	public boolean safeAge(int childage) {
		if (ageRangeLow != null && ageRangeLow > childage) {
			return false;
		}
		if (ageRangeHigh != null && ageRangeHigh < childage) {
			return false;
		}
		return true;
	}

	@Override
	public void display() {
	// Override to add age range of child goods	
		
		StringBuffer sb = new StringBuffer();
		
		if (ageRangeLow == null) {
			ageRangeLow = 0;
		}
		if (ageRangeHigh == null) {
			ageRangeHigh = 18;
		}		
		
		sb.append("Category: " + getGoodsCategory() + 
				  "\nUPC: " + barcode + 
				  "\nName: " + name + 
				  "\nBrand: " + brand + 
				  "\nAge Range: " + ageRangeLow + " - " + ageRangeHigh + 
				  "\nPrice Range: " + priceRangeLow + " - " + priceRangeHigh + 
				  "\nRate Counts: " + rateCount + 
				  "\nRating: " + String.format("%.2f", rating) + 
				  "\nReviews: \n"); 
		
		for (Review review : reviews) {
			sb.append(review.toString() + "\n");
		}
		
		System.out.println(sb.toString());
	}
	
	@Override
	public GoodsCategory getGoodsCategory() {
		return GoodsCategory.CHILD;
	}
	
	
	public Integer getAgeRangeLow() {
		return ageRangeLow;
	}

	public Integer getAgeRangeHigh() {
		return ageRangeHigh;
	}
}
