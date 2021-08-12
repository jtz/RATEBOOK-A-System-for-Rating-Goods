package goods.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

import goods.model.*;
import goods.service.*;

public class MainUI extends Application {
	static Integer rowIndex = 0;

	public static void main(String[] args) {
		launch(args);
	}

	private GoodsService goodsService = new GoodsService();
	// root pane
	private GridPane root = new GridPane();
	// list Goods pane
	private ScrollPane listGoodslPane = new ScrollPane();
	// Add goods grid
	private GridPane addGoodsPane = new GridPane();
	// Goods detail grid
	private ScrollPane oneGoodsPane = new ScrollPane();
	// record all display nodes
	private List<Node> displayNodes = new ArrayList<Node>();
	// alert
	private Alert alert = new Alert(AlertType.NONE);

	@Override
	public void start(Stage primaryStage) {
		try {
			// setup Menu
			Scene scene = new Scene(root, 380, 500);
			MenuBar menubar = new MenuBar();
			Menu listMenu = new Menu();
			Menu addMenu = new Menu();

			// add Menu to root Pane
			menubar.getMenus().addAll(listMenu, addMenu);
			root.add(menubar, 0, 0);
			GridPane.setHgrow(menubar, Priority.ALWAYS);

			// add Add Goods Label and event to menu
			Label addGoodsLabel = new Label("Add Goods");
			addGoodsLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent arg0) {
					addGoods();
				}
			});
			addMenu.setGraphic(addGoodsLabel);

			// add List Goods label and event to menu
			Label searchGoodsLabel = new Label("List Goods");
			searchGoodsLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent arg0) {
					listGoods();
				}
			});
			listMenu.setGraphic(searchGoodsLabel);

			// add List Grid to root Pane
			root.add(listGoodslPane, 0, 1);
			GridPane.setHgrow(listGoodslPane, Priority.ALWAYS);
			displayNodes.add(listGoodslPane);

			// add Add Goods to root Pane
			root.add(addGoodsPane, 0, 1);
			GridPane.setHgrow(addGoodsPane, Priority.ALWAYS);
			displayNodes.add(addGoodsPane);

			// add Goods detail to root Pane
			root.add(oneGoodsPane, 0, 1);
			GridPane.setHgrow(oneGoodsPane, Priority.ALWAYS);
			displayNodes.add(oneGoodsPane);

			// start RateBook as list Goods page
			listGoods();

			primaryStage.setScene(scene);
			primaryStage.setTitle("RateBook 0.1");
			primaryStage.show();

		} catch (Exception e) {
			e.printStackTrace();
			alert.setContentText("Get exception: " + e.getMessage());
			alert.setAlertType(AlertType.ERROR);
			alert.show();
		}
	}

	
	/**
	 * List all Goods from RateBook
	 */
	private void listGoods() {
		// list Goods grid
		GridPane listGrid = new GridPane();
		listGoodslPane.setContent(listGrid);
		listGoodslPane.setFitToWidth(true);

		int rowIndex = 0;
		// display all goods
		try {
			List<Goods> goodsList = goodsService.findAllGoods();
			if (goodsList != null) {
				for (Goods goods : goodsList) {
					// display name
					Label label11 = new Label("Goods: " + goods.getName());
					listGrid.add(label11, 0, rowIndex);
					GridPane.setHgrow(label11, Priority.ALWAYS);
					Button button = new Button("Show Detail");
					listGrid.add(button, 1, rowIndex++);
					// display barcode
					Label label12 = new Label("Barcode: " + goods.getBarcode());
					listGrid.add(label12, 0, rowIndex++);
					// display rating
					Label label13 = new Label("Rating: " + String.format("%.2f", goods.getRating()));
					listGrid.add(label13, 0, rowIndex++);
					// set separator
					Separator hSeparatorOne = new Separator(Orientation.HORIZONTAL);
//					 hSeparatorOne.setStyle("-fx-background-color: red;");
					listGrid.add(hSeparatorOne, 0, rowIndex++);
					GridPane.setHgrow(hSeparatorOne, Priority.ALWAYS);
					// set button b1 click
					button.setOnMouseClicked(new EventHandler<MouseEvent>() {
						@Override
						public void handle(MouseEvent arg0) {
							oneGoods(goods.getBarcode());
						}
					});
				}
			}
		} catch (GoodsException e) {
			e.printStackTrace();
			alert.setContentText("Get exception when list Goods: " + e.getMessage());
			alert.setAlertType(AlertType.ERROR);
			alert.show();
		}
		disPlay(listGoodslPane);
	}

	
	/**
	 * Add one Goods to RateBook
	 */
	private void addGoods() {
		addGoodsPane.getChildren().clear();

		Label barcode = new Label("Goods Barcode: ");
		Label name = new Label("Goods Name: ");
		Label brand = new Label("Goods Brand: ");
		Label type = new Label("Goods Category: ");
		TextField barcodeText = new TextField();
		TextField nameText = new TextField();
		TextField brandText = new TextField();
		RadioButton radioButton1 = new RadioButton("Food");
		RadioButton radioButton2 = new RadioButton("Child");
		ToggleGroup radioGroup = new ToggleGroup();
		radioButton1.setToggleGroup(radioGroup);
		radioButton2.setToggleGroup(radioGroup);
		HBox hbox = new HBox(type, radioButton1, radioButton2);

		Button button = new Button("Submit");
		addGoodsPane.addRow(0, barcode, barcodeText);
		addGoodsPane.addRow(1, name, nameText);
		addGoodsPane.addRow(2, brand, brandText);
		addGoodsPane.addRow(3, hbox);
		addGoodsPane.addRow(4, button);

		button.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent arg0) {
				String barcode = barcodeText.getText();
				if (barcode == null || barcode.isEmpty()) {
					alert.setContentText("Barcode can't be null");
					alert.setAlertType(AlertType.WARNING);
					alert.show();
					return;
				}
				
				String name = nameText.getText();
				if (name == null || name.isEmpty()) {
					alert.setContentText("Name can't be null");
					alert.setAlertType(AlertType.WARNING);
					alert.show();
					return;
				}
				
				String brand = brandText.getText();
				if (brand == null || brand.isEmpty()) {
					alert.setContentText("Brand can't be null");
					alert.setAlertType(AlertType.WARNING);
					alert.show();
					return;
				}
				
				RadioButton selectedButton = (RadioButton)radioGroup.getSelectedToggle();
				if (selectedButton == null) {
					alert.setContentText("Category can't be null");
					alert.setAlertType(AlertType.WARNING);
					alert.show();
					return;
				}
				
				Goods goods = null;
				if ("Food".equals(selectedButton.getText())) {
					goods = new FoodGoods(barcode, name, brand);
				} else {
					goods = new ChildGoods(barcode, name, brand);
				}
				
				try {
					boolean result = goodsService.addGoods(goods);
					if (result) {
						alert.setContentText("Add goods successfully!");
						alert.setAlertType(AlertType.INFORMATION);
						alert.show();
						listGoods();
					} else {
						alert.setContentText("Goods has already existed in RateBook!");
						alert.setAlertType(AlertType.WARNING);
						alert.show();
					}
				} catch (GoodsException e) {
					alert.setContentText("Get exception when add Goods: " + e.getMessage());
					alert.setAlertType(AlertType.ERROR);
					alert.show();
				}
			}
		});
		disPlay(addGoodsPane);
	}

	
	/**
	 * display One Goods info
	 */
	private void oneGoods(String barcode) {
		Goods goods = null;
		try {
			goods = goodsService.findGoodsByBarcode(barcode);
		} catch (GoodsException e) {
			alert.setContentText("Get exception: " + e.getMessage());
			alert.setAlertType(AlertType.ERROR);
			alert.show();
			return;
		}

		// not find goods by barcode
		if (goods == null) {
			alert.setContentText("Not find goods: " + barcode);
			alert.setAlertType(AlertType.WARNING);
			alert.show();
			return;
		}

		// Goods detail grid
		GridPane goodsGrid = new GridPane();
		oneGoodsPane.setContent(goodsGrid);
		oneGoodsPane.setFitToWidth(true);

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
		int index = 0;

		Label label11 = new Label("Goods: " + goods.getName());
		goodsGrid.addRow(index++, label11);
		Label label12 = new Label("Goods Category: " + goods.getGoodsCategory());
		goodsGrid.add(label12, 0, index++);
		Label label13 = new Label("Barcode: " + goods.getBarcode());
		goodsGrid.add(label13, 0, index++);
		Label label14 = new Label("Rating: " + String.format("%.2f", goods.getRating()));
		goodsGrid.add(label14, 0, index++);
		Label label15 = null;
		if (goods.getPriceRangeLow() > 0) {
			label15 = new Label("Price from: " + String.format("%.2f", goods.getPriceRangeLow()) + " to: "
					+ String.format("%.2f", goods.getPriceRangeHigh()));
		} else {
			label15 = new Label("Price from: no price yet");			
		}
		 
		goodsGrid.add(label15, 0, index++);
		Separator hSeparatorOne = new Separator(Orientation.HORIZONTAL);
		hSeparatorOne.setStyle("-fx-background-color: red;");
		goodsGrid.addRow(index++, hSeparatorOne);
		GridPane.setHgrow(hSeparatorOne, Priority.ALWAYS);

		for (Review review : goods.getReviews()) {
			Label label21 = new Label("Client: " + review.getClient());
			goodsGrid.add(label21, 0, index);
			GridPane.setHgrow(label11, Priority.ALWAYS);
			Label label22 = new Label("Time: " + simpleDateFormat.format(review.getCreateTime()));
			goodsGrid.add(label22, 1, index++);
			Label label23 = new Label("Review: " + review.getReview());
			goodsGrid.add(label23, 0, index++);

			Separator hSeparator = new Separator(Orientation.HORIZONTAL);
			goodsGrid.add(hSeparator, 0, index++);
			GridPane.setHgrow(hSeparator, Priority.ALWAYS);
		}

		disPlay(oneGoodsPane);
	}
	
	
	/**
	 * Setting to show which node in UI
	 * @param node
	 */
	private void disPlay(Node node) {
		for (Node displayNode : displayNodes) {
			if (displayNode.equals(node)) {
				displayNode.setVisible(true);
			} else {
				displayNode.setVisible(false);
			}
		}
	}
}
