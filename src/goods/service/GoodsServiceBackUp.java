package goods.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import goods.model.ChildGoods;
import goods.model.Goods;
import goods.model.GoodsException;
import goods.model.Review;
import goods.util.Pair;

/**
 * This class is the old version of GoodsService, which is not used in release 6
 */
public class GoodsServiceBackUp {
	
	private static final String GOODSFILE = "goods.txt";
	private static final String GOODSREVIEWSFILE = "goodsreviews.txt";
	
	/**
	 * Users can add goods(create new goods profile) to the system
	 * PRECON: The added goods do not exist in the system
	 * POSTCON: Return the goods instance with new info 
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
		String goodsString = goods.goodsToTxt();
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(GOODSFILE, true)); 
			bw.write(goodsString);  
			bw.newLine();
			bw.flush();
		} 
		catch (IOException e) {
			e.printStackTrace();
			throw new GoodsException("Could not store goods. " + e.getMessage());
		} 
		finally { // (close file)
			if (bw != null) 
				try {
					bw.close();
				} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		return true;
	}
	
	/**
	 * Ratebook Administrator can remove Goods
	 * PRECON: The updated goods exist in the system
	 * POSTCON: Remove Goods info from txt file
	 * @param barcode
	 * @throws GoodsException 
	 */
	public void removeGoods(String barcode) throws GoodsException {
		Goods checkGoods = findGoodsByBarcode(barcode);
		if (checkGoods == null) {
			// if Goods do not exit in system, exit
			return;
		}
		
		List<String> goodsList = new ArrayList<String>();
		try (Scanner goodsScanner = new Scanner(new File(GOODSFILE))) {
			while (goodsScanner.hasNextLine()) {
				String oneLine = goodsScanner.nextLine();
				if (oneLine != null && !oneLine.isEmpty()) {
					// If find this barcode in txt file, do not add into goodslist that will write later
					if (!oneLine.startsWith(barcode + "|")) {
						goodsList.add(oneLine);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new GoodsException("List goods got exception. " + e.getMessage());
		}
		
		
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(GOODSFILE, false))) {
			for (String oneLine : goodsList) {
				bw.write(oneLine);  
				bw.newLine();
			}
			bw.flush();
		} 
		catch (IOException e) {
			e.printStackTrace();
			throw new GoodsException("Could not store goods in file. " + e.getMessage());
		}
	}
	
	/**
	 * User can update Goods info (like name, brand or ageRange ...)
	 * PRECON: The updated goods exist in the system
	 * POSTCON: Call addGoods() and return the goods instance with new info
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
		
		// first remove
		removeGoods(goods.getBarcode());
		// then add and return
		return addGoods(goods);
	}
	
	/**
	 * Find goods by barcode in txt file
	 * PRECON: The txt file that store goods info already exists
	 * POSTCON: If this Goods exist (by barcode), return the Goods, if not return null
	 * @param barcode
	 * @return goods or null
	 * @throws GoodsException 
	 */
	public Goods findGoodsByBarcode(String barcode) throws GoodsException {
		try (Scanner goodsScanner = new Scanner(new File(GOODSFILE))) {
			while (goodsScanner.hasNextLine()) {
				String oneLine = goodsScanner.nextLine();
				if (oneLine != null && !oneLine.isEmpty()) {
					// find this barcode in txt file
					if (oneLine.startsWith(barcode + "|")) {
						Goods goods = Goods.txtToGoods(oneLine);
						findGoodsReviews(goods);
						return goods;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new GoodsException("Find goods by barcode got exception. " + e.getMessage());
		}
		return null;
	}
	
	/**
	 * Find all goods in txt file
	 * PRECON: The txt file that store goods info already exists
	 * POSTCON: Return a list of all goods with info
	 * @param barcode
	 * @return a list of all goods
	 * @throws GoodsException 
	 */
	public List<Goods> findAllGoods() throws GoodsException {
		try (Scanner goodsScanner = new Scanner(new File(GOODSFILE))) {
			List<Goods> list = new ArrayList<Goods>();
			while (goodsScanner.hasNextLine()) {
				String oneLine = goodsScanner.nextLine();
				if (oneLine != null && !oneLine.isEmpty()) {
					Goods goods = Goods.txtToGoods(oneLine);
					findGoodsReviews(goods);
					list.add(goods);
				}
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			throw new GoodsException("Find all goods got exception. " + e.getMessage());
		}
	}
	
	/**
	 * Find all childGoods filtered by SafeAge in txt file
	 * PRECON: The txt file that store goods info already exists
	 * POSTCON: Return a list of all eligible child goods
	 * @param childAge
	 * @return a list of all eligible child goods
	 * @throws GoodsException
	 */
	public List<ChildGoods> findChildGoodsBySafeAge(Integer childAge) throws GoodsException {
		try (Stream<String> stream = Files.lines(Paths.get(GOODSFILE))) {
			List<ChildGoods> list = stream
				.filter(line -> line != null && !line.isEmpty())
				.map(t -> {
					try {
						return Goods.txtToGoods(t);
					} catch (GoodsException e) {
						return null;
					}
				})
				.filter(goods -> goods!= null && goods instanceof ChildGoods)
				.map(goods -> (ChildGoods)goods)
				.filter(childGoods -> childGoods.safeAge(childAge))
				.collect(Collectors.toList());
			// list.forEach(g -> findGoodsReviews(g));
			return list;
		} catch (IOException e1) {
			e1.printStackTrace();
			throw new GoodsException("Find goods got exception. " + e1.getMessage());
		}
	}
	
	/**
	 * Each user can add 1 review for each Goods. Currently not support update review.
	 * @param goods
	 * @param client
	 * @param review
	 * @throws GoodsException 
	 */
	public void addGoodsReviews(Goods goods, String client, String review) throws GoodsException {
		/*
		Path filePath = Paths.get(GOODSREVIEWSFILE);
		try (Stream<String> lines = Files.lines(filePath)){
			boolean findGoods = lines
				.filter(line -> line != null && !line.isEmpty())
				.anyMatch(line -> line.startsWith(goods.getBarcode() + "|" + client + "|"));
			if(findGoods) {
				// find this client add review before for the goods, do nothing
				return;
			}
		} catch(IOException e) {
			e.printStackTrace();
			throw new GoodsException("Could not find goods. " + e.getMessage());
		}
		
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(GOODSREVIEWSFILE, true))) {
			Review oneReview = new Review(goods.getBarcode(), client, new Date(), review);
			bw.write(oneReview.reviewToTxt());  
			bw.newLine();
			bw.flush();
		} 
		catch (IOException e) {
			e.printStackTrace();
			throw new GoodsException("Could not store goods. " + e.getMessage());
		}
	}
	*/
	
		try
	    {
	        try (ObjectInputStream infile = new ObjectInputStream
	       		 (new FileInputStream(GOODSREVIEWSFILE));)       
	        {
		         try (ObjectOutputStream outfile = new ObjectOutputStream
		         		(new FileOutputStream(GOODSREVIEWSFILE));)  
		         {	        	 
		        	 while (true)
		        	 {
		        		 Review reviewRecord = (Review)(infile.readObject());
		        		 addOneReview(reviewRecord);
	                     outfile.writeObject(reviewRecord);  
		        	 }	
		         }
	        }
	    }
	    catch (EOFException e)
	    {
	        System.out.println("EOF reached in " + GOODSREVIEWSFILE);    
	    }
	
	    catch (FileNotFoundException e)
	    {
	        e.printStackTrace(); 
	        throw new GoodsException("Could not find files. " + e.getMessage());
	    }
	
	    catch (IOException e)
	    {
	        e.printStackTrace(); 
	        throw new GoodsException("Could not store reviews. " + e.getMessage());
	    }
	
	    finally
	    {
	   	 System.out.println("Successfully add review to the file " + 
	   	 			GOODSREVIEWSFILE);
	    }
	}

	
	
	
	
	
	/**
	 * Find all reviews of the goods
	 * @param goods
	 * @throws GoodsException
	 */
	private void findGoodsReviews(Goods goods) {
		try (Scanner goodsScanner = new Scanner(new File(GOODSREVIEWSFILE))) {
			while (goodsScanner.hasNextLine()) {
				String oneLine = goodsScanner.nextLine();
				if (oneLine != null && !oneLine.isEmpty()) {
					// find this review 
					if (oneLine.startsWith(goods.getBarcode() + "|")) {
						Review review = Review.txtToReview(oneLine);
						goods.addReview(review);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/** 
	 * Find all reviews and corresponding goods of specific user
	 * PRECON: The client already exists
	 * POSTCON: Return a list of all pairs of review and goods name for specific user
	 * @return List<Pair(name, review)>
	 */
	public List<Pair<String, String>> findClientReviews(String client) throws GoodsException {

		// map(key = barcode, value = Pair(name, review))
		Map<String, Pair<String, String>> map = new HashMap<>();
		
		// find review and corresponding barcode by client name in goodsreviews.txt
		try (Scanner goodsScanner = new Scanner(new File(GOODSREVIEWSFILE))) {		
			while (goodsScanner.hasNextLine()) {
				String oneLine = goodsScanner.nextLine();
				if (oneLine != null && !oneLine.isEmpty()) {
					Review reviewprofile = Review.txtToReview(oneLine);
					// find this client
					if (reviewprofile.getClient().equals(client)) {
						// set this client's review as first item of pair
						Pair<String, String> pair = new Pair<String, String>();
						pair.setFirst(reviewprofile.getReview());
						// get corresponding barcode of reviewed goods
						String barcode = reviewprofile.getBarcode();
						map.put(barcode, pair);
					}
				}
			}		
		} catch (Exception e) {
			e.printStackTrace();
			throw new GoodsException("Find review by client got exception. " + e.getMessage());
		}
		
		// find goods name by barcode in map in goods.txt
		try (Scanner goodsScanner = new Scanner(new File(GOODSFILE))) {		
			while (goodsScanner.hasNextLine()) {
				String oneLine = goodsScanner.nextLine();
				if (oneLine != null && !oneLine.isEmpty()) {
					Goods goodsprofile = Goods.txtToGoods(oneLine);
					// find this barcode in map
					if(map.containsKey(goodsprofile.getBarcode())) {
						// set this goods name as second item of pair
						Pair<String, String> pair = map.get(goodsprofile.getBarcode());
						pair.setSecond(goodsprofile.getName());
						map.put(goodsprofile.getBarcode(), pair);
					}
				}
			}		
		} catch (Exception e) {
			e.printStackTrace();
			throw new GoodsException("Find goods name by barcode got exception. " + e.getMessage());
		}
		
		// a list of all pairs of review and goods name for specific user
		return new ArrayList<Pair<String, String>>(map.values());
	}
		
}

    

