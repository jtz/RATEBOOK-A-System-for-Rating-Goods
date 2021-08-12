package goods.service;

import java.sql.SQLException;
import java.util.List;

import goods.db.GoodsData;
import goods.model.ChildGoods;
import goods.model.Goods;
import goods.model.GoodsException;
import goods.util.Pair;

public class GoodsService {
	
	private GoodsData goodsData = new GoodsData();
	
	/**
	 * Users can add goods(create new goods profile) to the system database
	 * PRECON: The added goods do not exist in the system database
	 * POSTCON: Return true if add successfully, false if goods are already added 
	 * @param goods
	 * @return add Goods result
	 * @throws GoodsException
	 */
	public boolean addGoods(Goods goods) throws GoodsException {
		Goods checkGoods = findGoodsByBarcode(goods.getBarcode());
		if (checkGoods != null) {
			// already add, do not add anymore
			return false;
		}
		
		try {
			goodsData.addGoods(goods);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new GoodsException(e.getMessage());
		}
	}
	
	/**
	 * User can update Goods info (like name, brand or ageRange ...)
	 * PRECON: The updated goods exist in the system database
	 * POSTCON: Call addGoods() and return true if update successfully
	 * @param goods
	 * @return addGoods(goods) result
	 * @throws GoodsException 
	 */
	public boolean updateGoods(Goods goods) throws GoodsException {
		Goods checkGoods = findGoodsByBarcode(goods.getBarcode());
		if (checkGoods == null) {
			// if Goods do not exit in system, show error
			throw new GoodsException("Goods " + goods.getBarcode() + " not exits in RateBook, can't update!");
		}
		
		try {
			goodsData.updateGoods(goods);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new GoodsException(e.getMessage());
		}
	}
	
	/**
	 * Find goods by barcode in database
	 * PRECON: The DB that store goods info already exists
	 * POSTCON: If this Goods exist (by barcode), return the Goods, if not return null
	 * @param barcode
	 * @return goods or null
	 * @throws GoodsException 
	 */
	public Goods findGoodsByBarcode(String barcode) throws GoodsException {
		try {
			return goodsData.findGoodsByBarcode(barcode);
		} catch (Exception e) {
			e.printStackTrace();
			throw new GoodsException(e.getMessage());
		}
	}
	
	/**
	 * Find all goods in database
	 * PRECON: The database file that store goods info already exists
	 * POSTCON: Return a list of all goods with info
	 * @param barcode
	 * @return a list of all goods
	 * @throws GoodsException 
	 */
	public List<Goods> findAllGoods() throws GoodsException {
		try {
			return goodsData.findAllGoods();
		} catch (Exception e) {
			e.printStackTrace();
			throw new GoodsException(e.getMessage());
		}
	}
	
	/**
	 * Find all childGoods filtered by SafeAge in database
	 * PRECON: The database that store goods info already exists
	 * POSTCON: Return a list of all eligible child goods
	 * @param childAge
	 * @return a list of all eligible child goods
	 * @throws GoodsException
	 */
	public List<ChildGoods> findChildGoodsBySafeAge(Integer childAge) throws GoodsException {
		try {
			return goodsData.findChildGoodsBySafeAge(childAge);
		} catch (Exception e1) {
			e1.printStackTrace();
			throw new GoodsException("Find goods got exception. " + e1.getMessage());
		}
	}
	
	/**
	 * Each user can add 1 review for each Goods. If same client review exit, just update.
	 * @param goods
	 * @param client
	 * @param review
	 * @throws GoodsException 
	 */
	public void addGoodsReviews(Goods goods, String client, String review) throws GoodsException {
		try {
			goodsData.addGoodsReview(goods, client, review);
		} catch (Exception e) {
			e.printStackTrace();
			throw new GoodsException("Add goods review got exception. " + e.getMessage());
		}
	}
	
	/** 
	 * Find all reviews and corresponding goods of specific user
	 * PRECON: The specific user is already exists
	 * POSTCON: Return a list of all pairs of review and goods name for specific user
	 * @return List<Pair(name, review)>
	 */
	public List<Pair<String, String>> findClientReviews(String client) throws GoodsException {
		try {
			return goodsData.findClientReviews(client);
		} catch (Exception e1) {
			e1.printStackTrace();
			throw new GoodsException("Find goods and review got exception. " + e1.getMessage());
		}
	}
	
    /**
     * Find the total numbers of clients and reviews.
     * @return Pair(client count, review count)
     * @throws GoodsException
     */
	public Pair<Integer, Integer> findClientAndReviewCounts() throws GoodsException {
		try {
			return goodsData.findClientAndReviewCounts();
		} catch (Exception e) {
			e.printStackTrace();
			throw new GoodsException(e.getMessage());
		}
	}
		
}
