package goods.test;

import goods.model.ChildGoods;
import goods.model.FoodGoods;
import goods.model.Goods;
import goods.model.GoodsException;
import goods.service.GoodsService;
import goods.util.Pair;
import java.util.List;

public class GoodsNoJUnitTest {

	/**
	 * Test the following functions of Ratebook
	 */
	public static void main(String[] args) {
		GoodsService goodsService = new GoodsService();
		// 1st, user can add goods
		addGoods(goodsService);
        
		// 2nd, user  can search goods by UPC
		searchByUPC(goodsService, "11987654"); // goods already exist in the system
		searchByUPC(goodsService, "11987655"); // goods do not exist in the system
						
		// 3rd, user can search for goods by category and conditions 	
		searchChildGoodsWithCondition(goodsService, 1); // all child goods used for at least age 1	
		searchChildGoodsWithCondition(goodsService, 5); // all child goods used for at least age 5	
		
		// 4th, user can add ratings, reviews and purchase prices
		addRatingAndReviewAndPrice(goodsService);
		
		// 5th, user can view all goods information
		displayAllGoods(goodsService);
		
		// 6th, administrator can view all reviews of a user
		searchReviewsByClient(goodsService, "client A");
		searchReviewsByClient(goodsService, "client D");
		
	}
	
	/**
	 * Add goods(create new goods profiles) to the system database
	 * PRE: Goods class and its inherited classes have been created
	 * POST: New instances of Goods are created and stored in database
	 * @param goodsService
	 */
	static void addGoods(GoodsService goodsService) {
		
		System.out.println("\n********** 1st start adding Goods **********");
		
		ChildGoods goods1 = new ChildGoods("11987654", "Maxi Cosi Mico Max 30 Infant Car Seat", "Maxi Cosi");
		ChildGoods goods2 = new ChildGoods("11987677", "Chicco KeyFit 30 Infant Car Seat", "Chicco");
		Goods goods3 = new FoodGoods("12788866", "Cheerios Whole Grain Oats Cereal", "Cheerios");

		try {
			goodsService.addGoods(goods1);
			goodsService.addGoods(goods2);
			goodsService.addGoods(goods3);
		} catch (GoodsException e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		// setup safe age of Child Goods 
		goods1.setAgeRangeLow(1);
		goods1.setAgeRangeHigh(4);
		goods2.setAgeRangeLow(6);
		try {
			goodsService.updateGoods(goods1);
			goodsService.updateGoods(goods2);
		} catch (GoodsException e) {
			e.printStackTrace();
			System.exit(0);
		}

		System.out.println("New Goods are added to the system successfully!");	
	}
	

	/**
	 * Search for goods by UPC(universal product code) in the system database
	 * POST: Display goods info if exist, print massage if not exist
	 * @param barcode
	 */
	static void searchByUPC(GoodsService goodsService, String barcode) {
		
		System.out.println("\n********** 2nd start searching by UPC **********");
		
		try {
			Goods good = goodsService.findGoodsByBarcode(barcode);
			if (good != null) {
				// polymorphism
				good.display();
			} else {
				System.out.println("Sorry, the goods you are searching for do not exist.");
			}
		} catch (GoodsException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}	
	
	
	/**
	 * Search for all child goods used for at least age 1
	 * PRE: All categories of goods are appeared in GoodsCategory class
	 * POST: Display UPC and name if exist, print massage if not exist
	 * @param age
	 */
	static void searchChildGoodsWithCondition(GoodsService goodsService, Integer age) {
		
		System.out.println("\n********** 3rd start searching for child goods used for age: " + age + " **********");
		
		try {
			List<ChildGoods> list = goodsService.findChildGoodsBySafeAge(age);
			if (list != null && !list.isEmpty()) {
				for (ChildGoods childGoods : list) {
					System.out.println(childGoods.getBarcode() + ", " + childGoods.getName() + 
		                    " can be used for child with age >= " + 
		                    childGoods.getAgeRangeLow()); //downcasting
				}
	
			} else {
				System.out.println("Sorry, the goods you are searching for do not exist.");
			}
		} catch (GoodsException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	
	/**
	 * Add ratings, reviews and purchase prices of goods to the system database
	 * PRE: Goods already exist in the database
	 * POST: Goods profile are updated
	 * @param goodsService
	 */
	static void addRatingAndReviewAndPrice(GoodsService goodsService) {
		
		System.out.println("\n********** 4th start adding ratings, reviews and purchase prices **********");
		try {
			// for goods1, client A rate, review and price
			Goods goods1 = goodsService.findGoodsByBarcode("11987654");
			goodsService.addGoodsReviews(goods1, "client A", "It works as advertised.");
			goods1.addRating(3.1f);
			goods1.addPrice(200);
			goodsService.updateGoods(goods1);
			
			// for goods1, client B rate, review and price
			goods1 = goodsService.findGoodsByBarcode("11987654");
			goodsService.addGoodsReviews(goods1, "client B", "Bought this for a gift for my daughter to use for our new grandson, she is very happy with it!");
			goods1.addRating(5f);
			goods1.addPrice(210);
			goodsService.updateGoods(goods1);
			
			/// for goods1, client C rate, review and price
			goods1 = goodsService.findGoodsByBarcode("11987654");
			goodsService.addGoodsReviews(goods1, "client C", "Expensive and uncomfortable.");
			goods1.addRating(1f);
			goods1.addPrice(230);
			goodsService.updateGoods(goods1);
	
			// for goods2, client D rate, review and price
			Goods goods2 = goodsService.findGoodsByBarcode("11987677");
			goodsService.addGoodsReviews(goods2, "client D", "Too small for my kids");
			goods2.addRating(2.7f);
			goods2.addPrice(300);
			goodsService.updateGoods(goods2);			
			
			// for goods2, client E rate and price, no review
			goods2 = goodsService.findGoodsByBarcode("11987677");
			goods2.addRating(4.5f);
			goods2.addPrice(330);
			goodsService.updateGoods(goods2);
	
			// for goods3, client F rate, review and price
			Goods goods3 = goodsService.findGoodsByBarcode("12788866");
			goodsService.addGoodsReviews(goods3, "client F", "Great breakfast choice for those who have to watch their sugar or have a sensitive stomach.");
			goods3.addRating(3.0f);
			goods3.addPrice(2.5f);
			goodsService.updateGoods(goods3);	
			
			// for goods3, client A rate review and price
			goods3 = goodsService.findGoodsByBarcode("12788866");
			goodsService.addGoodsReviews(goods3, "client A", "Arrived quickly, fresh. Good price.");
			goods3.addRating(4.5f);
			goods3.addPrice(3.2f);
			goodsService.updateGoods(goods3);
			
		} catch (GoodsException e) {
			e.printStackTrace();
			System.exit(0);
		}		
		System.out.println("Your ratings, reviews and purchase prices are added to the system successfully!");
	}
	

	/**
	 * Display all the information of all goods
	 * @param goodsService
	 */
	static void displayAllGoods(GoodsService goodsService) {
		
		System.out.println("\n********** 5th start displaying all goods with comments **********");
		
		int childCount = 0;
		int foodCount = 0;
		Pair<Integer, Integer> pair = null;
		try {
			List<Goods> goodsList = goodsService.findAllGoods();
			if (goodsList != null) {
				for (Goods goods : goodsList) {
					// polymorphism
					switch (goods.getGoodsCategory()) {
					case CHILD:
						childCount++;
						break;
					case FOOD:
						foodCount++;
						break;
					}
					// polymorphism
					goods.display();
				}
			}
			
			pair = goodsService.findClientAndReviewCounts();
		} catch (GoodsException e) {
			e.printStackTrace();
			System.exit(0);
		}	
		
		System.out.println("Include " + childCount + " child goods, " + foodCount + " food goods.");
		System.out.println("Include " + pair.getFirst() + " clients, " + pair.getSecond() + " reviews.");
	}


	/** 
	 * Display all reviews and corresponding goods of specific user
	 * PRE: The client already exists in the database
	 * POST: Display all reviews and corresponding goods of specific user
	 * @param goodsService
	 * @param client
	 */
	static void searchReviewsByClient(GoodsService goodsService, String client) {
		
		System.out.println("\n********** 6th start searching reviews by client **********");
		
		try {
			List<Pair<String, String>> reviewlist = goodsService.findClientReviews(client);
			if (!reviewlist.isEmpty()) {
				System.out.println(client + ":" );
				for (Pair<String, String> pair : reviewlist) {
					System.out.println("\""+ pair.getFirst() + "\" for " + pair.getSecond());
				}
			} else {
				System.out.println("Sorry, the client you are searching for do not exist.");
			}
		} catch (GoodsException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	
}
