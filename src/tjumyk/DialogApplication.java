package tjumyk;

import java.util.Map;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.ImageViewBuilder;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.RectangleBuilder;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextBuilder;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import netscape.javascript.JSObject;

public abstract class DialogApplication extends Application {

    public final int DEFAULT_WIDTH = 600;
    public final int DEFAULT_HEIGHT = 400;
    public final Font DEFAULT_FONT = new Font(Font.getDefault().getName(), 16);
    public final Color DEFAULT_FONT_COLOR = Color.WHITESMOKE;
    public final Image[] DIALOG_BTNS = {new Image(this.getClass().getResourceAsStream("window_close.png")),
        new Image(this.getClass().getResourceAsStream("window_close_fc.png")),
        new Image(this.getClass().getResourceAsStream("window_minimize.png")),
        new Image(this.getClass().getResourceAsStream("window_minimize_fc.png"))};
    protected Text title;
    protected Stage stage;
    protected static String[] args;
    private double initX;
    private double initY;
    private Point2D dragAnchor;
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        Group root = new Group();
        Scene scene = new Scene(root, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        JSObject browser = getHostServices().getWebContext();
        if (browser != null) {
            Map<String,String> parmas = getParameters().getNamed();
            String bgColorStr = parmas.get("bgColor");
            if(bgColorStr!=null && bgColorStr.length()>0){
                String[] colors = bgColorStr.split(",");
                if(colors.length == 3) {
                    Paint bgColor = Color.rgb(
                            Integer.parseInt(colors[0],16),
                            Integer.parseInt(colors[1],16),
                            Integer.parseInt(colors[2],16));
                    scene.setFill(bgColor);
                }else if(colors.length == 4) {
                    Paint bgColor = Color.rgb(
                            Integer.parseInt(colors[0],16),
                            Integer.parseInt(colors[1],16),
                            Integer.parseInt(colors[2],16),
                            Double.parseDouble(colors[3]));
                    scene.setFill(bgColor);
                }
            }
        }else {
            scene.setFill(Color.TRANSPARENT);
        }
        setDraggable(scene);
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setScene(scene);
        loadStage(root);
        primaryStage.show();
        primaryStage.centerOnScreen();
    }
    
    public void setTitle(String title) {
        this.title.setText(title);
        stage.setTitle(title);
    }
    
    protected void loadStage(Group root) {
        initStage(root, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        this.setTitle("DialogApplication");
    }    
    
    protected void initStage(final Group root, int width, int height) {
        stage.setWidth(width);
        stage.setHeight(height);
        root.getChildren().addAll(
                ImageViewBuilder
                .create()
                .fitWidth(width - 20)
                .fitHeight(height - 20)
                .image(new Image(this.getClass().getResourceAsStream("bg.jpg")))
                .layoutX(10)
                .layoutY(10)
                .clip(RectangleBuilder.create().width(width - 20).height(height - 20).arcHeight(15)
                .arcWidth(15).build()).build(),
                RectangleBuilder.create().width(width - 50).height(height - 70).opacity(0.5).arcHeight(15).arcWidth(15)
                .fill(Color.DARKGRAY).layoutX(25).layoutY(45).onMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                event.consume();
            }
        }).build(),
                title = TextBuilder.create().fill(DEFAULT_FONT_COLOR).font(DEFAULT_FONT).effect(new Glow())
                .layoutX(30).layoutY(35).onMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                event.consume();
            }
        }).build(),
                ImageViewBuilder.create().image(DIALOG_BTNS[0]).layoutX(width - 55).layoutY(10)
                .effect(new DropShadow()).onMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Platform.exit();
            }
        }).onMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                ImageView view = (ImageView) event.getSource();
                view.setImage(DIALOG_BTNS[1]);
            }
        }).onMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                ImageView view = (ImageView) event.getSource();
                view.setImage(DIALOG_BTNS[0]);
            }
        }).onMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                event.consume();
            }
        }).build(),
                ImageViewBuilder.create().image(DIALOG_BTNS[2]).layoutX(width - 89).layoutY(10)
                .effect(new DropShadow()).onMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                stage.setIconified(true);
            }
        }).onMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                ImageView view = (ImageView) event.getSource();
                view.setImage(DIALOG_BTNS[3]);
            }
        }).onMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                ImageView view = (ImageView) event.getSource();
                view.setImage(DIALOG_BTNS[2]);
            }
        }).onMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                event.consume();
            }
        }).build());
        root.setEffect(new DropShadow());
    }
    
    public void setDraggable(final Scene scene) {
        scene.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent me) {
                // Node s = (Node) me.getSource();
                initX = scene.getWindow().getX();
                initY = scene.getWindow().getY();
                dragAnchor = new Point2D(me.getScreenX(), me.getScreenY());
                // self.toFront();
            }
        });
        scene.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent me) {
                // Node s = (Node) me.getSource();
                double dragX = me.getScreenX() - dragAnchor.getX();
                double dragY = me.getScreenY() - dragAnchor.getY();
                // calculate new position of the circle
                double newXPosition = initX + dragX;
                double newYPosition = initY + dragY;
                scene.getWindow().setX(newXPosition);
                scene.getWindow().setY(newYPosition);
                me.consume();
            }
        });
    }
    
    public void addIcon(Image image) {
        stage.getIcons().add(image);
    }
}
