package pdfwriter;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class Message_box
{
	public static int info_message = 0;

	public static int warning_message = 1;

	private static Stage new_stage;

	private static Text new_text;

	private static Button new_button;

	private static VBox new_group;

	private static Font my_font;

	public static void show(String mesaj, String baslik, int message_type)
	{
		my_font = Font.font("Arial", 20);
		new_stage = new Stage();
		new_stage.setTitle(baslik);
		new_stage.setResizable(false);
		new_text = new Text();
		new_text.setText(mesaj);
		new_text.setWrappingWidth(500);
		new_text.setTextOrigin(VPos.CENTER);
		new_text.setTextAlignment(TextAlignment.CENTER);
		new_text.setFont(my_font);
		new_text.setTranslateY(10);
		new_button = new Button("OK");
		new_button.setPrefWidth(100);
		new_button.setPrefHeight(25);
		new_button.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent event)
			{
				new_stage.close();
			}
		});
		new_group = new VBox();
		new_group.setLayoutY(10);
		new_group.setAlignment(Pos.TOP_CENTER);
		new_group.setSpacing(30);
		new_group.getChildren().addAll(new_text, new_button);
		new_group.setPrefWidth(300);
		new_group.setPrefHeight(200);
		new_stage.setScene(new Scene(new_group));
		if (message_type == warning_message)
		{
			new_stage.getIcons().add(new Image(PdfWriter.class.getResourceAsStream("imgs/warning.png")));
		}
		else if (message_type == info_message)
		{
			new_stage.getIcons().add(new Image(PdfWriter.class.getResourceAsStream("imgs/info.png")));
		}
		new_stage.setWidth(new_text.getWrappingWidth() + 40);
		new_stage.setHeight(170);
		new_stage.show();
	}

	public static void show(String mesaj)
	{
		show(mesaj, "Info", info_message);
	}
}
