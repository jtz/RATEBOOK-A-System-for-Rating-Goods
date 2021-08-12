package goods.model;

public class FoodGoods extends Goods{

	public FoodGoods(String barcode, String name, String brand) {
		super(barcode, name, brand);
	}

	@Override
	public GoodsCategory getGoodsCategory() {
		return GoodsCategory.FOOD;
	}

}
