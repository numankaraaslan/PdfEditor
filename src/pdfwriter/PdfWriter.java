package pdfwriter;

import java.awt.image.BufferedImage;
import java.io.*;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.swing.filechooser.FileSystemView;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.PDFRenderer;

public class PdfWriter extends Application
{
    private ImageView imgview_pdf;
    private Slider slider_page;
    private PDDocument doc;
    private PDPageTree doc_pages;
    private PDFRenderer renderer;
    private PDPageContentStream content_stream;
    private final VBox root = new VBox( 10 ), vbox_list = new VBox( 10 );
    private final HBox hbox_controls = new HBox( 10 ), hbox_controls_image = new HBox( 10 ), hbox_controls_text_rectangle = new HBox( 10 ), hbox_pdf = new HBox( 10 );
    private Button btn_load_pdf, btn_save_pdf, btn_draw_img, btn_draw_text, btn_draw_rect;
    private Text txt_image_x, txt_image_height, txt_image_y, txt_image_width, txt_shape_thick, txt_textimage;
    private TextField txtfield_image_x, txtfield_image_height, txtfield_image_y, txtfield_image_width, txtfield_shape_thick, txtfield_textimage;
    private int page_index = 1;
    private float zoom_level = 1.0f;
    private String desktop_path;
    private ListView<CheckBox> listview_pages;
    private CheckBox checkbox_all, checkbox_fill_rect;
    private Stage primaryStage;
    private ColorPicker color_chooser;
    private ComboBox<Integer> combobox_font_size;
    private ComboBox<Float> combobox_zoom;
    private BufferedImage buffered;

    @Override
    public void start( Stage primaryStage )
    {
        this.primaryStage = primaryStage;
        Constants.def_font = new Font( "Arial", 18 );
        desktop_path = FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath() + "\\";
        txt_shape_thick = Builders.build_text( "Thickness" );
        txt_image_x = Builders.build_text( "Position X" );
        txt_image_y = Builders.build_text( "Position Y" );
        txt_image_width = Builders.build_text( "Width" );
        txt_image_height = Builders.build_text( "Height" );
        txt_textimage = Builders.build_text( "Text" );
        txtfield_image_width = Builders.build_textField( "100", 100 );
        txtfield_image_height = Builders.build_textField( "50", 100 );
        txtfield_image_x = Builders.build_textField( "10", 100 );
        txtfield_image_y = Builders.build_textField( "10", 100 );
        txtfield_shape_thick = Builders.build_textField( "1", 100 );
        txtfield_textimage = Builders.build_textField( "", 150 );
        btn_load_pdf = Builders.build_button( "Load pdf", true );
        btn_save_pdf = Builders.build_button( "Save pdf", false );
        btn_draw_img = Builders.build_button( "Draw image", false );
        btn_draw_text = Builders.build_button( "Draw text", false );
        btn_draw_rect = Builders.build_button( "Draw rectangle", false );
        btn_load_pdf.setOnAction( get_btn_load_action() );
        btn_draw_rect.setOnAction( get_btn_draw_rect_action() );
        btn_draw_img.setOnAction( get_btn_draw_image_action() );
        btn_draw_text.setOnAction( get_btn_draw_text_action() );
        btn_save_pdf.setOnAction( get_btn_save_pdf_action() );
        imgview_pdf = new ImageView();
        imgview_pdf.setPreserveRatio( true );
        slider_page = new Slider( 0, 1, 0 );
        slider_page.setSnapToTicks( true );
        slider_page.setStyle( Constants.style );
        slider_page.setBlockIncrement( 1 );
        slider_page.setMinorTickCount( 0 );
        slider_page.setShowTickMarks( true );
        slider_page.setShowTickLabels( true );
        slider_page.setPrefWidth( 300 );
        slider_page.setMajorTickUnit( 1 );
        slider_page.setDisable( true );
        slider_page.valueProperty().addListener( new ChangeListener<Number>()
        {
            @Override
            public void changed( ObservableValue<? extends Number> observable, Number oldValue, Number newValue )
            {
                page_index = newValue.intValue();
                refresh_image();
            }
        } );
        checkbox_fill_rect = new CheckBox( "Fill" );
        checkbox_fill_rect.setFont( Constants.def_font );
        checkbox_all = new CheckBox( "Select all" );
        checkbox_all.setFont( Constants.def_font );
        checkbox_all.selectedProperty().addListener( new ChangeListener<Boolean>()
        {
            @Override
            public void changed( ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue )
            {
                for ( int i = 0; i < listview_pages.getItems().size(); i++ )
                {
                    listview_pages.getItems().get( i ).setSelected( newValue );
                }
            }
        } );
        color_chooser = new ColorPicker();
        color_chooser.setValue( Color.BLUE );
        combobox_font_size = new ComboBox<>( FXCollections.observableArrayList( 8, 9, 10, 12, 14, 16, 18, 20, 22, 24, 26, 28, 30, 32, 34, 36, 38, 40, 42, 44, 46, 48, 50, 52, 54, 56, 58, 60, 62, 64, 66, 68, 70, 72 ) );
        combobox_font_size.getSelectionModel().select( 3 );
        combobox_font_size.setStyle( Constants.style );
        combobox_zoom = new ComboBox<>( FXCollections.observableArrayList( 50F, 100F, 150F, 200F, 250F, 300F, 350F, 400F, 450F, 500F, 550F, 600F, 650F, 700F, 750F, 800F, 850F, 900F, 950F, 1000F ) );
        combobox_zoom.getSelectionModel().select( 1 );
        combobox_zoom.setStyle( Constants.style );
        combobox_zoom.getSelectionModel().selectedItemProperty().addListener( new ChangeListener<Float>()
        {
            @Override
            public void changed( ObservableValue<? extends Float> observable, Float oldValue, Float newValue )
            {
                zoom_level = newValue / 100;
                refresh_image();
            }
        } );
        ScrollPane scroll_pdf = new ScrollPane( imgview_pdf );
        scroll_pdf.prefWidthProperty().bind( this.primaryStage.widthProperty().subtract( 210 ) );
        scroll_pdf.prefHeightProperty().bind( this.primaryStage.heightProperty().subtract( 250 ) );
        scroll_pdf.setCenterShape( true );
        listview_pages = new ListView<>();
        listview_pages.setPrefWidth( 200 );
        listview_pages.prefHeightProperty().bind( scroll_pdf.prefHeightProperty() );
        hbox_controls.getChildren().addAll( btn_load_pdf, slider_page, combobox_zoom, btn_save_pdf );
        hbox_controls.setAlignment( Pos.CENTER_LEFT );
        hbox_controls_image.getChildren().addAll( txt_image_x, txtfield_image_x, txt_image_y, txtfield_image_y, txt_image_width, txtfield_image_width, txt_image_height, txtfield_image_height, btn_draw_img );
        hbox_controls_image.setAlignment( Pos.CENTER_LEFT );
        hbox_controls_text_rectangle.getChildren().addAll( txt_textimage, txtfield_textimage, color_chooser, combobox_font_size, btn_draw_text, txt_shape_thick, txtfield_shape_thick, checkbox_fill_rect, btn_draw_rect );
        hbox_controls_text_rectangle.setAlignment( Pos.CENTER_LEFT );
        vbox_list.getChildren().addAll( checkbox_all, listview_pages );
        hbox_pdf.getChildren().addAll( vbox_list, scroll_pdf );
        root.setPadding( new Insets( 10 ) );
        root.getChildren().addAll( hbox_controls, hbox_controls_image, hbox_controls_text_rectangle, hbox_pdf );
        Scene scene = new Scene( root, 1280, 768 );
        this.primaryStage.setTitle( "Hello World of PDF Editing" );
        this.primaryStage.setScene( scene );
        this.primaryStage.show();
    }

    public static void main( String[] args )
    {
        launch( args );
    }

    private EventHandler<ActionEvent> get_btn_load_action()
    {
        return new EventHandler<ActionEvent>()
        {
            @Override
            public void handle( ActionEvent event )
            {
                FileChooser file_chooser = new FileChooser();
                file_chooser.setTitle( "Select Pdf" );
                FileChooser.ExtensionFilter extension = new FileChooser.ExtensionFilter( "PDF files", "*.pdf" );
                file_chooser.getExtensionFilters().add( extension );
                file_chooser.setSelectedExtensionFilter( extension );
                String file_path = "";
                file_chooser.setInitialDirectory( new File( desktop_path ) );
                File selected_file = file_chooser.showOpenDialog( primaryStage.getOwner() );
                if ( selected_file != null )
                {
                    file_path = selected_file.getAbsolutePath();
                    try
                    {
                        if ( doc != null )
                        {
                            doc.close();
                        }
                        doc = PDDocument.load( new File( file_path ) );
                    }
                    catch ( IOException ex )
                    {
                        Message_box.show( "ex " + ex.getLocalizedMessage() );
                    }
                    doc_pages = doc.getPages();
                    slider_page.setMin( 1 );
                    slider_page.setMax( doc_pages.getCount() );
                    slider_page.setValue( 1 );
                    slider_page.setDisable( false );
                    btn_save_pdf.setDisable( false );
                    btn_draw_img.setDisable( false );
                    btn_draw_text.setDisable( false );
                    btn_draw_rect.setDisable( false );
                    renderer = new PDFRenderer( doc );
                    listview_pages.getItems().clear();
                    page_index = 1;
                    int pdf_width = ( int ) doc_pages.get( 0 ).getMediaBox().getWidth();
                    int pdf_height = ( int ) doc_pages.get( 0 ).getMediaBox().getHeight();
                    txtfield_image_x.setText( pdf_width - 100 + "" );
                    txtfield_image_y.setText( pdf_height - 50 + "" );
                    txtfield_image_width.setText( "100" );
                    txtfield_image_height.setText( "50" );
                    for ( int i = 0; i < doc_pages.getCount(); i++ )
                    {
                        CheckBox page_checkbox = new CheckBox( "Page " + ( i + 1 ) );
                        page_checkbox.setFont( Constants.def_font );
                        listview_pages.getItems().add( page_checkbox );
                    }
                    checkbox_all.setSelected( false );
                    refresh_image();
                }
            }
        };
    }

    private EventHandler<ActionEvent> get_btn_draw_image_action()
    {
        return new EventHandler<ActionEvent>()
        {
            @Override
            public void handle( ActionEvent event )
            {
                if ( !check_selected_pages() )
                {
                    Message_box.show( "Select at least 1 page on the left", "Warning", Message_box.warning_message );
                    listview_pages.requestFocus();
                    return;
                }
                FileChooser file_chooser = new FileChooser();
                file_chooser.setTitle( "Select image" );
                FileChooser.ExtensionFilter extension = new FileChooser.ExtensionFilter( "Image files", "*.jpg", "*.jpeg", "*.png" );
                file_chooser.getExtensionFilters().add( extension );
                file_chooser.setSelectedExtensionFilter( extension );
                file_chooser.setInitialDirectory( new File( desktop_path ) );
                File selected_file = file_chooser.showOpenDialog( primaryStage.getOwner() );
                if ( selected_file != null )
                {
                    PDImageXObject x_object = null;
                    try
                    {
                        x_object = PDImageXObject.createFromFileByContent( selected_file, doc );
                    }
                    catch ( IOException ex )
                    {
                    }
                    int pos_x = Integer.parseInt( txtfield_image_x.getText().trim() );
                    int pos_y = Integer.parseInt( txtfield_image_y.getText().trim() );
                    int img_width = Integer.parseInt( txtfield_image_width.getText().trim() );
                    int img_height = Integer.parseInt( txtfield_image_height.getText().trim() );
                    for ( int i = 0; i < listview_pages.getItems().size(); i++ )
                    {
                        CheckBox check = listview_pages.getItems().get( i );
                        if ( check.isSelected() )
                        {
                            try
                            {
                                content_stream = new PDPageContentStream( doc, doc_pages.get( i ), PDPageContentStream.AppendMode.APPEND, true );
                                content_stream.drawImage( x_object, pos_x, pos_y, img_width, img_height );
                                content_stream.close();
                            }
                            catch ( IOException ex )
                            {
                                Message_box.show( "ex " + ex.getLocalizedMessage() );
                            }
                        }
                    }
                    refresh_image();
                }
                else
                {
                    Message_box.show( "Please select image file", "Warning", Message_box.warning_message );
                }
            }
        };
    }

    private boolean check_selected_pages()
    {
        boolean res = false;
        for ( int i = 0; i < listview_pages.getItems().size(); i++ )
        {
            res = res || listview_pages.getItems().get( i ).isSelected();
        }
        return res;
    }

    private EventHandler<ActionEvent> get_btn_draw_text_action()
    {
        return new EventHandler<ActionEvent>()
        {
            @Override
            public void handle( ActionEvent event )
            {
                if ( !check_selected_pages() )
                {
                    Message_box.show( "Select at least 1 page on the left", "Warning", Message_box.warning_message );
                    listview_pages.requestFocus();
                    return;
                }
                if ( !txtfield_textimage.getText().trim().equals( "" ) )
                {
                    int pos_x = Integer.parseInt( txtfield_image_x.getText().trim() );
                    int pos_y = Integer.parseInt( txtfield_image_y.getText().trim() );
                    int red = ( int ) ( color_chooser.getValue().getRed() * 255 );
                    int green = ( int ) ( color_chooser.getValue().getGreen() * 255 );
                    int blue = ( int ) ( color_chooser.getValue().getBlue() * 255 );
                    for ( int i = 0; i < listview_pages.getItems().size(); i++ )
                    {
                        CheckBox check = listview_pages.getItems().get( i );
                        if ( check.isSelected() )
                        {
                            try
                            {
                                content_stream = new PDPageContentStream( doc, doc_pages.get( i ), PDPageContentStream.AppendMode.APPEND, true );
                                content_stream.beginText();
                                PDFont selected_font = PDType1Font.TIMES_ROMAN;
                                content_stream.setFont( selected_font, combobox_font_size.getValue() );
                                content_stream.setNonStrokingColor( red, green, blue );
                                content_stream.newLineAtOffset( pos_x, pos_y );
                                content_stream.showText( txtfield_textimage.getText().trim() );
                                content_stream.endText();
                                content_stream.close();
                            }
                            catch ( IOException ex )
                            {
                                Message_box.show( "ex " + ex.getLocalizedMessage() );
                            }
                        }
                    }
                    refresh_image();
                }
                else
                {
                    Message_box.show( "Please enter text", "Warning", Message_box.warning_message );
                    txtfield_textimage.requestFocus();
                }
            }
        };
    }

    private EventHandler<ActionEvent> get_btn_draw_rect_action()
    {
        return new EventHandler<ActionEvent>()
        {
            @Override
            public void handle( ActionEvent event )
            {
                if ( !check_selected_pages() )
                {
                    Message_box.show( "Select at least 1 page on the left", "Warning", Message_box.warning_message );
                    listview_pages.requestFocus();
                    return;
                }
                int pos_x = Integer.parseInt( txtfield_image_x.getText().trim() );
                int pos_y = Integer.parseInt( txtfield_image_y.getText().trim() );
                int img_width = Integer.parseInt( txtfield_image_width.getText().trim() );
                int img_height = Integer.parseInt( txtfield_image_height.getText().trim() );
                int red = ( int ) ( color_chooser.getValue().getRed() * 255 );
                int green = ( int ) ( color_chooser.getValue().getGreen() * 255 );
                int blue = ( int ) ( color_chooser.getValue().getBlue() * 255 );
                for ( int i = 0; i < listview_pages.getItems().size(); i++ )
                {
                    CheckBox check = listview_pages.getItems().get( i );
                    if ( check.isSelected() )
                    {
                        try
                        {
                            content_stream = new PDPageContentStream( doc, doc_pages.get( i ), PDPageContentStream.AppendMode.APPEND, true );
                            content_stream.setLineWidth( Float.parseFloat( txtfield_shape_thick.getText().trim() ) );
                            content_stream.addRect( pos_x, pos_y, img_width, img_height );
                            if ( checkbox_fill_rect.isSelected() )
                            {
                                content_stream.setNonStrokingColor( red, green, blue );
                                content_stream.fill();
                            }
                            else
                            {
                                content_stream.setStrokingColor( red, green, blue );
                                content_stream.stroke();
                            }
                            content_stream.stroke();
                            content_stream.close();
                        }
                        catch ( IOException ex )
                        {
                            Message_box.show( "ex " + ex.getLocalizedMessage() );
                        }
                    }
                }
                refresh_image();
            }
        };
    }

    private void refresh_image()
    {
        try
        {
            buffered = renderer.renderImage( page_index - 1, zoom_level );
        }
        catch ( IOException ex )
        {
            Message_box.show( "ex " + ex.getLocalizedMessage() );
        }
        imgview_pdf.setImage( SwingFXUtils.toFXImage( buffered, null ) );
        imgview_pdf.setFitWidth( buffered.getWidth() );
        imgview_pdf.setFitHeight( buffered.getHeight() );
    }

    private EventHandler<ActionEvent> get_btn_save_pdf_action()
    {
        return new EventHandler<ActionEvent>()
        {
            @Override
            public void handle( ActionEvent event )
            {
                try
                {
                    doc.save( desktop_path + "out.pdf" );
                    Message_box.show( "PDF file is exported to your desktop", "Success", Message_box.info_message );
                }
                catch ( IOException ex )
                {
                    Message_box.show( "ex " + ex.getLocalizedMessage() );
                }
            }
        };
    }
}
