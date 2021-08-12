package goods.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import goods.model.ChildGoods;
import goods.model.FoodGoods;
import goods.model.Goods;
import goods.model.Goods.GoodsCategory;
import goods.util.Pair;
import goods.model.Review;

import static goods.db.CreateDB.url;

public class GoodsData {
	
	/**
	 * Add goods info to database
	 * @param goods
	 */
    public void addGoods(Goods goods) throws SQLException {
    	String sql = "INSERT INTO goods( "
    			+ "barcode,"
    			+ "name,"
    			+ "brand,"
    			+ "rateCount,"
    			+ "rating,"
    			+ "priceRangeLow,"
    			+ "priceRangeHigh,"
    			+ "goodsCategory,"
    			+ "ageRangeLow,"
    			+ "ageRangeHigh"
    			+ " ) VALUES(?,?,?,?,?,?,?,?,?,?)";
    	
        try (Connection conn = DriverManager.getConnection(url);
        		PreparedStatement pstmt = conn.prepareStatement(sql)){
        	pstmt.setString(1, goods.getBarcode());
        	pstmt.setString(2, goods.getName());
        	pstmt.setString(3, goods.getBrand());
        	pstmt.setFloat(4, goods.getRating());
        	pstmt.setInt(5, goods.getRateCount());
        	pstmt.setFloat(6, goods.getPriceRangeHigh());
        	pstmt.setFloat(7, goods.getPriceRangeLow());
        	pstmt.setString(8, goods.getGoodsCategory().name());
        	
        	if (goods instanceof ChildGoods) {
        		ChildGoods childGoods = (ChildGoods)goods;
        		if (childGoods.getAgeRangeLow() != null) {
        			pstmt.setInt(9, childGoods.getAgeRangeLow());
        		} else {
        			pstmt.setNull(9, Types.INTEGER);
        		}
        		if (childGoods.getAgeRangeHigh() != null) {
        			pstmt.setInt(10, childGoods.getAgeRangeHigh());
        		} else {
        			pstmt.setNull(10, Types.INTEGER);
        		}
        	} else {
        		pstmt.setNull(9, Types.INTEGER);
        		pstmt.setNull(10, Types.INTEGER);
        	}
        	pstmt.executeUpdate();
        	
        	System.out.println("Successfully add goods " + goods.getBarcode() + " into DB.");
        } catch (SQLException e) {
            throw e;
        }
    }
    
    /**
	 * Find goods by barcode in database
	 * @param barcode
	 */
	public Goods findGoodsByBarcode(String barcode) throws Exception {
    	String sql = "SELECT barcode,"
    			+ "name,"
    			+ "brand,"
    			+ "rateCount,"
    			+ "rating,"
    			+ "priceRangeLow,"
    			+ "priceRangeHigh,"
    			+ "goodsCategory,"
    			+ "ageRangeLow,"
    			+ "ageRangeHigh"
    			+ " FROM goods WHERE barcode = ?";
    	
        try (Connection conn = DriverManager.getConnection(url);
        		PreparedStatement pstmt = conn.prepareStatement(sql)){
        	pstmt.setString(1, barcode);
        	ResultSet rs  = pstmt.executeQuery();
            while (rs.next()) {
            	Goods goods = null;
                String name = rs.getString("name");
                String brand = rs.getString("brand");
                GoodsCategory goodsCategory = GoodsCategory.valueOf(rs.getString("goodsCategory"));
                if (GoodsCategory.CHILD.equals(goodsCategory)) {
                	goods = new ChildGoods(barcode, name, brand);
                	int ageRangeLow = rs.getInt("ageRangeLow");
                	if (!rs.wasNull()) {
                		((ChildGoods)goods).setAgeRangeLow(ageRangeLow);
                	}
                	int ageRangeHigh = rs.getInt("ageRangeHigh");
                	if (!rs.wasNull()) {
                		((ChildGoods)goods).setAgeRangeHigh(ageRangeHigh);
                	}
                } else {
                	goods = new FoodGoods(barcode, name, brand);
                }
                goods.setRateCount(rs.getInt("rateCount"));
                goods.setRating(rs.getFloat("rating"));
                goods.setPriceRangeLow(rs.getFloat("priceRangeLow"));
                goods.setPriceRangeHigh(rs.getFloat("priceRangeHigh"));
                
                System.out.println("Successfully find goods " + goods.getBarcode() + " from DB.");
                // find reviews for this goods
                findGoodsReviews(goods);
                
                return goods;
            }
        } catch (Exception e) {
            throw e;
        }
		return null;
	}
	
	/**
	 * Update Goods info to database.
	 * @param goods
	 */
    public void updateGoods(Goods goods) throws SQLException {
    	String sql = "UPDATE goods SET "
    			+ "name = ? , "
    			+ "brand = ? , "
    			+ "rateCount = ? , "
    			+ "rating = ? , "
    			+ "priceRangeLow = ? , "
    			+ "priceRangeHigh = ? , "
    			+ "goodsCategory = ? , "
    			+ "ageRangeLow = ? , "
    			+ "ageRangeHigh = ? WHERE barcode = ?";
    	
        try (Connection conn = DriverManager.getConnection(url);
        		PreparedStatement pstmt = conn.prepareStatement(sql)){
        	pstmt.setString(1, goods.getName());
        	pstmt.setString(2, goods.getBrand());
        	pstmt.setInt(3, goods.getRateCount());
        	pstmt.setFloat(4, goods.getRating());
        	pstmt.setFloat(5, goods.getPriceRangeLow());
        	pstmt.setFloat(6, goods.getPriceRangeHigh());
        	pstmt.setString(7, goods.getGoodsCategory().name());
        	
        	if (goods instanceof ChildGoods) {
        		ChildGoods childGoods = (ChildGoods)goods;
        		if (childGoods.getAgeRangeLow() != null) {
        			pstmt.setInt(8, childGoods.getAgeRangeLow());
        		} else {
        			pstmt.setNull(8, Types.INTEGER);
        		}
        		if (childGoods.getAgeRangeHigh() != null) {
        			pstmt.setInt(9, childGoods.getAgeRangeHigh());
        		} else {
        			pstmt.setNull(9, Types.INTEGER);
        		}
        	} else {
        		pstmt.setNull(8, Types.INTEGER);
        		pstmt.setNull(9, Types.INTEGER);
        	}
        	pstmt.setString(10, goods.getBarcode());
        	pstmt.executeUpdate();
        	
        	System.out.println("Successfully update goods " + goods.getBarcode() + " into DB.");
        } catch (SQLException e) {
            throw e;
        }
    }
    
    /**
	 * Find all goods info in database, sort by rating score in descending order.
	 */
	public List<Goods> findAllGoods() throws Exception {
    	String sql = "SELECT barcode,"
    			+ "name,"
    			+ "brand,"
    			+ "rateCount,"
    			+ "rating,"
    			+ "priceRangeLow,"
    			+ "priceRangeHigh,"
    			+ "goodsCategory,"
    			+ "ageRangeLow,"
    			+ "ageRangeHigh"
    			+ " FROM goods"
    			+ " ORDER BY rating DESC";  	
    	
    	List<Goods> goodsList = new ArrayList<Goods>();
        try (Connection conn = DriverManager.getConnection(url);
        		PreparedStatement pstmt = conn.prepareStatement(sql)){
        	ResultSet rs  = pstmt.executeQuery();
            while (rs.next()) {
            	Goods goods = null;
            	String barcode = rs.getString("barcode");
                String name = rs.getString("name");
                String brand = rs.getString("brand");
                GoodsCategory goodsCategory = GoodsCategory.valueOf(rs.getString("goodsCategory"));
                if (GoodsCategory.CHILD.equals(goodsCategory)) {
                	goods = new ChildGoods(barcode, name, brand);
                	int ageRangeLow = rs.getInt("ageRangeLow");
                	if (!rs.wasNull()) {
                		((ChildGoods)goods).setAgeRangeLow(ageRangeLow);
                	}
                	int ageRangeHigh = rs.getInt("ageRangeHigh");
                	if (!rs.wasNull()) {
                		((ChildGoods)goods).setAgeRangeHigh(ageRangeHigh);
                	}
                } else {
                	goods = new FoodGoods(barcode, name, brand);
                }
                goods.setRateCount(rs.getInt("rateCount"));
                goods.setRating(rs.getFloat("rating"));
                goods.setPriceRangeLow(rs.getFloat("priceRangeLow"));
                goods.setPriceRangeHigh(rs.getFloat("priceRangeHigh"));
                
                // find reviews for this goods
                findGoodsReviews(goods);
                
                goodsList.add(goods);
            }
        } catch (Exception e) {
            throw e;
        }
		return goodsList;
	}
	
	/**
	 * Find all childGoods filtered by SafeAge in database
	 * @param childAge
	 */
	public List<ChildGoods> findChildGoodsBySafeAge(Integer childAge) throws Exception {
    	String sql = "SELECT barcode,"
    			+ "name,"
    			+ "brand,"
    			+ "rateCount,"
    			+ "rating,"
    			+ "priceRangeLow,"
    			+ "priceRangeHigh,"
    			+ "goodsCategory,"
    			+ "ageRangeLow,"
    			+ "ageRangeHigh"
    			+ "  FROM goods WHERE goodsCategory = 'CHILD' AND "
    			+ "  ( ageRangeLow is null OR ageRangeLow <= ? ) AND "
    			+ "  ( ageRangeHigh is null OR ageRangeHigh >= ? )";
    	
    	List<ChildGoods> goodsList = new ArrayList<ChildGoods>();
        try (Connection conn = DriverManager.getConnection(url);
        		PreparedStatement pstmt = conn.prepareStatement(sql)){
        	pstmt.setInt(1, childAge);
        	pstmt.setInt(2, childAge);
        	ResultSet rs  = pstmt.executeQuery();
            while (rs.next()) {
            	ChildGoods goods = null;
            	String barcode = rs.getString("barcode");
                String name = rs.getString("name");
                String brand = rs.getString("brand");
            	goods = new ChildGoods(barcode, name, brand);
            	int ageRangeLow = rs.getInt("ageRangeLow");
            	if (!rs.wasNull()) {
            		((ChildGoods)goods).setAgeRangeLow(ageRangeLow);
            	}
            	int ageRangeHigh = rs.getInt("ageRangeHigh");
            	if (!rs.wasNull()) {
            		((ChildGoods)goods).setAgeRangeHigh(ageRangeHigh);
            	}

                goods.setRateCount(rs.getInt("rateCount"));
                goods.setRating(rs.getFloat("rating"));
                goods.setPriceRangeLow(rs.getFloat("priceRangeLow"));
                goods.setPriceRangeHigh(rs.getFloat("priceRangeHigh"));
                
                // find reviews for this goods
                findGoodsReviews(goods);
                
                goodsList.add(goods);
            }
        } catch (Exception e) {
            throw e;
        }
		return goodsList;
	}
	
	/**
	 * Add review to database.
	 * Using replace commend
	 * If client review not exit: insert
	 * If client review exit: update
	 * @param goods
	 * @param client
	 * @param review
	 * @throws SQLException
	 */
	public void addGoodsReview(Goods goods, String client, String review) throws SQLException {
    	String sql = "REPLACE INTO review( "
    			+ "barcode,"
    			+ "client,"
    			+ "createTime,"
    			+ "review"
    			+ " ) VALUES(?,?,?,?)";
    	
        try (Connection conn = DriverManager.getConnection(url);
        		PreparedStatement pstmt = conn.prepareStatement(sql)){
        	pstmt.setString(1, goods.getBarcode());
        	pstmt.setString(2, client);
        	pstmt.setString(3, Review.dateFormat.format(new Date()));
        	pstmt.setString(4, review);
        	pstmt.executeUpdate();
        	
        	System.out.println("Successfully add/update goods " + goods.getBarcode() + " review into DB.");
        } catch (SQLException e) {
            throw e;
        }
	}
	
	/** 
	 * Find all reviews and the corresponding goods name of specific user in database.
	 * @param client
	 * @return List<Pair<review, name>
	 * @throws Exception
	 */
	public List<Pair<String, String>> findClientReviews(String client) throws SQLException{
    	String sql = "select gd.name, rw.review from goods gd join review rw on gd.barcode = rw.barcode and rw.client = ?";
    	
    	List<Pair<String, String>> list = new ArrayList<Pair<String, String>>();
        try (Connection conn = DriverManager.getConnection(url);
        		PreparedStatement pstmt = conn.prepareStatement(sql)){
        	pstmt.setString(1, client);
        	ResultSet rs  = pstmt.executeQuery();
            while (rs.next()) {
            	Pair<String, String> pair = new Pair<String, String>();
            	pair.setFirst(rs.getString("review"));
            	pair.setSecond(rs.getString("name"));
            	list.add(pair);
            }
        } catch (SQLException e) {
            throw e;
        }
        return list;
	}
	
	
	/**
	 * Find review of specific goods in database.
	 * @param goods
	 * @throws Exception
	 */
	private void findGoodsReviews(Goods goods) throws Exception {
    	String sql = "SELECT "
    			+ "barcode,"
    			+ "client,"
    			+ "createTime,"
    			+ "review"
    			+ " FROM review WHERE barcode = ?";
    	
        try (Connection conn = DriverManager.getConnection(url);
        		PreparedStatement pstmt = conn.prepareStatement(sql)){
        	pstmt.setString(1, goods.getBarcode());
        	ResultSet rs  = pstmt.executeQuery();
            while (rs.next()) {
            	Review review = new Review();
            	review.setBarcode(rs.getString("barcode"));
                review.setClient(rs.getString("client"));
                review.setCreateTime(Review.dateFormat.parse(rs.getString("createTime")));
                review.setReview(rs.getString("review"));
                goods.addReview(review);
            }
            
            if (!goods.getReviews().isEmpty()) {
            	System.out.println("Successfully get reviews for " + goods.getBarcode());
            }
        } catch (SQLException | ParseException e) {
            throw e;
        }
	}
	
	/**
	 * Find the total numbers of clients and reviews.
     * @return Pair(client count, review count)
	 * @throws SQLException
	 */
	public Pair<Integer, Integer> findClientAndReviewCounts() throws SQLException{
		// get client count
    	String sql1 = "select count(*) as ct from (select client from review group by client)";
    	// get review count
    	String sql2 = "select count(*) as ct from review";
    	
    	Pair<Integer, Integer> pair = new Pair<Integer, Integer>();
        try (Connection conn = DriverManager.getConnection(url);
        		PreparedStatement pstmt1 = conn.prepareStatement(sql1);
        		PreparedStatement pstmt2 = conn.prepareStatement(sql2)){
        	ResultSet rs1  = pstmt1.executeQuery();
            while (rs1.next()) {
            	pair.setFirst(rs1.getInt("ct"));
            }
            
        	ResultSet rs2  = pstmt2.executeQuery();
            while (rs2.next()) {
            	pair.setSecond(rs2.getInt("ct"));
            }
        } catch (SQLException e) {
            throw e;
        }
        return pair;
	}
	
}
