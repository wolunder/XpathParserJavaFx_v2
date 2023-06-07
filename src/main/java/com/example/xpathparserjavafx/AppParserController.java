package com.example.xpathparserjavafx;

import com.example.xpathparserjavafx.compare.CompareXml;
import com.example.xpathparserjavafx.exception.FileTypeException;
import com.example.xpathparserjavafx.exception.ParserFormatException;
import com.example.xpathparserjavafx.export.ExportFileReport;
import com.example.xpathparserjavafx.model.Cad;
import com.example.xpathparserjavafx.model.RegRecordOwner;
import com.example.xpathparserjavafx.parser.InterpretationRecords;
import com.example.xpathparserjavafx.parser.ParserPDF;
import com.example.xpathparserjavafx.parser.ParserXml;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AppParserController {
    @FXML
    private Tab tab_egrn;

    @FXML
    private Button tab_egrn_btn_convert;

    @FXML
    private Button tab_egrn_btn_file;

    @FXML
    private Button tab_egrn_btn_file1;

    @FXML
    private CheckBox tab_egrn_check_convert_or_compare;

    @FXML
    private Label tab_egrn_label_main_notification;

    @FXML
    private Label tab_egrn_label_notification;

    @FXML
    private Label tab_egrn_label_notification2;

    @FXML
    private ProgressBar tab_egrn_progress_bar;

    @FXML
    private ProgressIndicator tab_egrn_progress_indicator;

    @FXML
    private Tab tab_table;

    @FXML
    private TableColumn<?, ?> tab_table_c1;

    @FXML
    private TableColumn<?, ?> tab_table_c2;

    @FXML
    private TableColumn<?, ?> tab_table_c3;

    @FXML
    private ScrollBar tab_table_h_scroll;

    @FXML
    private TableView<?> tab_table_table_view;

    @FXML
    private ScrollBar tab_table_v_scroll;

    @FXML
    private TextField text_denominator;
    private ParserXml parserXml = new ParserXml();
    private ParserPDF parserPDF = new ParserPDF();
    private CompareXml compareXml = new CompareXml();
    private ExportFileReport exportFileReport = new ExportFileReport();
    private List<File> fileList;
    private List<File> compareFileList;
    private List<RegRecordOwner> listRecords;
    private List<RegRecordOwner> compareListRecords;
    private boolean flag = false;
    private Cad cad;
    private Cad compareCad;
    private double progress;
    static double progressBar;


    @FXML
    void initialize() {
        //инициализация интерфейса
        tab_egrn_label_notification.setText("Выберите файл и введите площадь участка в га, и \nвыберите тип выписки");
        tab_egrn_label_main_notification.setText("");
        tab_egrn_progress_bar.setStyle("-fx-accent:  #B0E0E6;");
        tab_egrn_progress_indicator.setStyle("-fx-accent:  #B0E0E6;");
        tab_egrn_btn_convert.setDisable(true);
        tab_egrn_btn_file1.setDisable(true);
        FileChooser fileChooser1 = new FileChooser();
        fileChooser1.setTitle("Выберите XML файл");
        // выбор типа преобразования
        tab_egrn_check_convert_or_compare.setOnAction(actionEvent -> {
            this.flag = tab_egrn_check_convert_or_compare.isSelected();

            if (this.flag) {
                tab_egrn_btn_file1.setDisable(false);

            } else {
                tab_egrn_btn_convert.setDisable(true);
                tab_egrn_btn_file1.setDisable(true);
                tab_egrn_btn_file1.setStyle("-fx-background-color:  #B0E0E6");
                tab_egrn_btn_file1.setText("Выбрать файл");
                tab_egrn_btn_file.setStyle("-fx-background-color:  #B0E0E6");
                tab_egrn_btn_file.setText("Выбрать файл");
                compareFileList = null;
                fileList = null;
            }
        });

        tab_egrn_btn_file.setOnAction(actionEvent -> {
            tab_egrn_progress_bar.setProgress(0.0);
            tab_egrn_progress_indicator.setProgress(0.0);
            fileList = createListFileChoicer(fileList, fileChooser1, this.flag);

            if ( fileList != null) {
                tab_egrn_btn_file.setText("Загружен");
                tab_egrn_label_notification.setText("");
                tab_egrn_label_main_notification.setText("Загружено файлов: " + fileList.size() + ".");
                tab_egrn_btn_file.setStyle("-fx-background-color: #F0E68C");
                tab_egrn_btn_convert.setDisable(false);
            }
        });

        tab_egrn_btn_file1.setOnAction(actionEvent -> {
            tab_egrn_progress_bar.setProgress(0.0);
            tab_egrn_progress_indicator.setProgress(0.0);
            compareFileList = createListFileChoicer(fileList, fileChooser1, this.flag);

            if (compareFileList != null) {
                tab_egrn_btn_file1.setText("Загружен");
                tab_egrn_label_main_notification.setText("Загружено файлов: 2.");
                tab_egrn_btn_file1.setStyle("-fx-background-color: #F0E68C");
                tab_egrn_btn_convert.setDisable(false);
            }
        });

        tab_egrn_btn_convert.setOnAction(actionEvent -> {
            try {
                tab_egrn_label_main_notification.setText("Начинается конвертация");
                if (!flag) { // конвертация в таблицу
                    File file = null;
                    this.progress = (10.0 / fileList.size()) / 10;//прогрес загрузки
                    for (int i = 0; i < fileList.size(); i++) {
//                        file = fileList.get(i);
                        this.cad = transformFileToCad(fileList.get(i), this.cad);

                        if (cad.isTransfer() != true && cad.getArea().length() > 0) {
                            InterpretationRecords.setFactorDenumerator(cad.getArea().length() - 4);
                        }
                        exportFileReport.export(cad);
                        file = null;
                        increaseProgressParser();
                    }
                } else { // сравнение по имени правообладателей
                    // file1
                    this.cad = transformFileToCad(this.fileList.get(0), this.cad);
                    //file2
                    this.compareCad = transformFileToCad(this.compareFileList.get(0), this.compareCad);

                    if (this.compareCad.isTransfer()) {
                        this.cad.setListOwner(compareXml.compareTransferListAndRegRecordList(compareCad.getListOwner(), cad.getListOwner()));
                    } else {
                        this.cad.setListOwner(compareXml.compareRegRecordList(compareCad.getListOwner(), cad.getListOwner()));
                    }
                    exportFileReport.export(this.cad);
                    tab_egrn_progress_indicator.setProgress(1.0);
                    tab_egrn_progress_bar.setProgress(1.0);
                }
                tab_egrn_label_main_notification.setText("Файлы на ходятся в \nC:\\конвертированные выписки");

            } catch (ParserConfigurationException e) {
                    tab_egrn_label_notification.setText("Ошибка конвертирования");
                }
                catch (NullPointerException e){
                    e.printStackTrace();
                    tab_egrn_label_notification.setText("Ошибка!");
                    tab_egrn_label_main_notification.setText("Выбран неправильный формат выписки  \nили не выбран совсем. ");
                }
                catch (FileNotFoundException e) {
                    tab_egrn_label_notification.setText("Ошибка!");
                    tab_egrn_label_main_notification.setText("Файл уже открыт в системе.\n Закройте файл и повторите операцию.");
                } catch (IOException e) {
                    tab_egrn_label_notification.setText("Ошибка загрузки файла");
                } catch (SAXException e) {
                    tab_egrn_label_notification.setText("Ошибка преобразования");
                }
                catch (IllegalArgumentException e){
                    tab_egrn_label_notification.setText("Ошибка программы. Отсутствует файл");
                }
                catch (Exception e){
                    tab_egrn_label_notification.setText("Ошибка!");
                    tab_egrn_label_main_notification.setText(e.getMessage());
                } finally {
                reset();
            }
        });


    }

    private List<File> createListFileChoicer(List<File> fileList, FileChooser fileChooser, boolean isCompare) {
        Stage stage1 = new Stage();
        HBox hBox = new HBox();
        hBox.setSpacing(20);
        Scene sceneFileChooser = new Scene(hBox, 350, 100);
        stage1.setScene(sceneFileChooser);
        FileChooser.ExtensionFilter filterPDF_XML = new FileChooser.ExtensionFilter("PDF andXML files (*.pdf)", "*.pdf","*.xml");
        fileChooser.getExtensionFilters().addAll( filterPDF_XML);
        if (!isCompare) {
            fileList = fileChooser.showOpenMultipleDialog(stage1);
        } else {
            fileList = new ArrayList<>(1);
            fileList.add(fileChooser.showOpenDialog(stage1));
        }
        return fileList;
    }

    private void increaseProgressParser() {
        progressBar += progress;
        tab_egrn_progress_bar.setProgress(progressBar);
        tab_egrn_progress_indicator.setProgress(progressBar);
    }

    private Cad transformFileToCad(File file, Cad cad) throws IOException, XPathExpressionException, ParserConfigurationException, ParserFormatException, SAXException {
        if (file.getName().endsWith(".xml")) {
            cad = parserXml.parseFile(file);
        } else if (file.getName().endsWith(".pdf")) {
            cad = parserPDF.parseFile(file);
        }
        return cad;
    }

    public void reset() {
        this.cad = null;
        this.compareCad = null;
        tab_egrn_btn_convert.setDisable(true);
        tab_egrn_btn_file1.setDisable(true);
        flag = false;
        tab_egrn_label_notification.setText("Выберите файл и введите площадь участка в га, и \nвыберите тип выписки");
        tab_egrn_btn_file.setText("Загрузите файл");
        tab_egrn_btn_file1.setText("Загрузите файл");
        tab_egrn_btn_file.setStyle("-fx-background-color: #B0E0E6");
        tab_egrn_btn_file1.setStyle("-fx-background-color: #B0E0E6");
        tab_egrn_check_convert_or_compare.setSelected(false);
    }

}
