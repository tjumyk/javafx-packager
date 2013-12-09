package tjumyk;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.ButtonBuilder;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ChoiceBoxBuilder;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressBarBuilder;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFieldBuilder;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleButtonBuilder;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBoxBuilder;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextBuilder;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class Main extends DialogApplication {

	TextField mainClass, srcDir, outDir, outFile, libs, preloader;
	ToggleButton createjar;// , createexe, deploy, makeall;
	ProgressBar progress;
	Tooltip tip;
	ChoiceBox<Object> preloader_class_choices;
	ToggleGroup build_group = new ToggleGroup();

	List<File> lib_files = new ArrayList<File>();
	File preloader_file;

	private static final String USER_DATA_DIR = System.getenv("APPDATA")
			+ File.separator + "JavaFxPackager" + File.separator;

	final String TEMP_FOLDER = System.getProperty("java.io.tmpdir")
			+ "javafxPackagerTempFolder\\";
	List<File> copiedFiles = new LinkedList<File>();

	// final String EXE4J_PARAMS = "";// -v -x -L A-XVK349962F-1r0d5h5cw8upz ";

	@Override
	protected void loadStage(Group root) {
		initStage(root, 600, 400);
		addIcon(new Image(this.getClass().getResourceAsStream("javaFx.png")));
		setTitle("JavaFx Packager V3.0 [Copyright©tjumyk]");
		BorderPane pane = new BorderPane();
		pane.setLayoutY(60);
		pane.setLayoutX(50);
		pane.setOpacity(0.80);
		pane.setPrefHeight(300);
		root.getChildren().add(pane);
		tip = new Tooltip();
		tip.setAutoFix(true);
		tip.setAutoHide(true);
		tip.setTextAlignment(TextAlignment.CENTER);

		pane.setCenter(VBoxBuilder
				.create()
				.onMouseDragged(new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event) {
						event.consume();
					}
				})
				.spacing(10)
				.children(
						HBoxBuilder
								.create()
								.children(
										TextBuilder.create().text("Main Class")
												.font(DEFAULT_FONT)
												.fill(this.DEFAULT_FONT_COLOR)
												.build(),
										mainClass = TextFieldBuilder.create()
												.prefWidth(330).prefHeight(20)
												.build(),
										ButtonBuilder
												.create()
												.text("Browse")
												.prefHeight(20)
												.onMouseClicked(
														new EventHandler<MouseEvent>() {
															public void handle(
																	MouseEvent event) {
																handleFindMainClass();
															}
														}).build()).spacing(10)
								.build(),
						HBoxBuilder
								.create()
								.children(
										TextBuilder.create()
												.text("Source Folder")
												.font(DEFAULT_FONT)
												.fill(this.DEFAULT_FONT_COLOR)
												.build(),
										srcDir = TextFieldBuilder.create()
												.prefWidth(380).prefHeight(20)
												.build()).spacing(10).build(),
						HBoxBuilder
								.create()
								.children(
										TextBuilder.create()
												.text("Output Folder")
												.font(DEFAULT_FONT)
												.fill(this.DEFAULT_FONT_COLOR)
												.build(),
										outDir = TextFieldBuilder.create()
												.prefWidth(380).prefHeight(20)
												.build()).spacing(10).build(),
						HBoxBuilder
								.create()
								.children(
										TextBuilder.create()
												.text("Output Name")
												.font(DEFAULT_FONT)
												.fill(this.DEFAULT_FONT_COLOR)
												.build(),
										outFile = TextFieldBuilder.create()
												.prefWidth(380).prefHeight(20)
												.build()).spacing(10).build(),
						HBoxBuilder
								.create()
								.children(
										TextBuilder.create()
												.text("Package Type")
												.font(DEFAULT_FONT)
												.fill(this.DEFAULT_FONT_COLOR)
												.build(),
										createjar = ToggleButtonBuilder
												.create().text("Create Jar")
												.toggleGroup(build_group)
												.selected(true).build())
								.spacing(10).build(),
						HBoxBuilder
								.create()
								.children(
										TextBuilder.create()
												.text("Dependent Libraries")
												.font(DEFAULT_FONT)
												.fill(this.DEFAULT_FONT_COLOR)
												.build(),
										libs = TextFieldBuilder.create()
												.prefHeight(20).prefWidth(220)
												.editable(false).build(),
										ButtonBuilder
												.create()
												.text("Add")
												.prefHeight(20)
												.onMouseClicked(
														new EventHandler<MouseEvent>() {
															public void handle(
																	MouseEvent event) {
																handleAddLib();
															}
														}).build(),
										ButtonBuilder
												.create()
												.text("Clear")
												.prefHeight(20)
												.onMouseClicked(
														new EventHandler<MouseEvent>() {
															public void handle(
																	MouseEvent event) {
																lib_files
																		.clear();
																libs.setText("");
															}
														}).build()).spacing(10)
								.build(),
						HBoxBuilder
								.create()
								.children(
										TextBuilder.create().text("Preloader")
												.font(DEFAULT_FONT)
												.fill(this.DEFAULT_FONT_COLOR)
												.build(),
										preloader = TextFieldBuilder.create()
												.editable(false).prefHeight(20)
												.prefWidth(265).build(),
										ButtonBuilder
												.create()
												.text("Browse")
												.prefHeight(20)
												.onMouseClicked(
														new EventHandler<MouseEvent>() {
															public void handle(
																	MouseEvent event) {
																handleSetPreloader();
															}
														}).build(),
										ButtonBuilder
												.create()
												.text("Remove")
												.prefHeight(20)
												.onMouseClicked(
														new EventHandler<MouseEvent>() {
															public void handle(
																	MouseEvent event) {
																preloader
																		.setText("");
																preloader_class_choices
																		.getItems()
																		.clear();
															}
														}).build())

								.spacing(10).build(),
						HBoxBuilder
								.create()
								.children(
										TextBuilder.create()
												.text("Preloader Class")
												.font(DEFAULT_FONT)
												.fill(this.DEFAULT_FONT_COLOR)
												.build(),
										preloader_class_choices = ChoiceBoxBuilder
												.create().prefWidth(365)
												.prefHeight(20).build())

								.spacing(10).build()).build());
		pane.setBottom(VBoxBuilder
				.create()
				.children(
						HBoxBuilder
								.create()
								.children(
										progress = ProgressBarBuilder.create()
												.visible(false).prefWidth(400)
												.build())

								.spacing(10).alignment(Pos.CENTER).build(),
						HBoxBuilder
								.create()
								.alignment(Pos.CENTER)
								.children(
										ButtonBuilder
												.create()
												.text("Start")
												.prefHeight(20)
												.onMouseClicked(
														new EventHandler<MouseEvent>() {
															@Override
															public void handle(
																	MouseEvent event) {
																handleStartPack();
															}
														}).build()).spacing(10)
								.build()).spacing(5).build());
	}

	protected boolean checkValidate() {
		if (mainClass.getText().length() <= 0 || srcDir.getText().length() <= 0
				|| outDir.getText().length() <= 0
				|| outFile.getText().length() <= 0
				|| build_group.getSelectedToggle() == null)
			return false;
		else
			return true;
	}

	protected void scanLibs(String path) throws IOException {
		File temp = new File(path);
		if (temp.exists()) {
			File[] files = temp.listFiles();
			for (File file : files) {
				if (file.isDirectory())
					scanLibs(file.getCanonicalPath());
				else {
					if (file.getName().endsWith(".jar")) {
						lib_files.add(file);
						libs.setText(libs.getText() + file.getName() + ";");
					}
				}
			}
		}

	}

	protected void copyLibs() throws Exception {
		// System.out.println(lib_files);
		copiedFiles.clear();
		for (File file : lib_files) {
			copyLib(file);
		}
	}

	private void clearCopiedFiles() {
		System.out.println("[CopyLib] Clearing copied files...");
		for (File file : copiedFiles) {
			File dir = file.getParentFile();
			if (file.isFile()) {
				file.delete();
				deleteDirIfEmpty(dir);
			} else
				deleteDirIfEmpty(file);
		}
		copiedFiles.clear();
	}

	private void deleteDirIfEmpty(File dir) {
		File parent = dir.getParentFile();
		if (dir.list().length <= 0) {
			dir.delete();
			deleteDirIfEmpty(parent);
		}
	}

	protected void copyLib(File file) throws Exception {
		if (file.exists()) {
			System.out.println("[CopyLib] " + file.getAbsolutePath());
			JarFile jarFile = new JarFile(file);
			Enumeration<JarEntry> entries = jarFile.entries();
			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				File new_file = new File(srcDir.getText() + entry.getName());
				if (new_file.exists())
					continue;

				if (entry.isDirectory()) {
					new_file.mkdirs();
				} else {
					InputStream in = jarFile.getInputStream(entry);
					File dir = new_file.getParentFile();
					if (!dir.exists())
						dir.mkdirs();

					FileOutputStream out = new FileOutputStream(new_file);
					byte[] buffer = new byte[1024];
					int length;
					while ((length = in.read(buffer)) != -1) {
						out.write(buffer, 0, length);
					}
					in.close();
					out.close();
				}
				copiedFiles.add(new_file);
			}
			jarFile.close();
		}
	}

	private void handleFindMainClass() {
		FileChooser chooser = new FileChooser();
		chooser.getExtensionFilters().add(
				new ExtensionFilter("class file", "*.class"));
		chooser.setTitle("Please Choose Main Class");
		String lastPath = readLastPath();
		if (lastPath == null || lastPath.length() <= 0)
			lastPath = System.getProperty("user.dir");
		chooser.setInitialDirectory(new File(lastPath));
		File file = chooser.showOpenDialog(stage.getOwner());
		if (file != null) {
			try {
				String path = file.getCanonicalPath();
				saveLastPath(file.getParent());
				libs.setText("");
				lib_files.clear();
				preloader.setText("");
				preloader_class_choices.getItems().clear();
				int index;
				if ((index = path.lastIndexOf("\\bin\\")) != -1) {
					mainClass.setText(path.substring(index + 5,
							path.lastIndexOf('.')).replace('\\', '.'));
					srcDir.setText(path.substring(0, index + 5));
					outDir.setText((path = path.substring(0, index))
							+ "\\dist\\");
					outFile.setText(path.substring(path.lastIndexOf('\\') + 1,
							path.length()));
				} else {
					mainClass.setText(path.substring(
							path.lastIndexOf('\\') + 1, path.lastIndexOf('.')));
					path = path.substring(0, path.lastIndexOf('\\'));
					srcDir.setText(path + "\\");
					outDir.setText((path = path.substring(0,
							path.lastIndexOf('\\') + 1))
							+ "dist\\");
					outFile.setText("output");
				}
				scanLibs(path + "\\lib\\");
				File[] scan = new File(path + "\\").getParentFile().listFiles(
						new FilenameFilter() {
							@Override
							public boolean accept(File dir, String name) {
								if (name.startsWith(outFile.getText())
										&& name.contains("Preloader"))
									return true;
								else
									return false;
							}
						});
				// System.out.println(Arrays.toString(scan));
				if (scan.length > 0) {
					File test = new File(scan[0].getCanonicalPath()
							+ "\\dist\\");
					if (test.exists()) {
						scan = test.listFiles(new FilenameFilter() {
							@Override
							public boolean accept(File dir, String name) {
								if (name.startsWith(outFile.getText())
										&& name.contains("Preloader")
										&& name.endsWith(".jar"))
									return true;
								else
									return false;
							}
						});
						if (scan.length > 0) {
							preloader_file = scan[0];
							preloader.setText(scan[0].getName());

							JarFile jar = new JarFile(scan[0]);
							Enumeration<JarEntry> entries = jar.entries();
							while (entries.hasMoreElements()) {
								String item = entries.nextElement().getName();
								if (item.endsWith(".class")) {
									item = item.replace('/', '.');
									preloader_class_choices.getItems()
											.add(item.substring(0,
													item.length() - 6));
								}
							}
							jar.close();
							if (preloader_class_choices.getItems().size() == 1)
								preloader_class_choices.getSelectionModel()
										.select(0);
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private String readLastPath() {
		File savePathFile = new File(USER_DATA_DIR + "lastPath.txt");
		FileReader reader = null;
		try {
			if (savePathFile.exists()) {
				reader = new FileReader(savePathFile);
				StringBuilder sb = new StringBuilder();
				char[] buf = new char[1024];
				int len;
				while ((len = reader.read(buf, 0, buf.length)) > 0) {
					sb.append(new String(buf, 0, len));
				}
				String path = sb.toString();
				File f = new File(path);
				if (f.exists())
					return path;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null)
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return null;
	}

	private void saveLastPath(String path) throws IOException {
		File dir = new File(USER_DATA_DIR);
		if (!dir.exists())
			dir.mkdirs();
		File savePathFile = new File(USER_DATA_DIR + "lastPath.txt");
		if (!savePathFile.exists())
			savePathFile.createNewFile();

		FileWriter writer = new FileWriter(savePathFile);
		writer.append(path);
		writer.flush();
		writer.close();
	}

	private void handleAddLib() {
		FileChooser chooser = new FileChooser();
		chooser.getExtensionFilters().add(
				new ExtensionFilter("Jar Library", "*.jar"));
		chooser.setTitle("Please Choose Libraries");
		if (srcDir.getText().length() > 0) {
			File dir = new File(srcDir.getText()).getParentFile();
			File libDir = new File(dir.getAbsolutePath() + "/lib");
			if (libDir.exists())
				chooser.setInitialDirectory(libDir);
			else
				chooser.setInitialDirectory(dir);
		} else
			chooser.setInitialDirectory(new File(System.getProperty("user.dir")));
		
		List<File> lib = chooser.showOpenMultipleDialog(stage.getOwner());
		if (lib != null) {
			for (File file : lib) {
				try {
					String path = file.getCanonicalPath();
					lib_files.add(file);
					String name = path.substring(path.lastIndexOf('\\') + 1);
					libs.setText(libs.getText() + name + ";");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void handleSetPreloader() {
		FileChooser chooser = new FileChooser();
		chooser.getExtensionFilters().add(
				new ExtensionFilter("Jar Preloader", "*.jar"));
		chooser.setTitle("Please Choose Preloader File");
		if (srcDir.getText().length() > 0)
			chooser.setInitialDirectory(new File(srcDir.getText())
					.getParentFile().getParentFile());
		else
			chooser.setInitialDirectory(new File(System.getProperty("user.dir")));
		File file = chooser.showOpenDialog(stage.getOwner());
		if (file != null) {
			try {
				preloader_file = file;
				preloader.setText(file.getName());
				preloader_class_choices.getItems().clear();
				JarFile jar = new JarFile(file);
				Enumeration<JarEntry> entries = jar.entries();
				while (entries.hasMoreElements()) {
					String item = entries.nextElement().getName();
					if (item.endsWith(".class")) {
						item = item.replace('/', '.');
						preloader_class_choices.getItems().add(
								item.substring(0, item.length() - 6));
					}
				}
				jar.close();
				if (preloader_class_choices.getItems().size() == 1)
					preloader_class_choices.getSelectionModel().select(0);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void handleStartPack() {
		if (!checkValidate()) {
			tip.setText("You miss something!");
			tip.show(stage);
			tip.setX(stage.getX() + (stage.getWidth() - tip.getWidth()) / 2);
			tip.setY(stage.getY() + (stage.getHeight() - tip.getHeight()) / 2);
			return;
		}
		progress.setVisible(true);
		new Thread() {
			public void run() {
				try {

					copyLibs();
					ArrayList<String> newArgs = new ArrayList<String>();

					if (createjar.isSelected()
					// ||
					// createexe.isSelected()
					) {
						newArgs.add("-createjar");
						newArgs.add("-appclass");
						newArgs.add(mainClass.getText());
						newArgs.add("-srcdir");
						newArgs.add(srcDir.getText());
						newArgs.add("-outdir");
						newArgs.add(outDir.getText());
						newArgs.add("-outfile");
						newArgs.add(outFile.getText());
						newArgs.add("-nocss2bin");
						newArgs.add("-v");
						// newArgs.add("-noembedlauncher");
						// newArgs.add("-runtimeversion");
						// newArgs.add("2.0");
					}
					if (preloader.getText().length() > 0
							&& preloader_class_choices.getSelectionModel()
									.getSelectedIndex() >= 0) {
						copyLib(preloader_file);
						newArgs.add("-preloader");
						newArgs.add((String) preloader_class_choices
								.getSelectionModel().getSelectedItem());
					}
					args = newArgs.toArray(new String[0]);
					System.out.println("[Command] " + Arrays.toString(args));
					com.sun.javafx.tools.packager.Main.main(args);

					clearCopiedFiles();
					System.out.println("Finished!");
					Platform.runLater(new Thread() {

						public void run() {
							progress.setVisible(false);
							tip.setText("Finished!");
							tip.show(stage);
							tip.setX(stage.getX()
									+ (stage.getWidth() - tip.getWidth()) / 2);
							tip.setY(stage.getY()
									+ (stage.getHeight() - tip.getHeight()) / 2);
						}
					});
				} catch (final Exception e) {
					e.printStackTrace();
					progress.setVisible(false);
					Platform.runLater(new Runnable() {

						@Override
						public void run() {
							tip.setText("[Exception] "+e.getLocalizedMessage());
							tip.show(stage);
							tip.setX(stage.getX()
									+ (stage.getWidth() - tip.getWidth()) / 2);
							tip.setY(stage.getY()
									+ (stage.getHeight() - tip.getHeight()) / 2);
						}
					});

				}
			}
		}.start();
	}

	public static void main(String[] args) {
		if (args != null && args.length > 0)
			try {
				com.sun.javafx.tools.packager.Main.main(args);
			} catch (Exception e) {
				e.printStackTrace();
			}
		else
			launch(args);
	}

}
