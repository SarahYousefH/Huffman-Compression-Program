package application;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.InputMismatchException;
import java.util.Scanner;

import TestLast.HuffmanFinal;
import application.testTreeVisual.Tree;
import application.testTreeVisual.TreeView;
import dataStructures.BinaryTree;
import dataStructures.TreeNode;
import javafx.application.Application;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

public class HuffmanInterface extends Application {

	private static final double ZOOM_FACTOR = 1.1;
	// global variables for fx
	private TabPane tabPane;
	private Scanner scan;

	File toCompressFile;
	File afterCompression;

	File afterDeCompression;
	File toDecompresFile;

	@Override
	public void start(Stage primaryStage) {
		/**
		 * create main pane and main Tabs.
		 */
		tabPane = new TabPane();
		tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);

		// tabPane.getStyleClass().add("border-pane");

		Tab compressTab = new Tab("Compress");
		Tab decompressTab = new Tab("Decompress");
		Tab statsTab = new Tab("Statistics");

		tabPane.getTabs().addAll(compressTab, statsTab, decompressTab);
		Scene scene = new Scene(tabPane, 950, 600);
		scene.getStylesheets().add("styles.css");

		/**
		 * file tab contents and file chooser
		 */

		/**
		 * program compress screen
		 */

		BorderPane compressfilePane = new BorderPane();
		Button compressButton = new Button("Upload file to Compress");

		compressfilePane.setCenter(compressButton);
		compressTab.setContent(compressfilePane);

		compressButton.setOnAction(e -> {

			// create a flag for error and error description to be shown
			boolean errorExist = false;
			String erorInfo = "";

			// check of errors while reading
			try {

				// read text file and ensure it is chosen correctly

				toCompressFile = chooseFile(primaryStage, null); // null no specific extension

				if (toCompressFile != null)
					// Check if the selected file is not huffman
					if (toCompressFile.getName().toLowerCase().endsWith(".huf"))
						throw new IllegalArgumentException("This is a compressed file, please chose Another");

				afterCompression = HuffmanFinal.huffmanCompress(toCompressFile);

			} catch (FileNotFoundException e1) {
				errorExist = true;
				erorInfo = "File Not Found";

			} catch (NullPointerException e2) {
				errorExist = true;
				erorInfo = "No file Choosen";

			} catch (IllegalArgumentException e4) {
				errorExist = true;
				erorInfo = "This is a compressed file, please chose Another";

			} catch (Exception e3) {
				errorExist = true;
				erorInfo = e3.toString();

			}

			if (errorExist) {
				Alert alert2 = new Alert(AlertType.ERROR);
				alert2.setTitle("File Error info");
				alert2.setHeaderText("There was an error in reading the sequence from the file");
				alert2.setContentText(erorInfo);
				alert2.showAndWait();
			} else {
				Label success = new Label(
						"*File compressed successsfully and saved in same directory as original file");
				success.setAlignment(Pos.BASELINE_CENTER);
				success.setPadding(new Insets(50));

				// what to do if no error
				compressfilePane.setBottom(success);

			}

		});

		/**
		 * program decompress screen in same way
		 */

		BorderPane decompressfilePane = new BorderPane();
		Button decompressButton = new Button("Upload file to Decompress");

		decompressfilePane.setCenter(decompressButton);

		decompressTab.setContent(decompressfilePane);
		decompressButton.setOnAction(e -> {

			// create a flag for error and error description to be shown
			boolean errorExist = false;
			String erorInfo = "";

			// check of errors while reading
			try {

				// read text file and ensure it is chosen correctly

				toDecompresFile = chooseFile(primaryStage, "huf");

				afterDeCompression = HuffmanFinal.huffmanUnCompress(toDecompresFile);

			} catch (FileNotFoundException e1) {
				errorExist = true;
				erorInfo = "File Not Found";

			} catch (NullPointerException e2) {
				errorExist = true;
				erorInfo = "No file Choosen";

			} catch (IllegalArgumentException e4) {
				errorExist = true;
				erorInfo = "This is a compressed file, please chose Another";

			} catch (Exception e3) {
				errorExist = true;
				erorInfo = e3.toString();

			}

			if (errorExist) {
				Alert alert2 = new Alert(AlertType.ERROR);
				alert2.setTitle("File Error info");
				alert2.setHeaderText("There was an error in reading the sequence from the file");
				alert2.setContentText(erorInfo);
				alert2.showAndWait();
			} else {
				Label success = new Label(
						"*File decompressed successsfully and saved in same directory as compressed file");
				success.setAlignment(Pos.BASELINE_CENTER);
				success.setPadding(new Insets(50));

				// what to do if no error
				decompressfilePane.setBottom(success);

			}

		});

		/**
		 * program statistics screen in same way
		 */

		HBox h1 = new HBox(20);
		VBox v1 = new VBox();

		Label l2 = new Label("No file compressed yet");
		l2.setPadding(new Insets(400));
		l2.setAlignment(Pos.CENTER);
		statsTab.setContent(l2);

		boolean isSet = false;
		ObservableList<TableCell> cells = FXCollections.observableArrayList();

		statsTab.setOnSelectionChanged(e -> {
			if (afterCompression != null) {

				for (int i = 0; i < HuffmanFinal.BYTES; i++) {
					if (HuffmanFinal.freq[i] != 0)
						cells.add(new TableCell(i, HuffmanFinal.codesBinaries[i], HuffmanFinal.lengths[i],
								HuffmanFinal.freq[i]));
				}

				TableView<TableCell> tableView = new TableView<>();
				tableView.setPlaceholder(new Label("No Compressed file yet"));

				// set up table view
				TableColumn<TableCell, Integer> byteValueColumn = new TableColumn<>("Byte Value");
				TableColumn<TableCell, String> asciiColumn = new TableColumn<>("ASCII code");
				TableColumn<TableCell, String> huffmanColumn = new TableColumn<>("Huffman Code");
				TableColumn<TableCell, Integer> lengthColumn = new TableColumn<>("Length");
				TableColumn<TableCell, Integer> freqColumn = new TableColumn<>("Frequency");

				// Set cell value factories for each column using SimpleStringProperty

				byteValueColumn.setCellValueFactory(
						cellData -> new SimpleIntegerProperty(cellData.getValue().getByteValue()).asObject());

				asciiColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAscii()));

				huffmanColumn
						.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getHuffman()));

				lengthColumn.setCellValueFactory(
						cellData -> new SimpleIntegerProperty(cellData.getValue().getLength()).asObject());

				freqColumn.setCellValueFactory(
						cellData -> new SimpleIntegerProperty(cellData.getValue().getFreq()).asObject());

				// Add columns to the TableView
				tableView.getColumns().addAll(byteValueColumn, asciiColumn, huffmanColumn, lengthColumn, freqColumn);
				tableView.setMinWidth(500);

				// do width things

				tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_LAST_COLUMN);

				// Make columns unresizable
				byteValueColumn.setResizable(false);
				asciiColumn.setResizable(false);
				huffmanColumn.setResizable(false);
				huffmanColumn.setMinWidth(200);
				lengthColumn.setResizable(false);
				freqColumn.setResizable(false);
				// tableView.setPadding(new Insets(30));
				tableView.getStyleClass().add("view");

				/**
				 * show it
				 */
				tableView.setItems(cells);
				h1.getChildren().clear();
				h1.getChildren().add(tableView);
				h1.setPadding(new Insets(40));

				/**
				 * show some data
				 */

				GridPane grid = new GridPane();
				grid.setPadding(new Insets(10, 10, 10, 10));
				grid.setVgap(35);
				grid.setHgap(10);

				// Original Size Label and TextField
				Label originalSizeLabel = new Label("Original Size in bytes:");
				TextField originalSizeTextField = new TextField();
				long orginalSize = toCompressFile.length();
				originalSizeTextField.setText(orginalSize + "");
				originalSizeTextField.setEditable(false);
				grid.add(originalSizeLabel, 0, 0);
				grid.add(originalSizeTextField, 1, 0);

				// New Size Label and TextField
				Label newSizeLabel = new Label("New Size in bytes:");
				TextField newSizeTextField = new TextField();
				long newSize = afterCompression.length();
				newSizeTextField.setText(newSize + "");
				newSizeTextField.setEditable(false);
				grid.add(newSizeLabel, 0, 1);
				grid.add(newSizeTextField, 1, 1);

				// Compression Factor Label and TextField
				double factor = (double) newSize / orginalSize * 100;

				double reduction = (double) (orginalSize -newSize) / orginalSize * 100;

				// Using String.format
				String formattedNumber1 = String.format("%.2f", factor);
				String formattedNumber2 = String.format("%.2f", reduction);


				Label compressionFactorLabel = new Label("Compression Factor:");
				TextField compressionFactorTextField = new TextField();
				compressionFactorTextField.setEditable(false);
				compressionFactorTextField.setText(formattedNumber1 + "%");
				grid.add(compressionFactorLabel, 0, 2);
				grid.add(compressionFactorTextField, 1, 2);

				
				Label reductionLabel = new Label("Reduction Factor:");
				TextField reductionTextField = new TextField();
				reductionTextField.setEditable(false);
				reductionTextField.setText(formattedNumber2 + "%");
				grid.add(reductionLabel, 0, 3);
				grid.add(reductionTextField, 1, 3);
				
				
				
				// Display Button
				Button treeButton = new Button("See Huffman Tree");
				treeButton.setAlignment(Pos.CENTER);
				grid.add(treeButton, 0, 4, 2, 1);

				/**
				 * show grid
				 */
				h1.getChildren().add(grid);
				statsTab.setContent(h1);

				/**
				 * program view button
				 */

				treeButton.setOnAction(e2 -> {

					/// testing visualizing
					Stage info2 = new Stage();
					BorderPane b = new BorderPane();
					Pane p = new Pane();
					p.setMinWidth(800);
					p.setMinHeight(800);

					// System.out.println(p.getWidth());

					ScrollPane scrollPane = new ScrollPane();
					scrollPane.setContent(p);
					scrollPane.pannableProperty().set(true);
					b.setCenter(scrollPane);

					Label t = new Label("Use buttons: - to zoom out and + to zoom in");
					t.setPadding(new Insets(15));
					t.setAlignment(Pos.CENTER);
					b.setBottom(t);
					b.setAlignment(t, Pos.CENTER);

					scrollPane.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
						if (event.getCode() == KeyCode.EQUALS) {
							p.setScaleX(p.getScaleX() * ZOOM_FACTOR);
							p.setScaleY(p.getScaleY() * ZOOM_FACTOR);
							event.consume();
						} else if (event.getCode() == KeyCode.MINUS) {
							p.setScaleX(p.getScaleX() / ZOOM_FACTOR);
							p.setScaleY(p.getScaleY() / ZOOM_FACTOR);
							event.consume();
						}
					});

					scrollPane.requestFocus();

					this.visualizeTree(HuffmanFinal.huffmanTree, p);

					// TreeView treeView = new TreeView();
					// scrollPane.setContent(treeView.setTree(HuffmanFinal.huffmanTree));

					p.setScaleX(0.8);
					p.setScaleY(0.8);

					// Scene scene22 = new Scene(b, p.getWidth() + 200, p.getHeight() + 100);
					Scene scene22 = new Scene(b, 1300, 800);

					scene22.getStylesheets().add("styles.css");

					info2.setTitle("Huffman Tree");
					info2.setScene(scene22);
					info2.show();
					p.getStyleClass().add("root");
					scrollPane.requestFocus();

				});

			} else {
				statsTab.setContent(l2);
			}

		});

		/**
		 * Make scene and show primary stage and add tabs
		 */
		primaryStage.setTitle("Huffman Coding Program"); // Set the window title
		primaryStage.setScene(scene); // Place the scene in the window
		primaryStage.show();// show main scene

	}

	public static void main(String[] args) {
		launch(args);
	}

	public File chooseFile(Stage fileStage, String extension) {
		// create file chooser object
		FileChooser fileChooser = new FileChooser();
		// set initial directory
		fileChooser.setInitialDirectory(new File("/Users/sarahhassouneh"));
		fileChooser.getExtensionFilters().clear();
		// add an extension filter
		if (extension != null)
			fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Text Files", "*." + extension));
		fileChooser.setTitle("Choose a file to Compress");
		File selectedFile = fileChooser.showOpenDialog(fileStage);
		return selectedFile;

	}

	// a method to draw the tree
	int raduis = 30;
	int levelHeight = 200;
	double levelWidth = 6000;

	public void visualizeTree(BinaryTree tree, Pane pane) {

		double x = raduis * Math.pow(2, tree.getHeight(tree.root)) - 1500;
		
		// levelWidth*=50;
		pane.setPadding(new Insets(20));
		visualizeTree(tree.getRoot(), pane, levelWidth, levelHeight, 0);
		pane.resize(1000, 1000);
		pane.resize(levelWidth + 100, levelHeight * tree.getHeight(tree.root) + 100);

	}

	// recursive function to draw a tree
	private void visualizeTree(TreeNode node, Pane pane, double x, double y, int level) {

		if (node == null || level > 7) {
			return;
		}

		pane.getChildren().add(circleText(x, y, node.toString() + ""));

		// Draw left child and connect it with a line use recursion
		if (node.getLeft() != null) {
			// this represents displacement
			double pos = levelWidth / Math.pow(2, level + 1);

			double xOfchild = x - pos;
			double yOfchild = y + levelHeight;

			visualizeTree(node.getLeft(), pane, xOfchild, yOfchild, level + 1);
			Line line = new Line(x, y, xOfchild, yOfchild);
			pane.getChildren().add(line);
			line.toBack();
		}

		// Draw left child and connect it with a line use recursion
		if (node.getRight() != null) {
			double pos = levelWidth / Math.pow(2, level + 1);
			double xOfchild = x + pos;
			double yOfchild = y + levelHeight;
			visualizeTree(node.getRight(), pane, xOfchild, yOfchild, level + 1);
			Line line = new Line(x, y, xOfchild, yOfchild);
			pane.getChildren().add(line);
			line.toBack();

		}

	}

	StackPane circleText(double x, double y, String text) {
		StackPane pane = new StackPane();
		pane.setLayoutX(x - raduis);
		pane.setLayoutY(y - raduis);
		Circle circle = new Circle(raduis);

		circle.setFill(Color.rgb(100, 149, 237));
		Label l = new Label(text);

		// circle.getStyleClass().add("circle");
		l.getStyleClass().add("label2");

		pane.getChildren().addAll(circle, l);

		return pane;
	}

	class TreeView extends Pane {
		private double radius = 20; // Tree node radius
		private double vGap = 50; // Gap between two levels in a tree
		BinaryTree tree;

		public TreeView() {
		}

		public TreeView(BinaryTree tree) {
			this.tree = tree;
		}

		public void setTree(BinaryTree huffmanTree) {
			this.tree = huffmanTree;
			repaint();
		}

		protected void repaint() {
			getChildren().clear();

			if (tree == null)
				return;

			if (tree.root != null) {
				// Display tree recursively
				displayTree(tree.root, getWidth() / 2, 30, getWidth() / 4);
			}
		}

		/** Display a subtree rooted at position (x, y) */
		private void displayTree(TreeNode root, double x, double y, double hGap) {

			if (root.left == null) { // Display the character for leaf node
				getChildren().add(new Text(x - 6, y + 34, root.toString() + ""));
			}
			if (root.left != null) {
				// Draw a line to the left node
				getChildren().addAll(new Line(x - hGap, y + vGap, x, y),
						new Text((x - hGap + x) / 2 - 5, (y + vGap + y) / 2, "0"));

				// Draw the left subtree recursively
				displayTree(root.left, x - hGap, y + vGap, hGap / 2);
			}

			if (root.right != null) {
				// Draw a line to the right node
				getChildren().addAll(new Line(x + hGap, y + vGap, x, y),
						new Text((x + hGap + x) / 2 + 5, (y + vGap + y) / 2, "1"));

				// Draw the right subtree recursively
				displayTree(root.right, x + hGap, y + vGap, hGap / 2);
			}

			// Display the root
			Circle circle = new Circle(x, y, radius);
			circle.setFill(Color.WHITE);
			circle.setStroke(Color.BLACK);
			getChildren().addAll(circle);
			// getChildren().addAll(new Text(x - 6, y + 4, root.weight + ""));
		}
	}

}
