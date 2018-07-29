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
    private PDFRenderer renderer;
    private final VBox root = new VBox( 10 );
    private final VBox vbox_list = new VBox( 10 );
    private final HBox hbox_controls = new HBox( 10 );
    private final HBox hbox_controls_2 = new HBox( 10 );
    private final HBox hbox_controls_3 = new HBox( 10 );
    private final HBox hbox_pdf = new HBox( 10 );
    private Button btn_load_pdf;
    private Button btn_save_pdf;
    private Button btn_draw_img;
    private Button btn_draw_text;
    private Button btn_draw_rect;
    private Font def_font;
    private PDPageTree doc_pages;
    private PDPageContentStream content_stream;
    private Text txt_image_x, txt_image_height, txt_image_y, txt_image_width;
    private Text txt_shape_thick;
    private Text txt_textimage;
    private TextField txtfield_image_x, txtfield_image_height, txtfield_image_y, txtfield_image_width;
    private TextField txtfield_shape_thick;
    private TextField txtfield_textimage;
    private int page_index = 0;
    private float zoom_level = 1.0f;
    private String desktop_path;
    private ListView<CheckBox> listview_pages;
    private CheckBox checkbox_all;
    private CheckBox checkbox_fill_rect;
    private Stage primaryStage;
    private ColorPicker color_chooser;
    private ComboBox<Integer> combobox_font_size;
    private ComboBox<Float> combobox_zoom;
    private BufferedImage buffered;

    @Override
    public void start( Stage primaryStage )
    {
        this.primaryStage = primaryStage;
        def_font = new Font( "Arial", 18 );
        desktop_path = desktop_path = FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath() + "\\";
        txt_shape_thick = new Text( "Thickness " );
        txt_shape_thick.setFont( def_font );
        txtfield_shape_thick = new TextField( "4" );
        txtfield_shape_thick.setPrefWidth( 100 );
        txtfield_shape_thick.setFont( def_font );
        txt_image_x = new Text( "Position X " );
        txt_image_y = new Text( "Position Y " );
        txt_image_width = new Text( "Width  " );
        txt_image_height = new Text( "Height " );
        txtfield_image_x = new TextField( "10" );
        txtfield_image_y = new TextField( "10" );
        txtfield_image_width = new TextField( "100" );
        txtfield_image_height = new TextField( "50" );
        txt_image_x.setFont( def_font );
        txt_image_y.setFont( def_font );
        txt_image_width.setFont( def_font );
        txt_image_height.setFont( def_font );
        txtfield_image_x.setFont( def_font );
        txtfield_image_y.setFont( def_font );
        txtfield_image_width.setFont( def_font );
        txtfield_image_height.setFont( def_font );
        txtfield_image_x.setPrefWidth( 100 );
        txtfield_image_y.setPrefWidth( 100 );
        txtfield_image_width.setPrefWidth( 100 );
        txtfield_image_height.setPrefWidth( 100 );
        txt_textimage = new Text( "Text " );
        txt_textimage.setFont( def_font );
        txtfield_textimage = new TextField( "" );
        txtfield_textimage.setPrefWidth( 150 );
        txtfield_textimage.setFont( def_font );
        btn_load_pdf = new Button();
        btn_load_pdf.setFont( def_font );
        btn_load_pdf.setText( "Load pdf" );
        btn_save_pdf = new Button();
        btn_save_pdf.setFont( def_font );
        btn_save_pdf.setText( "Save pdf" );
        btn_save_pdf.setDisable( true );
        btn_draw_img = new Button();
        btn_draw_img.setFont( def_font );
        btn_draw_img.setText( "Draw image" );
        btn_draw_img.setDisable( true );
        btn_draw_text = new Button();
        btn_draw_text.setFont( def_font );
        btn_draw_text.setText( "Draw text" );
        btn_draw_text.setDisable( true );
        btn_draw_rect = new Button();
        btn_draw_rect.setFont( def_font );
        btn_draw_rect.setText( "Draw rectangle" );
        btn_draw_rect.setDisable( true );
        imgview_pdf = new ImageView();
        imgview_pdf.setPreserveRatio( true );
        slider_page = new Slider( 0, 1, 0 );
        slider_page.setSnapToTicks( true );
        slider_page.setStyle( "-fx-font-size : 16pt" );
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
        btn_load_pdf.setOnAction( get_btn_load_action() );
        btn_draw_rect.setOnAction( get_btn_draw_rect_action() );
        btn_draw_img.setOnAction( get_btn_draw_image_action() );
        btn_draw_text.setOnAction( get_btn_draw_text_action() );
        btn_save_pdf.setOnAction( new EventHandler<ActionEvent>()
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
        } );
        checkbox_fill_rect = new CheckBox( "Fill" );
        checkbox_fill_rect.setFont( def_font );
        checkbox_all = new CheckBox( "Select all" );
        checkbox_all.setFont( def_font );
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
        combobox_font_size.setStyle( "-fx-font-size : 16pt" );
        combobox_zoom = new ComboBox<>( FXCollections.observableArrayList( 50F, 100F, 150F, 200F, 250F, 300F, 350F, 400F, 450F, 500F, 550F, 600F, 650F, 700F ) );
        combobox_zoom.getSelectionModel().select( 1 );
        combobox_zoom.setStyle( "-fx-font-size : 16pt" );
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
        scroll_pdf.prefWidthProperty().bind( primaryStage.widthProperty().subtract( 210 ) );
        scroll_pdf.prefHeightProperty().bind( primaryStage.heightProperty().subtract( 250 ) );
        scroll_pdf.setCenterShape( true );
        listview_pages = new ListView<>();
        listview_pages.setPrefWidth( 200 );
        listview_pages.prefHeightProperty().bind( scroll_pdf.prefHeightProperty() );
        hbox_controls.getChildren().add( btn_load_pdf );
        hbox_controls.getChildren().add( slider_page );
        hbox_controls.getChildren().add( combobox_zoom );
        hbox_controls.getChildren().add( btn_save_pdf );
        hbox_controls.setAlignment( Pos.CENTER_LEFT );
        hbox_controls_2.getChildren().add( txt_image_x );
        hbox_controls_2.getChildren().add( txtfield_image_x );
        hbox_controls_2.getChildren().add( txt_image_y );
        hbox_controls_2.getChildren().add( txtfield_image_y );
        hbox_controls_2.getChildren().add( txt_image_width );
        hbox_controls_2.getChildren().add( txtfield_image_width );
        hbox_controls_2.getChildren().add( txt_image_height );
        hbox_controls_2.getChildren().add( txtfield_image_height );
        hbox_controls_2.getChildren().add( btn_draw_img );
        hbox_controls_2.setAlignment( Pos.CENTER_LEFT );
        hbox_controls_3.getChildren().add( txt_textimage );
        hbox_controls_3.getChildren().add( txtfield_textimage );
        hbox_controls_3.getChildren().add( color_chooser );
        hbox_controls_3.getChildren().add( combobox_font_size );
        hbox_controls_3.getChildren().add( btn_draw_text );
        hbox_controls_3.getChildren().add( txt_shape_thick );
        hbox_controls_3.getChildren().add( txtfield_shape_thick );
        hbox_controls_3.getChildren().add( checkbox_fill_rect );
        hbox_controls_3.getChildren().add( btn_draw_rect );
        hbox_controls_3.setAlignment( Pos.CENTER_LEFT );
        vbox_list.getChildren().add( checkbox_all );
        vbox_list.getChildren().add( listview_pages );
        hbox_pdf.getChildren().add( vbox_list );
        hbox_pdf.getChildren().add( scroll_pdf );
        root.setPadding( new Insets( 10 ) );
        root.getChildren().add( hbox_controls );
        root.getChildren().add( hbox_controls_2 );
        root.getChildren().add( hbox_controls_3 );
        root.getChildren().add( hbox_pdf );
        Scene scene = new Scene( root, 1280, 768 );
        primaryStage.setTitle( "Hello World of PDF Editing" );
        primaryStage.setScene( scene );
        primaryStage.show();
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
                    slider_page.setMax( doc_pages.getCount() - 1 );
                    slider_page.setValue( 0 );
                    slider_page.setDisable( false );
                    btn_save_pdf.setDisable( false );
                    btn_draw_img.setDisable( false );
                    btn_draw_text.setDisable( false );
                    btn_draw_rect.setDisable( false );
                    renderer = new PDFRenderer( doc );
                    listview_pages.getItems().clear();
                    page_index = 0;
                    int pdf_width = ( int ) doc_pages.get( page_index ).getMediaBox().getWidth();
                    int pdf_height = ( int ) doc_pages.get( page_index ).getMediaBox().getHeight();
                    txtfield_image_x.setText( pdf_width - 100 + "" );
                    txtfield_image_y.setText( pdf_height - 50 + "" );
                    txtfield_image_width = new TextField( "100" );
                    txtfield_image_height = new TextField( "50" );
                    for ( int i = 0; i < doc_pages.getCount(); i++ )
                    {
                        CheckBox page_checkbox = new CheckBox( "Page " + i );
                        page_checkbox.setFont( def_font );
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
            }
        };
    }

    private EventHandler<ActionEvent> get_btn_draw_text_action()
    {
        return new EventHandler<ActionEvent>()
        {
            @Override
            public void handle( ActionEvent event )
            {
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
            buffered = renderer.renderImage( page_index, zoom_level );
        }
        catch ( IOException ex )
        {
            Message_box.show( "ex " + ex.getLocalizedMessage() );
        }
        imgview_pdf.setImage( SwingFXUtils.toFXImage( buffered, null ) );
        imgview_pdf.setFitWidth( buffered.getWidth() );
        imgview_pdf.setFitHeight( buffered.getHeight() );
    }
}
