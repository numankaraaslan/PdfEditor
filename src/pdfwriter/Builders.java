package pdfwriter;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class Builders
{
    public static TextField build_textField( String text, int width )
    {
        TextField txtfield = new TextField( text );
        txtfield.setFont( Constants.def_font );
        txtfield.setPrefWidth( width );
        return txtfield;
    }

    public static Text build_text( String text )
    {
        Text some_text = new Text( text );
        some_text.setFont( Constants.def_font );
        return some_text;
    }

    public static Button build_button( String text, boolean enabled )
    {
        Button btn = new Button( text );
        btn.setFont( Constants.def_font );
        btn.setDisable( !enabled );
        return btn;
    }
}
