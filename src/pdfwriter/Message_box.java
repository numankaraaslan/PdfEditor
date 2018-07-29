package pdfwriter;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.SceneBuilder;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBuilder;
import javafx.scene.layout.VBox;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextBuilder;
import javafx.stage.Stage;
import javafx.stage.StageBuilder;

public class Message_box
{
    public static int info_message = 0;
    public static int warning_message = 1;
    private static Stage new_stage;
    private static Text new_text;
    private static Button new_button;
    private static VBox new_group;
    private static Font my_font;

    public static void show( String mesaj, String baslik, int message_type )
    {
        my_font = Font.font( "Arial", 20 );
        new_stage = StageBuilder.create().title( baslik ).resizable( false ).build();
        new_text = TextBuilder.create().text( mesaj ).wrappingWidth( 500 ).textOrigin( VPos.CENTER ).textAlignment( TextAlignment.CENTER ).font( my_font ).translateY( 10 ).build();
        new_button = ButtonBuilder.create().text( "OK" ).prefWidth( 100 ).prefHeight( 25 ).onAction( new EventHandler<ActionEvent>()
        {
            @Override
            public void handle( ActionEvent event )
            {
                new_stage.close();
            }
        } ).build();
        new_group = VBoxBuilder.create().layoutY( 10 ).alignment( Pos.TOP_CENTER ).spacing( 30 ).children( new_text, new_button ).prefWidth( 300 ).prefHeight( 200 ).build();
        new_stage.setScene( SceneBuilder.create().root( new_group ).build() );
        new_stage.setWidth( new_text.getWrappingWidth() + 40 );
        new_stage.setHeight( 170 );
        new_stage.show();
    }

    public static void show( String mesaj )
    {
        show( mesaj, "Info", info_message );
    }
}
